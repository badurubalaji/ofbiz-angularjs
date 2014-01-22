package org.ofbiz.angularjs.example.controller;

/**
 * Socket Controller
 * 
 * @param $scope
 */
function SocketController($scope, SocketService) {
    var socket = SocketService.newSocket("ws://controlws/endpoint", "eeee");
}
