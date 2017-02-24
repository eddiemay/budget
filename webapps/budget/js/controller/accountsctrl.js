com.digitald4.budget.AccountsCtrl = function(sharedData, accountService) {
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.accountService = accountService;
	this.refresh();
};

com.digitald4.budget.AccountsCtrl.prototype.refresh = function() {
	this.accountService.getAccounts(this.sharedData.getSelectedPortfolioId(), this.sharedData.getStartDate().getTime(),
	    function(accounts) {
		    this.accounts = accounts;
	    }.bind(this), notify);
	this.newAccount = {portfolio_id: this.sharedData.getSelectedPortfolioId()};
};

com.digitald4.budget.AccountsCtrl.prototype.showAddDialog = function() {
	$.modal({
		content:  '<p>Add Transaction</p>' +
				  '<input type="text" ng-model="name">',
		title: 'Add Transaction',
		maxWidth: 500,
		buttons: {
			'Ok': function(win) { addTranscaction(); win.closeModal(); },
			'Cancel': function(win) { win.closeModal(); }
		}
	});
};

com.digitald4.budget.AccountsCtrl.prototype.addAccount = function() {
	this.accountService.create(this.newAccount, function(account) {
		this.accounts.push(account);
		this.newAccount = {portfolio_id: this.sharedData.getSelectedPortfolioId()};
	}.bind(this), notify);
};

com.digitald4.budget.AccountsCtrl.prototype.updateAccount = function(account, property) {
  var index = this.accounts.indexOf(account);
	this.accountService.update(account, [property], function(account) {
		this.accounts.splice(index, 1, account);
	}.bind(this), notify);
};