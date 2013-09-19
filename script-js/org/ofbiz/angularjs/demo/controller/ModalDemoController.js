package org.ofbiz.angularjs.demo.controller;

/**
 * Model Demo Controller
 * 
 * @param $scope
 */
function ModalDemoController($scope) {
    $scope.open = function () {
        $scope.shouldBeOpen = true;
    };
    
    $scope.close = function () {
        $scope.closeMsg = 'I was closed at: ' + new Date();
        $scope.shouldBeOpen = false;
    };
    
    $scope.items = ['item1', 'item2'];
    
    $scope.opts = {
        backdropFade: true,
        dialogFade:true
    };
}
