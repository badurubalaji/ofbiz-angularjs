package org.ofbiz.angularjs.directive;

/**
 * Context Directive
 */
function ContextDirective() {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude, $http, appBusy) {
        var target = $attrs.target;
        var parameters = $scope.$eval($attrs.parameters);
        var model = $attrs.model;
        var field = $attrs.field;

        $http.post(target, parameters).success(function (response) {
            if (field != null) {
                $scope[model] = response[field];
            } else {
                $scope[model] = response;
            }
        });
    }

    /**
     * Compile
     */
    this.compile = function() {
        return {
            pre: function() {
            
            },
            post: function($scope, $element, $attrs, controller) {

            }
        };
    };
}
