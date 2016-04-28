com.digitald4.budget.Connector = function() {
	this.url = 'bs';
	this.dataType = 'json';
	this.requestType = 'GET';
};

com.digitald4.budget.Connector.prototype.url;
com.digitald4.budget.Connector.prototype.dataType;
com.digitald4.budget.Connector.prototype.requestType;
	
com.digitald4.budget.Connector.prototype.performRequest =
		function(request, successCallback, errorCallback) {
	// Send
	logRequest(request);
	$.ajax({
		url: this.url,
		dataType: this.dataType,
		type: this.requestType,
		data: request,
		success: function(response, textStatus, XMLHttpRequest) {
			if (response.valid) {
				successCallback(response.data);
			} else {
				console.log('error: ' + response.error);
				console.log('StackTrace:' + response.stackTrace);
				errorCallback(response.error);
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			errorCallback(errorThrown);
		}
	});
};

logRequest = function(request) {
	var text = '';
	for (var prop in request) {
		text += prop + ': ' + request[prop] + ', ';
	}
  console.log('performing action: {' + text + '}');
};

/*this.accountHash = {'1': {id: '1', name: 'Chase'},
'2': {id: '2', name: 'Bank of America'},
'4': {id: '4', name: 'Best Buy'},
'5': {id: '5', name: 'Condo'},
'9': {id: '9', name: 'Ally'},
'6': {id: '6', name: 'Wells Fargo'},
'7': {id: '7', name: 'Google Paycheck'},
'8': {id: '8', name: 'Capital One'},
bankAccounts: ['1', '2', '9']};
successCallback(this.accountHash);*/
