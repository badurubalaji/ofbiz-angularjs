package org.ofbiz.angularjs.directive;

/**
 * http://stackoverflow.com/questions/20212813/ui-bootstrap-datepicker-is-open-not-working-in-modal
 * @param $rootScope
 * @param $timeout
 * @param $compile
 * @param DateTimeUtil
 */
function DateTimeDirective($rootScope, $timeout, $compile, $filter, ScopeUtil, DateTimeUtil) {

    this.link = function($scope, $element, $attrs, ngModel) {

        var style = $attrs.style;
        var format = $attrs.format;
        var readOnly = $attrs.readOnly == "true";
        var minDate = $attrs.minDate;
        var maxDate = $attrs.maxDate;
        var dateDisabled = $attrs.dateDisabled;
        var closeText = $attrs.closeText;

        $scope.internalModel = null;

        var required = $element.attr("required");

        var inputElement = angular.element("<input type=\"text\" name=\"" + $attrs.name + "\" class=\"form-control " + style + "\" datepicker-popup=\"{{'" + format + "'}}\" ng-model=\"internalModel\""
                + " is-open=\"opened\" min-date=\"" + minDate + "\" max-date=\"" + maxDate + "\" datepicker-options=\"dateOptions\""
                + " date-disabled=\"" + dateDisabled + "\" close-text=\"" + closeText + "\"/>");

        var spanElement = angular.element("<span class=\"input-group-btn\">"
                + "<button type=\"button\" class=\"btn btn-default\" ng-click=\"open($event)\"><i class=\"glyphicon glyphicon-calendar\"></i></button>"
                + "</span>");

        var pElement = angular.element("<p class=\"input-group\"></p>");

        if (readOnly) {
            inputElement.attr("ng-disabled", "true");
            inputElement.attr("readonly", "true");
            pElement.append(inputElement);

            //TODO I don't know why we need this.
            pElement.css("width", function(index) {
                return "100%";
            });
        } else {
            pElement.append(inputElement);
            pElement.append(spanElement);
        }

        if (required != null) {
            inputElement.attr("ng-required", true);
        }

        $element.removeAttr("name");
        $element.removeAttr("required");

        $element.html(pElement);

        $scope.$watch("ngModel", function(newVal) {
            if (newVal != null) {

                if (!$scope.isCallback) {
                    var date = null;
                    if (_.isString(newVal)) {
                        date = new Date(newVal);
                    } else {
                        date = newVal;
                    }

                    $scope.internalModel = $filter("date")(date, format);;
                    $timeout(function() {
                        $scope.$apply();
                    });
                } else {
                    $scope.isCallback = false;
                }

                ngModel.$setValidity("required", true);
            } else {
                ngModel.$setValidity("required", false);
            }
        });

        $scope.$watch("internalModel", function(newVal) {
            var timestamp = $filter("date")(newVal, "yyyy-MM-dd HH:mm:ss.sss");
            $scope.isCallback = true;
            ScopeUtil.setClosestScopeProperty($scope, $attrs.ngModel, timestamp);
        });

        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.opened = true;
          };

        $scope.dateOptions = {
            formatYear: 'yy',
            startingDay: 1
        };

        $compile($element.contents())($scope);
    }
}
