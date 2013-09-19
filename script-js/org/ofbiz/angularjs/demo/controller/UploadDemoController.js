package org.ofbiz.angularjs.demo.controller;

/**
 * Upload Demo Controller
 * @param $scope
 */
function UploadDemoController($scope) {
    $scope.uploadComplete = function (content, completed) {
        if (completed && content.length > 0) {
          $scope.response = JSON.parse(content); // Presumed content is a json string!
          $scope.response.style = {
            color: $scope.response.color,
            "font-weight": "bold"
          };
    
          // Clear form (reason for using the 'ng-model' directive on the input elements)
          $scope.fullname = '';
          $scope.gender = '';
          $scope.color = '';
          // Look for way to clear the input[type=file] element
        }
    };
}
