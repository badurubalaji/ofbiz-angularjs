package org.ofbiz.angularjs.directive;

/**
 * Html Directive
 * http://docs.angularjs.org/api/ng.$compile
 */
function HtmlDirective(HttpService, $compile) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs) {

        $scope.$watch("ngModel", function(newValue) {
            if (newValue != null) {
                $element.html($scope.ngModel);
                $compile($element.contents())($scope);
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
