package org.ofbiz.angularjs.directive;

/**
 * Modal Directive
 */
function ModalDirective(HttpService, $compile, $modal, $templateCache, $log) {

    /**
     * Link
     */
    this.link = function($scope, $element, $attrs) {

        var templateElement = $element.find("script")[0];
        var templateContents = angular.element(templateElement).html();

        $scope.$watch("shouldBeOpen", function(newVal) {
            if (newVal == true) {
                var modalInstance = $modal.open({
                      template: templateContents,
                      controller: function ($scope, $modalInstance, items) {
                          $scope.$parent.$watch("shouldBeOpen", function(shouldBeOpen, oldShouldBeOpen) {

                                  if (shouldBeOpen == false) {
                                    $modalInstance.close();
                                  }

                                  $scope.close = function () {
                                      $modalInstance.close();
                                      $scope.$parent.shouldBeOpen = false;
                                      $scope.$parent.$apply();
                                  };
                          });
                      },
                        /*
                      size: size,
                      */
                      resolve: {
                        items: function () {
                          return $scope.items;
                        }
                      }
                    });

                    modalInstance.result.then(function (selectedItem) {
                      $scope.selected = selectedItem;
                    }, function () {
                      $log.info('Modal dismissed at: ' + new Date());
                    });
            } else {
                $scope.shouldBeOpen = false;
            }
        });
    }
}
