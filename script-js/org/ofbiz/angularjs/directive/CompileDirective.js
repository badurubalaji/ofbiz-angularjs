package org.ofbiz.angularjs.directive;

/**
 * Compile Directive
 * http://docs.angularjs.org/api/ng.$compile
 */
function CompileDirective(HttpService, $compile) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        $scope.$watch(
                function($scope) {
                   // watch the 'compile' expression for changes
                  return $scope.$eval($attrs.compile);
                },
                function(value) {
                  // when the 'compile' expression changes
                  // assign it into the current DOM
                    $element.html(value);
         
                  // compile the new DOM and link it to the current
                  // scope.
                  // NOTE: we only compile .childNodes so that
                  // we don't get into infinite loop compiling ourselves
                  $compile($element.contents())($scope);
                }
            );
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
