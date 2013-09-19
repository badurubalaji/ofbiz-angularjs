package org.ofbiz.angularjs.common.service;

/**
 * HTTP Service
 */
function HttpService() {
    
    /**
     * Get
     */
    this.get = function(uri, handler) {
        var onSuccess = handler.onSuccess;
        $http.get('jsonFiles/largeLoad.json').success(handler.onSuccess);
    }
}