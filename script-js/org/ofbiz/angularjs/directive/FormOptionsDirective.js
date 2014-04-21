package org.ofbiz.angularjs.directive;

/**
 * Form Options Directive
 * 
 */
function FormOptionsDirective($http, appBusy, $parse, FormService) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {

        
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
