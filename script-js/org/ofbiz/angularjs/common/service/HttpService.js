package org.ofbiz.angularjs.common.service;

function HttpService($rootScope, $q, $http, appBusy) {
    
    /**
     * Post
     */
    this.post = function(target, parameters) {
        return this.post(target, parameters, null);
    }
    
    this.post = function(target, parameters, header) {
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
        $rootScope.$emit("ON_HTTP_REQUEST_WILL_SEND", {});
        $http.post(target, parameters)
        .success(function(data, status, headers, config) {
            appBusy.set(false);
            if (data._ERROR_MESSAGE_LIST_) {
                var responseMessages = [];
                _.each(data._ERROR_MESSAGE_LIST_, function(errorMessage) {
                    responseMessages.push({ type: "error", msg: errorMessage });
                });
                $rootScope.$emit("ON_HTTP_RESPONSE_MESSAGE_RECEIVED", responseMessages);
            } else {
                successFn(data);
                if (data._SUCCESS_MESSAGE_LIST_) {
                    _.each(data._SUCCESS_MESSAGE_LIST_, function(successMessage) {
                        $rootScope.$emit("ON_HTTP_RESPONSE_MESSAGE_RECEIVED", [{ type: "success", msg: successMessage }]);
                    });
                }
            }
        })
        .error(function(data, status, headers, config) {
            appBusy.set(false);
            errorFn(data);
            $rootScope.$emit("ON_HTTP_RESPONSE_MESSAGE_RECEIVED", { type: "error", msg: "HTTP Request Error!." });
        });
        return promise;
    }
}
