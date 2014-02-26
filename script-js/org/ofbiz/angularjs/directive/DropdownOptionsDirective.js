package org.ofbiz.angularjs.directive;

function DropdownOptionsDirective(HttpService, $rootScope) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude, $http) {
        var target = $attrs.target;
        var parameters = $scope.$eval($attrs.parameters);
        var fieldName = $attrs.fieldName;
        var descriptionFieldName = $attrs.descriptionFieldName;
        var placeholder = $attrs.placeholder;
        var ngModel = $attrs.ngModel;
        
        $scope.select2Options = {
            placeholder: placeholder,
            allowClear: true,
            data:[]
        };
        
        HttpService.post(target, parameters).success(function (response) {
            var options = response.options;
            if (options) {
                var data = [];
	            for (var i = 0; i < options.length; i ++) {
	                var option = options[i];
	                data.push({"id": option[fieldName], "text": option[descriptionFieldName]});
	            }
	            
	            $scope.select2Options.data = data;
	            $($element).select2($scope.select2Options);
            }
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
