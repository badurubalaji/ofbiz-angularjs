package org.ofbiz.angularjs.example.controller;

function CommonExampleFeatureViewController($scope, $stateParams) {
    $scope.exampleFeatureId = $stateParams.exampleFeatureId;
}
