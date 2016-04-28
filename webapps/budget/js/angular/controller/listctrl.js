com.digitald4.budget.ListCtrl = function($scope, SharedData, BillService, AccountService) {
	this.scope = $scope;
	this.scope.apply = function() {
		$scope.$apply();
		setupDatepicker();
	};
	this.sharedData = SharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.billService = BillService;
	this.accountService = AccountService;
	this.scope.addBill = this.addBill.bind(this);
	this.scope.updateBill = this.updateBill.bind(this);
	this.scope.updateBillTrans = this.updateBillTrans.bind(this);
	this.scope.applyTemplate = this.applyTemplate.bind(this);
	this.refresh();
};

com.digitald4.budget.ListCtrl.prototype.scope;

com.digitald4.budget.ListCtrl.prototype.billService;
com.digitald4.budget.ListCtrl.prototype.accountService;

com.digitald4.budget.ListCtrl.prototype.makeNew = function() {
	var baseDate = this.sharedData.getStartDate()
	var month = baseDate.getMonth() + 1;
	if (month < 10) {
		month = '0' + month;
	}
	var newBill = {'accounts': [], 'dueDate': month + '/15/' + baseDate.getFullYear()};
	for (var x = 0; x < this.scope.bankAccounts.length; x++) {
		var ba = this.scope.bankAccounts[x];
		newBill.accounts.push({'id': ba.id});
	}
	this.scope.newBill = newBill;
};

com.digitald4.budget.ListCtrl.prototype.refresh = function() {
	var scope = this.scope;
	var s = this;
	this.accountService.getBankAccounts(this.sharedData.getSelectedPortfolioId(), function(bankAccounts) {
		scope.bankAccounts = bankAccounts;
		s.makeNew();
		scope.apply();
	}, function(error) {
		notify(error);
	});
	
	this.accountService.getAccounts(this.sharedData.getSelectedPortfolioId(), function(accounts) {
		scope.accounts = accounts;
		scope.apply();
	}, function(error) {
		notify(error);
	});
	
	this.billService.getBills(this.sharedData.getSelectedPortfolioId(), this.sharedData.getStartDate().toJSON(),
			this.sharedData.getEndDate().toJSON(), function(bills) {
		scope.bills = bills;
		scope.apply();
	}, function(error) {
		notify(error);
	});
	
	this.accountService.getTemplates(this.sharedData.getSelectedPortfolioId(), function(templates) {
		scope.templates = templates;
		scope.$apply();
	}, notify);
};

com.digitald4.budget.ListCtrl.prototype.addBill = function() {
	var scope = this.scope;
	var s = this;
	scope.billAddError = undefined;
	this.billService.addBill(scope.newBill, this.sharedData.getSelectedPortfolioId(),
			com.digitald4.budget.DisplayWindow.MONTH, function(bills) {
		scope.bills = bills;
		s.makeNew();
		scope.apply();
	}, function(error) {
		scope.billAddError = error;
		scope.apply();
	});
};

com.digitald4.budget.ListCtrl.prototype.updateBill = function(bill, property) {
	var scope = this.scope;
	scope.billUpdateError = undefined;
	this.billService.updateBill(bill, property, this.sharedData.getSelectedPortfolioId(),
			com.digitald4.budget.DisplayWindow.MONTH, function(bills) {
		scope.bills = bills;
		scope.apply();
	}, function(error) {
		scope.billUpdateError = error;
		scope.apply();
	});
};

com.digitald4.budget.ListCtrl.prototype.updateBillTrans = function(billTrans, property) {
	var scope = this.scope;
	scope.billUpdateError = undefined;
	this.billService.updateBillTrans(billTrans, property, this.sharedData.getSelectedPortfolioId(),
			com.digitald4.budget.DisplayWindow.MONTH, function(bills) {
		scope.bills = bills;
		scope.apply();
	}, function(error) {
		scope.billUpdateError = error;
		scope.apply();
	});
};

com.digitald4.budget.ListCtrl.prototype.applyTemplate = function() {
	var scope = this.scope;
	scope.applyTemplateError = undefined;
	this.billService.applyTemplate(scope.selectedTemplate, this.sharedData.getMonth().toJSON(),
			com.digitald4.budget.DisplayWindow.MONTH, function(bills) {
		scope.bills = bills;
		scope.apply();
	}, notify);
};