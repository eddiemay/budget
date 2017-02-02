com.digitald4.budget.BillService = function(restService) {
	this.protoService = new com.digitald4.common.ProtoService("bill", restService);
};

com.digitald4.budget.BillService.prototype.protoService;

com.digitald4.budget.BillService.prototype.getBills = function(portfolioId, refDate, dateRange,
		successCallback, errorCallback) {
	this.protoService.performRequest('list', {portfolio_id: portfolioId, ref_date: refDate,
		date_range: dateRange}, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.addBill = function(newBill, successCallback, errorCallback) {
	this.protoService.create(newBill, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.updateBill = function(bill, property,
		successCallback, errorCallback) {
	this.protoService.update(bill, [property], successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.deleteBill = function(bill,
		successCallback, errorCallback) {
	this.protoService.Delete(bill.id, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.updateBillTrans = function(bill,
		successCallback, errorCallback) {
	var request = {
			bill_id: bill.id,
			transaction: bill.transaction};
	this.protoService.performRequest('updateTrans', request, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.applyTemplate = function(template, refDate, dateRange,
		successCallback, errorCallback) {
	this.protoService.performRequest('applyTemplate',
			{template_id: template.id, ref_date: refDate, date_range: dateRange},
			successCallback, errorCallback);
};
