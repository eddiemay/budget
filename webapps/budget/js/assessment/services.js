com.digitald4.common = {};
com.digitald4.common.connector = {};

com.digitald4.common.connector.JQuery = function(url, dataType, requestType) {
	this.url = url;
	this.dataType = dataType;
	this.requestType = requestType;
	
	this.performRequest = function(request, successCallback, errorCallback) {
		// Send
		$.ajax({
			url: this.url,
			dataType: this.dataType,
			type: this.requestType,
			data: request,
			success: function(response, textStatus, XMLHttpRequest) {
				successCallback(response, textStatus, XMLHttpRequest);
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				errorCallback(XMLHttpRequest, textStatus, errorThrown);
			}
		});
	};
};

com.digitald4.iis.module.service('NurseService', ['$log',
    function($log) {
		this.connector = new com.digitald4.common.connector.JQuery('nurseService', 'json', 'GET');
		this.getAppointment = function(appId, successCallback, errorCallback) {
			var request = {
					action: 'getAppointment',
					id: appId
			};
			this.connector.performRequest(request, function(response, textStatus, XMLHttpRequest) {
				if (response.valid) {
					successCallback(response.data);
				}
				else {
					$log('invalid request');
					errorCallback('invalid');
				}
			}, function(XMLHttpRequest, textStatus, errorThrown) {
				$log(XMLHttpRequest + ' ' + textStatus + ' ' + errorThrown);
				errorCallback(errorThrown);
			});
		};
	}
]);