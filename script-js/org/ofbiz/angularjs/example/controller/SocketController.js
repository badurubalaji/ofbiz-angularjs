package org.ofbiz.angularjs.example.controller;

/**
 * Socket Controller
 * 
 * @param $scope
 */
function SocketController($scope, SocketService) {
    $scope.$watch("name", function(newValue, oldValue) {
       if (newValue != null) {
           SocketService.send(newValue);
       }
    });
    SocketService.addHandler("ON_SOCKET_MESSAGE", function(event, message) {
        $scope.response = message;
        $scope.$apply();
    })
}
