package org.ofbiz.angularjs.directive;

/**
 * Dropdown Directive
 *
 * http://plnkr.co/edit/5Zaln7QT2gETVcGiMdoW?p=preview
 *
 * @param $compile
 * @param HttpService
 * @param $rootScope
 * @param $http
 * @param ScopeUtil
 */
function DropdownDirective($compile, FormService, $rootScope, $http, ScopeUtil) {

    /**
     * Link
     */
    this.link = function($scope, $element, $attrs) {
        var target = $attrs.target;
        var parameters = $scope.parameters;
        var dependentParameterNames = null;
        var fieldName = $attrs.fieldName;
        var descriptionFieldName = $attrs.descriptionFieldName;
        var placeholder = $attrs.placeholder;
        var defaultValue = null;

        if (!_.isEmpty($attrs.dependentParameterNames)) {
            dependentParameterNames = $attrs.dependentParameterNames.replace(" ", "").split(",");
        }

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
                    if (callback != null) {
                        try {
                            callback(option);
                        } catch (e) {
                            console.warn(e);
                        }
                    }
                    $scope.model = option;
                }
            }
        };

        function isValidDependency(validateParameters) {
            if (validateParameters != null) {
                var parameterNames = _.keys(validateParameters);
                if (dependentParameterNames != null) {
                    for (var i = 0; i < dependentParameterNames.length; i ++) {
                        var dependentParameterName = dependentParameterNames[i];
                        if (!_.contains(parameterNames, dependentParameterName)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        function setup() {
            if (fieldName == null) {
                fieldName = "id";
            }
            if (descriptionFieldName == null) {
                descriptionFieldName = "text";
            }

            if (isValidDependency(parameters)) {
                FormService.post(target, parameters).success(function (response) {
                    console.log("success");
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

                        select2.on("change", function(e) {
                            $scope.model = e.val;
                            ScopeUtil.setClosestScopeProperty($scope, $attrs.model, e.val);
                            $scope.$apply();
                        });

                        select2.select2("val", null);
                        if (defaultOption != null) {
                            select2.select2("val", defaultOption);
                        }
                    }
                }).error(function() {
                    var select2 = $($element).select2($scope.select2Options);
                });
            }
        }
    }
}
