package org.ofbiz.angularjs.directive;

/**
 * Text Area Wrapper Directive
 *
 */
function TextareaWrapperDirective($compile, $timeout) {

    this.link = function($scope, $element, $attrs) {
        var style = $attrs.style;
        var placeholder = $attrs.placeholder;
        var visualEditorEnable = $attrs.visualEditorEnable == "true";

        var textareaElement = angular.element("<textarea class='form-control" + style + "' ng-model='textareaModel'></textarea>");

        var tinyInstance = null;

        $scope.textareaModel = null;

        $scope.$parent.$watch($attrs.ngModel, function(newVal, oldVal) {
            if (newVal != oldVal) {
                $scope.textareaModel = newVal;
                $timeout(function() {
                    $scope.$apply();
                    if (tinyInstance != null) {
                        var bookmark = tinyInstance.selection.getBookmark(2, true);
                        tinyInstance.setContent($scope.ngModel || '');
                        tinyInstance.selection.moveToBookmark(bookmark);
                    }
                });
            }
        });

        $scope.$watch("textareaModel", function(newVal, oldVal) {
            if (newVal != oldVal) {
                $scope.ngModel = newVal;
                $timeout(function() {
                    $scope.$apply();
                });
            }
        });

        if (placeholder != null) {
            textareaElement.attr("placeholder", placeholder);
        }
        if (visualEditorEnable) {
            $scope.tinymceOptions = {
                init_instance_callback : function(editor) {
                    tinyInstance = tinymce.get(editor.id);
                }
            }
            textareaElement.attr("ui-tinymce", "tinymceOptions");
        }
        $element.html(textareaElement);
        $compile($element.contents())($scope);
    };
}
