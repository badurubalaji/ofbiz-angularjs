package org.ofbiz.angularjs.directive;

/**
 * Form Options Directive
 * 
 */
function FormOptionsDirective() {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude, $http, appBusy) {
        $scope.$http = $http;
        $scope.appBusy = appBusy;
        
        $scope.$on("ON_SUBMIT_SUCCESS", $scope[$attrs.onSubmitSuccess]);
        $scope.$on("ON_SUBMIT_ERROR", $scope[$attrs.onSubmitError]);
    }

    
    this.compile = function() {
        return {
            pre: function() {
            
            },
            post: function($scope, $element, $attrs, controller) {
                if ($attrs.ngUpload == undefined) {
                    var target = $attrs.target;
                    
                    $element.find("input[type=submit]").bind("click", function() {
                        var data = $element.serialize();
                        org.ofbiz.angularjs.directive.FormOptionsDirective.post($scope, target, data);
                    });
                }
            }
        };
    };
    
    /**
     * Post
     */
    this.post = function() {
        $scope.appBusy.set();
        $scope.$http({method: "POST", url: target, data: data, headers: {"Content-Type": "application/x-www-form-urlencoded"}})
        .success(function(data, status, headers, config) {
            $scope.appBusy.set(false);
            if (data._ERROR_MESSAGE_ != undefined) {
                $scope.$emit("ON_SUBMIT_ERROR", data, status, headers, config);
            } else {
                $scope.$emit("ON_SUBMIT_SUCCESS", data, status, headers, config);
            }
        })
        .error(function(data, status, headers, config) {
            $scope.appBusy.set(false);
            $scope.$emit("ON_SUBMIT_ERROR", data, status, headers, config);
        });
    }
}
