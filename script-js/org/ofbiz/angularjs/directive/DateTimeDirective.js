package org.ofbiz.angularjs.directive;

/**
 * http://stackoverflow.com/questions/20212813/ui-bootstrap-datepicker-is-open-not-working-in-modal
 * @param $rootScope
 * @param $timeout
 * @param $compile
 * @param DateTimeUtil
 */
function DateTimeDirective($rootScope, $timeout, $compile, DateTimeUtil) {

    this.link = function($scope, $element, $attrs, controller) {

        var style = $attrs.style;
        var format = $attrs.format;
        var readOnly = $attrs.readOnly == "true";

        $scope.seconds = null;

        var divElement = null;

        if (readOnly) {
            divElement = angular.element("<span>{{seconds | validDate:['" + format + "']}}</span>");
        } else {
          divElement = angular.element("<div class=\"ade-editable\" ade-date='{\"className\": \"" + style
              + "\", \"format\": \"" + format + "\", \"absolute\": true}' ng-model=\"seconds\">{{seconds | validDate:['" + format + "']}}</div>");
        }

        $element.html(divElement);

        $scope.$watch("ngModel", function(newVal) {
            if (newVal != null) {
                if (!$scope.isCallback) {
                    $scope.seconds = DateTimeUtil.getTime(newVal);
                    $timeout(function() {
                        $scope.$apply();
                    });
                } else {
                    $scope.isCallback = false;
                }
            }
        });

        $scope.$watch("seconds", function(newVal) {
            if (newVal != null) {
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
                        $timeout(function() {
                            $scope.$apply();
                        });
                    } catch (e) {
                        // ignore Error: [$rootScope:inprog]
                    }
                }
            }
        });

        $rootScope.$watch("ADE_hidePopup", function(newVal) {
            $scope.ADE_hidePopup = newVal;
        });

        $compile($element.contents())($scope);
    }
}
