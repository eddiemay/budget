var ONE_HOUR = 1000 * 60 * 60;
var ONE_DAY = ONE_HOUR * 24;

com.digitald4.budget.CalCtrl = function($filter, sharedData, billService, accountService) {
	this.dateFilter = $filter('date');
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.billService = billService;
	this.accountService = accountService;
	this.accountService.getAccounts(this.sharedData.activePortfolioId,
			this.sharedData.getStartDate().getTime(), function(accounts) {
		var accountHash = {};
		for (var a = 0; a < accounts.length; a++) {
		  var account = accounts[a];
		  accountHash[account.id] = account;
		}
		this.accounts = accountHash;
	}.bind(this), function(error) {
		notify(error);
	});
	this.refresh();
};

com.digitald4.budget.CalCtrl.prototype.billService;
com.digitald4.budget.CalCtrl.prototype.accountService;

com.digitald4.budget.CalCtrl.prototype.setupCalendar = function() {
	var month = this.sharedData.getMonth();
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
				otherMonth: currDay.getMonth() != month.getMonth(),
				bills: []
			};
			weekdays.push(day);
			days[this.dateFilter(day.date.getTime(), 'MMdd')] = day;
			currDay = addDay(currDay);
		}
		week.days = weekdays;
		weeks.push(week);
	} while (currDay.getMonth() == month.getMonth());
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

	this.billService.getBills(this.sharedData.getSelectedPortfolioId(), this.sharedData.getStartDate().getTime(),
	    'CAL_MONTH', this.billsSuccessCallback.bind(this), notify);
};

com.digitald4.budget.CalCtrl.prototype.billsSuccessCallback = function(bills) {
	for (var d in this.days) {
		this.days[d].bills = [];
	}
	this.bills = bills;
	for (var t = 0; t < bills.length; t++) {
		var bill = bills[t];
		var account = this.accounts['' + bill.account_id];
    if (account) {
      bill.name = bill.name || account.name;
    }
		var day = this.days[this.dateFilter(bill.due_date, 'MMdd')];
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
	this.billUpdateError = undefined;
	var index = this.bills.indexOf(this.eBill);
	this.billService.update(this.eBill, [property], function(bill) {
		this.bills.splice(index, 1, bill);
	  this.billsSuccessCallback(this.bills);
	}.bind(this), notify);
};