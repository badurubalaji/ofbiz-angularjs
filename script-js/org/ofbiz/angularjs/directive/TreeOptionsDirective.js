package org.ofbiz.angularjs.directive;
/**
 * Tree Options Directive
 *
 */
function TreeOptionsDirective() {

    this.controller = function($scope, $element, $attrs, $transclude, $http) {
        var id = _.uniqueId("tree_");
        $attrs.treeId = id;
        var currentNodeKey = id + ".currentNode";
        $scope.$watch(currentNodeKey, function(newObj, oldObj) {
            var tree = $scope[id]
            if (tree != null) {
                var currentNode = tree["currentNode"];
                if(angular.isObject(currentNode)) {
                    // TODO fire event
                    console.log( currentNode );
                }
            }
        }, false);
    };

    this.compile = function() {
        return {
            pre: function() {

            },
            post: function() {

            }
        };
    };

    this.link = function() {

    };
}
