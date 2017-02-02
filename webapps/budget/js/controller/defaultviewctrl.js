com.digitald4.budget.DefaultViewCtrl = function($scope) {
	this.scope = $scope;
	this.scope.count = 0;
	this.scope.doSomething = this.doSomething.bind(this);
};

com.digitald4.budget.DefaultViewCtrl.prototype.scope;

com.digitald4.budget.DefaultViewCtrl.prototype.doSomething = function() {
	this.scope.count++;
	this.scope.$apply();
};