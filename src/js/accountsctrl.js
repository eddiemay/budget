com.digitald4.budget.AccountsCtrl = function(sharedData, accountService) {
	sharedData.refresh = this.refresh.bind(this);
	sharedData.setControlType(sharedData.CONTROL_TYPE.NONE);
	this.sharedData = sharedData;
	this.accountService = accountService;
	this.refresh();
};

com.digitald4.budget.AccountsCtrl.prototype.refresh = function() {
	this.accountService.list(this.sharedData.getSelectedPortfolioId(), function(response) {
	  this.accounts = response.items;
	  this.listAccounts = [{name: '', id: 0}];
	  for (var a = 0; a < this.accounts.length; a++) {
	    this.listAccounts.push(this.accounts[a]);
	  }
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
		this.listAccounts.push(account);
		this.newAccount = {portfolio_id: this.sharedData.getSelectedPortfolioId()};
	}.bind(this), notify);
};

com.digitald4.budget.AccountsCtrl.prototype.updateAccount = function(account, property) {
  var index = this.accounts.indexOf(account);
	this.accountService.update(account, [property], function(account) {
		this.accounts.splice(index, 1, account);
		this.listAccounts.splice(index + 1, 1, account);
	}.bind(this), notify);
};