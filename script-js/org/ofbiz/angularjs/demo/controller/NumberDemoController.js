package org.ofbiz.angularjs.demo.controller;

/**
 * Number Demo Controller
 * 
 * @param $scope
 */
function NumberDemoController($scope) {
    var numberModel = {};
    numberModel.price = 12;
    $scope.numberModel = numberModel;
}
