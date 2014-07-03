package org.ofbiz.angularjs.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.DataResourceWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

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
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request
                .getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        
        GenericValue userLogin = (GenericValue) session
                .getAttribute("userLogin");
        
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        
        try {
            StringBuilder sb = new StringBuilder("{\"fields\": [");
            if (request.getHeader("Content-Type") != null
                    && request.getHeader("Content-Type").startsWith(
                            "multipart/form-data")) {
                ServletFileUpload upload = new ServletFileUpload(
                        new DiskFileItemFactory(10240,
                                FileUtil.getFile("runtime/tmp")));
                List<FileItem> fileItems = null;
                Locale locale = UtilHttp.getLocale(request);
                
                try {
                    fileItems = UtilGenerics.checkList(upload
                            .parseRequest(request));
                } catch (FileUploadException e) {
                    request.setAttribute("_ERROR_MESSAGE_", e.toString());
                    return "error";
                }
                
                if (fileItems.size() == 0) {
                    String errMsg = UtilProperties.getMessage(
                            DataResourceWorker.err_resource,
                            "dataResourceWorker.no_files_uploaded", locale);
                    request.setAttribute("_ERROR_MESSAGE_", errMsg);
                    Debug.logWarning(
                            "[DataEvents.uploadImage] No files uploaded",
                            module);
                    return "error";
                }
                
                for (FileItem item : fileItems) {
                    String fieldName = item.getFieldName();
                    String fileName = item.getName();
                    InputStream inputStream = item.getInputStream();
                    
                    if (UtilValidate.isNotEmpty(fileName)) {
                        fieldMap.put(fieldName, item);
                    } else {
                        String value = read(inputStream);
                        fieldMap.put(fieldName, value);
                    }
                }
                
                int fieldCount = fieldMap.size();
                int fieldIndex = 0;
                for (String fieldName : fieldMap.keySet()) {
                    Object fieldValue = fieldMap.get(fieldName);
                    
                    sb.append("{");
                    sb.append("\"fieldName\":\"").append(fieldName)
                            .append("\",");
                    sb.append(" \"value\":\"").append(fieldValue).append("\"");
                    
                    if (fieldValue instanceof FileItem) {
                        FileItem fileItem = (FileItem) fieldValue;
                        String fileName = fileItem.getName();
                        InputStream inputStream = fileItem.getInputStream();
                        
                        byte[] fileBytes = fileItem.get();
                        if (fileBytes != null && fileBytes.length > 0) {
                            String mimeType = DataResourceWorker
                                    .getMimeTypeFromImageFileName(fileName);
                            if (UtilValidate.isNotEmpty(mimeType)) {
                                
                                sb.append(", \"file\":{");
                                
                                sb.append("\"size\":\"")
                                        .append(size(inputStream)).append("\"");
                                
                                // IMAGE_OBJECT type would cause this error:
                                // java.lang.ClassCastException:
                                // java.nio.HeapByteBuffer cannot be cast to
                                // java.sql.Blob
                                
                                ByteBuffer uploadedFile = ByteBuffer
                                        .wrap(fileBytes);
                                
                                String dataResourceId = (String) fieldMap
                                        .get("dataResourceId");
                                String contentName = (String) fieldMap
                                        .get("contentName");
                                String description = (String) fieldMap
                                        .get("description");
                                String contentTypeId = (String) fieldMap
                                        .get("contentTypeId");
                                String contentPurposeTypeId = (String) fieldMap
                                        .get("contentPurposeTypeId");
                                
                                GenericValue dataResource = delegator.findOne(
                                        "DataResource", UtilMisc.toMap(
                                                "dataResourceId",
                                                dataResourceId), false);
                                
                                if (UtilValidate.isEmpty(dataResource)) {
                                    
                                    Map<String, Object> createContentFromUploadedFileInMap = new HashMap<String, Object>();
                                    createContentFromUploadedFileInMap.put(
                                            "userLogin", userLogin);
                                    createContentFromUploadedFileInMap.put(
                                            "contentName", contentName);
                                    createContentFromUploadedFileInMap.put(
                                            "description", description);
                                    createContentFromUploadedFileInMap.put(
                                            "contentTypeId", contentTypeId);
                                    createContentFromUploadedFileInMap.put(
                                            "contentPurposeTypeId",
                                            contentPurposeTypeId);
                                    createContentFromUploadedFileInMap.put(
                                            "_uploadedFile_fileName", fileName);
                                    // createContentFromUploadedFileInMap.put(
                                    // "dataResourceTypeId", "IMAGE_OBJECT");
                                    createContentFromUploadedFileInMap.put(
                                            "uploadedFile", uploadedFile);
                                    createContentFromUploadedFileInMap.put(
                                            "isPublic", "Y");
                                    Map<String, Object> createContentFromUploadedFileResults = dispatcher
                                            .runSync(
                                                    "createContentFromUploadedFile",
                                                    createContentFromUploadedFileInMap);
                                    String contentId = (String) createContentFromUploadedFileResults
                                            .get("contentId");
                                    dataResourceId = (String) createContentFromUploadedFileResults
                                            .get("dataResourceId");
                                    
                                    sb.append(",\"contentId\":\"")
                                            .append(contentId).append("\"");
                                    sb.append(",\"dataResourceId\":\"")
                                            .append(dataResourceId)
                                            .append("\"");
                                } else {
                                    String contentId = (String) fieldMap
                                            .get("contentId");
                                    
                                    Map<String, Object> updateContentAndUploadedFileInMap = new HashMap<String, Object>();
                                    updateContentAndUploadedFileInMap.put(
                                            "userLogin", userLogin);
                                    updateContentAndUploadedFileInMap.put(
                                            "contentId", contentId);
                                    updateContentAndUploadedFileInMap.put(
                                            "contentName", contentName);
                                    updateContentAndUploadedFileInMap.put(
                                            "description", description);
                                    updateContentAndUploadedFileInMap.put(
                                            "contentTypeId", contentTypeId);
                                    updateContentAndUploadedFileInMap.put(
                                            "contentPurposeTypeId",
                                            contentPurposeTypeId);
                                    updateContentAndUploadedFileInMap.put(
                                            "dataResourceId", dataResourceId);
                                    updateContentAndUploadedFileInMap.put(
                                            "uploadedFile", uploadedFile);
                                    updateContentAndUploadedFileInMap.put(
                                            "_uploadedFile_fileName", fileName);
                                    dispatcher.runSync(
                                            "updateContentAndUploadedFile",
                                            updateContentAndUploadedFileInMap);
                                    
                                    sb.append(", \"dataResourceId\":\"")
                                            .append(dataResourceId)
                                            .append("\"");
                                }
                                
                                sb.append("}");
                            } else {
                                request.setAttribute("_ERROR_MESSAGE_",
                                        "mimeType is empty.");
                                return "error";
                            }
                        }
                    }
                    
                    sb.append("}");
                    if (fieldIndex < fieldCount - 1) {
                        sb.append(",");
                    }
                    fieldIndex++;
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
