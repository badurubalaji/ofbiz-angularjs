package org.ofbiz.angularjs.directive;

/**
 * File Options Directive
 */
function FileOptionsDirective(HttpService, $parse) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        
        var model = $parse($attrs.model);
        var modelSetter = model.assign;
        
        $scope.onFileSelect = function($files) {
            $scope.$apply(function() {
                modelSetter($scope, $files);
            });
        }
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
