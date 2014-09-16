package org.ofbiz.angularjs.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;

public class CommonEvents {

    private static class DateJsonValueProcessor implements JsonValueProcessor {

        private String format;

        public DateJsonValueProcessor(String format) {
            this.format = format;
        }

        public Object processArrayValue(Object value, JsonConfig jsonConfig) {
            return null;
        }

        public Object processObjectValue(String key, Object value,
                JsonConfig jsonConfig) {
            if (value == null) {
                return "";
            }
            if (value instanceof java.sql.Timestamp) {
                String str = new SimpleDateFormat(format)
                        .format((java.sql.Timestamp) value);
                return str;
            }
            if (value instanceof java.util.Date) {
                String str = new SimpleDateFormat(format)
                        .format((java.util.Date) value);
                return str;
            }

            return value.toString();
        }
    }

    public static final String module = CommonEvents.class.getName();

    private static final String[] ignoreAttrs = new String[] { // Attributes removed for security reason; _ERROR_MESSAGE_ is kept
    "javax.servlet.request.key_size", "_CONTEXT_ROOT_",
            "_FORWARDED_FROM_SERVLET_", "javax.servlet.request.ssl_session",
            "javax.servlet.request.ssl_session_id", "multiPartMap",
            "javax.servlet.request.cipher_suite", "targetRequestUri",
            "_SERVER_ROOT_URL_", "_CONTROL_PATH_", "thisRequestUri" };

    private static JsonConfig jsonConfig = new JsonConfig();

    static {
        jsonConfig.registerJsonValueProcessor(Timestamp.class,
                new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss.SSS"));
        jsonConfig.registerJsonValueProcessor(Date.class,
                new DateJsonValueProcessor("yyyy-MM-dd"));
    }

    public static String jsonResponseFromRequestAttributes(
            HttpServletRequest request, HttpServletResponse response) {
        // pull out the service response from the request attribute

        Map<String, Object> attrMap = UtilHttp.getJSONAttributeMap(request);

        for (String ignoreAttr : ignoreAttrs) {
            if (attrMap.containsKey(ignoreAttr)) {
                attrMap.remove(ignoreAttr);
            }
        }

        // create a JSON Object for return
        JSONObject json = JSONObject.fromObject(attrMap, jsonConfig);
        writeJSONtoResponse(json, request.getMethod(), response);

        return "success";
    }

    private static void writeJSONtoResponse(JSON json, String httpMethod,
            HttpServletResponse response) {
        String jsonStr = json.toString();
        if (jsonStr == null) {
            Debug.logError("JSON Object was empty; fatal error!", module);
            return;
        }

        // This was added for security reason (OFBIZ-5409), you might need to
        // remove the "//" prefix when handling the JSON response
        // Though normally you simply have to access the data you want, so
        // should not be annoyed by the "//" prefix
        if ("GET".equalsIgnoreCase(httpMethod)) {
            Debug.logWarning(
                    "for security reason (OFBIZ-5409) the the '//' prefix was added handling the JSON response.  "
                            + "Normally you simply have to access the data you want, so should not be annoyed by the '//' prefix."
                            + "You might need to remove it if you use Ajax GET responses (not recommended)."
                            + "In case, the util.js scrpt is there to help you",
                    module);
            jsonStr = "//" + jsonStr;
        }

        // set the X-JSON content type
        response.setContentType("application/x-json");
        // jsonStr.length is not reliable for unicode characters
        try {
            response.setContentLength(jsonStr.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with Json encoding: " + e, module);
        }

        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, module);
        }
    }
}
