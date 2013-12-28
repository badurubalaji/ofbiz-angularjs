package org.ofbiz.angularjs.directive;

/**
 * Tab Options Directive
 */
function TabOptionsDirective(HttpService, $templateCache) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        var target = $attrs.target;
        var tabbableElement = $element.parent().parent();
        var tabContentElement = angular.element(tabbableElement.children()[1]);
        
        HttpService.post(target, {}).success(function(response) {
            $scope.$parent.viewExample = response;
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
