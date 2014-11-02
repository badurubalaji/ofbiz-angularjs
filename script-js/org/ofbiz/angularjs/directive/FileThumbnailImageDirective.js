package org.ofbiz.angularjs.directive;

function FileThumbnailImageDirective($compile, FormService, $rootScope, $http, ScopeUtil) {

    this.link = function($scope, $element, $attrs) {

        var width = $scope.width;
        var height = $scope.height;

        if (width == null) {
            width = "300px";
        }

        if (height == null) {
            height = "300px";
        }

        $scope.$watch("dataResourceId", function(newVal) {
            if (newVal != null) {
                var dataResourceId = newVal;
                $element.html("<a href=\"/content/control/ViewBinaryDataResource?dataResourceId=" + dataResourceId + "\""
                    + " target=\"_blank\">"
                    + "<p style=\""
                    + "width: " + width + ";"
                    + "height: " + height + ";"
                    + "background-image: url('serveFileThumbnailImage?dataResourceId=" + dataResourceId + "');"
                    + "background-size: contain;"
                    + "background-repeat: no-repeat;"
                    + "background-position: 50% 50%;"
                    + "\"></p>"
                    + "</a>");
            }
        });
    }
}
