package org.ofbiz.angularjs.servlet.http;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;

public class JsonServletRequestWrapper extends HttpServletRequestWrapper {
    
    public final static String module = JsonServletRequestWrapper.class.getName();
    
    private JSONObject jsonObject = null;

    public JsonServletRequestWrapper(HttpServletRequest request) {
        super(request);
        
        try {
            String jsonString = IOUtils.toString(request.getReader());
            jsonObject = (JSONObject) JSONSerializer.toJSON(jsonString);
        } catch (IOException e) {
            Debug.logError(e, module);
        }
    }
    
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        if (UtilValidate.isNotEmpty(jsonObject)) {
            Iterator<String> keyIter = UtilGenerics.cast(jsonObject.keys());
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                String value = jsonObject.getString(key);
                parameterMap.put(key, new String[] { value });
            }
        }
        return parameterMap;
    }
    
    @Override
    public String getParameter(String name) {
        String value = null;
        
        if (UtilValidate.isNotEmpty(jsonObject)) {
            Object obj = jsonObject.get(name);
            if (UtilValidate.isNotEmpty(obj)) {
                value = obj.toString();
            }
        }
        if (UtilValidate.isNotEmpty(value)) {
            return value;
        } else {
            return super.getParameter(name);
        }
    }
    
    @Override
    public Enumeration<String> getParameterNames() {
        Enumeration<String> originalParameterNames = super.getParameterNames();
        List<String> newParameterNames = Collections.list(originalParameterNames);
        if (UtilValidate.isNotEmpty(jsonObject)) {
            newParameterNames.addAll(jsonObject.keySet());
        }
        return Collections.enumeration(newParameterNames);
    }
    
    @Override
    public String[] getParameterValues(String name) {
        String value = null;
        if (UtilValidate.isNotEmpty(jsonObject)) {
            Object obj = (Object) jsonObject.get(name);
            if (UtilValidate.isNotEmpty(obj)) {
                value = obj.toString();
            }
        }
        if (UtilValidate.isNotEmpty(value)) {
            return new String[] { value };
        } else {
            return super.getParameterValues(name);
        }
    }

}
