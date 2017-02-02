com.digitald4.budget.BudgetCtrl = function(sharedData, userService, portfolioService) {
	this.sharedData = sharedData;
	this.userService = userService;

	userService.getActive(function(user) {
	  sharedData.setUser(user);
	}, notify);
	
	portfolioService.getPortfolios(sharedData.getSelectedPortfolioId(), function(portfolioData) {
		sharedData.setPortfolioData(portfolioData);
	}, notify);
};

com.digitald4.budget.BudgetCtrl.prototype.logout = function() {
  this.userService.logout();
};
