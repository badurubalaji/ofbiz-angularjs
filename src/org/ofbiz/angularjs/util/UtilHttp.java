package org.ofbiz.angularjs.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class UtilHttp {
    
    public final static String module = UtilHttp.class.getName();
    
    public final static List<Map<String, String>> getMultiFormRowItems(
            HttpServletRequest request) {
        List<Map<String, String>> rowItems = new LinkedList<Map<String, String>>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        int rowCount = org.ofbiz.base.util.UtilHttp
                .getMultiFormRowCount(request);
        Set<String> keySet = parameterMap.keySet();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            String curSuffix = org.ofbiz.base.util.UtilHttp.MULTI_ROW_DELIMITER
                    + rowIndex;
            Map<String, String> rowItem = new HashMap<String, String>();
            for (String key : keySet) {
                if (key.endsWith(curSuffix)) {
                    String paramName = key.substring(0, key.indexOf(curSuffix));
                    String paramValue = parameterMap.get(key)[0];
                    rowItem.put(paramName, paramValue);
                }
            }
            rowItems.add(rowItem);
        }
        return rowItems;
    }
}
