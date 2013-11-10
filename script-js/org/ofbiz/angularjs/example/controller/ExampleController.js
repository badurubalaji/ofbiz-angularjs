package org.ofbiz.angularjs.example.controller;

/**
 * Example Controller
 * 
 * @param $scope
 */
function ExampleController($scope, $http) {
	
	$scope.editExample = {};
    
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
        openEditExampleModal(rowItem.entity.exampleId);
        return true;
    }
    
    $scope.onAfterSelectionChanged = function(rowItem, event) {
        // fire twice (afterSelected/afterDeselected)
        // https://github.com/angular-ui/ng-grid/issues/395?source=cc
    }
    
    $scope.onRowDoubleClicked = function(rowItem) {
        var exampleId = rowItem.exampleId;
        $scope.shouldOpenEditExampleModal = true;
    }
    
    $scope.onFindExampleClicked = function(event) {
        getExamples();
    }
    
    $scope.editExampleModalOptions = {
        backdropFade: false,
        dialogFade: false
    };
    
    $scope.closeEditExampleModal = function() {
        $scope.shouldOpenEditExampleModal = false;
    }
    
    $scope.updateExample = function() {
        var postData = {};
        postData.exampleId = $scope.editExample.exampleId;
        postData.exampleName = $scope.editExample.exampleName;
        $http.post("updateExample", postData).success(function(reponse) {
            getExamples();
            $scope.shouldOpenEditExampleModal = false;
        });
    }
    
    function openEditExampleModal(exampleId) {
        var postData = {exampleId: exampleId};
        $http.post("getExample", postData).success(function (response) {
            var example = response["example"];
            $scope.editExample.exampleId = example.exampleId;
            $scope.editExample.exampleName = example.exampleName;
            $scope.shouldOpenEditExampleModal = true;
        });
    }
    
    function getExamples() {
        var parameters = {};
        if ($scope.example) {
            parameters.exampleId = $scope.example.exampleId;
            parameters.exampleId_op = "contains";
            parameters.exampleId_ic = "Y";
            parameters.exampleName = $scope.example.exampleName;
            parameters.exampleName_op = "contains";
            parameters.exampleName_ic = "Y";
            $scope.$broadcast("exampleGrid", {"parameters": parameters});
        }
    }
}
