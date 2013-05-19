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
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.angularjs.component.NgComponentConfig;
import org.ofbiz.angularjs.component.NgComponentConfig.ClasspathInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.ControllerResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.DirectiveResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.FactoryResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.FilterResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.ProviderResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.ServiceResourceInfo;
import org.ofbiz.angularjs.javascript.JavaScriptClass;
import org.ofbiz.angularjs.javascript.JavaScriptFactory;
import org.ofbiz.angularjs.javascript.JavaScriptPackage;
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
    public final static String NG_APPS_INIT_PATH = "/WEB-INF/ng-apps.xml";
    
    private static void buildJsClasses(List<File> files, StringBuilder builder) {
        for (File file : files) {
            try {
                Document document = UtilXml.readXmlDocument(file.toURI().toURL());
                JavaScriptFactory.addJavaScriptClass(document.getDocumentElement());
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }
        
        List<JavaScriptPackage> rootJavaScriptPackages = JavaScriptFactory.getRootJavaScriptPackages();
        for (JavaScriptPackage rootJavaScriptPackage : rootJavaScriptPackages) {
            
        }
    }
    
    private static void buildAppJsFunction(String name, String defaultPath, List<? extends Element> directiveElements
            , List<? extends Element> viewElements, List<? extends Element> filterElements, List<? extends Element> serviceElements, StringBuilder builder) {
        builder.append("angular.module('" + name + "', [])\n");
        
        // views
        builder.append(".config(['$routeProvider', function($routeProvider) {\n");
        builder.append("\t$routeProvider.\n");
        for (Element viewElement : viewElements) {
            String path = UtilXml.elementAttribute(viewElement, "path", null);
            String uri = UtilXml.elementAttribute(viewElement, "uri", null);
            String controller = UtilXml.elementAttribute(viewElement, "controller", null);
            builder.append("\t\twhen('" + path + "', {templateUrl: '" + uri + "', controller: '" + controller + "'}).\n");
        }
        builder.append("\t\totherwise({redirectTo: '" + defaultPath + "'});");
        builder.append("}])\n");
        
        // directives
        if (UtilValidate.isNotEmpty(directiveElements)) {
            for (Element directiveElement : directiveElements) {
                try {
                    String directiveName = UtilXml.elementAttribute(directiveElement, "name", null);
                    String path = UtilXml.elementAttribute(directiveElement, "path", null);
                    builder.append(".directive('" + directiveName + "', " + path + "." + directiveName + ")\n");
                } catch (Exception e) {
                    Debug.logError(e, module);
                }
            }
        }
        
        // filters
        if (UtilValidate.isNotEmpty(filterElements)) {
            for (Element filterElement : filterElements) {
                try {
                    String filterName = UtilXml.elementAttribute(filterElement, "name", null);
                    String path = UtilXml.elementAttribute(filterElement, "path", null);
                    builder.append(".filter('" + filterName + "', " + path + "." + filterName + ")\n");
                } catch (Exception e) {
                    Debug.logError(e, module);
                }
            }
        }
        
        // services
        if (UtilValidate.isNotEmpty(serviceElements)) {
            for (Element serviceElement : serviceElements) {
                try {
                    String serviceName = UtilXml.elementAttribute(serviceElement, "name", null);
                    String path = UtilXml.elementAttribute(serviceElement, "path", null);
                    builder.append(".factory('" + serviceName + "', " + path + "." + serviceName + ")\n");
                } catch (Exception e) {
                    Debug.logError(e, module);
                }
            }
        }
        
        builder.append(";");
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
                        List<? extends Element> directiveElements = UtilXml.childElementList(ngAppElement, "directive");
                        List<? extends Element> filterElements = UtilXml.childElementList(ngAppElement, "filter");
                        List<? extends Element> serviceElements = UtilXml.childElementList(ngAppElement, "service");
                        buildAppJsFunction(name, defaultPath, directiveElements, viewElements, filterElements, serviceElements, builder);
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

    public static String buildClassesJs(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        List<File> classFiles = new LinkedList<File>();
        List<ClasspathInfo> classpathResoruceInfos = NgComponentConfig.getAllClasspathResourceInfos();
        for (ClasspathInfo classpathResourceInfo : classpathResoruceInfos) {
            try {
                List<File> files = FileUtil.findFiles("xml", classpathResourceInfo.createResourceHandler().getFullLocation(), null, null);
                classFiles.addAll(files);
            } catch (Exception e) {
                Debug.logWarning(e, module);
            }
        }
        
        buildJsClasses(classFiles, builder);

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

    public static String buildControllersJs(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        List<ControllerResourceInfo> controllerInfos = NgComponentConfig.getAllControllerResourceInfos();
        for (ControllerResourceInfo controllerInfo : controllerInfos) {
            try {
                Document document = controllerInfo.createResourceHandler().getDocument();
                List<? extends Element> ngControllerElements = UtilXml.childElementList(document.getDocumentElement(), "ng-controller");
                for (Element ngControllerElement : ngControllerElements) {
                    String name = UtilXml.elementAttribute(ngControllerElement, "name", null);
                    String path = UtilXml.elementAttribute(ngControllerElement, "path", null);
                }
            } catch (Exception e) {
                Debug.logWarning(e, module);
            }
        }
        response.setContentType("text/javascript");
        
        return "success";
    }

    public static String buildDirectivesJs(HttpServletRequest request, HttpServletResponse response) {
        List<DirectiveResourceInfo> directiveInfos = NgComponentConfig.getAllDirectiveResourceInfos();
        for (DirectiveResourceInfo directiveInfo : directiveInfos) {
            Debug.logInfo("============ Directive: " + directiveInfo.getLocation(), module);
        }
        response.setContentType("text/javascript");
        return "success";
    }

    public static String buildFiltersJs(HttpServletRequest request, HttpServletResponse response) {
        List<FilterResourceInfo> filterInfos = NgComponentConfig.getAllFilterResourceInfos();
        for (FilterResourceInfo filterInfo : filterInfos) {
            Debug.logInfo("============ Filter: " + filterInfo.getLocation(), module);
        }
        response.setContentType("text/javascript");
        return "success";
    }

    public static String buildServicesJs(HttpServletRequest request, HttpServletResponse response) {
        List<ServiceResourceInfo> serviceInfos = NgComponentConfig.getAllServiceResoruceInfos();
        for (ServiceResourceInfo serviceInfo : serviceInfos) {
            Debug.logInfo("============ Service: " + serviceInfo.getLocation(), module);
        }
        response.setContentType("text/javascript");
        return "success";
    }

    public static String buildFactorysJs(HttpServletRequest request, HttpServletResponse response) {
        List<FactoryResourceInfo> factoryInfos = NgComponentConfig.getAllFactoryResourceInfos();
        for (FactoryResourceInfo factoryInfo : factoryInfos) {
            Debug.logInfo("============ Factory: " + factoryInfo.getLocation(), module);
        }
        response.setContentType("text/javascript");
        return "success";
    }

    public static String buildProvidersJs(HttpServletRequest request, HttpServletResponse response) {
        List<ProviderResourceInfo> providerInfos = NgComponentConfig.getAllProviderResourceInfos();
        for (ProviderResourceInfo providerInfo : providerInfos) {
            Debug.logInfo("============ Provider: " + providerInfo.getLocation(), module);
        }
        response.setContentType("text/javascript");
        return "success";
    }
}
