package org.ofbiz.angularjs.directive;

/**
 * File Options Directive
 */
function FileWrapperDirective(HttpService, $parse, $compile, ScopeUtil) {

    /**
     * Link
     */
    this.link = function($scope, $element, $attrs, ngModel) {

        var required = $element.attr("required");

        $scope.onFileSelect = function($event, $files) {
            ScopeUtil.setClosestScopeProperty($scope, $attrs.ngModel, $files);
            ngModel.$setValidity("required", true);
        }

        var inputElement = angular.element("<input type='file' ng-file-select='onFileSelect($event, $files)'/>");
        inputElement.attr("name", $attrs.name);

        if (required != null) {
            inputElement.attr("required", required);
            ngModel.$setValidity("required", false);
        }
        $element.removeAttr("required");

        $element.html(inputElement);
        $compile($element.contents())($scope);
    }
}
