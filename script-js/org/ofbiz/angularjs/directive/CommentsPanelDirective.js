package org.ofbiz.angularjs.directive;

/**
 * Comment Panel Directive
 * 
 * @param $compile
 * @param FormService
 */
function CommentsPanelDirective($compile, FormService) {
    
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
            pre: function($scope, $element, $attrs) {
                FormService.post("CommentsPanelTemplate", {}).success(function(data) {
                    $element.html(data);
                    $scope[$attrs.ngModel] = $scope.ngModel;
                    $compile($element.contents())($scope);
                });
            },
            post: function($scope, $element, $attrs) {
                $scope.$watch($attrs.ngModel, function(newVal, oldVal) {
                    if (newVal != oldVal) {
                        $compile($element.contents())($scope);
                    }
                });
            }
        };
    };
}
