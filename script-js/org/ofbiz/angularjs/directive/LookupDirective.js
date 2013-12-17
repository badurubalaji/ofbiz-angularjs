package org.ofbiz.angularjs.directive;

/**
 * Lookup Directive
 * http://plnkr.co/edit/AT2RhpE4Qhj4iN5qMtdi?p=preview
 * Fetch data from sercer: http://plnkr.co/edit/t1neIS?p=preview
 * JSONP: http://docs.angularjs.org/api/ng.$http#methods_jsonp
 */
function LookupDirective(HttpService) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude, $http) {
        var target = $attrs.target;
        var descriptionFieldName = $attrs.descriptionFieldName;
        
        $scope.getOptions = function($viewValue) {
            
            var parameters = {queryString: $viewValue};
            
            return $http.post(target, parameters).then(function(response) {
                return response.data.options
            });
        }
        
        $scope.getOptionDescription = function(option) {
            if (option && descriptionFieldName) {
                return option[descriptionFieldName];
            } else {
                return "";
            }
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
