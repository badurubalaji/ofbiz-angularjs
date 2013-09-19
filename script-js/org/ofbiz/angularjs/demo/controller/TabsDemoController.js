package org.ofbiz.angularjs.demo.controller;

/**
 * Tabs Demo Controller
 * 
 * @param $scope
 */
function TabsDemoController($scope) {
    $scope.tabs = [
        { title:"Dynamic Title 1", content:"Dynamic content 1" },
        { title:"Dynamic Title 2", content:"Dynamic content 2", disabled: true }
    ];
    
    $scope.alertMe = function() {
         setTimeout(function() {
             alert("You've selected the alert tab!");
         });
    };
   
    $scope.navType = 'pills';
}
