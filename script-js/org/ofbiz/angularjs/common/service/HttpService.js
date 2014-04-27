package org.ofbiz.angularjs.common.service;

function HttpService($rootScope, $q, $http, appBusy) {
    
    /**
     * Post
     */
    this.post = function(target, parameters) {
        return this.post(target, parameters, {"Content-Type": "application/x-www-form-urlencoded"});
    }
    
    this.post = function(target, parameters, headers) {
        var deferred = $q.defer();
        var promise = deferred.promise;
        var successFn = null;
        var errorFn = null;
        
        promise.success = function(fn) {
            successFn = fn;
            return promise;
        }
        
        promise.error = function(fn) {
            errorFn = fn;
            return promise;
        }
        appBusy.set();
        var queryString = null;
        if (typeof(parameters) == "object") {
            queryString = "";
            var keys = _.keys(parameters);
            _.each(keys, function(paramName) {
                var paramValue = parameters[paramName];
                if (paramValue != null) {
                    queryString += paramName + "=" + paramValue + "&";
                }
            });
            queryString = queryString.substring(0, queryString.length - 1);
        } else {
            queryString = parameters;
        }
        
        if (headers == null) {
            headers = {"Content-Type": "application/x-www-form-urlencoded"};
        }

        $rootScope.$emit("ON_HTTP_REQUEST_WILL_SEND", {});
        $http({method: "POST", url: target, data: queryString, headers: headers})
        .success(function(data, status, headers, config) {
            appBusy.set(false);
            if (data._ERROR_MESSAGE_LIST_) {
                var responseMessages = [];
                _.each(data._ERROR_MESSAGE_LIST_, function(errorMessage) {
                    responseMessages.push({ type: "error", msg: errorMessage });
                });
                
                if (typeof(errorFn) == "function") {
                    errorFn(data);
                }
                $rootScope.$emit("ON_HTTP_RESPONSE_MESSAGE_RECEIVED", responseMessages);
            } else {
                if (typeof(successFn) == "function") {
                    successFn(data);
                }
                if (data._SUCCESS_MESSAGE_LIST_) {
                    _.each(data._SUCCESS_MESSAGE_LIST_, function(successMessage) {
                        $rootScope.$emit("ON_HTTP_RESPONSE_MESSAGE_RECEIVED", [{ type: "success", msg: successMessage }]);
                    });
                }
            }
        })
        .error(function(data, status, headers, config) {
            appBusy.set(false);
            if (typeof(errorFn) == "function") {
                errorFn(data);
            }
            $rootScope.$emit("ON_HTTP_RESPONSE_MESSAGE_RECEIVED", { type: "error", msg: "HTTP Request Error!." });
        });
        return promise;
    }
}
