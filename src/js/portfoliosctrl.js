com.digitald4.budget.PortfoliosCtrl = function(sharedData, portfolioService) {
	this.sharedData = sharedData;
	this.portfolioService = portfolioService;
	var UserRoleUI = proto.budget.UserRoleUI;
	this.UserRoleName = {
	    12: 'Owner',
	    2: 'Can Edit',
	    3: 'Read Only'
	};
	this.refresh();
};

com.digitald4.budget.PortfoliosCtrl.prototype.portfolioService;

com.digitald4.budget.PortfoliosCtrl.prototype.refresh = function() {
	this.portfolioService.list({}, function(response) {
		this.sharedData.setPortfolioData(response.items);
	}.bind(this), notify);
	this.newPortfolio = {};
};

com.digitald4.budget.PortfoliosCtrl.prototype.addPortfolio = function() {
  var userId = this.sharedData.user.id;
  this.newPortfolio.portfolio_user = [{user_id: userId, role: 12}];
	this.portfolioService.create(this.newPortfolio, function(portfolio) {
		this.sharedData.portfolios.push(portfolio);
		this.newPortfolio = {};
	}.bind(this), notify);
};

com.digitald4.budget.PortfoliosCtrl.prototype.updatePortfolio = function(portfolio, property) {
	var index = this.sharedData.portfolios.indexOf(portfolio);
	this.portfolioService.update(portfolio, [property], function(portfolio) {
	  this.sharedData.portfolios.splice(index, 1, portfolio);
	}.bind(this), notify);
};