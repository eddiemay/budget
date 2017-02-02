com.digitald4.budget.AccountService = function(restService) {
  this.protoService = new com.digitald4.common.ProtoService("account", restService);
};

com.digitald4.budget.AccountService.prototype.protoService;

com.digitald4.budget.AccountService.prototype.getAccounts = function(portfolioId, refDate,
		successCallback, errorCallback) {
	this.protoService.performRequest('list', {portfolio_id: portfolioId, ref_date: refDate},
			successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.addAccount = function(newAccount,
		successCallback, errorCallback) {
	this.protoService.create(newAccount, successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.updateAccount = function(account, property,
		successCallback, errorCallback) {
	this.protoService.update(account, [property], function(account_) {
		account.sortValue = account_.sortValue;
		successCallback(account);
	}, errorCallback);
};

com.digitald4.budget.AccountService.prototype.getSummaryData = function(portfolioId, year,
		successCallback, errorCallback) {
	this.protoService.performRequest('getSummary', {portfolio_id: portfolioId, year: year},
			successCallback, errorCallback);
};
