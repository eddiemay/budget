com.digitald4.budget.ListCtrl = function($filter, sharedData, billService, accountService, templateService) {
	this.dateFilter = $filter('date');
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.billService = billService;
	this.accountService = accountService;
	this.templateService = templateService;
	this.statuses = [
	    {id: 0, name: 'Unknown'},
	    {id: 1, name: 'Estimated'},
	    {id: 2, name: 'Billed'},
	    {id: 3, name: 'Scheduled'},
	    {id: 4, name: 'Pending'},
	    {id: 5, name: 'Paided'}];
	this.refresh();
};

com.digitald4.budget.ListCtrl.prototype.billService;
com.digitald4.budget.ListCtrl.prototype.accountService;
com.digitald4.budget.ListCtrl.prototype.templateService;

com.digitald4.budget.ListCtrl.prototype.makeNew = function() {
	var baseDate = this.sharedData.getStartDate()
	var month = baseDate.getMonth() + 1;
	if (month < 10) {
		month = '0' + month;
	}
	var newBill = {'trans': [],
			'due_date': new Date(month + '/15/' + baseDate.getFullYear()).getTime(),
			'portfolio_id': this.sharedData.getSelectedPortfolioId()};
	for (var x = 0; x < this.bankAccounts.length; x++) {
		var ba = this.bankAccounts[x];
		newBill.trans.push({'debit_account_id': ba.id});
	}
	this.newBill = newBill;
};

com.digitald4.budget.ListCtrl.calcBalances = function(accounts, bills) {
	var accountHash = {};
	var paymentAccounts = [];
	for (var a = 0; a < accounts.length; a++) {
		var account = accounts[a];
		account.balance = account.balance || {balance: 0, balance_year_to_date: 0};
		accountHash['' + account.id] = {
				account: account,
				balance: {balance: account.balance.balance,
									balance_year_to_date: account.balance.balance_year_to_date}
		};
		if (account.payment_account) {
			paymentAccounts.push(account);
		}
	}
	for (var b = 0; b < bills.length; b++) {
		var bill = bills[b];
		var account = accountHash['' + bill.account_id]; 
		if (account) {
			bill.name = bill.name || account.account.name;
			account.balance.balance += bill.amount_due;
			account.balance.balance_year_to_date += bill.amount_due;
			bill.balancePost = account.balance.balance_year_to_date;
		} else {
			bill.balancePost = bill.amount_due;
		}
		bill.trans = [];
		for (var x = 0; x < paymentAccounts.length; x++) {
			var paymentAccount = accountHash['' + paymentAccounts[x].id];
			var transAcct = {'debit_account_id': paymentAccount.account.id};
			if (bill.account_id == paymentAccount.account.id) {
				transAcct.amount = bill.amount_due;
			}
			bill.transaction = bill.transaction || [];
			for (var t = 0; t < bill.transaction.length; t++) {
				if (bill.transaction[t].debit_account_id == paymentAccount.account.id
						&& bill.transaction[t].amount != 0) {
					transAcct = bill.transaction[t];
					paymentAccount.balance.balance -= transAcct.amount;
					paymentAccount.balance.balance_year_to_date -= transAcct.amount;
				}
			}
			transAcct.balancePost = paymentAccount.balance.balance;
			bill.trans.push(transAcct);
		}
	}
	return bills;
};

com.digitald4.budget.ListCtrl.compare = function(bill, bill2) {
	if (bill.due_date < bill2.due_date) {
		return -1;
	} else if (bill.due_date > bill2.due_date) {
		return 1;
	} else if (bill.rank > bill2.rank) {
		return -1;
	} else if (bill.rank < bill2.rank) {
		return 1;
	} else if (bill.amount_due < bill2.amount_due) {
		return -1;
	} else if (bill.amount_due > bill2.amount_due) {
		return 1;
	} else if (bill.id < bill2.id) {
		return -1;
	} else if (bill.id > bill2.id) {
		return 1;
	}
	return 0;
};

com.digitald4.budget.ListCtrl.prototype.refresh = function() {
	this.accounts = undefined;
	this.bills = undefined;
	this.accountService.getAccounts(this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDate().getTime(), function(accounts) {
		this.accounts = accounts;
		if (this.bills) {
			this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills);
		}
		this.bankAccounts = [];
		for (var a = 0; a < accounts.length; a++) {
			var account = accounts[a];
			if (account.payment_account) {
				this.bankAccounts.push(account);
			}
		}
		this.makeNew();
	}.bind(this), notify);

	this.billService.getBills(this.sharedData.getSelectedPortfolioId(), this.sharedData.getStartDate().getTime(), 'MONTH',
			function(bills) {
				var sortedBills = [];
				for (var b = 0; b < bills.length; b++) {
					this.insertBill(sortedBills, bills[b]);
				}
				if (this.accounts) { 
					this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, sortedBills);
				} else {
					this.bills = sortedBills;
				}
			}.bind(this), notify);

	this.templateService.list(this.sharedData.getSelectedPortfolioId(), {}, function(templates) {
		this.templates = templates;
	}.bind(this), notify);
};

com.digitald4.budget.ListCtrl.prototype.insertBill = function(bills, bill) {
	for (var b = 0; b < bills.length; b++) {
		if (com.digitald4.budget.ListCtrl.compare(bills[b], bill) > 0) {
			bills.splice(b, 0, bill);
			return;
		}
	}
	bills.push(bill);
};

com.digitald4.budget.ListCtrl.prototype.addBill = function() {
	this.billAddError = undefined;
	this.newBill.amount_due = parseFloat(this.newBill.amount_due);
	this.newBill.transaction = [];
	for (var t = 0; t < this.newBill.trans.length; t++) {
		var tran = this.newBill.trans[t];
		tran.amount = parseFloat(tran.amount);
		if (tran.amount && tran.amount != 0) {
			this.newBill.transaction.push(tran);
		}
	}
	this.newBill.trans = undefined;
	this.billService.create(this.newBill, function(bill) {
		this.insertBill(this.bills, bill);
		this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills);
		this.makeNew();
	}.bind(this), function(error) {
		this.billAddError = error;
	}.bind(this));
};

com.digitald4.budget.ListCtrl.prototype.updateBill = function(bill, property) {
	this.billUpdateError = undefined;
	var index = this.bills.indexOf(bill);
	this.billService.update(bill, [property], function(bill) {
		this.bills.splice(index, 1);
		this.insertBill(this.bills, bill);
		this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills);
	}.bind(this), function(error) {
		this.billUpdateError = error;
	}.bind(this));
};

com.digitald4.budget.ListCtrl.prototype.deleteBill = function(bill) {
	this.billUpdateError = undefined;
	var index = this.bills.indexOf(bill);
	this.billService.Delete(bill.id, function(success) {
		if (success) {
			this.bills.splice(index, 1);
			this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills);
		}
	}.bind(this), function(error) {
		this.billUpdateError = error;
	}.bind(this));
};

com.digitald4.budget.ListCtrl.prototype.updateBillTrans = function(bill) {
	var index = this.bills.indexOf(bill);
	bill.transaction = [];
	for (var t = 0; t < bill.trans.length; t++) {
		var tran = bill.trans[t];
		tran.amount = parseFloat(tran.amount);
		if (tran.amount && tran.amount != 0) {
			bill.transaction.push(tran);
		}
	}
	this.billUpdateError = undefined;
	this.billService.update(bill, ['transaction'], function(bill) {
		this.bills[index] = bill;
		this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills);
	}.bind(this), notify);
};

com.digitald4.budget.ListCtrl.prototype.applyTemplate = function() {
	this.applyTemplateError = undefined;
	this.billService.applyTemplate(this.selectedTemplate, this.sharedData.getMonth().getTime(), 'MONTH',
			function(bills) {
		    var sortedBills = [];
        for (var b = 0; b < bills.length; b++) {
          this.insertBill(sortedBills, bills[b]);
        }
		    this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, sortedBills);
	    }.bind(this), notify);
};