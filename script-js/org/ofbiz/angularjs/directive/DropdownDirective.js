package org.ofbiz.angularjs.directive;

/**
 * Dropdown Directive
 *
 * http://stackoverflow.com/questions/26389542/clear-selected-option-in-ui-select-angular
 *
 * @param $compile
 * @param HttpService
 * @param $rootScope
 * @param $http
 * @param ScopeUtil
 */
function DropdownDirective($timeout, $compile, HttpService, FormService, ScopeUtil, $http) {

    /**
     * Link
     */
    this.link = function($scope, $element, $attrs, ngModel) {
        var target = $attrs.target;
        var parameters = $scope.parameters;
        var dependentParameterNames = null;
        var fieldName = $attrs.fieldName;
        var descriptionFieldName = $attrs.descriptionFieldName;
        var placeholder = $attrs.placeholder;
        var defaultValue = null;

        if (_.isEmpty(target)) {
            console.error("Target cannot be empty.");
        }

        if (!_.isEmpty($attrs.dependentParameterNames)) {
            dependentParameterNames = $attrs.dependentParameterNames.replace(" ", "").split(",");
        }

        if (_.isEmpty(parameters)) {
            parameters = {};
        }
        
        if (fieldName == null) {
            fieldName = "id";
        }
        if (descriptionFieldName == null) {
            descriptionFieldName = "text";
        }

        $scope.$watch("parameters", function(newVal) {
            if (newVal != null) {
                parameters = newVal;
                $scope.refreshOptions(null);
            }
        });

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

        // create directive
        var uiSelectElement = angular.element("<ui-select></ui-select>");
        uiSelectElement.attr("ng-model", "option.selected");
        uiSelectElement.attr("theme", "bootstrap");
        uiSelectElement.attr("style", "width: 300px;");
        uiSelectElement.attr("ng-disabled", "disabled");
        uiSelectElement.attr("reset-search-input", "false");
        uiSelectElement.attr("search-enabled", "false");

        var uiSelectMatch = angular.element("<ui-select-match></<ui-select-match>");
        uiSelectMatch.attr("placeholder", placeholder);
        uiSelectMatch.html("{{$select.selected." + descriptionFieldName + "}}");

        // https://github.com/prajwalkman/angular-slider/pull/29

        var uiSelectChoices = angular.element("<ui-select-choices></ui-select-choices>");
        uiSelectChoices.attr("repeat", "option in options track by $index");
        uiSelectChoices.append("<div ng-bind-html=\"option." + descriptionFieldName + " | highlight: $select.search\"></div>");

        uiSelectElement.append(uiSelectMatch);
        uiSelectElement.append(uiSelectChoices);
        $element.html(uiSelectElement);


        $scope.option = {};
        
        $scope.refreshOptions = function(term) {
            if (!_.isEmpty(term)) {
                parameters.term = term;
            }
            
            if (isValidDependency(parameters)) {
                if (!_.isEmpty(target)) {
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

                                if (option[fieldName] == defaultValue) {
                                    $scope.option.selected = option;
                                }
                            }
                        }
                        $scope.options = options;
                    });
                }
            }
        };

        // watch selected option
        $scope.$watch("option.selected", function(newVal) {

           if (newVal == null) {
               ngModel.$setValidity("required", false);
           } else {
               ngModel.$setValidity("required", true);
           }

           $scope.ngModel = newVal;
        });
        
        // set default value
        $scope.$watchCollection("[defaultValue, parameters]", function(newValues, oldValues) {
            defaultValue = newValues[0];
            var parametersParam = newValues[1];

            if (parametersParam == null) {
                 parametersParam = {};
            }
            if(!_.isEmpty(defaultValue)) {
                parameters = _.clone(parametersParam);
                parameters.viewSize = 10;
                $scope.refreshOptions(defaultValue);
            } else {
                $scope.refreshOptions(null);
            }
        });

        $compile($element.contents())($scope);
    }
}
