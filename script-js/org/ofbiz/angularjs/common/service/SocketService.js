package org.ofbiz.angularjs.common.service;

/**
 * Socket Service
 */
function SocketService() {
    
    this.newSocket = function(url, protocal) {
        return new WebSocket(url, [protocal] );
    }
}
