com.digitald4.budget.TemplatesCtrl = function($scope, SharedData, BillService, AccountService) {
	this.scope = $scope;
	this.sharedData = SharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.billService = BillService;
	this.accountService = AccountService;
	this.newTemplate = {};
	this.refresh();
};

com.digitald4.budget.TemplatesCtrl.prototype.scope;
com.digitald4.budget.TemplatesCtrl.prototype.accountService;
com.digitald4.budget.TemplatesCtrl.prototype.billService;

com.digitald4.budget.TemplatesCtrl.prototype.makeNew = function() {
	var newBill = {'accounts': []};
	for (var x = 0; x < this.bankAccounts.length; x++) {
		var ba = this.bankAccounts[x];
		newBill.accounts.push({'id': ba.id});
	}
	this.newTemplateBill = newBill;
};

com.digitald4.budget.TemplatesCtrl.prototype.refresh = function() {
	var scope = this.scope;
	var s = this;
	
	this.accountService.getTemplates(this.sharedData.getSelectedPortfolioId(), function(templates) {
		s.templates = templates;
		scope.$apply();
	}, notify);
	
	this.accountService.getBankAccounts(this.sharedData.getSelectedPortfolioId(),
			function(bankAccounts) {
		s.bankAccounts = bankAccounts;
		s.makeNew();
		scope.$apply();
	}, notify);
	
	this.accountService.getAccounts(this.sharedData.getSelectedPortfolioId(), function(accounts) {
		s.accounts = accounts;
		scope.$apply();
	}, notify);
};

com.digitald4.budget.TemplatesCtrl.prototype.setBills = function(bills) {
	this.bills = bills;
	this.scope.$apply();
};

com.digitald4.budget.TemplatesCtrl.prototype.refreshBills = function() {
	this.billService.getTemplateBills(this.selectedTemplate.id, this.setBills.bind(this), notify);
};

com.digitald4.budget.TemplatesCtrl.prototype.addBill = function() {
	var scope = this.scope;
	var s = this;
	this.billAddError = undefined;
	this.billService.addTemplateBill(this.newTemplateBill, this.selectedTemplate.id, function(bills) {
		s.makeNew();
		s.setBills(bills);
	}, function(error) {
		s.billAddError = error;
		scope.$apply();
	});
};

com.digitald4.budget.TemplatesCtrl.prototype.addTemplate = function() {
	var scope = this.scope;
	var s = this;
	this.billAddError = undefined;
	this.accountService.addTemplate(this.newTemplate, this.sharedData.getSelectedPortfolioId(),
			function(templates) {
		s.templates = templates;
		s.newTemplate = {};
		scope.$apply();
	}, function(error) {
		s.billAddError = error;
		scope.$apply();
	});
};

com.digitald4.budget.TemplatesCtrl.prototype.updateBill = function(templateBill, property) {
	var scope = this.scope;
	var s = this;
	this.billUpdateError = undefined;
	this.billService.updateTemplateBill(templateBill, property, this.selectedTemplate.id,
			this.setBills.bind(this), function(error) {
		s.billUpdateError = error;
		scope.$apply();
	});
};

com.digitald4.budget.TemplatesCtrl.prototype.updateBillTrans = function(billTrans, property) {
	var scope = this.scope;
	var s = this;
	this.billUpdateError = undefined;
	this.billService.updateTemplateBillTrans(billTrans, property, this.selectedTemplate.id,
			this.setBills.bind(this), function(error) {
		s.billUpdateError = error;
		scope.$apply();
	});
};