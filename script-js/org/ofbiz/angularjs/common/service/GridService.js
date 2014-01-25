package org.ofbiz.angularjs.common.service;

/**
 * Grid Service
 */
function GridService($rootScope) {
    
    this.load = function(gridName, parameters) {
        console.log("load: " + gridName);
    }
}
