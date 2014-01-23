package org.ofbiz.angularjs.example.controller;

/**
 * Socket Controller
 * 
 * @param $scope
 */
function SocketController($scope, SocketService) {
    var socket = SocketService.newSocket("wss://localhost:8443/angularjs/controlws");
    $scope.$watch("name", function(name) {
       if (name != null) {
           socket.send(name);
       }
    });
    SocketService.addHandler("ON_SOCKET_MESSAGE", function(event, message) {
        console.log("on socket message: " + message);
    })
}
