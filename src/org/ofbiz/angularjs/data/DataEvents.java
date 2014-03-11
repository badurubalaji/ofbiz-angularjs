package org.ofbiz.angularjs.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;

public class DataEvents {
    
    public final static String module = DataEvents.class.getName();
    
    private static int size(InputStream stream) {
        int length = 0;
        try {
            byte[] buffer = new byte[2048];
            int size;
            while ((size = stream.read(buffer)) != -1) {
                length += size;
                // for (int i = 0; i < size; i++) {
                // System.out.print((char) buffer[i]);
                // }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return length;
        
    }
    
    private static String read(InputStream stream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
        
    }
    
    public static String upload(HttpServletRequest request,
            HttpServletResponse response) {
        try {
            StringBuilder sb = new StringBuilder("{\"result\": [");
            
            if (request.getHeader("Content-Type") != null
                    && request.getHeader("Content-Type").startsWith(
                            "multipart/form-data")) {
                ServletFileUpload upload = new ServletFileUpload();
                
                FileItemIterator iterator = upload.getItemIterator(request);
                
                while (iterator.hasNext()) {
                    sb.append("{");
                    FileItemStream item = iterator.next();
                    sb.append("\"fieldName\":\"").append(item.getFieldName())
                            .append("\",");
                    if (item.getName() != null) {
                        sb.append("\"name\":\"").append(item.getName())
                                .append("\",");
                    }
                    if (item.getName() != null) {
                        sb.append("\"size\":\"")
                                .append(size(item.openStream())).append("\"");
                    } else {
                        sb.append("\"value\":\"")
                                .append(read(item.openStream())).append("\"");
                    }
                    sb.append("}");
                    if (iterator.hasNext()) {
                        sb.append(",");
                    }
                }
            } else {
                sb.append("{\"size\":\"" + size(request.getInputStream())
                        + "\"}");
            }
            
            sb.append("]");
            sb.append(", \"requestHeaders\": {");
            
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                sb.append("\"").append(header).append("\":\"")
                        .append(request.getHeader(header)).append("\"");
                if (headerNames.hasMoreElements()) {
                    sb.append(",");
                }
            }
            sb.append("}}");
            
            response.getWriter().write(sb.toString());
        } catch (Exception ex) {
            String errMsg = "Could not upload file: " + ex.getMessage();
            Debug.logError(errMsg, module);
            return "error";
        }
        return "success";
    }
    
}
