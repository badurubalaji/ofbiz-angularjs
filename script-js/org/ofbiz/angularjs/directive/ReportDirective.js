package org.ofbiz.angularjs.directive;

function ReportDirective(HttpService) {

    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {

        function setContent() {
            var parametersStr = "";
            var paramKeys = _.keys(parameters);
            _.each(paramKeys, function(paramName) {
                var paramValue = parameters[paramName];
                parametersStr += paramName + "_EQS_" + paramValue;
                parametersStr += ",";
            });


            if (format == "png" || format == "jpg") {
                var src = "serveReport" + "?__location=" + location + "&__parameters=" + parametersStr;
                $element.html("<iframe src=\"" + src + "\" width=\"" + width + "\" height=\"" + height + "\"/>");
            } else if (format == "html" || format == "pdf") {
                var src = "/birt/output?__report=" + location + "&&__format=" + format;
                $element.html("<iframe src=\"" + src + "\" width=\"" + width + "\" height=\"" + height + "\"/>");
            } else {
                 console.log("Unsupported fomat found: " + format);
            }
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
          if (_.isEmpty(newValue)) {
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
