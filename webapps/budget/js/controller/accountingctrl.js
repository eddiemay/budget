com.digitald4.budget.AccountingCtrl = function(sharedData, billService, accountService) {
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.billService = billService;
	this.accountService = accountService;
	this.refresh();
};

com.digitald4.budget.AccountingCtrl.prototype.billService;
com.digitald4.budget.AccountingCtrl.prototype.accountService;

com.digitald4.budget.Transaction = function(bill, index) {
	this.bill = bill;
	this.trans = bill.transaction[index];
	this.index = index;
};

com.digitald4.budget.AccountingCtrl.prototype.refresh = function() {
	this.accountService.getAccounts(this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDate().getTime(), function(accounts) {
				this.accounts = accounts;
				this.paymentAccounts = [];
				for (var a = 0; a < accounts.length; a++) {
					var account = accounts[a];
					if (account.payment_account) {
						this.paymentAccounts.push(account);
					}
				}
			}.bind(this), notify);
	
	this.billService.getBills(this.sharedData.getSelectedPortfolioId(), this.sharedData.getStartDate().getTime(), 'MONTH',
			function(bills) {
				this.transactions = [];
				for (var b = 0; b < bills.length; b++) {
					var bill = bills[b];
					for (var t = 0; t < bill.transaction.length; t++) {
						this.transactions.push(new com.digitald4.budget.Transaction(bill, t));
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

com.digitald4.budget.AccountingCtrl.prototype.updateTransaction = function(trans, property) {
	this.transUpdateError = undefined;
	this.billService.updateBill(trans, property, function(transactions) {
		this.transactions = transactions;
	}.bind(this), function(error) {
		this.transUpdateError = error;
	}.bind(this));
};