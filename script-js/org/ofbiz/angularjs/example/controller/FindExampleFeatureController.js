package org.ofbiz.angularjs.example.controller;

/**
 * Find Example Feature Controller
 * 
 * @param $scope
 */
function FindExampleFeatureController($scope, $state) {
    
    $scope.onFindButtonClicked = function() {
        getExampleFeatures();
    }
    
    $scope.onExampleFeatureRowDoubleClicked = function(rowItem) {
        var exampleFeatureId = rowItem.exampleFeatureId;
        $state.go("feature.edit", {"exampleFeatureId": exampleFeatureId});
    }
    
    function getExampleFeatures() {
        var parameters = {};
        if ($scope.findExampleFeature) {
            parameters.exampleFeatureId = $scope.findExampleFeature.exampleFeatureId;
            parameters.exampleFeatureId_op = "contains";
            parameters.exampleId_ic = "Y";
            parameters.description = $scope.findExampleFeature.description;
            parameters.description_op = "contains";
            parameters.description_ic = "Y";
        }
        $scope.$broadcast("exampleFeatureGrid", {"parameters": parameters});
    }
}
