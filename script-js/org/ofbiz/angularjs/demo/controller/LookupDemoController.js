package org.ofbiz.angularjs.demo.controller;
/**
 * Lookup Demo Controller
 * @param $scope
 */
function LookupDemoController($scope) {
    $scope.$watch("lookupExample", function(newVal) {
        console.log("looked up exampe: " + JSON.stringify(newVal));
    });
}
