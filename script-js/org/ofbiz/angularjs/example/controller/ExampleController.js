package org.ofbiz.angularjs.example.controller;

/**
 * Example Controller
 * 
 * @param $scope
 */
function ExampleController($scope) {
    
    /**
     * On Submit Success
     */
    $scope.onSubmitSuccess = function(event, data, status, headers, config) {
        $scope.examples = data.examples;
    }
    
    /**
     * On Submit Error
     */
    $scope.onSubmitError = function(data, status, headers, config) {
        console.log("submit error");
    }
}
