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
            var dateSeconds = newVal;

            // try to get current time
            /*
            var currentDate = new Date();
            var seconds = currentDate.getSeconds();
            var minutes = currentDate.getMinutes();
            var hour = currentDate.getHours();
            var timeSeconds = ((hour * 60 * 60) + (minutes * 60) + seconds);
            */

            var totalSeconds = dateSeconds;
            $scope.ngModel = DateTimeUtil.toTimestamp(totalSeconds);
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
