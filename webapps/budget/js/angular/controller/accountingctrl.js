com.digitald4.budget.AccountingCtrl = function($scope, SharedData, BillService, AccountService) {
	this.scope = $scope;
	this.sharedData = SharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.billService = BillService;
	this.accountService = AccountService;
	this.scope.addTransaction = this.addTransaction.bind(this);
	this.scope.updateTransaction = this.updateTransaction.bind(this);
	this.refresh();
};

com.digitald4.budget.AccountingCtrl.prototype.scope;
com.digitald4.budget.AccountingCtrl.prototype.billService;
com.digitald4.budget.AccountingCtrl.prototype.accountService;

com.digitald4.budget.AccountingCtrl.prototype.refresh = function() {
	var scope = this.scope;
	this.accountService.getAccounts(this.sharedData.getSelectedPortfolioId(), function(accounts) {
		scope.accounts = accounts;
		scope.$apply();
	}, function(error) {
		notify(error);
	});
	
	this.accountService.getBankAccounts(this.sharedData.getSelectedPortfolioId(), function(bankAccounts) {
		scope.bankAccounts = bankAccounts;
		scope.$apply();
	}, function(error) {
		notify(error);
	});
	
	this.billService.getTransactions(this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDate().toJSON(), this.sharedData.getEndDate().toJSON(), function(transactions) {
		scope.transactions = transactions;
		scope.$apply();
	}, function(error) {
		notify(error);
	});
	this.scope.newtrans = {};
};

com.digitald4.budget.AccountingCtrl.prototype.addTransaction = function() {
	var scope = this.scope;
	scope.transAddError = undefined;
	this.billService.addTransaction(scope.newtrans, this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDate().toJSON(), this.sharedData.getEndDate().toJSON(), function(transactions) {
		scope.transactions = transactions;
		scope.newtrans = {};
		scope.$apply();
	}, function(error) {
		scope.transAddError = error;
		scope.$apply();
	});
};

com.digitald4.budget.AccountingCtrl.prototype.updateTransaction = function(trans, property) {
	var scope = this.scope;
	scope.transUpdateError = undefined;
	this.billService.updateTransaction(trans, property, this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDate().toJSON(), this.sharedData.getEndDate().toJSON(), function(transactions) {
		scope.transactions = transactions;
		scope.$apply();
	}, function(error) {
		scope.transUpdateError = error;
		scope.$apply();
	});
};