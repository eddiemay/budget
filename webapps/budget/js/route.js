com.digitald4.budget.router = function($routeProvider) {
	$routeProvider
		.when('/', {
				controller: 'DefaultViewCtrl',
				controllerAs: 'defaultViewCtrl',
				templateUrl: 'js/html/defview.html'
		}).when('/accounts', {
				controller: 'AccountsCtrl',
				templateUrl: 'js/html/accounts.html'
		}).when('/list', {
				controller: 'ListCtrl',
				controllerAs: 'listCtrl',
				templateUrl: 'js/html/listview.html'
		}).when('/cal', {
				controller: 'CalCtrl',
				controllerAs: 'calCtrl',
				templateUrl: 'js/html/calview.html'
		}).when('/accounting', {
				controller: 'AccountingCtrl',
				controllerAs: 'accountingCtrl',
				templateUrl: 'js/html/accview.html'
		}).when('/summary', {
				controller: 'SummaryCtrl',
				controllerAs: 'summaryCtrl',
				templateUrl: 'js/html/sumview.html'
		}).when('/settings', {
				templateUrl: 'js/html/settings.html'
		}).when('/templates', {
				controller: 'TemplatesCtrl',
				controllerAs: 'templatesCtrl',
				templateUrl: 'js/html/templates.html'
		}).otherwise({ redirectTo: '/'});
};
