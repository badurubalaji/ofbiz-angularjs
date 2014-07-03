package org.ofbiz.angularjs.demo.controller;

/**
 * Upload Demo Controller
 * @param $scope
 */
function UploadDemoController($scope, FormService) {
    $scope.onUploadSuccess = function (data) {
        $scope.dataResourceId = data.dataResourceId;
    };

    $scope.$watch("files", function(files) {
        console.log("change: " + files);
    });

    $scope.upload = function() {
        FormService.upload("upload"
            , $scope.files
            , {fullname: $scope.fullname})
            .progress(function(percent) {
                console.log("percent: " + percent);
            })
            .success(function(data) {
                _.each(data.fields, function(field) {
                    var file = field.file;
                    if (file != null) {
                        console.log("file uploaded: " + file);
                        $scope.dataResourceId = file.dataResourceId;
                    }
                });
            });
    }
}
