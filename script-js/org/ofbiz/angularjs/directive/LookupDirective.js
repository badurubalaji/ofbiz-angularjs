package org.ofbiz.angularjs.directive;

/**
 * Lookup Directive
 * http://plnkr.co/edit/AT2RhpE4Qhj4iN5qMtdi?p=preview
 * Fetch data from sercer: http://plnkr.co/edit/t1neIS?p=preview
 * JSONP: http://docs.angularjs.org/api/ng.$http#methods_jsonp
 */
function LookupDirective(HttpService, FormService, ScopeUtil) {

    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        var target = $attrs.target;
        var parameters = $scope.parameters;
        var fieldName = $attrs.fieldName;
        var descriptionFieldName = $attrs.descriptionFieldName;
        var placeholder = $attrs.placeholder;

        if (_.isEmpty(parameters)) {
            parameters = {};
        }

        $scope.$watch("parameters", function(newVal) {
            if (newVal != null) {
                loadOptions(newVal, null);
            }
        });

        function loadOptions(httpParams, defaultValue) {
            HttpService.post(target, httpParams).success(function (response) {
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

        $scope.select2Options = {
            placeholder: placeholder,
            allowClear: true,
            minimumInputLength: 1,
            ajax: {
                url: target,
                dataType: "json",
                type: "POST",
                data: function(term, page) {
                    var data = _.clone(parameters);
                    data.term = term;
                    data.viewSize = 10;
                    return data;
                },
                results: function(data, page) {
                    var options = null;
                    if (data.options != null) {
                        options = data.options;
                    } else {
                        options = [];
                    }
                    return {results: options};
                }
            },
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
                    $scope.ngModel = option;
                }
            }
        };

        if (fieldName == null) {
            fieldName = "id";
        }
        if (descriptionFieldName == null) {
            descriptionFieldName = "text";
        }

        // set default value
        $scope.$watch("defaultValue", function(defaultValue) {
            if(!_.isEmpty(defaultValue)) {
                var httpParams = _.clone(parameters);
                httpParams.term = defaultValue;
                httpParams.viewSize = 10;
                loadOptions(httpParams, defaultValue);
            }
        });

        $($element).on("change", function(e) {
            //$scope.ngModel = e.val;
            ScopeUtil.setTopScopeProperty($scope, $attrs.ngModel, e.val);
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
