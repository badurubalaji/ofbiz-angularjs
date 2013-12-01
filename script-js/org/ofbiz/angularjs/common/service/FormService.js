package org.ofbiz.angularjs.common.service;

/**
 * Form Service
 */
function FormService() {
    
    /**
     * Post
     */
    this.post = function($http, target, data, appBusy, $scope) {
        appBusy.set();
        $http({method: "POST", url: target, data: data, headers: {"Content-Type": "application/x-www-form-urlencoded"}})
        .success(function(data, status, headers, config) {
            $scope.appBusy.set(false);
            if (data._ERROR_MESSAGE_ != undefined) {
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
