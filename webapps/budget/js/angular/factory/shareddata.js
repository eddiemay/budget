com.digitald4.budget.SharedData = function() {
	var today = new Date();
	this.month = new Date(today.getFullYear(), today.getMonth(), 1);
};

com.digitald4.budget.SharedData.prototype.refresh = function() {
	console.log('warning refresh not bound');
};

com.digitald4.budget.SharedData.prototype.setPortfolioData =
		function(portfolioData) {
	this.user = portfolioData.user;
	this.portfolios = portfolioData.portfolios;
	this.activePortfolioId = portfolioData.activePortfolio.id;
};

com.digitald4.budget.SharedData.prototype.getSelectedPortfolioId = function() {
	return this.activePortfolioId;
};

com.digitald4.budget.SharedData.prototype.getMonth = function() {
	return this.month;
};

com.digitald4.budget.SharedData.prototype.setMonth =
		function(year, month) {
	this.month = new Date(year, month, 1);
	this.refresh();
};

com.digitald4.budget.SharedData.prototype.prevMonth = function() {
	this.month.setDate(0);
	this.month.setDate(1);
	this.refresh();
};

com.digitald4.budget.SharedData.prototype.nextMonth = function() {
	this.month.setDate(32);
	this.month.setDate(1);
	this.refresh();
};

com.digitald4.budget.SharedData.prototype.getStartDate = function() {
	var startDate = new Date(this.month.getFullYear(), this.month.getMonth(), 1);
	return startDate;
};

com.digitald4.budget.SharedData.prototype.getEndDate = function() {
	var endDate = new Date(this.month);
	endDate.setDate(31);
	while (endDate.getMonth() != this.month.getMonth()) {
		endDate.setTime(endDate.getTime() - ONE_DAY);
	}
	return endDate;
};

com.digitald4.budget.SharedData.prototype.getStartDateCal = function() {
	var startDate = this.getStartDate();
	startDate = new Date(startDate.getTime() - startDate.getDay() * ONE_DAY);
	return startDate;
};

com.digitald4.budget.SharedData.prototype.getEndDateCal = function() {
	var endDate = this.getEndDate();
	endDate.setTime(endDate.getTime() + (6 - endDate.getDay()) * ONE_DAY);
	return endDate;
};
