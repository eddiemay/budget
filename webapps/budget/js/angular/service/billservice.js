com.digitald4.budget.DisplayWindow = {
  DAY: 1,
  WEEK: 2,
  MONTH: 3,
  CAL_MONTH: 4,
  YEAR: 5
};

com.digitald4.budget.BillService = function(RestService) {
	this.restService = RestService;
};

com.digitald4.budget.BillService.prototype.restService;

com.digitald4.budget.BillService.prototype.getBills = function(portfolioId, startDate, endDate,
		successCallback, errorCallback) {
	this.restService.performRequest({action: 'getBills', portfolioId: portfolioId, startDate: startDate,
		endDate: endDate}, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.getTransactions = function(portfolioId, startDate,
		endDate, successCallback, errorCallback) {
	this.restService.performRequest({action: 'getTransactions', portfolioId: portfolioId, startDate: startDate,
		endDate: endDate}, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.addTransaction = function(newTrans, portfolioId, startDate,
		endDate, successCallback, errorCallback) {
	newTrans.action = 'addTransaction';
	newTrans.portfolioId = portfolioId;
	newTrans.startDate = startDate;
	newTrans.endDate = endDate;
	this.restService.performRequest(newTrans, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.updateTransaction = function(trans, property,
		portfolioId, startDate, endDate, successCallback, errorCallback) {
	var request = {action: 'updateTransaction',
			id: trans.id,
			property: property,
			value: trans[property],
			portfolioId: portfolioId,
			startDate: startDate,
			endDate: endDate};
	this.restService.performRequest(request, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.addBill = function(newBill, portfolioId, disWin,
		successCallback, errorCallback) {
	newBill.action = 'addBill';
	newBill.portfolioId = portfolioId;
	newBill.displayWindow = disWin;
	this.restService.performRequest(newBill, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.updateBill = function(bill, property, portfolioId,
		disWin, successCallback, errorCallback) {
	var request = {action: 'updateBill',
			id: bill.id,
			transId: bill.transId,
			property: property,
			value: bill[property],
			portfolioId: portfolioId,
			displayWindow: disWin};
	this.restService.performRequest(request, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.updateBillTrans = function(billTrans, property,
		portfolioId, disWin, successCallback, errorCallback) {
	var request = {action: 'updateBillTrans',
			id: billTrans.id,
			billId: billTrans.billId,
			accountId: billTrans.accountId,
			property: property,
			value: billTrans[property],
			portfolioId: portfolioId,
			displayWindow: disWin};
	this.restService.performRequest(request, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.getTemplateBills = function(templateId, successCallback,
		errorCallback) {
	this.restService.performRequest({action: 'getTemplateBills', templateId: templateId},
			successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.addTemplateBill = function(newTemplateBill, templateId,
		successCallback, errorCallback) {
	newTemplateBill.action = 'addTemplateBill';
	newTemplateBill.templateId = templateId;
	this.restService.performRequest(newTemplateBill, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.updateTemplateBill = function(templateBill, property,
		templateId, successCallback, errorCallback) {
	var request = {action: 'updateTemplateBill',
			id: templateBill.id,
			transId: templateBill.transId,
			property: property,
			value: templateBill[property],
			templateId: templateId};
	this.restService.performRequest(request, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.updateTemplateBillTrans = function(billTrans, property,
		templateId, successCallback, errorCallback) {
	var request = {action: 'updateTemplateBillTrans',
			id: billTrans.id,
			templateBillId: billTrans.templateBillId,
			accountId: billTrans.accountId,
			property: property,
			value: billTrans[property],
			templateId: templateId};
	this.restService.performRequest(request, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.applyTemplate = function(template, refDate, disWin,
		successCallback, errorCallback) {
	var request = {action: 'applyTemplate',
			portfolioId: template.portfolioId,
			templateId: template.id,
			refDate: refDate,
			displayWindow: disWin}
	this.restService.performRequest(request, successCallback, errorCallback);
};

// 2504 W Cypress St Compton, CA 90220