com.digitald4.budget.TemplatesCtrl = function(sharedData, templateService, accountService, templateBillService) {
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.templateService = templateService;
	this.accountService = accountService;
	this.templateBillService = templateBillService;
	this.newTemplate = {};
	this.refresh();
};

com.digitald4.budget.TemplatesCtrl.prototype.refresh = function() {
  this.accountService.list(this.sharedData.getSelectedPortfolioId(), function(response) {
    this.accounts = response.result;
    this.paymentAccounts = [];
    for (var a = 0; a < this.accounts.length; a++) {
      this.accounts[a].balance = {balance: 0, balanceYearToDate: 0};
      if (this.accounts[a].paymentAccount) {
        this.paymentAccounts.push(this.accounts[a]);
      }
    }
  }.bind(this), notify);
	
	this.templateService.list(this.sharedData.getSelectedPortfolioId(), function(response) {
		this.templates = response.result;
	}.bind(this), notify);
};

com.digitald4.budget.TemplatesCtrl.prototype.selectionChanged = function() {
  this.templateBillService.list(this.selectedTemplate.id, function(response) {
    var sorted = com.digitald4.budget.ListCtrl.sortBills(response.result);
    this.bills = com.digitald4.budget.ListCtrl.calcBalances(this.accounts, sorted, []);
  }.bind(this), notify);
};

com.digitald4.budget.TemplatesCtrl.prototype.addTemplate = function() {
	this.newTemplate.portfolioId = this.sharedData.getSelectedPortfolioId();
	this.templateService.create(this.newTemplate, function(template) {
		this.templates.push(template);
		this.newTemplate = {};
	}.bind(this), notify);
};

com.digitald4.budget.TemplatesCtrl.prototype.addBill = function() {
	this.newTemplateBill.templateId = this.selectedTemplate.id;
	this.newTemplateBill.portfolioId = this.selectedTemplate.portfolioId;
	this.templateBillService.create(this.newTemplateBill, function(bill) {
		com.digitald4.budget.ListCtrl.insertBill(this.bills, bill);
		com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, []);
		this.newTemplateBill = {};
	}.bind(this), notify);
};

com.digitald4.budget.TemplatesCtrl.prototype.updateBill = function(templateBill, property) {
	this.templateBillService.update(templateBill, [property], function(bill) {
    this.bills.splice(this.bills.indexOf(templateBill), 1);
		com.digitald4.budget.ListCtrl.insertBill(this.bills, bill);
		com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, []);
  }.bind(this), notify);
};

com.digitald4.budget.TemplatesCtrl.prototype.deleteBill = function(templateBill, property) {
	this.templateBillService.Delete(templateBill.id, function() {
    this.bills.splice(this.bills.indexOf(templateBill), 1);
    com.digitald4.budget.ListCtrl.calcBalances(this.accounts, this.bills, []);
  }.bind(this), notify);
};