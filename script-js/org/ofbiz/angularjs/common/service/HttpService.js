package org.ofbiz.angularjs.common.service;

function HttpService($rootScope, $q, $http, appBusy) {
    
    /**
     * Post
     */
    this.post = function(target, parameters) {
        var deferred = $q.defer();
        var promise = deferred.promise;
        var successFn = null;
        var errorFn = null;
        
        promise.success = function(fn) {
            successFn = fn;
        }
        
        promise.error = function(fn) {
            errorFn = fn;
        }
        
        appBusy.set();
        $http.post(target, parameters)
        .success(function(data, status, headers, config) {
            appBusy.set(false);
            successFn(data);
            $rootScope.$emit("ON_HTTP_RESPONSE_MESSAGE_RECEIVED", { type: "success", msg: "HTTP Request Success!." });
        })
        .error(function(data, status, headers, config) {
            appBusy.set(false);
            errorFn(data);
            $rootScope.$emit("ON_HTTP_RESPONSE_MESSAGE_RECEIVED", { type: "error", msg: "HTTP Request Error!." });
        });
        return promise;
    }
}
