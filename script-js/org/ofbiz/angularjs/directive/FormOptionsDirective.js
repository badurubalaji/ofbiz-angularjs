package org.ofbiz.angularjs.directive;

/**
 * Form Options Directive
 *
 */
function FormOptionsDirective($http, appBusy, $parse, FormService, NotificationService) {

    /**
     * Link
     */
    this.link = function($scope, $element, $attrs) {

        var name = $element.attr("name");
        $element.bind("submit", function() {
            var form = $scope[name];
            if (form.$valid) {
                $scope.$eval($attrs.onSubmit);
            } else {
                form.submitted = true;
                $scope.$apply();
                NotificationService.showToast("Please verify the form.");
            }
        });
    }
}
