var ONE_DAY = 1000 * 60 * 60 * 24;
var ONE_HOUR = 1000 * 60 * 60;
com.digitald4.budget.CalCtrl = function($scope, $filter, SharedData, BillService, AccountService) {
	this.scope = $scope;
	this.dateFilter = $filter('date');
	this.sharedData = SharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.billService = BillService;
	this.accountService = AccountService;
	this.refresh();
};

com.digitald4.budget.CalCtrl.prototype.scope;

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
			days[day.date.getTime()] = day;
			currDay = addDay(currDay);
		}
		week.days = weekdays;
		weeks.push(week);
	} while (currDay.getMonth() == month.getMonth());
	this.days = days;
	this.weeks = weeks;
	console.log('Weeks: ' + this.weeks.length);
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
	var scope = this.scope;
	var s = this;
	
	this.accountService.getAccounts(this.sharedData.activePortfolioId, function(accounts) {
		s.accounts = accounts;
		scope.$apply();
	}, function(error) {
		notify(error);
	});
	
	var billsSuccessCallback = this.billsSuccessCallback.bind(this);
	
	this.billService.getBills(this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDateCal().toJSON(), this.sharedData.getEndDateCal().toJSON(),
			billsSuccessCallback, function(error) {
		notify(error);
	});
};

com.digitald4.budget.CalCtrl.prototype.billsSuccessCallback = function(bills) {
	var scope = this.scope;
	for (var d in this.days) {
		this.days[d].bills = [];
	}
	this.bills = bills;
	for (var t = 0; t < bills.length; t++) {
		var bill = bills[t];
		var day = this.days[Date.parse(bill.dueDate)];
		if (day) {
			day.bills.push(bill);
		}
	}
	this.closeAddBillDialog();
	scope.$apply();
};

com.digitald4.budget.CalCtrl.prototype.showAddBillDialog = function(date) {
	this.newBill = {dueDate: this.dateFilter(date, 'MM/dd/yyyy')};
	this.addDialogShown = true;
};

com.digitald4.budget.CalCtrl.prototype.closeAddBillDialog = function() {
	this.addDialogShown = false;
};

com.digitald4.budget.CalCtrl.prototype.addBill = function() {
	var scope = this.scope;
	var tis = this;
	this.billAddError = undefined;
	this.billService.addBill(this.newBill, this.sharedData.getSelectedPortfolioId(),
			com.digitald4.budget.DisplayWindow.CAL_MONTH, this.billsSuccessCallback, function(error) {
		tis.billAddError = error;
		scope.$apply();
	});
};

com.digitald4.budget.CalCtrl.prototype.editBill = function(bill) {
	this.eBill = bill;
	this.editDialogShown = true;
};

com.digitald4.budget.CalCtrl.prototype.closeEditBillDialog = function() {
	this.editDialogShown = false;
};

com.digitald4.budget.CalCtrl.prototype.updateBill = function(property) {
	var scope = this.scope;
	var tis = this;
	this.billUpdateError = undefined;
	this.billService.updateBill(this.eBill, property, this.sharedData.getSelectedPortfolioId(),
			com.digitald4.budget.DisplayWindow.CAL_MONTH, this.billsSuccessCallback, function(error) {
		tis.billUpdateError = error;
		scope.$apply();
	});
};

com.digitald4.budget.CalCtrl.prototype.updateBillTrans = function(billTrans, property) {
	var scope = this.scope;
	var tis = this;
	this.billUpdateError = undefined;
	this.billService.updateBillTrans(billTrans, property, this.sharedData.getSelectedPortfolioId(),
			com.digitald4.budget.DisplayWindow.CAL_MONTH, this.billsSuccessCallback, function(error) {
		tis.billUpdateError = error;
		scope.$apply();
	});
};