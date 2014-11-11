package org.ofbiz.angularjs.demo.controller;

/**
 * Model Demo Controller
 *
 * @param $scope
 */
function ModalDemoController($scope, $timeout) {
    $scope.open = function () {
        $timeout(function() {
            $scope.shouldBeOpen = true;
        }, 100);
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
