package org.ofbiz.angularjs.directive;

/**
 * Google Chart Options Directive
 */
function GoogleChartOptionsDirective() {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude, $http, appBusy) {
        var model = $attrs.chart;
        var style = $attrs.style;
        var type = $attrs.type;
        var displayed = $attrs.displayed;
        var title = $attrs.title;
        var isStacked = $attrs.isStacked;
        var fill = $attrs.fill;
        var displayExactValues = $attrs.displayExactValues;
        var vTitle = $attrs.vTitle;
        var hTitle = $attrs.hTitle;
        var vGridLinesCount = $attrs.vGridLinesCount;
        var target = $attrs.target;
        
        var data = {
            "cols": [
              {
                "id": "month",
                "label": "Month",
                "type": "string"
              },
              {
                "id": "laptop-id",
                "label": "Laptop",
                "type": "number"
              },
              {
                "id": "desktop-id",
                "label": "Desktop",
                "type": "number"
              },
              {
                "id": "server-id",
                "label": "Server",
                "type": "number"
              }
            ],
            "rows": [
              {
                "c": [
                  {
                    "v": "January"
                  },
                  {
                    "v": 19,
                    "f": "42 items"
                  },
                  {
                    "v": 12,
                    "f": "Ony 12 items"
                  },
                  {
                    "v": 7,
                    "f": "7 servers"
                  }
                ]
              },
              {
                "c": [
                  {
                    "v": "February"
                  },
                  {
                    "v": 13
                  },
                  {
                    "v": 1,
                    "f": "1 unit (Out of stock this month)"
                  },
                  {
                    "v": 14
                  }
                ]
              },
              {
                "c": [
                  {
                    "v": "March"
                  },
                  {
                    "v": 24
                  },
                  {
                    "v": 5
                  },
                  {
                    "v": 11
                  }
                ]
              }
            ]
          }
        
        $scope[model] = {
          "type": type,
          "displayed": (displayed == "true"),
          "cssStyle": style,
          "data": data,
          "options": {
            "title": title,
            "isStacked": isStacked,
            "fill": parseInt(fill),
            "displayExactValues": (displayExactValues == "true"),
            "vAxis": {
              "title": vTitle,
              "gridlines": {
                "count": parseInt(vGridLinesCount)
              }
            },
            "hAxis": {
              "title": hTitle
            }
          }
        }
    }
}
