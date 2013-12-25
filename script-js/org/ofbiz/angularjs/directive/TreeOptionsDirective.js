package org.ofbiz.angularjs.directive;
/**
 * Tree Options Directive
 * 
 */
function TreeOptionsDirective() {

    this.controller = function($scope, $element, $attrs, $transclude, $http) {
        console.log("tree options");
        $attrs.treeId = _.uniqueId("tree_");
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
