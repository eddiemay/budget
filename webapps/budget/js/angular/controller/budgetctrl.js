com.digitald4.budget.BudgetCtrl = function(sharedData, portfolioService) {
	this.sharedData = sharedData;
	
	portfolioService.getPortfolios(sharedData.getSelectedPortfolioId(), function(portfolioData) {
		sharedData.setPortfolioData(portfolioData);
	}, function(error) {
	});
};
