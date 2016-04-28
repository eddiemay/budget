com.digitald4.budget.PortfolioService = function(RestService) {
	this.restService = RestService;
};

com.digitald4.budget.PortfolioService.prototype.restService;

com.digitald4.budget.PortfolioService.prototype.getPortfolios = function(portfolioId, successCallback, errorCallback) {
	this.restService.performRequest({action: 'getPortfolios', portfolioId: portfolioId}, successCallback, errorCallback);
};

com.digitald4.budget.PortfolioService.prototype.addPortfolio = function(newPortfolio, successCallback, errorCallback) {
	newPortfolio.action = 'addPortfolio';
	this.restService.performRequest(newPortfolio, successCallback, errorCallback);
};

com.digitald4.budget.PortfolioService.prototype.updatePortfolio = function(portfolio, property, successCallback, errorCallback) {
	var request = {action: 'updatePortfolio',
			id: portfolio.id,
			property: property,
			value: portfolio[property]};
	this.restService.performRequest(request, successCallback, errorCallback);
};