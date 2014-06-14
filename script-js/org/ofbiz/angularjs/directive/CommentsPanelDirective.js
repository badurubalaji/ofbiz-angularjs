package org.ofbiz.angularjs.directive;

/**
 * Comment Panel Directive
 * 
 * @param $compile
 * @param FormService
 */
function CommentsPanelDirective($compile, FormService) {

    function loadComments(contentId, $element, $scope) {
        
        // get comments
        FormService.post("getComments", {contentId: contentId}).success(function(getCommentsResponse) {
            
            $scope.onAddNewComment = function() {
                if (!_.isEmpty($scope.newComment)) {
                    if (!_.isEmpty($scope.newComment.textData)) {
                        var parameters = _.clone($scope.newComment);
                        parameters.contentIdFrom = contentId;
                        FormService.post("createComment", parameters).success(function(data) {
                            var newContentId = data.contentId;
                            $scope.onCreateSuccess(data);
                            $scope.newComment = {};
                            
                            // reload comments
                            FormService.post("getComments", {contentId: contentId}).success(function(reloadCommentsResponse) {
                                $scope.comments = reloadCommentsResponse.comments;
                            });
                        });
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
                    if (!_.isEmpty(newVal)) {
                        loadComments(newVal, $element, $scope);
                    }
                });
            },
            post: function($scope, $element, $attrs) {
            }
        };
    };
}
