com.digitald4.budget.ListCtrl = function($scope, sharedData, billService, accountService) {
	this.scope = {
		apply: function() {
			$scope.$apply();
			setupDatepicker();
		}
	};
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.billService = billService;
	this.accountService = accountService;
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
	for (var x = 0; x < this.bankAccounts.length; x++) {
		var ba = this.bankAccounts[x];
		newBill.accounts.push({'id': ba.id});
	}
	this.newBill = newBill;
};

com.digitald4.budget.ListCtrl.prototype.refresh = function() {
	this.accountService.getBankAccounts(this.sharedData.getSelectedPortfolioId(),
			function(bankAccounts) {
				this.bankAccounts = bankAccounts;
				this.makeNew();
				// this.scope.apply();
			}.bind(this), function(error) {
				notify(error);
	});
	
	this.accountService.getAccounts(this.sharedData.getSelectedPortfolioId(), function(accounts) {
		this.accounts = accounts;
		//this.scope.apply();
	}.bind(this), function(error) {
		notify(error);
	});
	
	this.billService.getBills(this.sharedData.getSelectedPortfolioId(), this.sharedData.getStartDate().toJSON(),
			this.sharedData.getEndDate().toJSON(), function(bills) {
				this.bills = bills;
				this.scope.apply();
			}.bind(this), function(error) {
				notify(error);
	});
	
	this.accountService.getTemplates(this.sharedData.getSelectedPortfolioId(), function(templates) {
		this.templates = templates;
		// this.scope.apply();
	}.bind(this), notify);
};

com.digitald4.budget.ListCtrl.prototype.addBill = function() {
	this.billAddError = undefined;
	this.billService.addBill(this.newBill, this.sharedData.getSelectedPortfolioId(),
			com.digitald4.budget.DisplayWindow.MONTH, function(bills) {
		this.bills = bills;
		this.makeNew();
		this.scope.apply();
	}.bind(this), function(error) {
		this.billAddError = error;
		this.scope.apply();
	}.bind(this));
};

com.digitald4.budget.ListCtrl.prototype.updateBill = function(bill, property) {
	this.billUpdateError = undefined;
	this.billService.updateBill(bill, property, this.sharedData.getSelectedPortfolioId(),
			com.digitald4.budget.DisplayWindow.MONTH, function(bills) {
		this.bills = bills;
		this.scope.apply();
	}.bind(this), function(error) {
		this.billUpdateError = error;
	}.bind(this));
};

com.digitald4.budget.ListCtrl.prototype.updateBillTrans = function(billTrans, property) {
	this.billUpdateError = undefined;
	this.billService.updateBillTrans(billTrans, property, this.sharedData.getSelectedPortfolioId(),
			com.digitald4.budget.DisplayWindow.MONTH, function(bills) {
		this.bills = bills;
		this.scope.apply();
	}.bind(this), function(error) {
		this.billUpdateError = error;
		this.scope.apply();
	});
};

com.digitald4.budget.ListCtrl.prototype.applyTemplate = function() {
	this.applyTemplateError = undefined;
	this.billService.applyTemplate(this.selectedTemplate, this.sharedData.getMonth().toJSON(),
			com.digitald4.budget.DisplayWindow.MONTH, function(bills) {
		this.bills = bills;
		this.scope.apply();
	}.bind(this), notify);
};