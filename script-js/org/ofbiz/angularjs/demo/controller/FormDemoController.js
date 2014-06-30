package org.ofbiz.angularjs.demo.controller;

/**
 * Form Demo Controller
 * @param $scope
 */
function FormDemoController($scope) {

    $scope.master= {};

    $scope.lookupGeoParameters = {geoTypeId: 'COUNTRY'};

    $scope.update = function(user) {
        $scope.master= angular.copy(user);
    };

    $scope.reset = function() {
        $scope.user = angular.copy($scope.master);
    };

    $scope.reset();

    $scope.onSubmitSuccess = function(event, data, status, headers, config) {
        $scope.user = {};
    }

    $scope.onSubmitError = function(event, data, status, headers, config) {
        console.log("error: " + angular.toJson(data));
    }
}
