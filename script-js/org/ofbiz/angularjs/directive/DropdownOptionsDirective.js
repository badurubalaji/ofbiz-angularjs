package org.ofbiz.angularjs.directive;

function DropdownOptionsDirective(HttpService, $rootScope) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude, $http) {
        var target = $attrs.target;
        var fieldName = $attrs.fieldName;
        var descriptionFieldName = $attrs.descriptionFieldName;
        var placeholder = $attrs.placeholder;
        var ngModel = $attrs.ngModel;
        
        var parameters = {};
        $http.post(target, parameters).then(function(response) {
            $($element).select2({
                placeholder: "Select a State",
                allowClear: true,
                data:[{id:0,text:'enhancement'},{id:1,text:'bug'},{id:2,text:'duplicate'},{id:3,text:'invalid'},{id:4,text:'wontfix'}]
            }).on("change", function(event) {
                var data = $($element).select2("data");
            });
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
