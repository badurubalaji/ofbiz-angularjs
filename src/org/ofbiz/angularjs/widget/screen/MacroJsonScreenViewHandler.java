package org.ofbiz.angularjs.widget.screen;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.angularjs.servlet.http.JsonServletRequestWrapper;
import org.ofbiz.webapp.view.ViewHandlerException;
import org.ofbiz.widget.screen.MacroScreenViewHandler;

public class MacroJsonScreenViewHandler extends MacroScreenViewHandler {

    @Override
    public void render(String name, String page, String info,
            String contentType, String encoding, HttpServletRequest request,
            HttpServletResponse response) throws ViewHandlerException {
        JsonServletRequestWrapper jsonRequest = new JsonServletRequestWrapper(request);
        super.render(name, page, info, contentType, encoding, jsonRequest, response);
    }
}
