package org.ofbiz.angularjs.common.service;

/**
 * Socket Service
 */
function SocketService($rootScope) {

    var eventScope = $rootScope.$new(true);
    
    this.newSocket = function(url) {
        var socket = new WebSocket(url);
        socket.onopen = function(event) {
            
        }
        socket.onmessage = function(event) {
            eventScope.$emit("ON_SOCKET_MESSAGE", event.data);
        }
        socket.onclose = function(event) {
            
        }
        return socket;
    }
    
    this.addHandler = function(eventName, handler) {
        eventScope.$on(eventName, handler);
    }
}
