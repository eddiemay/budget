var ONE_HOUR = 1000 * 60 * 60;
var ONE_DAY = ONE_HOUR * 24;

com.digitald4.budget.CalCtrl = function($filter, sharedData, billService, accountService) {
	this.dateFilter = $filter('date');
	sharedData.refresh = this.refresh.bind(this);
	sharedData.setControlType(sharedData.CONTROL_TYPE.MONTH);
	this.sharedData = sharedData;
	this.billService = billService;
	this.accountService = accountService;
	this.refresh();
};

com.digitald4.budget.CalCtrl.prototype.setupCalendar = function() {
	var month = this.sharedData.getStartDate().getMonth();
	var currDay = new Date(this.sharedData.getStartDateCal());
	var weeks = [];
	var woy = 1;
	var days = {};
	do {
		var week = {
			weekOfYear: woy++
		};
		var weekdays = [];
		for (var d = 0; d < 7; d++) {
			var day = {
				date: currDay,
				weekend: (d == 0 || d == 6),
				otherMonth: currDay.getMonth() != month,
				bills: []
			};
			weekdays.push(day);
			days[this.dateFilter(day.date.getTime(), 'MMdd')] = day;
			currDay = addDay(currDay);
		}
		week.days = weekdays;
		weeks.push(week);
	} while (currDay.getMonth() == month);
	this.days = days;
	this.weeks = weeks;
};

addDay = function(date) {
	var ret = new Date(date.getTime() + ONE_DAY);
	if (ret.getDay() == date.getDay() || ret.getHours() < date.getHours()) {
		ret.setTime(ret.getTime() + ONE_HOUR);
	} else if (ret.getHours() > date.getHours()) {
		ret.setTime(ret.getTime() - ONE_HOUR);
	}
	return ret;
};

com.digitald4.budget.CalCtrl.prototype.refresh = function() {
	this.setupCalendar();

	if (this.selectedPortfolioId != this.sharedData.getSelectedPortfolioId()) {
	  this.selectedPortfolioId = this.sharedData.getSelectedPortfolioId();
    this.accountService.list(this.sharedData.getSelectedPortfolioId(), function(response) {
      var accounts = response.items;
      var accountHash = {};
      for (var a = 0; a < accounts.length; a++) {
        var account = accounts[a];
        accountHash[account.id] = account;
      }
      this.accounts = accountHash;
    }.bind(this), notify);
  }

	this.billService.list(this.sharedData.getSelectedPortfolioId(), this.sharedData.getYear(),
	    this.sharedData.getMonth(), this.billsSuccessCallback.bind(this), notify);
};

com.digitald4.budget.CalCtrl.prototype.billsSuccessCallback = function(response) {
	for (var d in this.days) {
		this.days[d].bills = [];
	}
	this.bills = response.items;
	for (var t = 0; t < this.bills.length; t++) {
		var bill = this.bills[t];
		bill.dueDate = new Date(bill.year, bill.month - 1, bill.day);
		var account = this.accounts['' + bill.account_id];
    if (account) {
      bill.name = bill.name || account.name;
    }
		var day = this.days[this.dateFilter(bill.dueDate, 'MMdd')];
		if (day) {
			day.bills.push(bill);
		}
	}
	this.closeAddBillDialog();
};

com.digitald4.budget.CalCtrl.prototype.showAddBillDialog = function(date) {
	this.newBill = {dueDate: this.dateFilter(date, 'MM/dd/yyyy')};
	this.addDialogShown = true;
};

com.digitald4.budget.CalCtrl.prototype.closeAddBillDialog = function() {
	this.addDialogShown = false;
};

com.digitald4.budget.CalCtrl.prototype.addBill = function() {
  var date = new Date(this.newBill.dueDate);
  this.newBill.year = date.getFullYear();
  this.newBill.month = date.getMonth() + 1;
  this.newBill.day = date.getDate();
  this.newBill.dueDate = undefined;
	this.billService.create(this.newBill, function(bill) {
	  this.bills.push(bill);
	  this.billsSuccessCallback(this.bills);
	}.bind(this), notify);
};

com.digitald4.budget.CalCtrl.prototype.editBill = function(bill) {
	this.eBill = bill;
	this.editDialogShown = true;
};

com.digitald4.budget.CalCtrl.prototype.closeEditBillDialog = function() {
	this.editDialogShown = false;
};

com.digitald4.budget.CalCtrl.prototype.updateBill = function(property) {
	var index = this.bills.indexOf(this.eBill);
  var props = [property];
  if (property == 'dueDate') {
    var date = new Date(this.eBill.dueDate);
    this.eBill.year = date.getFullYear();
    this.eBill.month = date.getMonth() + 1;
    this.eBill.day = date.getDate();
    props = ['year', 'month', 'day'];
  }
	this.billService.update(this.eBill, props, function(bill) {
		this.bills.splice(index, 1, bill);
	  this.billsSuccessCallback(this.bills);
	}.bind(this), notify);
};