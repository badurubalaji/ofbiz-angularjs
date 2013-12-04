package org.ofbiz.angularjs.example.controller;

function ExampleCommonDecoratorController($rootScope, $scope) {
    
    $rootScope.$on("ON_HTTP_RESPONSE_MESSAGE_RECEIVED", function(event, responseMessage) {
        // $scope.alerts = [responseMessage];
    });
    
    $scope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams) {
        $scope.alerts = [];
    });
    
    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
    };
}
