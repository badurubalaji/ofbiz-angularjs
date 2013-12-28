package org.ofbiz.angularjs.directive;

/**
 * Tab Options Directive
 */
function TabOptionsDirective(HttpService, $templateCache) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        var targetUri = $attrs.targetUri;
        var targetParameters = $scope.$eval($attrs.targetParameters);
        var targetContentModel = $attrs.targetContentModel;
        var tabbableElement = $element.parent().parent();
        var tabContentElement = angular.element(tabbableElement.children()[1]);
        
        if (targetUri != null && targetContentModel != null) {
            var parameters = {};
            if (targetParameters != null) {
                parameters = $scope.$eval($attrs.targetParameters);
            }
            HttpService.post(targetUri, parameters).success(function(response) {
                var html;
                if (response.indexOf("<!DOCTYPE html>") >= 0) {
                    html = response.replace("<!DOCTYPE html>", "");
                } else {
                    html = response;
                }
                $scope.$parent[targetContentModel] = html;
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
