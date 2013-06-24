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
import org.ofbiz.angularjs.directive.ModelNgDirective;
import org.ofbiz.angularjs.factory.ModelNgFactory;
import org.ofbiz.angularjs.filter.ModelNgFilter;
import org.ofbiz.angularjs.javascript.JavaScriptFactory;
import org.ofbiz.angularjs.javascript.JavaScriptRenderer;
import org.ofbiz.angularjs.model.NgModelDispatcherContext;
import org.ofbiz.angularjs.provider.ModelNgProvider;
import org.ofbiz.angularjs.service.ModelNgService;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentConfig.WebappInfo;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AngularJsEvents {

    public final static String module = AngularJsEvents.class.getName();
    public final static String NG_APPS_INIT_PATH = "/WEB-INF/ng-apps.xml";
    
    private static void buildJsClasses(List<File> files, StringBuilder builder) throws IOException {
        JavaScriptFactory.clear();
        
        for (File file : files) {
            try {
                Document document = UtilXml.readXmlDocument(file.toURI().toURL());
                Element element = document.getDocumentElement();
                String nodeName = element.getNodeName();
                if ("javascript-class".equals(nodeName)) {
                    JavaScriptFactory.addJavaScriptClass(element);
                } else {
                    Debug.logWarning(nodeName + " is not javascript class.", module);
                }
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }
        
        JavaScriptRenderer renderer = new JavaScriptRenderer(builder);
        renderer.render(JavaScriptFactory.getRootJavaScriptPackages());
    }
    
    private static void buildAppJsFunction(String name, String defaultPath
            , List<? extends Element> moduleElements, List<? extends Element> viewElements, StringBuilder builder) {
        builder.append("\nangular.module('" + name + "', [");
        
        // modules
        List<String> moduleNames = new LinkedList<String>();
        for (Element moduleElemnent : moduleElements) {
            String moduleName = UtilXml.elementAttribute(moduleElemnent, "name", null);
            moduleNames.add("'" + moduleName + "'");
        }
        
        if (UtilValidate.isNotEmpty(moduleNames)) {
            builder.append(StringUtil.join(moduleNames, ","));
        }
        builder.append("])\n");
        
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
        for (ModelNgDirective modelNgDirective : NgModelDispatcherContext.getAllModelNgDirectives()) {
            builder.append(".directive('" + modelNgDirective.name + "', " + createDirectiveJsFunction(modelNgDirective) + ")\n");
        }
        
        // filters
        for (ModelNgFilter modelNgFilter : NgModelDispatcherContext.getAllModelNgFilters()) {
            builder.append(".filter('" + modelNgFilter.name + "', " + modelNgFilter.location + "." + modelNgFilter.invoke + ")\n");
        }
        
        // services
        for (ModelNgService modelNgService : NgModelDispatcherContext.getAllModelNgServices()) {
            builder.append(".service('" + modelNgService.name + "', " + modelNgService.location + ")\n");
        }
        
        // providers
        for (ModelNgProvider modelNgProvider : NgModelDispatcherContext.getAllModelNgProviders()) {
            builder.append(".provider('" + modelNgProvider.name + "', " + modelNgProvider.location + "." + modelNgProvider.invoke + ")\n");
        }
        
        // factories
        for (ModelNgFactory modelNgFactory : NgModelDispatcherContext.getAllModelNgFactories()) {
            builder.append(".factory('" + modelNgFactory.name + "', " + modelNgFactory.location + ")\n");
        }
        
        builder.append(";");
    }
    
    private static String createDirectiveJsFunction(ModelNgDirective modelNgDirective) {
        StringBuilder builder = new StringBuilder();
        builder.append("function() {");
        builder.append("}");
        return builder.toString();
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
                        List<? extends Element> moduleElements = UtilXml.childElementList(ngAppElement, "module");
                        List<? extends Element> viewElements = UtilXml.childElementList(ngAppElement, "view");
                        buildAppJsFunction(name, defaultPath, moduleElements, viewElements, builder);
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
        
        try {
            buildJsClasses(classFiles, builder);
            String javaScriptString = builder.toString();
            response.setContentType("text/javascript");
            response.setContentLength(javaScriptString.getBytes("UTF8").length);
            
            Writer out = response.getWriter();
            out.write(javaScriptString);
            out.flush();
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with JavaScript encoding: " + e, module);
        } catch (IOException e) {
            Debug.logError(e, module);
        }
        
        return "success";
    }
}
