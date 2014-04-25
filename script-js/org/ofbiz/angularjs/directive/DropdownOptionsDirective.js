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
        var defaultValue = $scope.$eval($attrs.defaultValue);
        
        if (fieldName == null) {
            fieldName = "id";
        }
        if (descriptionFieldName == null) {
            descriptionFieldName = "text";
        }
        
        $scope.select2Options = {
            placeholder: placeholder,
            allowClear: true,
            data:[],
            id: function(object) {
                return object;
            },
            formatSelection: function(object, container) {
                return object[descriptionFieldName];
            },
            formatResult: function(object, container, query) {
                return object[descriptionFieldName];
            },
            initSelection: function(element, callback) {
                // TODO http://ivaynberg.github.io/select2/
                var data = [];
                $(element.val().split(",")).each(function () {
                    var dataObj = {};
                    dataObj[fieldName] = this;
                    dataObj[descriptionFieldName] = this;
                    data.push(dataObj);
                });
                callback(data);
            }
        };
        
        HttpService.post(target, parameters).success(function (response) {
            var options = response.options;
            if (options) {
                var data = [];
                for (var i = 0; i < options.length; i ++) {
                    var option = options[i];
                    var dataObj = {};
                    dataObj[fieldName] = option[fieldName];
                    dataObj[descriptionFieldName] = option[descriptionFieldName];
                    data.push(dataObj);
                }
                
                $scope.select2Options.data = data;
                var select2 = $($element).select2($scope.select2Options);
                select2.select2("val", null);
                select2.select2("val", defaultValue);
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
