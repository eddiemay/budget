com.digitald4.budget.TemplatesCtrl = function($scope, sharedData, billService, accountService) {
	this.scope = $scope;
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.billService = billService;
	this.accountService = accountService;
	this.newTemplate = {};
	this.refresh();
};

com.digitald4.budget.TemplatesCtrl.prototype.scope;
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
	this.accountService.getAccounts(this.sharedData.getSelectedPortfolioId(), function(accounts) {
		this.accounts = accounts;
		this.paymentAccounts = [];
		this.accountHash = {};
		for (var a = 0; a < accounts.length; a++) {
			var account = accounts[a];
			account.balance = 0;
			this.accountHash['' + account.id] = account;
			if (account.payment_account) {
				this.paymentAccounts.push(account);
			}
		}
		this.makeNew();
		this.scope.$apply();
	}.bind(this), notify);
	
	this.accountService.getTemplates(this.sharedData.getSelectedPortfolioId(), function(templates) {
		this.templates = templates;
		// scope.$apply();
	}.bind(this), notify);
};

com.digitald4.budget.TemplatesCtrl.prototype.setBills = function(bills) {
	for (var b = 0; b < bills.length; b++) {
		var bill = bills[b];
		bill.accounts = [];
		for (var x = 0; x < this.paymentAccounts.length; x++) {
			var paymentAccount = this.paymentAccounts[x];
			var account = {'id': paymentAccount.id};
			if (bill.account_id == paymentAccount.id) {
				account.amount = -bill.amount_due;
				paymentAccount.balance -= account.amount;
			}
			for (var t = 0; t < bill.transaction.length; t++) {
				if (bill.transaction[t].debit_account_id == paymentAccount.id
						&& bill.transaction[t].amount != 0) {
					account.amount = bill.transaction[t].amount;
					paymentAccount.balance -= account.amount;
				}
			}
			account.balance_post = paymentAccount.balance;
			bill.accounts.push(account);
		}
		var account = this.accountHash['' + bill.account_id]; 
		if (account) {
			account.balance += bill.amount_due; 
			bill.balance_post = account.balance;
		} else {
			bill.balance_post = bill.amount_due;
		}
	}
	this.selectedTemplate.bill = bills;
	// this.scope.$apply();
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