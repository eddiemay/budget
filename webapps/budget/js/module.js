com.digitald4.budget.module = angular.module('budget', ['DD4Common', 'ngRoute', 'ui.date'])
    .config(com.digitald4.budget.router)
    .service('accountService', function(apiConnector) {
      var accountService = new com.digitald4.common.JSONService("account", apiConnector);
      accountService.getAccounts = function(portfolioId, refDate, successCallback, errorCallback) {
        this.performRequest('GET', {portfolios: portfolioId, ref_date: refDate}, undefined, successCallback, errorCallback);
      };
      accountService.getSummaryData = function(portfolioId, year, successCallback, errorCallback) {
        this.performRequest(['summary'], {portfolios: portfolioId, year: year}, undefined, successCallback, errorCallback);
      };
      return accountService;
    })
    .service('billService', function(apiConnector) {
      var billService = new com.digitald4.common.JSONService("bill", apiConnector);
      billService.getBills = function(portfolioId, refDate, dateRange, successCallback, errorCallback) {
        this.performRequest('GET', {portfolio_id: portfolioId, ref_date: refDate, date_range: dateRange}, undefined,
            successCallback, errorCallback);
      };
      billService.applyTemplate = function(template, refDate, dateRange, successCallback, errorCallback) {
        this.performRequest(['applyTemplate', 'POST'], undefined, {template_id: template.id, ref_date: refDate},
            successCallback, errorCallback);
      };
      return billService;
    })
    .service('portfolioService', function(apiConnector) {
      return new com.digitald4.common.JSONService("portfolio", apiConnector);
    })
    .service('templateService', function(apiConnector) {
      var templateService = new com.digitald4.common.JSONService("template", apiConnector);
      templateService.list = function(portfolioId, filter, successCallback, errorCallback) {
        this.performRequest('GET', {portfolio_id: portfolioId}, filter, successCallback, errorCallback);
      };
      return templateService;
    })
    .factory('sharedData', function() {
      return new com.digitald4.budget.SharedData();
    })
    .controller('BudgetCtrl', com.digitald4.budget.BudgetCtrl)
    .controller('PortfoliosCtrl', com.digitald4.budget.PortfoliosCtrl)
    .controller('AccountsCtrl', com.digitald4.budget.AccountsCtrl)
    .directive('portfolios', function() {
      return {
        restrict: 'A',
        replace: true,
        controller: 'PortfoliosCtrl',
        controllerAs: 'portfoliosCtrl',
        templateUrl: 'js/html/portfolios.html'
      };
    })
    .directive('accounts', function() {
      return {
        restrict: 'A',
        replace: true,
        controller: 'AccountsCtrl',
        controllerAs: 'accountsCtrl',
        templateUrl: 'js/html/accounts.html'
      };
    });
