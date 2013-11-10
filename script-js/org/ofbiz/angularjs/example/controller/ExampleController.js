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
    
    $scope.onBeforeSelectionChanged = function(rowItem, event) {
        var exampleId = rowItem.entity.exampleId
        return true;
    }
    
    $scope.onAfterSelectionChanged = function(rowItem, event) {
        // fire twice (afterSelected/afterDeselected)
        // https://github.com/angular-ui/ng-grid/issues/395?source=cc
    }
    
    $scope.onRowDoubleClicked = function(rowItem) {
        var exampleId = rowItem.exampleId;
    }
    
    $scope.onFindExampleClicked = function(event) {
        var parameters = {};
        if ($scope.example) {
            parameters.exampleId = $scope.example.exampleId;
            parameters.exampleId_op = "contains";
            parameters.exampleId_ic = "Y";
            parameters.exampleName = $scope.example.exampleName;
            parameters.exampleName_op = "contains";
            parameters.exampleName_ic = "Y";
        }
        $scope.$broadcast("exampleGrid", {"parameters": parameters});
    }
}
