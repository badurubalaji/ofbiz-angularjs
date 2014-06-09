package org.ofbiz.angularjs.directive;

function CommentsPanelDirective(FormService) {
    
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
                });
            },
            post: function($scope, $element, $attrs) {

            }
        };
    };
}
