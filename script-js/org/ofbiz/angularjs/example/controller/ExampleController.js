package org.ofbiz.angularjs.example.controller;

/**
 * Example Controller
 * 
 * @param $scope
 */
function ExampleController($scope, $http) {
    
    $scope.editExample = {};
    
    $scope.editExampleModalOptions = {
        backdropFade: false,
        dialogFade: false
    };
    
    /**
     * On Submit Success
     */
    function onSubmitSuccess(event, data, status, headers, config) {
        $scope.examples = data.examples;
    }
    $scope.onSubmitSuccess = onSubmitSuccess;
    
    /**
     * On Submit Error
     */
    function onSubmitError(data, status, headers, config) {
        console.log("submit error");
    }
    $scope.onSubmitError = onSubmitError;
    
    function onBeforeSelectionChanged(rowItem, event) {
        openEditExampleModal(rowItem.entity.exampleId);
        return true;
    }
    $scope.onBeforeSelectionChanged = onBeforeSelectionChanged;
    
    function onAfterSelectionChanged(rowItem, event) {
        // fire twice (afterSelected/afterDeselected)
        // https://github.com/angular-ui/ng-grid/issues/395?source=cc
    }
    $scope.onAfterSelectionChanged = onAfterSelectionChanged;
    
    function onRowDoubleClicked(rowItem) {
        var exampleId = rowItem.exampleId;
        $scope.shouldOpenEditExampleModal = true;
    }
    $scope.onRowDoubleClicked = onRowDoubleClicked;
    
    function onFindExampleClicked(event) {
        getExamples();
    }
    $scope.onFindExampleClicked = onFindExampleClicked;
    
    function closeEditExampleModal() {
        $scope.shouldOpenEditExampleModal = false;
    }
    $scope.closeEditExampleModal = closeEditExampleModal;
    
    function onUpdateExampleSuccess() {
        getExamples();
        $scope.shouldOpenEditExampleModal = false;
    }
    $scope.onUpdateExampleSuccess = onUpdateExampleSuccess;
    
    function onUpdateExampleError() {
        
    }
    $scope.onUpdateExampleError = onUpdateExampleError;
    
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
        if ($scope.findExample) {
            parameters.exampleId = $scope.findExample.exampleId;
            parameters.exampleId_op = "contains";
            parameters.exampleId_ic = "Y";
            parameters.exampleName = $scope.findExample.exampleName;
            parameters.exampleName_op = "contains";
            parameters.exampleName_ic = "Y";
        }
        $scope.$broadcast("exampleGrid", {"parameters": parameters});
    }
}
