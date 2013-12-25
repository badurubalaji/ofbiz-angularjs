package org.ofbiz.angularjs.demo.controller;

/**
 * Tree Demo Controller
 * @param $scope
 */
function TreeDemoController($scope) {

    
    $scope.demoTree = 
        [
            { "label" : "User", "id" : "role1", "children" : [
                { "label" : "subUser1", "id" : "role11", "children" : [] },
                { "label" : "subUser2", "id" : "role12", "children" : [
                    { "label" : "subUser2-1", "id" : "role121", "children" : [
                        { "label" : "subUser2-1-1", "id" : "role1211", "children" : [] },
                        { "label" : "subUser2-1-2", "id" : "role1212", "children" : [] }
                    ]}
                ]}
            ]},
            { "label" : "Admin", "id" : "role2", "children" : [] },
            { "label" : "Guest", "id" : "role3", "children" : [] }
        ];   

    $scope.demoTree2 = 
        [
            { "label" : "Product Category 1", "id" : "productCategoryId1", "children" : [
                { "label" : "Product 1", "id" : "productId1", "children" : [] },
                { "label" : "Product Category 2", "id" : "productCategoryId2", "children" : [
                    { "label" : "Product Category 3", "id" : "productCategoryId3", "children" : [
                        { "label" : "Product 2", "id" : "productId2", "children" : [] },
                        { "label" : "Product 3", "id" : "productId3", "children" : [] }
                    ]}
                ]}
            ]},
            { "label" : "Product 4", "id" : "productId4", "children" : [] },
            { "label" : "Product 5", "id" : "productId5", "children" : [] }
        ];   
}
