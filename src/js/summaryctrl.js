com.digitald4.budget.SummaryCtrl = function(sharedData, accountService, balanceService) {
	sharedData.refresh = this.refresh.bind(this);
	sharedData.setControlType(sharedData.CONTROL_TYPE.YEAR);
	this.sharedData = sharedData;
	this.accountService = accountService;
	this.balanceService = balanceService;
	this.refresh();
};

com.digitald4.budget.SummaryCtrl.prototype.refresh = function() {
  this.balances = undefined;
  this.nextJan = undefined;
	this.accountService.list(this.sharedData.getSelectedPortfolioId(), function(accounts) {
		var topLevelAccounts = [];
		var accountHash = {};
		for (var a = 0; a < accounts.length; a++) {
			if (!accounts[a].parent_account_id || accounts[a].parent_account_id == 0) {
				accounts[a].accounts = [];
				accountHash[accounts[a].id] = accounts[a];
				topLevelAccounts.push(accounts[a]);
			}
		}
		for (var a = 0; a < accounts.length; a++) {
			if (accounts[a].parent_account_id || accounts[a].parent_account_id != 0) {
				var topLevel = accountHash[accounts[a].parent_account_id];
				if (topLevel) {
				  topLevel.accounts.push(accounts[a]);
				}
			}
		}
		this.topLevelAccounts = topLevelAccounts;
	  if (this.balances && this.nextJan) {
	    this.buildSummary();
	  }
	}.bind(this), notify);

	this.balanceService.list(this.sharedData.getSelectedPortfolioId(), this.sharedData.getYear(), undefined,
	    function(balances) {
	  this.balances = balances[0];
	  if (this.topLevelAccounts && this.nextJan) {
	    this.buildSummary();
	  }
	}.bind(this), notify);

	this.balanceService.list(this.sharedData.getSelectedPortfolioId(), this.sharedData.getYear() + 1, 1,
	    function(balances) {
	  var nextJan = {};
	  for (var b = 0; b < balances.length; b++) {
	    var balance = balances[b];
	    nextJan[balance.account_id] = balance;
	  }
	  this.nextJan = nextJan;
	  if (this.topLevelAccounts && this.balances) {
	    this.buildSummary();
	  }
	}.bind(this), notify);
};

com.digitald4.budget.SummaryCtrl.prototype.buildSummary = function() {
  for (var t = 0; t < this.topLevelAccounts.length; t++) {
    var topLevel = this.topLevelAccounts[t];
    topLevel.summary = {'1': 0, '2': 0, '3': 0, '4': 0, '5': 0, '6': 0, '7': 0, '8': 0, '9': 0, '10': 0, '11': 0, '12': 0, total: 0};
    for (var a = 0; a < topLevel.accounts.length; a++) {
      var account = topLevel.accounts[a];
      account.summary = {};
      var prevBalance = {balance: 0, balance_y_t_d: 0};
      if (this.balances['1'] && this.balances['1'][account.id]) {
        prevBalance.balance = this.balances['1'][account.id].balance;
      }
      var total = 0;
      for (var s in topLevel.summary) {
        if (s == '1' || s == 'total') {
          continue;
        }
        var i = parseInt(s, 10) - 1;
        if (this.balances[s] && this.balances[s][account.id]) {
          account.summary[i] = this.balances[s][account.id].balance_y_t_d - prevBalance.balance_y_t_d;
          prevBalance = this.balances[s][account.id];
        } else {
          account.summary[i] = 0;
        }
        total += account.summary[i];
        topLevel.summary[i] += account.summary[i];
      }
      if (this.nextJan[account.id]) {
        account.summary['12'] = this.nextJan[account.id].balance - prevBalance.balance;
        total += account.summary['12'];
        topLevel.summary['12'] += account.summary['12'];
      } else {
        account.summary['12'] = 0;
      }
      account.summary.total = total;
      topLevel.summary.total += total;
    }
  }
};