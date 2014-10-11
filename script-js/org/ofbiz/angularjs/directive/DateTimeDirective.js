package org.ofbiz.angularjs.directive;

/**
 * http://stackoverflow.com/questions/20212813/ui-bootstrap-datepicker-is-open-not-working-in-modal
 * @param $rootScope
 * @param $timeout
 * @param $compile
 * @param DateTimeUtil
 */
function DateTimeDirective($rootScope, $timeout, $compile, DateTimeUtil) {

    this.link = function($scope, $element, $attrs, ngModel) {

        var style = $attrs.style;
        var format = $attrs.format;
        var readOnly = $attrs.readOnly == "true";

        $scope.seconds = null;

        var divElement = null;

        var adeReadonly = 0;
        var adeEditableStyle = "";

        if (readOnly) {
            adeReadonly = 1;
        } else {
        	adeEditableStyle = "ade-editable"
        }

        var required = $element.attr("required");

        divElement = angular.element("<div name=\"" + $attrs.name + "\" class=\"" + adeEditableStyle + " " + style + "\" ade-date=\"" + format + "\" ade-class=\"input-large\" ng-model=\"seconds\" ade-readonly=\"" + adeReadonly + "\"></div>");

        if (required != null) {
            divElement.attr("required", required);
        }

        $element.removeAttr("name");
        $element.removeAttr("required");

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

                if ($scope.seconds != null && $scope.seconds.toString() != "0,0,0") {
                    ngModel.$setValidity("required", true);
                } else {
                    $scope.seconds = null;
                    ngModel.$setValidity("required", false);
                }
            } else {
                ngModel.$setValidity("required", false);
            }
        });

        $scope.$watch("seconds", function(newVal) {

            if (newVal != null) {

                if ($scope.seconds.toString() != "0,0,0") {
                    ngModel.$setValidity("required", true);
                } else {
                    $scope.seconds = null;
                    ngModel.$setValidity("required", false);
                }

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
