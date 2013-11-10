package org.ofbiz.angularjs.webapp.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.angularjs.servlet.http.JsonServletRequestWrapper;
import org.ofbiz.webapp.control.ConfigXMLReader.Event;
import org.ofbiz.webapp.control.ConfigXMLReader.RequestMap;
import org.ofbiz.webapp.event.EventHandlerException;
import org.ofbiz.webapp.event.SimpleEventHandler;

public class JsonSimpleEventHandler extends SimpleEventHandler {
    
    public final static String module = JsonSimpleEventHandler.class.getName();

    @Override
    public String invoke(Event event, RequestMap requestMap,
            HttpServletRequest request, HttpServletResponse response)
            throws EventHandlerException {
        JsonServletRequestWrapper jsonRequest = new JsonServletRequestWrapper(request);
        return super.invoke(event, requestMap, jsonRequest, response);
    }
}
