package org.ofbiz.angularjs.demo.controller;

/**
 * Date Time Demo Controller
 * @param $scope
 */
function DateTimeDemoController($scope, DateTimeUtil) {
    $scope.date = DateTimeUtil.getTime("2014-04-14 22:52:37.787");

    $scope.$watch("date", function(newVal) {
        console.log("selected date: " + DateTimeUtil.toTimestamp(newVal));
    });
}
