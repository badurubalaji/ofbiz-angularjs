package org.ofbiz.angularjs.directive;

function ReportDirective(HttpService) {

    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
    }

    this.link = function($scope, $element, $attrs) {

        function setContent() {
            var src = "/birt/output?__report=" + location + "&&__format=" + format;
            $element.html("<iframe src=\"" + src + "\" width=\"" + width + "\" height=\"" + height + "\"/>");
        }

      var format = $attrs.format;
      var location = $attrs.location;
      var width = $attrs.width;
      var height = $attrs.height;
      var parameters = $scope.parameters;

      if (_.isEmpty(format)) {
          format = "html";
      }

      $scope.$watch("parameters", function(newValue) {
          parameters = newValue;
          setContent()
      });

      setContent();
    }
}
