com.digitald4.budget.PortfoliosCtrl = function($scope, SharedData, PortfolioService) {
	this.scope = $scope;
	this.scope.sharedData = SharedData;
	this.portfolioService = PortfolioService;
	this.scope.addPortfolio = this.addPortfolio.bind(this);
	this.scope.updatePortfolio = this.updatePortfolio.bind(this);
	this.refresh();
};

com.digitald4.budget.PortfoliosCtrl.prototype.scope;
com.digitald4.budget.PortfoliosCtrl.prototype.portfolioService;

com.digitald4.budget.PortfoliosCtrl.prototype.refresh = function() {
	var scope = this.scope;
	this.portfolioService.getPortfolios(scope.sharedData.getSelectedPortfolioId(), function(portfolioData) {
		scope.sharedData.setPortfolioData(portfolioData);
		scope.$apply();
	}, function(error) {
		notify(error);
	});
	this.scope.newPortfolio = {};
};

com.digitald4.budget.PortfoliosCtrl.prototype.addPortfolio = function() {
	var scope = this.scope;
	scope.addError = undefined;
	this.portfolioService.addPortfolio(scope.newPortfolio, function(portfolioData) {
		scope.sharedData.setPortfolioData(portfolioData);
		scope.newPortfolio = {};
		scope.$apply();
	}, function(error) {
		scope.addError = error;
		scope.$apply();
	});
};

com.digitald4.budget.PortfoliosCtrl.prototype.updatePortfolio = function(portfolio, property) {
	var scope = this.scope;
	scope.updateError = undefined;
	this.portfolioService.updatePortfolio(portfolio, property, function(portfolio) {
		scope.$apply();
	}, function(error) {
		scope.updateError = error;
		scope.$apply();
	});
};