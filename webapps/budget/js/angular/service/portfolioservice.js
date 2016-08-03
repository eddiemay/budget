com.digitald4.budget.PortfolioService = function(connector) {
	this.connector = connector;
};

com.digitald4.budget.PortfolioService.prototype.connector;

com.digitald4.budget.PortfolioService.prototype.getPortfolios = function(selectedId,
		successCallback, errorCallback) {
	this.connector.performRequest('portfolios', {selected_id: selectedId}, successCallback, errorCallback);
};

com.digitald4.budget.PortfolioService.prototype.addPortfolio = function(newPortfolio,
		successCallback, errorCallback) {
	this.connector.performRequest('create_portfolio', {portfolio: newPortfolio},
			successCallback, errorCallback);
};

com.digitald4.budget.PortfolioService.prototype.updatePortfolio = function(portfolio, property,
		successCallback, errorCallback) {
	var request = {id: portfolio.id,
			property: property,
			value: portfolio[property].toString()};
	this.connector.performRequest('update_portfolio', request, successCallback, errorCallback);
};

com.digitald4.budget.PortfolioService.prototype.deletePortfolio = function(portfolio, property,
		successCallback, errorCallback) {
	this.connector.performRequest('delete_portfolio', {portfolio_id: portfolio.id},
			successCallback, errorCallback);
};