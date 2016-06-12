com.digitald4.budget.SummaryCtrl = function($scope, sharedData, accountService) {
	this.scope = $scope;
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.accountService = accountService;
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