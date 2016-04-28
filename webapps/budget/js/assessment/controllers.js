com.digitald4.iis.AssessmentCtrl = function($scope, NurseService) {
	if (!$scope.appId) {
		$scope.appId = 34;
		//return;
	}
	NurseService.getAppointment($scope.appId, function(appointment) {
		$scope.appointment = appointment;
		$scope.$apply();
	}, function(error) {
	});
};

com.digitald4.iis.AssessmentCtrl.$inject = ['$scope', 'NurseService'];
