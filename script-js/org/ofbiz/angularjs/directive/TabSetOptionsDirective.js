package org.ofbiz.angularjs.directive;

/**
 * Tab Options Directive
 * http://plnkr.co/edit/fuVb0mzhmDCKr1xKp7Rn?p=preview
 */
function TabSetOptionsDirective(HttpService) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        var children = $element.children();
        _.each(children, function(child) {
            var divElement = angular.element(child);
            var ulElement = divElement.find("ul");
            var liElements = angular.element(ulElement).children();
            // iElements.length is alway 0
            _.each(liElements, function(liElement) {
            });
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
