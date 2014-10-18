package org.ofbiz.angularjs.directive;

/**
 * Click Confirm Message Directive
 *
 */
function ClickConfirmMessageDirective() {

    var confirmMessage = null;
    var clickAction = null;

    this.compile = function compile(tElement, tAttrs, transclude) {
        confirmMessage = tAttrs.clickConfirmMessage || "Are you sure?";
        if (!_.isEmpty(tAttrs.ngClick)) {
            clickAction = tAttrs.ngClick;
            tAttrs.$set("ngClick", "");
        }

        return {
            pre: function preLink(scope, iElement, iAttrs, controller) {
            },
            post: function postLink(scope, iElement, iAttrs, controller) {
                iElement.bind('click', function(event) {
                    if (window.confirm(confirmMessage)) {
                        scope.$eval(clickAction);
                    }
                });
            }
        };
    };
}
