com.digitald4.budget.AccountService = function(restService, connector) {
	this.restService = restService;
	this.connector = connector;
};

com.digitald4.budget.AccountService.prototype.restService;
com.digitald4.budget.AccountService.prototype.connector;

com.digitald4.budget.AccountService.prototype.getAccounts = function(portfolioId, refDate,
		successCallback, errorCallback) {
	this.connector.performRequest('accounts', {portfolio_id: portfolioId, ref_date: refDate},
			successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.addAccount = function(newAccount,
		successCallback, errorCallback) {
	this.connector.performRequest('create_account', {account: newAccount},
			successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.updateAccount = function(account, property,
		successCallback, errorCallback) {
	var request = {id: account.id,
			property: property,
			value: account[property]};
	this.connector.performRequest('update_account', request, function(account_) {
		account.sortValue = account_.sortValue;
		successCallback(account);
	}, errorCallback);
};

com.digitald4.budget.AccountService.prototype.getTemplates = function(portfolioId,
		successCallback, errorCallback) {
	this.connector.performRequest('templates', {portfolio_id: portfolioId},
			successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.addTemplate = function(newTemplate,
		successCallback, errorCallback) {
	this.connector.performRequest('create_template', {template: newTemplate},
			successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.getSummaryData = function(portfolioId, year,
		successCallback, errorCallback) {
	this.restService.performRequest({action: 'getSummaryData', portfolioId: portfolioId, year: year},
			successCallback, errorCallback);
};

// 2504 W Cypress St Compton, CA 90220
// Stevens, C41-340 Cell 5388