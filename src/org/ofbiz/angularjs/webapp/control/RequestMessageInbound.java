package org.ofbiz.angularjs.webapp.control;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;

public class RequestMessageInbound extends MessageInbound {
	
	public final static String module = RequestMessageInbound.class.getName();

    @Override
    protected void onBinaryMessage(ByteBuffer message) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onTextMessage(CharBuffer message) throws IOException {
        for (StreamInbound streamInbound : StreamInboundManager.getStreamInbounds()) {
            try {
                CharBuffer buffer = CharBuffer. wrap(message);
                streamInbound.getWsOutbound().writeTextMessage(buffer);
            } catch (IOException e) {
            // Ignore
            }
        }
    }

}
