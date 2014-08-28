package org.ofbiz.angularjs.directive;

/**
 * File Options Directive
 */
function FileOptionsDirective(HttpService, $parse, ScopeUtil) {

    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {

        $scope.onFileSelect = function($files) {
            $scope.ngModel = $files;
            ScopeUtil.setClosestScopeProperty($scope, $attrs.ngModel, $files);
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
