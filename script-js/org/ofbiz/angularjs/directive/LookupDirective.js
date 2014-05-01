package org.ofbiz.angularjs.directive;

/**
 * Lookup Directive
 * http://plnkr.co/edit/AT2RhpE4Qhj4iN5qMtdi?p=preview
 * Fetch data from sercer: http://plnkr.co/edit/t1neIS?p=preview
 * JSONP: http://docs.angularjs.org/api/ng.$http#methods_jsonp
 */
function LookupDirective(HttpService, FormService) {
    
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
        
        if (_.isEmpty(parameters)) {
            parameters = {};
        }
        
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
            minimumInputLength: 1,
            ajax: {
                url: target,
                dataType: "json",
                type: "POST",
                data: function(term, page) {
                    parameters.term = term;
                    parameters.viewSize = 10;
                    return parameters;
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
            
            // set default value
            if(!_.isEmpty(defaultValue)) {
                parameters.term = defaultValue;
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
