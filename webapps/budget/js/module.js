com.digitald4.budget.module = angular.module('budget', ['DD4Common', 'ngRoute', 'ui.date'])
    .config(com.digitald4.budget.router)
    .service('accountService', function(restService) {
      var accountService = new com.digitald4.common.ProtoService("account", restService);
      accountService.getAccounts = function(portfolioId, refDate, successCallback, errorCallback) {
        this.performRequest('list', {portfolio_id: portfolioId, ref_date: refDate}, successCallback, errorCallback);
      };
      accountService.getSummaryData = function(portfolioId, year, successCallback, errorCallback) {
        this.performRequest('getSummary', {portfolio_id: portfolioId, year: year}, successCallback, errorCallback);
      };
      return accountService;
    })
    .service('billService', function(restService) {
      var billService = new com.digitald4.common.ProtoService("bill", restService);
      billService.getBills = function(portfolioId, refDate, dateRange, successCallback, errorCallback) {
        this.performRequest('list', {portfolio_id: portfolioId, ref_date: refDate, date_range: dateRange},
            successCallback, errorCallback);
      };
      billService.applyTemplate = function(template, refDate, dateRange, successCallback, errorCallback) {
        this.performRequest('applyTemplate', {template_id: template.id, ref_date: refDate},
            successCallback, errorCallback);
      };
      return billService;
    })
    .service('portfolioService', function(restService) {
      return new com.digitald4.common.ProtoService("portfolio", restService);
    })
    .service('templateService', function(restService) {
      return new com.digitald4.common.ProtoService("template", restService);
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
