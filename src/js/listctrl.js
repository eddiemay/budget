com.digitald4.budget.ListCtrl = function($filter, sharedData, billService, balanceService, accountService,
    templateService) {
	this.dateFilter = $filter('date');
	sharedData.refresh = this.refresh.bind(this);
	sharedData.setControlType(sharedData.CONTROL_TYPE.MONTH);
	this.sharedData = sharedData;
	this.billService = billService;
	this.balanceService = balanceService;
	this.accountService = accountService;
	this.templateService = templateService;
	this.statuses = [
	    {id: 0, name: 'Unknown'},
	    {id: 1, name: 'Estimated'},
	    {id: 2, name: 'Billed'},
	    {id: 3, name: 'Scheduled'},
	    {id: 4, name: 'Pending'},
	    {id: 5, name: 'Paided'}];
	this.makeNew();
	this.refresh();
};

com.digitald4.budget.ListCtrl.prototype.makeNew = function() {
	var baseDate = this.sharedData.getStartDate();
	this.newBill = {
	  'dueDate': new Date(baseDate.getFullYear(), baseDate.getMonth(), 15).getTime(),
		'portfolio_id': this.sharedData.getSelectedPortfolioId()};
};

com.digitald4.budget.ListCtrl.calcBalances = function(accounts, bills, balances) {
	var rollingBalances = {};
	for (var a = 0; a < balances.length; a++) {
		rollingBalances[balances[a].account_id] = {
      balance: balances[a].balance || 0,
      balanceYTD: balances[a].balance_y_t_d || 0
		};
	}

	var paymentAccountIds = [];
  for (var a = 0; a < accounts.length; a++) {
    if (accounts[a].payment_account) {
      paymentAccountIds.push(accounts[a].id);
    }
  }

	for (var b = 0; b < bills.length; b++) {
		var bill = bills[b];
		rollingBalances[bill.account_id] = rollingBalances[bill.account_id] || {balance: 0, balanceYTD: 0};
		var rollingBalance = rollingBalances[bill.account_id];
    bill.name = bill.name || bill.account_name;
    rollingBalance.balance += bill.amount_due;
    rollingBalance.balanceYTD += bill.amount_due;
    bill.balancePost = rollingBalance.balanceYTD;

		bill.transaction = bill.transaction || {};
		bill.balancesPost = bill.balancesPost || {};
		for (var x = 0; x < paymentAccountIds.length; x++) {
		  var acctId = paymentAccountIds[x];
		  rollingBalances[acctId] = rollingBalances[acctId] || {balance: 0, balanceYTD: 0};
			rollingBalance = rollingBalances[acctId];

			var transaction = bill.transaction[acctId];
			if (transaction && transaction.amount) {
        rollingBalance.balance -= transaction.amount;
        rollingBalance.balanceYTD -= transaction.amount;
			} else if (typeof(transaction) == 'number') {
        rollingBalance.balance -= transaction;
        rollingBalance.balanceYTD -= transaction;
			}
			/*if (bill.account_id == acctId) {
        transaction.amount = bill.amount_due;
      }*/
			bill.balancesPost[acctId] = rollingBalance.balance;
		}
	}
	return bills;
};

com.digitald4.budget.ListCtrl.compare = function(bill, bill2) {
	if (bill.dueDate < bill2.dueDate) {
		return -1;
	} else if (bill.dueDate > bill2.dueDate) {
		return 1;
	} else if (bill.due_day < bill2.due_day) {
    return -1;
  } else if (bill.due_day > bill2.due_day) {
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
	this.bills = undefined;
	this.balances = undefined;

	if (this.selectedPortfolioId != this.sharedData.getSelectedPortfolioId()) {
	  this.selectedPortfolioId = this.sharedData.getSelectedPortfolioId();
    this.accountService.list(this.sharedData.getSelectedPortfolioId(), function(response) {
      this.accounts = response.items;
      if (this.balances && this.bills) {
        this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, this.balances);
      }
      this.paymentAccounts = [];
      for (var a = 0; a < this.accounts.length; a++) {
        if (this.accounts[a].payment_account) {
          this.paymentAccounts.push(this.accounts[a]);
        }
      }
    }.bind(this), notify);

    this.templateService.list(this.sharedData.getSelectedPortfolioId(), function(templates) {
      this.templates = templates;
    }.bind(this), notify);
  }

	this.balanceService.list(this.sharedData.getSelectedPortfolioId(), this.sharedData.getYear(),
	    this.sharedData.getMonth(), function(response) {
    this.balances = response.items;
    if (this.accounts && this.bills) {
      this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, this.balances);
    }
  }.bind(this), notify);

	this.billService.list(this.sharedData.getSelectedPortfolioId(), this.sharedData.getYear(), this.sharedData.getMonth(),
	    function(response) {
    var sorted = com.digitald4.budget.ListCtrl.sortBills(response.items);
    if (this.accounts && this.balances) {
      this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, sorted, this.balances);
    } else {
      this.bills = sorted;
    }
  }.bind(this), notify);
};

com.digitald4.budget.ListCtrl.sortBills = function(bills) {
  var sortedBills = [];
  for (var b = 0; b < bills.length; b++) {
    com.digitald4.budget.ListCtrl.insertBill(sortedBills, bills[b]);
  }
  return sortedBills;
};

com.digitald4.budget.ListCtrl.insertBill = function(bills, bill) {
  bill.dueDate = new Date(bill.year, bill.month - 1, bill.day);
	for (var b = 0; b < bills.length; b++) {
		if (com.digitald4.budget.ListCtrl.compare(bills[b], bill) > 0) {
			bills.splice(b, 0, bill);
			return bills;
		}
	}
	bills.push(bill);
	return bills;
};

com.digitald4.budget.ListCtrl.prototype.addBill = function() {
  var date = new Date(this.newBill.dueDate);
  this.newBill.year = date.getFullYear();
  this.newBill.month = date.getMonth() + 1;
  this.newBill.day = date.getDate();
  this.newBill.dueDate = undefined;
	this.billService.create(this.newBill, function(bill) {
		com.digitald4.budget.ListCtrl.insertBill(this.bills, bill);
		com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, this.balances);
		this.makeNew();
	}.bind(this), notify);
};

com.digitald4.budget.ListCtrl.prototype.updateBill = function(bill, property) {
	var index = this.bills.indexOf(bill);
  var props = [property];
  if (property == 'dueDate') {
    var date = new Date(bill.dueDate);
    bill.year = date.getFullYear();
    bill.month = date.getMonth() + 1;
    bill.day = date.getDate();
    props = ['year', 'month', 'day'];
  }
	this.billService.update(bill, props, function(bill) {
		this.bills.splice(index, 1);
		com.digitald4.budget.ListCtrl.insertBill(this.bills, bill);
		com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, this.balances);
	}.bind(this), notify);
};

com.digitald4.budget.ListCtrl.prototype.deleteBill = function(bill) {
	var index = this.bills.indexOf(bill);
	this.billService.Delete(bill.id, function(success) {
    this.bills.splice(index, 1);
    com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, this.balances);
	}.bind(this), notify);
};

com.digitald4.budget.ListCtrl.prototype.applyTemplate = function() {
	this.applyTemplateError = undefined;
	this.billService.applyTemplate(this.selectedTemplate, this.sharedData.getYear(), this.sharedData.getMonth(),
	    function(response) {
    var sortedBills = [];
    for (var b = 0; b < bills.length; b++) {
      com.digitald4.budget.ListCtrl.insertBill(sortedBills, response.items[b]);
    }
    this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, sortedBills, this.balances);
  }.bind(this), notify);
};