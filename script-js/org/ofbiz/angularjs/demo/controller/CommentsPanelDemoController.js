package org.ofbiz.angularjs.demo.controller;

/**
 * Comments Panel Demo Controller
 * @param $scope
 */
function CommentsPanelDemoController($scope) {
    $scope.contentId = "BLG10000";
    
    $scope.onCommentCreated = function(newComment) {
        console.log("onCommentCreated: " + newComment);
    }
}
