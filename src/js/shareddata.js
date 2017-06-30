com.digitald4.budget.SharedData = function() {
	var today = new Date();
	this.startDate = new Date(today.getFullYear(), today.getMonth(), 1);
	this.controlType = this.CONTROL_TYPE.NONE;
};

com.digitald4.budget.SharedData.prototype.CONTROL_TYPE = {
  NONE: 0,
  MONTH: 1,
  YEAR: 2,
};

com.digitald4.budget.SharedData.prototype.setControlType = function(controlType) {
  this.controlType = controlType;
};

com.digitald4.budget.SharedData.prototype.getControlType = function() {
  return this.controlType;
};

com.digitald4.budget.SharedData.prototype.refresh = function() {
	console.log('warning refresh not bound');
};

com.digitald4.budget.SharedData.prototype.setUser = function(user) {
  this.user = user;
};

com.digitald4.budget.SharedData.prototype.setPortfolioData = function(portfolios) {
	this.portfolios = portfolios;
	this.activePortfolioId = portfolios[0].id;
};

com.digitald4.budget.SharedData.prototype.getSelectedPortfolioId = function() {
	return this.activePortfolioId;
};

com.digitald4.budget.SharedData.prototype.getMonth = function() {
	return this.startDate.getMonth() + 1;
};

com.digitald4.budget.SharedData.prototype.setMonth = function(year, month) {
	this.startDate = new Date(year, month, 1);
	this.refresh();
};

com.digitald4.budget.SharedData.prototype.getYear = function() {
	return this.startDate.getFullYear();
};

com.digitald4.budget.SharedData.prototype.setYear = function(year) {
	this.startDate.setYear(year);
	this.refresh();
};

com.digitald4.budget.SharedData.prototype.prev = function() {
  if (this.controlType == this.CONTROL_TYPE.MONTH) {
    this.startDate.setDate(0);
    this.startDate.setDate(1);
    this.refresh();
  } else if (this.controlType == this.CONTROL_TYPE.YEAR) {
    this.setYear(this.getYear() - 1);
  }
};

com.digitald4.budget.SharedData.prototype.next = function() {
  if (this.controlType == this.CONTROL_TYPE.MONTH) {
    this.startDate.setDate(32);
    this.startDate.setDate(1);
    this.refresh();
  } else if (this.controlType == this.CONTROL_TYPE.YEAR) {
    this.setYear(this.getYear() + 1);
  }
};

com.digitald4.budget.SharedData.prototype.getStartDate = function() {
	return this.startDate;
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
