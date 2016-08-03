com.digitald4.budget.SummaryCtrl = function(sharedData, accountService) {
	this.sharedData = sharedData;
	this.sharedData.refresh = this.refresh.bind(this);
	this.accountService = accountService;
	this.refresh();
};

com.digitald4.budget.SummaryCtrl.prototype.sharedData;
com.digitald4.budget.SummaryCtrl.prototype.accountService;
com.digitald4.budget.SummaryCtrl.prototype.topLevelAccounts;

com.digitald4.budget.SummaryCtrl.prototype.refresh = function() {
	this.accountService.getSummaryData(this.sharedData.getSelectedPortfolioId(),
			this.sharedData.getStartDate().getFullYear(), function(accounts) {
		this.topLevelAccounts = [];
		for (var a = 0; a < accounts.length; a++) {
			if (!accounts[a].parent_account_id || accounts[a].parent_account_id == 0) {
				accounts[a].accounts = [];
				this.topLevelAccounts.push(accounts[a]);
			}
		}
		for (var a = 0; a < accounts.length; a++) {
			if (accounts[a].parent_account_id || accounts[a].parent_account_id != 0) {
				var account = accounts[a];
				for (var p = 0; p < this.topLevelAccounts.length; p++) {
					if (this.topLevelAccounts[p].id == account.parent_account_id) {
						var topLevel = this.topLevelAccounts[p];
						topLevel.accounts.push(accounts[a]);
						for (var m = 0; m < 13; m++) {
							topLevel.summary[m].total = topLevel.summary[m].total || 0;
							topLevel.summary[m].total += account.summary[m].total || 0;
						}
						break;
					}
				}
			}
		}
	}.bind(this), notify);
};