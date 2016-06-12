com.digitald4.budget.BudgetCtrl = function($scope, sharedData, portfolioService) {
	this.scope = $scope;
	this.scope.sharedData = sharedData;
	this.portfolioService = portfolioService;
	
	portfolioService.getPortfolios(sharedData.getSelectedPortfolioId(), function(portfolioData) {
		sharedData.setPortfolioData(portfolioData);
		$scope.$apply();
	}, function(error) {
	});
};

com.digitald4.budget.BudgetCtrl.prototype.scope;
com.digitald4.budget.BudgetCtrl.prototype.portfolioService;
