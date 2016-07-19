com.digitald4.budget.TemplatesCtrl = function(sharedData, billService, accountService) {
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.billService = billService;
	this.accountService = accountService;
	this.newTemplate = {};
	this.refresh();
};

com.digitald4.budget.TemplatesCtrl.prototype.accountService;
com.digitald4.budget.TemplatesCtrl.prototype.billService;

com.digitald4.budget.TemplatesCtrl.prototype.makeNew = function() {
	var newBill = {'accounts': []};
	for (var x = 0; x < this.paymentAccounts.length; x++) {
		var ba = this.paymentAccounts[x];
		newBill.accounts.push({'id': ba.id});
	}
	this.newTemplateBill = newBill;
};

com.digitald4.budget.TemplatesCtrl.prototype.refresh = function() {
	this.accountService.getAccounts(this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDate().getTime(),
			function(accounts) {
				this.accounts = accounts;
				this.paymentAccounts = [];
				for (var a = 0; a < accounts.length; a++) {
					var account = accounts[a];
					account.balance = {balance: 0, balance_year_to_date: 0};
					if (account.payment_account) {
						this.paymentAccounts.push(account);
					}
				}
				this.makeNew();
			}.bind(this), notify);
	
	this.accountService.getTemplates(this.sharedData.getSelectedPortfolioId(), function(templates) {
		this.templates = templates;
	}.bind(this), notify);
};

com.digitald4.budget.TemplatesCtrl.prototype.selectionChanged = function() {
	this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.selectedTemplate.bill);
};

com.digitald4.budget.TemplatesCtrl.prototype.addBill = function() {
	this.billAddError = undefined;
	this.newTemplateBill.template_id = this.selectedTemplate.id;
	this.billService.addTemplateBill(this.newTemplateBill, function(bill) {
		this.makeNew();
		this.bills.push(bill);
		this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills);
	}.bind(this), function(error) {
		this.billAddError = error;
	}.bind(this));
};

com.digitald4.budget.TemplatesCtrl.prototype.addTemplate = function() {
	this.billAddError = undefined;
	this.accountService.addTemplate(this.newTemplate, this.sharedData.getSelectedPortfolioId(),
			function(templates) {
		this.templates = templates;
		this.newTemplate = {};
	}.bind(this), function(error) {
		this.billAddError = error;
	}.bind(this));
};

com.digitald4.budget.TemplatesCtrl.prototype.updateBill = function(templateBill, property) {
	this.billUpdateError = undefined;
	this.billService.updateTemplateBill(templateBill, property, this.selectedTemplate.id,
			function(bill) {
				this.bills.remove(templateBill);
				this.bills.push(bill);
				this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills);
			}, function(error) {
				this.billUpdateError = error;
			}.bind(this));
};