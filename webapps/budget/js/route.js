com.digitald4.budget.router = function($routeProvider) {
	$routeProvider
		.when('/', {
				controller: com.digitald4.budget.DefaultViewCtrl,
				controllerAs: 'defaultViewCtrl',
				templateUrl: 'js/html/defview.html'
		}).when('/accounts', {
				controller: com.digitald4.budget.AccountsCtrl,
				templateUrl: 'js/html/accounts.html'
		}).when('/list', {
				controller: com.digitald4.budget.ListCtrl,
				controllerAs: 'listCtrl',
				templateUrl: 'js/html/listview.html'
		}).when('/cal', {
				controller: com.digitald4.budget.CalCtrl,
				controllerAs: 'calCtrl',
				templateUrl: 'js/html/calview.html'
		}).when('/accounting', {
				controller: com.digitald4.budget.AccountingCtrl,
				controllerAs: 'accountingCtrl',
				templateUrl: 'js/html/accview.html'
		}).when('/summary', {
				controller: com.digitald4.budget.SummaryCtrl,
				controllerAs: 'summaryCtrl',
				templateUrl: 'js/html/sumview.html'
		}).when('/settings', {
				templateUrl: 'js/html/settings.html'
		}).when('/templates', {
				controller: com.digitald4.budget.TemplatesCtrl,
				controllerAs: 'templatesCtrl',
				templateUrl: 'js/html/templates.html'
		}).otherwise({ redirectTo: '/'});
};
