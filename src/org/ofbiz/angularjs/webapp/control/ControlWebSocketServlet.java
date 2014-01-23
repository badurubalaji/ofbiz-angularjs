package org.ofbiz.angularjs.webapp.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

public class ControlWebSocketServlet extends WebSocketServlet {
	
	public final static String module = ControlWebSocketServlet.class.getName();

    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol,
            HttpServletRequest request) {
        StreamInbound streamInbound = new RequestMessageInbound();
        StreamInboundManager.addStreamInbound(streamInbound);
        return streamInbound;
    }
}
