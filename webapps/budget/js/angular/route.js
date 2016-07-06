var com = com || {
};

com.digitald4 = com.digitald4 || {
	common: {},
	budget: {}
}

var proto = proto || { 
};

proto.budget = proto.budget || {
};

var goog = goog || {
	provide: function(provides) {}
};

com.digitald4.budget.router = function($routeProvider) {
	$routeProvider
		.when('/', {
				controller: 'DefaultViewCtrl',
				templateUrl: 'html/defview.html'
		}).when('/accounts', {
				controller: 'AccountsCtrl',
				templateUrl: 'html/accounts.html'
		}).when('/list', {
				controller: 'ListCtrl',
				controllerAs: 'listCtrl',
				templateUrl: 'html/listview.html'
		}).when('/cal', {
				controller: 'CalCtrl',
				controllerAs: 'calCtrl',
				templateUrl: 'html/calview.html'
		}).when('/accounting', {
				controller: 'AccountingCtrl',
				templateUrl: 'html/accview.html'
		}).when('/summary', {
				controller: 'SummaryCtrl',
				templateUrl: 'html/sumview.html'
		}).when('/settings', {
				templateUrl: 'html/settings.html'
		}).when('/templates', {
				controller: 'TemplatesCtrl',
				controllerAs: 'templatesCtrl',
				templateUrl: 'html/templates.html'
		}).otherwise({ redirectTo: '/'});
};
