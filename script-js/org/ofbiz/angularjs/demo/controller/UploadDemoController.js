package org.ofbiz.angularjs.demo.controller;

/**
 * Upload Demo Controller
 * @param $scope
 */
function UploadDemoController($scope) {
    $scope.onUploadSuccess = function (data) {
        $scope.dataResourceId = data.dataResourceId;
    };
}
