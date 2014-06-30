package org.ofbiz.angularjs.demo.controller;

function ContextDemoController($scope, $timeout) {
    $scope.getExampleParameters = {exampleId: "EX01"};
    $scope.findContextModalExampleId = "EX02";

    $scope.open = function () {
        $scope.shouldBeOpen = true;
    };

    $scope.close = function () {
        $scope.shouldBeOpen = false;
    };

    $scope.opts = {
        backdropFade: true,
        dialogFade:true
    };
}
