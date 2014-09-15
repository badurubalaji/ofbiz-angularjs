package org.ofbiz.angularjs.directive;

function DateTimeDirective($rootScope, $compile, DateTimeUtil) {

    this.link = function($scope, $element, $attrs, controller) {

        var style = $attrs.style;
        var format = $attrs.format;

        $scope.seconds = 0;

        var divElement = angular.element("<div class=\"ade-editable\" ade-date='{\"className\": \"" + style
                + "\", \"format\": \"" + format + "\", \"absolute\": true}' ng-model=\"seconds\">{{seconds | validDate:['" + format + "']}}</div>");
        $element.html(divElement);

        $scope.$watch("ngModel", function(newVal) {
            if (!$scope.isCallback) {
                $scope.seconds = DateTimeUtil.getTime(newVal);
            } else {
                $scope.isCallback = false;
            }
        });

        $scope.$watch("seconds", function(newVal) {
            $scope.isCallback = true;
            $scope.ngModel = DateTimeUtil.toTimestamp(newVal);
            if(!$scope.$$phase) {
                try {
                    $scope.$apply();
                } catch (e) {
                    // ignore Error: [$rootScope:inprog]
                }
            }
        });

        $rootScope.$watch("ADE_hidePopup", function(newVal) {
            $scope.ADE_hidePopup = newVal;
        });

        $compile($element.contents())($scope);
    }
}
