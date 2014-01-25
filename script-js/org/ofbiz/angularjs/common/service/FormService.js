package org.ofbiz.angularjs.common.service;

/**
 * Form Service
 */
function FormService(HttpService) {
    
    /**
     * Post
     */
    this.post = function($http, target, data, appBusy, $scope) {
        appBusy.set();
        /*
        var config = {};
        config.method = "POST";
        config.url = target;
        config.data = data;
        config.header = {"Content-Type": "application/x-www-form-urlencoded"};
        HttpService.post(target, data, config.header)
        $http(config)
        */
        $http({method: "POST", url: target, data: data, headers: {"Content-Type": "application/x-www-form-urlencoded"}})
        
        .success(function(data, status, headers, config) {
            $scope.appBusy.set(false);
            if (data._ERROR_MESSAGE_ != undefined || data._ERROR_MESSAGE_LIST_ != undefined) {
                $scope.$emit("ON_SUBMIT_ERROR", data, status, headers, config);
            } else {
                $scope.$emit("ON_SUBMIT_SUCCESS", data, status, headers, config);
            }
        })
        .error(function(data, status, headers, config) {
            appBusy.set(false);
            $scope.$emit("ON_SUBMIT_ERROR", data, status, headers, config);
        });
    }
}
