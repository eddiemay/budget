com.digitald4.budget.BudgetCtrl = function(sharedData, userService, portfolioService) {
	this.sharedData = sharedData;

	userService.getActive(function(user) {
	  sharedData.setUser(user);
	}, notify);
	
	portfolioService.list({}, function(response) {
		sharedData.setPortfolioData(response.result);
	}, notify);
};
