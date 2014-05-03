package org.ofbiz.angularjs.directive;

/**
 * Form Options Directive
 * 
 */
function FormOptionsDirective($http, appBusy, $parse, FormService) {
    
    /**
     * Link
     */
    this.link = function($scope, $element, $attrs) {
        $element.bind("submit", function() {
            $scope.ngSubmit();
        });
    }
}
