package org.ofbiz.angularjs.demo.controller;
/**
 * Accordion Demo Controller
 * @param $scope
 */
function AccordionDemoController($scope) {
    $scope.oneAtATime = true;

    $scope.groups = [
        {
            title: "Dynamic Group Header - 1",
            content: "Dynamic Group Body - 1"
        },
        {
            title: "Dynamic Group Header - 2",
            content: "Dynamic Group Body - 2"
        }
    ];
    
    $scope.items = ['Item 1', 'Item 2', 'Item 3'];
    
    /**
     * Add Item
     */
    $scope.addItem = function() {
        var newItemNo = $scope.items.length + 1;
        $scope.items.push('Item ' + newItemNo);
    }
}
