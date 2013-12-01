package org.ofbiz.angularjs.directive;

/**
 * Context Directive
 */
function ContextDirective(HttpService) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        var target = $attrs.target;
        var parameters = $scope.$eval($attrs.parameters);
        var model = $attrs.model;
        var field = $attrs.field;

        HttpService.post(target, parameters).success(function (response) {
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
