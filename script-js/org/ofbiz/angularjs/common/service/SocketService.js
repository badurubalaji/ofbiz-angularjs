package org.ofbiz.angularjs.common.service;

/**
 * Socket Service
 */
function SocketService($rootScope) {

    var eventScope = $rootScope.$new(true);
    
    var socket = new WebSocket("wss://localhost:8443/angularjs/controlws");
    
    socket.onopen = function(event) {
        
    }
    socket.onmessage = function(event) {
        eventScope.$emit("ON_SOCKET_MESSAGE", event.data);
    }
    socket.onclose = function(event) {
        
    }
    
    this.send = function(data) {
        socket.send(data);
    }
    
    this.addHandler = function(eventName, handler) {
        eventScope.$on(eventName, handler);
    }
}
