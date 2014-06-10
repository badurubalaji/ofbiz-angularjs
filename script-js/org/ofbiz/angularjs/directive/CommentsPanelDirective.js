package org.ofbiz.angularjs.directive;

/**
 * Comment Panel Directive
 * 
 * @param $compile
 * @param FormService
 */
function CommentsPanelDirective($compile, FormService) {

    function loadComments(contentId, $element, $scope) {
        // get content response
        FormService.post("getComments", {contentId: contentId}).success(function(getCommentsResponse) {
        
            // get comments panel template
            FormService.post("CommentsPanelTemplate", {}).success(function(template) {
                $element.html(template);
                $scope["comments"] = getCommentsResponse.comments;
                $compile($element.contents())($scope);
            });
        });
    }
    
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
                $scope.$watch("contentId", function(newVal, oldVal) {
                    loadComments(newVal, $element, $scope);
                });
            },
            post: function($scope, $element, $attrs) {
            }
        };
    };
}
