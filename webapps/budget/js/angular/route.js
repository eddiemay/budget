var com = {
  digitald4: {
	  budget: {}
  }
};

com.digitald4.budget.router = function($routeProvider) {
	$routeProvider
		.when('/',
		    {
				controller: 'DefaultViewCtrl',
				templateUrl: 'html/defview.html'
		    })
		.when('/accounts',
				{
					controller: 'AccountsCtrl',
					templateUrl: 'html/accounts.html'
				})
		.when('/list',
		    {
				controller: 'ListCtrl',
				templateUrl: 'html/listview.html'
		    })
		.when('/cal',
			{
				controller: 'CalCtrl',
				controllerAs: 'calCtrl',
				templateUrl: 'html/calview.html'
			})
		.when('/accounting',
			{
				controller: 'AccountingCtrl',
				templateUrl: 'html/accview.html'
			})
		.when('/summary',
			{
				controller: 'SummaryCtrl',
				templateUrl: 'html/sumview.html'
			})
		.when('/settings',
			{
				templateUrl: 'html/settings.html'
			})
			.when('/templates',
			{
				controller: 'TemplatesCtrl',
				controllerAs: 'templatesCtrl',
				templateUrl: 'html/templates.html'
			})
		.otherwise({ redirectTo: '/'});
};
