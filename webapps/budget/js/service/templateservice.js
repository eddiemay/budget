com.digitald4.budget.TemplateService = function(restService) {
  this.protoService = new com.digitald4.common.ProtoService("template", restService);
};

com.digitald4.budget.TemplateService.prototype.protoService;

com.digitald4.budget.TemplateService.prototype.list = function(portfolioId,
		successCallback, errorCallback) {
	this.protoService.performRequest('list', {portfolio_id: portfolioId},
			successCallback, errorCallback);
};

com.digitald4.budget.TemplateService.prototype.create = function(newTemplate,
		successCallback, errorCallback) {
	this.restService.create(newTemplate, successCallback, errorCallback);
};
