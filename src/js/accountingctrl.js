com.digitald4.budget.AccountingCtrl = function(sharedData, billService, accountService) {
	sharedData.refresh = this.refresh.bind(this);
	sharedData.setControlType(sharedData.CONTROL_TYPE.MONTH);
	this.sharedData = sharedData;
	this.billService = billService;
	this.accountService = accountService;
	this.refresh();
};

com.digitald4.budget.AccountingCtrl.prototype.refresh = function() {
  if (this.selectedPortfolioId != this.sharedData.getSelectedPortfolioId()) {
    this.selectedPortfolioId = this.sharedData.getSelectedPortfolioId();
    this.accountService.list(this.sharedData.getSelectedPortfolioId(), function(response) {
      this.accounts = response.items;
      this.paymentAccounts = [];
      for (var a = 0; a < this.accounts.length; a++) {
        var account = this.accounts[a];
        if (account.payment_account) {
          this.paymentAccounts.push(account);
        }
      }
    }.bind(this), notify);
  }
	
	this.billService.list(this.sharedData.getSelectedPortfolioId(), this.sharedData.getYear(), this.sharedData.getMonth(),
			function(response) {
				this.transactions = [];
				for (var b = 0; b < response.items.length; b++) {
					var bill = response.items[b];
					bill.name = bill.name || bill.account_name;
					for (var id in bill.transaction) {
					  bill.transaction[id].payment_date = bill.transaction[id].payment_date ||
					      new Date(bill.year, bill.month - 1, bill.day);
						this.transactions.push({bill: bill, trans: bill.transaction[id], debitAccountId: parseInt(id, 10)});
					}
				}
			}.bind(this), notify);
	this.newTrans = {};
};

com.digitald4.budget.AccountingCtrl.prototype.addTransaction = function() {
	this.transAddError = undefined;
	this.billService.addTransaction(this.newTrans, this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDate().toJSON(), this.sharedData.getEndDate().toJSON(),
			function(transactions) {
				this.transactions = transactions;
				this.newTrans = {};
			}.bind(this), function(error) {
				this.transAddError = error;
			}.bind(this));
};

com.digitald4.budget.AccountingCtrl.prototype.updateBill = function(bill, property) {
	var index = this.bills.indexOf(bill);
	this.billService.update(bill, [property], function(bill) {
		this.bills.splice(index, 1);
		this.insertBill(this.bills, bill);
		this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills);
	}.bind(this), function(error) {
		this.billUpdateError = error;
	}.bind(this));
};

com.digitald4.budget.AccountingCtrl.prototype.updateTransaction = function(trans, property) {
	var index = this.transactions.indexOf(trans);
	this.billService.update(trans.bill, [property], function(bill) {
		this.transactions.splice(index, 1, {bill: bill, trans: bill.transaction[id]});
	}.bind(this), notify);
};