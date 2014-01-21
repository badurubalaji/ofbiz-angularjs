package org.ofbiz.angularjs.webapp.control;

import java.util.LinkedList;
import java.util.List;

import org.apache.catalina.websocket.StreamInbound;

public class StreamInboundManager {
    
    private static List<StreamInbound> streamInbounds = new LinkedList<StreamInbound>();

    public static void addStreamInbound(StreamInbound streamInbound) {
        streamInbounds.add(streamInbound);
    }
    
    public static List<StreamInbound> getStreamInbounds() {
        return streamInbounds;
    }
}
