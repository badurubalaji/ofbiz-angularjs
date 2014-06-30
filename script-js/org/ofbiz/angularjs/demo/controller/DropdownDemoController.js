package org.ofbiz.angularjs.demo.controller;

/**
 * Dropdown Demo Controller
 * @param $scope
 */
function DropdownDemoController($scope) {

    $scope.lookupGeoParameters = {geoTypeId: 'COUNTRY'};

    $scope.colors = [
       {name:'black', shade:'dark'},
       {name:'white', shade:'light'},
       {name:'red', shade:'dark'},
       {name:'blue', shade:'dark'},
       {name:'yellow', shade:'light'}
    ];
    $scope.color = $scope.colors[2]; // red
}
