com.digitald4.budget.SummaryCtrl = function($scope, SharedData, AccountService) {
	this.scope = $scope;
	this.sharedData = SharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.accountService = AccountService;
	this.refresh();
};

com.digitald4.budget.SummaryCtrl.prototype.scope;
com.digitald4.budget.SummaryCtrl.prototype.accountService;

com.digitald4.budget.SummaryCtrl.prototype.refresh = function() {
	var scope = this.scope;
	
	this.accountService.getSummaryData(this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDate().getFullYear(), function(accountSummaryList) {
		scope.accountSummaryList = accountSummaryList;
		scope.$apply();
	}, function(error) {
		notify(error);
	});
};