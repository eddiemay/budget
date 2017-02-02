com.digitald4.budget.AccountsCtrl = function($scope, sharedData, accountService) {
	this.scope = $scope;
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.accountService = accountService;
	this.scope.showAddDialog = this.showAddDialog.bind(this);
	this.scope.addAccount = this.addAccount.bind(this);
	this.scope.updateAccount = this.updateAccount.bind(this);
	this.refresh();
};

com.digitald4.budget.AccountsCtrl.prototype.scope;
com.digitald4.budget.AccountsCtrl.prototype.categories;
com.digitald4.budget.AccountsCtrl.prototype.accountService;

com.digitald4.budget.AccountsCtrl.prototype.refresh = function() {
	var scope = this.scope;
	
	this.accountService.getAccounts(this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDate().getTime(), function(accounts) {
		scope.accounts = accounts;
	}, function(error) {
		notify(error);
	});
	this.scope.newAccount = {portfolio_id: this.sharedData.getSelectedPortfolioId()};
};

com.digitald4.budget.AccountsCtrl.prototype.showAddDialog = function() {
	$.modal({
		content:  '<p>Add Transaction</p>' +
				  '<input type="text" ng-model="name">',
		title: 'Add Transaction',
		maxWidth: 500,
		buttons: {
			'Ok': function(win) { addTranscaction(); win.closeModal(); },
			'Cancel': function(win) { win.closeModal(); }
		}
	});
};

com.digitald4.budget.AccountsCtrl.prototype.addAccount = function() {
	var scope = this.scope;
	scope.addError = undefined;
	this.accountService.addAccount(scope.newAccount, function(accounts) {
		scope.accounts = accounts;
		scope.newAccount = {portfolio_id: this.sharedData.getSelectedPortfolioId()};
		scope.$apply();
	}, function(error) {
		scope.addError = error;
		scope.$apply();
	});
};

com.digitald4.budget.AccountsCtrl.prototype.updateAccount = function(account, property) {
	var scope = this.scope;
	scope.updateError = undefined;
	this.accountService.updateAccount(account, property, function(account) {
		scope.$apply();
	}, function(error) {
		scope.updateError = error;
		scope.$apply();
	});
};