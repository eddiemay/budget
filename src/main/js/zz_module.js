com.digitald4.budget.module = angular.module('budget', ['DD4Common', 'ngRoute', 'ui.date'])
    .config(com.digitald4.budget.router)
    .service('accountService', function(apiConnector) {
      var accountService = new com.digitald4.common.JSONService("account", apiConnector);
      accountService.list = function(portfolioId, successCallback, errorCallback) {
        this.list_({portfolios: portfolioId}, undefined, successCallback, errorCallback);
      };
      return accountService;
    })
    .service('balanceService', function(apiConnector) {
      var balanceService = new com.digitald4.common.JSONService("balance", apiConnector);
      balanceService.list = function(portfolioId, year, month, successCallback, errorCallback) {
        this.list_({portfolios: portfolioId, year: year, month: month}, undefined, successCallback, errorCallback);
      };
      return balanceService;
    })
    .service('billService', function(apiConnector) {
      var billService = new com.digitald4.common.JSONService("bill", apiConnector);
      billService.list = function(portfolioId, year, month, successCallback, errorCallback) {
        this.list_({portfolios: portfolioId, year: year, month: month}, undefined, successCallback, errorCallback);
      };
      billService.applyTemplate = function(template, year, month, successCallback, errorCallback) {
        this.performRequest(['applyTemplate', 'POST'], undefined, {templateId: template.id, year: year, month: month},
            successCallback, errorCallback);
      };
      return billService;
    })
    .service('portfolioService', function(apiConnector) {
      return new com.digitald4.common.JSONService("portfolio", apiConnector);
    })
    .service('templateService', function(apiConnector) {
      var templateService = new com.digitald4.common.JSONService("template", apiConnector);
      templateService.list = function(portfolioId, successCallback, errorCallback) {
        this.list_({portfolioId: portfolioId}, undefined, successCallback, errorCallback);
      };
      return templateService;
    })
    .service('templateBillService', function(apiConnector) {
      var templateBillService = new com.digitald4.common.JSONService("templateBill", apiConnector);
      templateBillService.list = function(templateId, successCallback, errorCallback) {
        this.list_({templateId: templateId}, undefined, successCallback, errorCallback);
      };
      return templateBillService;
    })
    .factory('sharedData', function() {
      return new com.digitald4.budget.SharedData();
    })
    .controller('AccountsCtrl', com.digitald4.budget.AccountsCtrl)
    .controller('BudgetCtrl', com.digitald4.budget.BudgetCtrl)
    .controller('PortfoliosCtrl', com.digitald4.budget.PortfoliosCtrl)
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
