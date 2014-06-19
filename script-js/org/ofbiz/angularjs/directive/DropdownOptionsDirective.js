package org.ofbiz.angularjs.directive;

function DropdownOptionsDirective(HttpService, $rootScope, $http, ScopeUtil) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        var target = $attrs.target;
        var parameters = $scope.parameters;
        var fieldName = $attrs.fieldName;
        var descriptionFieldName = $attrs.descriptionFieldName;
        var placeholder = $attrs.placeholder;
        var defaultValue = null;
        
        if (!_.isEmpty($attrs.defaultValue)) { // There is default value;
            $scope.$watch("defaultValue", function(newValue) {
                if (newValue != null) {
                    defaultValue = newValue;
                }
                setup();
            });
        } else { // There is not default value.
            setup();
        }
        
        // set top scope property when every the value is changed
        $scope.$watch($attrs.ngModel, function(newValue, oldValue) {
            if (newValue != oldValue) {
                ScopeUtil.setTopScopeProperty($scope, $attrs.ngModel, newValue);
                $scope.ngModel = newValue;
            }
        });
        
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
                // See http://ivaynberg.github.io/select2/
                var option = element.val();
                var value = option[fieldName];
                if (!_.isEmpty(value)) {
                    callback(option);
                    $scope.ngModel = option;
                }
            }
        };
        
        function setup() {
            if (fieldName == null) {
                fieldName = "id";
            }
            if (descriptionFieldName == null) {
                descriptionFieldName = "text";
            }
            
            HttpService.post(target, parameters).success(function (response) {
                var defaultOption = null;
                var options = response.options;
                if (options) {
                    var data = [];
                    for (var i = 0; i < options.length; i ++) {
                        var option = options[i];
                        var dataObj = {};
                        dataObj[fieldName] = option[fieldName];
                        dataObj[descriptionFieldName] = option[descriptionFieldName];
                        data.push(dataObj);

                        if (option[fieldName] == defaultValue) {
                            defaultOption = option;
                        }
                    }
                    
                    $scope.select2Options.data = data;
                    var select2 = $($element).select2($scope.select2Options);
                    select2.select2("val", null);
                    if (defaultOption != null) {
                        select2.select2("val", defaultOption);
                    }
                }
            });
        }
    }

    /**
     * Link
     */
    this.link = function($scope, $element, $attrs, ngModel) {
        ngModel.$render = function() {
            var viewValue = ngModel.$viewValue;
            if (!_.isEmpty(viewValue)) {
                //console.log("render: " + JSON.stringify(viewValue));
            }
        }
    };
}
