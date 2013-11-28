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
