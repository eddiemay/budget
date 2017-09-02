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
	    {id: 'PS_UNKNOWN', name: 'Unknown'},
	    {id: 'PS_ESTIMATED', name: 'Estimated'},
	    {id: 'PS_BILLED', name: 'Billed'},
	    {id: 'PS_SCHEDULED', name: 'Scheduled'},
	    {id: 'PS_PENDING', name: 'Pending'},
	    {id: 'PS_PAID', name: 'Paided'}];
	this.refresh();
};

com.digitald4.budget.ListCtrl.calcBalances = function(accounts, bills, balances) {
	var rollingBalances = {};
	for (var a = 0; a < balances.length; a++) {
		rollingBalances[balances[a].accountId] = {
      balance: balances[a].balance || 0,
      balanceYTD: balances[a].balanceYTD || 0
		};
	}

  var accountMap = {};
	var paymentAccountIds = [];
  for (var a = 0; a < accounts.length; a++) {
    var account = accounts[a];
    accountMap[account.id] = account;
    if (account.paymentAccount) {
      paymentAccountIds.push(account.id);
    }
  }

	for (var b = 0; b < bills.length; b++) {
		var bill = bills[b];
		rollingBalances[bill.accountId] = rollingBalances[bill.accountId] || {balance: 0, balanceYTD: 0};
		var rollingBalance = rollingBalances[bill.accountId];
		var account = accountMap[bill.accountId] || {name: 'Unknown Acccount'};
    bill.name = bill.name || account.name;
    rollingBalance.balance += bill.amountDue;
    rollingBalance.balanceYTD += bill.amountDue;
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
			/*if (bill.accountId == acctId) {
        transaction.amount = bill.amountDue;
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
	} else if (bill.dueDay < bill2.dueDay) {
    return -1;
  } else if (bill.dueDay > bill2.dueDay) {
    return 1;
	} else if (bill.rank > bill2.rank) {
		return -1;
	} else if (bill.rank < bill2.rank) {
		return 1;
	} else if (bill.amountDue < bill2.amountDue) {
		return -1;
	} else if (bill.amountDue > bill2.amountDue) {
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
	var baseDate = this.sharedData.getStartDate();

	if (this.selectedPortfolioId != this.sharedData.getSelectedPortfolioId()) {
	  this.selectedPortfolioId = this.sharedData.getSelectedPortfolioId();
    this.newBill = {
      dueDate: new Date(baseDate.getFullYear(), baseDate.getMonth(), 15).getTime(),
    };
    this.accountService.list(this.sharedData.getSelectedPortfolioId(), function(response) {
      this.accounts = response.result;
      if (this.balances && this.bills) {
        this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, this.balances);
      }
      this.accountMap = {};
      this.paymentAccounts = [];
      for (var a = 0; a < this.accounts.length; a++) {
        var account = this.accounts[a];
        this.accountMap[account.id] = account;
        if (account.paymentAccount) {
          this.paymentAccounts.push(account);
        }
      }
    }.bind(this), notify);

    this.templateService.list(this.sharedData.getSelectedPortfolioId(), function(response) {
      this.templates = response.result;
    }.bind(this), notify);
  } else if (!this.newBill.dateEdited) {
    this.newBill.dueDate = new Date(baseDate.getFullYear(), baseDate.getMonth(), 15).getTime();
  }

	this.balanceService.list(this.sharedData.getSelectedPortfolioId(), this.sharedData.getYear(),
	    this.sharedData.getMonth(), function(response) {
    this.balances = response.result;
    if (this.accounts && this.bills) {
      this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, this.balances);
    }
  }.bind(this), notify);

	this.billService.list(this.sharedData.getSelectedPortfolioId(), this.sharedData.getYear(), this.sharedData.getMonth(),
	    function(response) {
    var sorted = com.digitald4.budget.ListCtrl.sortBills(response.result);
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
  var dueDate = this.newBill.dueDate;
  var date = new Date(dueDate);
  this.newBill.year = date.getFullYear();
  this.newBill.month = date.getMonth() + 1;
  this.newBill.day = date.getDate();
  this.newBill.dueDate = undefined;
  this.newBill.portfolioId = this.accountMap[this.newBill.accountId].portfolioId;
	this.billService.create(this.newBill, function(bill) {
		com.digitald4.budget.ListCtrl.insertBill(this.bills, bill);
		com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, this.balances);
		this.newBill = {dueDate: dueDate};
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
	  var bills = response.result;
    var sortedBills = [];
    for (var b = 0; b < bills.length; b++) {
      com.digitald4.budget.ListCtrl.insertBill(sortedBills, bills[b]);
    }
    this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, sortedBills, this.balances);
  }.bind(this), notify);
};