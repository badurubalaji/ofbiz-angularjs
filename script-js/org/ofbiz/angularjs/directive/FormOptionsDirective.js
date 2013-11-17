package org.ofbiz.angularjs.directive;

/**
 * Form Options Directive
 * 
 */
function FormOptionsDirective() {
    
    var removeOnSubmitSuccessListener = null;
    var removeOnSubmitErrorListener = null;
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude, $http, appBusy, FormService) {
        $scope.$http = $http;
        $scope.appBusy = appBusy;
        $scope.FormService = FormService;
        
        if (removeOnSubmitSuccessListener != null) {
            removeOnSubmitSuccessListener();
        }
        if (removeOnSubmitErrorListener != null) {
            removeOnSubmitErrorListener();
        }
        removeOnSubmitSuccessListener = $scope.$on("ON_SUBMIT_SUCCESS", $scope[$attrs.onSubmitSuccess]);
        removeOnSubmitErrorListener = $scope.$on("ON_SUBMIT_ERROR", $scope[$attrs.onSubmitError]);
    }

    /**
     * Compile
     */
    this.compile = function() {
        return {
            pre: function() {
            
            },
            post: function($scope, $element, $attrs, controller) {
                if ($attrs.ngUpload == undefined) {
                    var target = $attrs.target;
                    
                    if (target) {
                        $element.find("input[type=submit]").bind("click", function() {
                            var data = $element.serialize();
                            $scope.FormService.post($scope.$http, target, data, $scope.appBusy, $scope);
                        });
                    }
                }
            }
        };
    };
}
