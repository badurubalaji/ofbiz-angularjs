package org.ofbiz.angularjs.directive;

function ReportDirective(HttpService) {

    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude, FormService) {

        function setContent() {

            var serveReportParams = _.clone(parameters);
            serveReportParams["__location"] = location;
            FormService.post("serveReport", serveReportParams).success(function(result) {
                $element.html(result);
            });
        }

      var format = $attrs.format;
      var location = $attrs.location;
      var width = $attrs.width;
      var height = $attrs.height;
      var parameters = $scope.parameters;

      if (_.isEmpty(parameters)) {
          parameters = {};
      }

      if (_.isEmpty(format)) {
          format = "html";
      }

      $scope.$watch("parameters", function(newValue) {
          if (!_.isEmpty(newValue)) {
              parameters = newValue;
              setContent()
          }
      });

      setContent();
    }

    /**
     * Compile
     */
    this.compile = function() {
        return {
            pre: function() {

            },
            post: function($scope, $element, $attrs, controller) {

            }
        };
    };
}
