package org.ofbiz.angularjs.demo.controller;
/**
 * Lookup Demo Controller
 * @param $scope
 */
function LookupDemoController($scope) {
    var editExample = {};
    editExample.exampleId = "EX01";
    $scope.editExample = editExample;
}
