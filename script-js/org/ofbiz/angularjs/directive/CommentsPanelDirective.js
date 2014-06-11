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
            
            $scope.onAddNewComment = function(newComment) {
                if (!_.isEmpty(newComment)) {
                    if (!_.isEmpty(newComment.textData)) {
                        var parameters = _.clone(newComment);
                        parameters.contentIdFrom = contentId;
                        FormService.post("createComment", parameters);
                    }
                }
            }
        
            // get comments panel template
            FormService.post("CommentsPanelTemplate", {}).success(function(template) {
                $element.html(template);
                $scope.comments = getCommentsResponse.comments;
                $scope.newComment = {};
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
