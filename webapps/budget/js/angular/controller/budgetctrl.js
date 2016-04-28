com.digitald4.budget.BudgetCtrl = function($scope, SharedData, PortfolioService) {
	this.scope = $scope;
	this.scope.sharedData = SharedData;
	this.portfolioService = PortfolioService;
	
	PortfolioService.getPortfolios(this.scope.sharedData.getSelectedPortfolioId(), function(portfolioData) {
		SharedData.setPortfolioData(portfolioData);
		$scope.$apply();
	}, function(error) {
	});
};

com.digitald4.budget.BudgetCtrl.prototype.scope;
com.digitald4.budget.BudgetCtrl.prototype.portfolioService;
