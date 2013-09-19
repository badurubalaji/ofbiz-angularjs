package org.ofbiz.angularjs.directive;

/**
 * UI Map Options Directive
 */
function UiMapOptionsDirective() {
    
    /**
     * Controller
     */
    this.controller = function ($scope, $element, $attrs, $transclude) {
        var height = $attrs.height;
        if (height == "") {
            height = 400;
        }
        
        angular.element($element).css("height", height + "px");
    }
}
