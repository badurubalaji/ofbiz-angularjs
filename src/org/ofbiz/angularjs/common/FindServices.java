package org.ofbiz.angularjs.common;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class FindServices {

    public static Map<String, Object> performFindJsonList(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        HttpServletRequest request = (HttpServletRequest) context.remove("request");
        String listName = (String) context.remove("listName");
        try {
            Map<String, Object> performFindListResults = dispatcher.runSync("performFindList", context);
            List<? extends Object> list = UtilGenerics.cast(performFindListResults.get("list"));
            int listSize = (Integer) performFindListResults.get("listSize");
            request.setAttribute(listName, list);
            request.setAttribute("listSize", listSize);
            request.setAttribute("viewIndex", context.get("viewIndex"));
            request.setAttribute("viewSize", context.get("viewSize"));
            return performFindListResults;
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError("Could not find list: " + listName + " because: " + e.getMessage());
        }
    }
}
