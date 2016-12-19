com.digitald4.budget.BillService = function(restService) {
	this.restService = restService;
};

com.digitald4.budget.BillService.prototype.restService;

com.digitald4.budget.BillService.prototype.getBills = function(portfolioId, refDate, dateRange,
		successCallback, errorCallback) {
	this.restService.performRequest('bills', {portfolio_id: portfolioId, ref_date: refDate,
		date_range: dateRange}, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.addBill = function(newBill, successCallback, errorCallback) {
	this.restService.performRequest('create_bill', {bill: newBill}, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.updateBill = function(bill, property,
		successCallback, errorCallback) {
	var request = {
			id: bill.id,
			update: [{property: property, value: bill[property].toString()}]
	};
	this.restService.performRequest('update_bill', request, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.deleteBill = function(bill,
		successCallback, errorCallback) {
	this.restService.performRequest('delete_bill', {id: bill.id}, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.updateBillTrans = function(bill,
		successCallback, errorCallback) {
	var request = {
			bill_id: bill.id,
			transaction: bill.transaction};
	this.restService.performRequest('update_bill_trans', request, successCallback, errorCallback);
};

com.digitald4.budget.BillService.prototype.applyTemplate = function(template, refDate, dateRange,
		successCallback, errorCallback) {
	this.restService.performRequest('apply_template',
			{template_id: template.id, ref_date: refDate, date_range: dateRange},
			successCallback, errorCallback);
};

// 2504 W Cypress St Compton, CA 90220