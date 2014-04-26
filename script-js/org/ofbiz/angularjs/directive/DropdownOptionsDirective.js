package org.ofbiz.angularjs.directive;

function DropdownOptionsDirective(HttpService, $rootScope, $http) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        var target = $attrs.target;
        var parameters = $scope.$eval($attrs.parameters);
        var fieldName = $attrs.fieldName;
        var descriptionFieldName = $attrs.descriptionFieldName;
        var placeholder = $attrs.placeholder;
        var ngModel = $attrs.ngModel;
        var defaultValue = null;
        
        if (!_.isEmpty($attrs.defaultValue)) { // There is default value;
            $scope.$watch($attrs.defaultValue, function(newValue) {
                if (newValue != null) {
                    defaultValue = newValue;
                } else {
                    defaultValue = $attrs.defaultValue;
                }
                setup();
            });
        } else { // There is not default value.
            setup();
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
                var option = element.val();
                var value = option[fieldName];
                if (!_.isEmpty(value)) {
                    callback(option);
                    $scope[ngModel] = option;
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
                var defaultDescription = null;
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
                            defaultDescription = option[descriptionFieldName];
                        }
                    }
                    
                    $scope.select2Options.data = data;
                    var select2 = $($element).select2($scope.select2Options);
                    select2.select2("val", null);
                    var dataObj = {};
                    dataObj[fieldName] = defaultValue;
                    dataObj[descriptionFieldName] = defaultDescription;
                    select2.select2("val", dataObj);
                }
            });
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
