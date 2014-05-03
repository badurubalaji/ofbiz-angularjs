package org.ofbiz.angularjs.directive;

/**
 * Context Directive
 */
function ContextDirective(HttpService) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        var target = $attrs.target;
        var parameters = $scope.$parent[$attrs.parameters];
        var field = $attrs.field;
        
        $scope.$watch("ngModel", function(newValue) {
            $scope[$attrs.ngModel] = newValue;
        });

        if (!_.isEmpty(target)) {
            HttpService.post(target, parameters).success(function (response) {
                if (field != null) {
                    $scope.ngModel = response[field];
                } else {
                    $scope.ngModel = response;
                }
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
