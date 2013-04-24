/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.angularjs.event;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentConfig.WebappInfo;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AngularJsEvents {

    public final static String module = AngularJsEvents.class.getName();
    public final static String NG_CONTROLLERS_INIT_PATH = "/WEB-INF/ng-controllers.xml";
    public final static String NG_APPS_INIT_PATH = "/WEB-INF/ng-apps.xml";
    
    private static void buildControllerJsFunction(String name, String xmlResource, StringBuilder builder) {
        builder.append("function " + name + "($scope, $routeParams) {\n");
        try {
            Document document = UtilXml.readXmlDocument(FileUtil.getFile(xmlResource).toURI().toURL());
            List<? extends Element> jsMethodElements = UtilXml.childElementList(document.getDocumentElement(), "js-method");
            for (Element jsMethodElement : jsMethodElements) {
                String jsMethodName = UtilXml.elementAttribute(jsMethodElement, "name", null);
                if (UtilValidate.isNotEmpty(jsMethodName) && jsMethodName.equals(name)) {
                    String functionContent = UtilXml.elementValue(jsMethodElement);
                    builder.append(functionContent);
                    break;
                }
            }
        } catch (Exception e) {
            Debug.logWarning(e, module);
        }
        builder.append("}\n");
    }
    
    private static void buildAppJsFunction(String name, String defaultPath, List<? extends Element> viewElements, StringBuilder builder) {
        builder.append("angular.module('" + name + "', []).\n");
        builder.append("    config(['$routeProvider', function($routeProvider) {\n");
        builder.append("    $routeProvider.\n");
        for (Element viewElement : viewElements) {
            String path = UtilXml.elementAttribute(viewElement, "path", null);
            String uri = UtilXml.elementAttribute(viewElement, "uri", null);
            String controller = UtilXml.elementAttribute(viewElement, "controller", null);
            builder.append("when('" + path + "', {templateUrl: '" + uri + "', controller: '" + controller + "'}).\n");
        }
        builder.append("otherwise({redirectTo: '" + defaultPath + "'});");
        builder.append("}]);\n");
    }

    public static String buildControllersJs(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        Collection<WebappInfo> webAppInfos = ComponentConfig.getAllWebappResourceInfos();
        for (WebappInfo webAppInfo : webAppInfos) {
            String ngControllersPath = webAppInfo.getLocation() + NG_CONTROLLERS_INIT_PATH;
            File ngControllersFile = new File(ngControllersPath);
            if (ngControllersFile.exists()) {
                try {
                    Document document = UtilXml.readXmlDocument(ngControllersFile.toURI().toURL());
                    List<? extends Element> ngControllerElements = UtilXml.childElementList(document.getDocumentElement(), "ng-controller");
                    for (Element ngControllerElement : ngControllerElements) {
                        String name = UtilXml.elementAttribute(ngControllerElement, "name", null);
                        String xmlResource = UtilXml.elementAttribute(ngControllerElement, "xml-resource", null);
                        buildControllerJsFunction(name, xmlResource, builder);
                    }
                } catch (Exception e) {
                    Debug.logWarning(e, module);
                }
            }
        }

        String javaScriptString = builder.toString();
        response.setContentType("text/javascript");
        
        try {
            response.setContentLength(javaScriptString.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with JavaScript encoding: " + e, module);
        }
        
        // return the JavaScript String
        Writer out;
        try {
            out = response.getWriter();
            out.write(javaScriptString);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, module);
        }
        
        return "success";
    }

    public static String buildAppsJs(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        Collection<WebappInfo> webAppInfos = ComponentConfig.getAllWebappResourceInfos();
        for (WebappInfo webAppInfo : webAppInfos) {
            String ngAppsPath = webAppInfo.getLocation() + NG_APPS_INIT_PATH;
            File ngAppsFile = new File(ngAppsPath);
            if (ngAppsFile.exists()) {
                try {
                    Document document = UtilXml.readXmlDocument(ngAppsFile.toURI().toURL());
                    List<? extends Element> ngAppElements = UtilXml.childElementList(document.getDocumentElement(), "ng-app");
                    for (Element ngAppElement : ngAppElements) {
                        String name = UtilXml.elementAttribute(ngAppElement, "name", null);
                        String defaultPath = UtilXml.elementAttribute(ngAppElement, "default-path", null);
                        List<? extends Element> viewElements = UtilXml.childElementList(ngAppElement, "view");
                        buildAppJsFunction(name, defaultPath, viewElements, builder);
                    }
                } catch (Exception e) {
                    Debug.logWarning(e, module);
                }
            }
        }

        String javaScriptString = builder.toString();
        response.setContentType("text/javascript");
        
        try {
            response.setContentLength(javaScriptString.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with JavaScript encoding: " + e, module);
        }
        
        // return the JavaScript String
        Writer out;
        try {
            out = response.getWriter();
            out.write(javaScriptString);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, module);
        }
        
        return "success";
    }
}
