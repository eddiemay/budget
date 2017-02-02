com.digitald4.budget.PortfolioService = function(restService) {
	this.protoService = new com.digitald4.common.ProtoService("portfolio", restService);
};

com.digitald4.budget.PortfolioService.prototype.protoService;

com.digitald4.budget.PortfolioService.prototype.getPortfolios = function(selectedId,
		successCallback, errorCallback) {
	this.protoService.list([], successCallback, errorCallback);
};

com.digitald4.budget.PortfolioService.prototype.addPortfolio = function(newPortfolio,
		successCallback, errorCallback) {
	this.protoService.create(newPortfolio, successCallback, errorCallback);
};

com.digitald4.budget.PortfolioService.prototype.updatePortfolio = function(portfolio, property,
		successCallback, errorCallback) {
	this.protoService.update(portfolio, [property], successCallback, errorCallback);
};

com.digitald4.budget.PortfolioService.prototype.deletePortfolio = function(portfolio, property,
		successCallback, errorCallback) {
	this.protoService.Delete(portfolio.id, successCallback, errorCallback);
};