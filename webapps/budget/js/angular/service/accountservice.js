com.digitald4.budget.AccountService = function(restService, connector) {
	this.restService = restService;
	this.connector = connector;
};

com.digitald4.budget.AccountService.prototype.restService;
com.digitald4.budget.AccountService.prototype.connector;

com.digitald4.budget.AccountService.prototype.getAccounts = function(portfolioId, successCallback,
		errorCallback) {
	this.restService.performRequest({action: 'getAccounts', portfolioId: portfolioId},
			successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.addAccount = function(newAccount, portfolioId,
		successCallback, errorCallback) {
	newAccount.action = 'addAccount';
	newAccount.portfolioId = portfolioId;
	this.restService.performRequest(newAccount, successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.updateAccount = function(account, property,
		successCallback, errorCallback) {
	var request = {action: 'updateAccount',
			id: account.id,
			property: property,
			value: account[property]};
	this.restService.performRequest(request, function(account_) {
		account.sortValue = account_.sortValue;
		successCallback(account);
	}, errorCallback);
};

com.digitald4.budget.AccountService.prototype.getBankAccounts = function(portfolioId,
		successCallback, errorCallback) {
	this.restService.performRequest({action: 'getPaymentAccounts', portfolioId: portfolioId},
			successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.getSummaryData = function(portfolioId, year,
		successCallback, errorCallback) {
	this.restService.performRequest({action: 'getSummaryData', portfolioId: portfolioId, year: year},
			successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.getTemplates = function(portfolioId,
		successCallback, errorCallback) {
	this.connector.performRequest('templates', {portfolio_id: portfolioId},
			successCallback, errorCallback);
};

com.digitald4.budget.AccountService.prototype.addTemplate = function(newTemplate, portfolioId,
		successCallback, errorCallback) {
	newTemplate.portfolio_id = portfolioId;
	this.connector.performRequest('create_template', newTemplate, successCallback, errorCallback);
};

// 2504 W Cypress St Compton, CA 90220
// Stevens, C41-340 Cell 5388