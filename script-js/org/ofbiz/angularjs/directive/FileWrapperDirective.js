package org.ofbiz.angularjs.directive;

/**
 * File Options Directive
 */
function FileWrapperDirective(HttpService, $parse, $compile, ScopeUtil) {

    /**
     * Link
     */
    this.link = function($scope, $element, $attrs) {

        $scope.onFileSelect = function($event, $files) {
            ScopeUtil.setClosestScopeProperty($scope, $attrs.ngModel, $files);
        }

        var inputElement = angular.element("<input type='file' ng-file-select='onFileSelect($event, $files)'/>");
        $element.html(inputElement);
        $compile($element.contents())($scope);
    }
}
