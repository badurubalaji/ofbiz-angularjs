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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.ofbiz.angularjs.component.NgComponentConfig;
import org.ofbiz.angularjs.component.NgComponentConfig.ClasspathInfo;
import org.ofbiz.angularjs.directive.ModelNgDirective;
import org.ofbiz.angularjs.factory.ModelNgFactory;
import org.ofbiz.angularjs.filter.ModelNgFilter;
import org.ofbiz.angularjs.javascript.JavaScriptClass;
import org.ofbiz.angularjs.javascript.JavaScriptFactory;
import org.ofbiz.angularjs.javascript.JavaScriptRenderer;
import org.ofbiz.angularjs.model.NgModelDispatcherContext;
import org.ofbiz.angularjs.module.ModelNgModule;
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

        Context context = Context.enter();
        for (File file : files) {
            String packageName = null;
            String className = null;
            try {
                if (file.getAbsolutePath().endsWith(".js")) {
                    Scriptable scope = context.initStandardObjects();
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String packageLine = reader.readLine();

                    // find package name
                    Pattern packagePattern = Pattern.compile(".*?package.*?(.*?);");
                    Matcher packageMatcher = packagePattern.matcher(packageLine);
                    while (packageMatcher.find()) {
                        packageName = packageMatcher.group(1).trim();
                    }
                    
                    context.evaluateReader(scope, reader, null, 2, null);
                    className = file.getName().replace(".js", "").trim();
                    Function classFunction = (Function) scope.get(className, scope);
                    JavaScriptFactory.addJavaScriptClass(packageName, className, classFunction, context);
                }
            } catch (Exception e) {
                Debug.logError(e, "Could not compile " + packageName + "." + className + " class", module);
            }
        }
        Context.exit();
        
        JavaScriptRenderer renderer = new JavaScriptRenderer(builder);
        renderer.render(JavaScriptFactory.getRootJavaScriptPackages());
    }
    
    private static void buildAppJsFunction(String name, String defaultPath, List<? extends Element> viewElements, StringBuilder builder) {
        builder.append("\nangular.module('" + name + "', [");
        
        // modules
        List<String> moduleNames = new LinkedList<String>();
        for (ModelNgModule modelModule : NgModelDispatcherContext.getAllModelNgModules()) {
            moduleNames.add("'" + modelModule.name + "'");
        }
        
        if (UtilValidate.isNotEmpty(moduleNames)) {
            builder.append(StringUtil.join(moduleNames, ","));
        }
        builder.append("])\n");
        
        // views
        builder.append(".config(['$routeProvider', function($routeProvider) {\n");
        builder.append("    $routeProvider.\n");
        for (Element viewElement : viewElements) {
            String path = UtilXml.elementAttribute(viewElement, "path", null);
            String uri = UtilXml.elementAttribute(viewElement, "uri", null);
            String controller = UtilXml.elementAttribute(viewElement, "controller", null);
            builder.append("        when('" + path + "', {templateUrl: '" + uri + "', controller: '" + controller + "'}).\n");
        }
        builder.append("        otherwise({redirectTo: '" + defaultPath + "'});");
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
        Debug.logInfo("22222222222222222222 createDirectiveJsFunction: " + modelNgDirective.name, module);
        JavaScriptClass javaScriptClass = JavaScriptFactory.getJavaScriptClass(modelNgDirective.location);
        Debug.logInfo("3333333333333333333 javaScriptClass: " + javaScriptClass, module);
        if (UtilValidate.isNotEmpty(javaScriptClass)) {
            // object builder
            StringBuilder objectBuilder = new StringBuilder();
            objectBuilder.append("var " + modelNgDirective.name + " = new " + modelNgDirective.location
                    + "(" + javaScriptClass.getConstructorArgument() + ");");
            if (UtilValidate.isNotEmpty(modelNgDirective.replace)) {
                objectBuilder.append(modelNgDirective.name + ".replace = " + modelNgDirective.replace + ";");
            }
            if (UtilValidate.isNotEmpty(modelNgDirective.transclude)) {
                objectBuilder.append(modelNgDirective.name + ".transclude = " + modelNgDirective.transclude + ";");
            }
            if (UtilValidate.isNotEmpty(modelNgDirective.scope)) {
                objectBuilder.append(modelNgDirective.name + ".scope = " + modelNgDirective.scope + ";");
            }
            if (UtilValidate.isNotEmpty(modelNgDirective.priority)) {
                objectBuilder.append(modelNgDirective.name + ".priority = " + modelNgDirective.priority + ";");
            }
            if (UtilValidate.isNotEmpty(modelNgDirective.restrict)) {
                objectBuilder.append(modelNgDirective.name + ".restrict = \"" + modelNgDirective.restrict + "\";");
            }
    
            StringBuilder builder = new StringBuilder();
            builder.append("function(" + javaScriptClass.getConstructorArgument() + ") {");
            builder.append(objectBuilder.toString());
            builder.append(" return " + modelNgDirective.name + ";");
            builder.append("}");
            return builder.toString();
        } else {
            return "";
        }
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

    public static String buildClassesJs(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        List<File> classFiles = new LinkedList<File>();
        List<ClasspathInfo> classpathResoruceInfos = NgComponentConfig.getAllClasspathResourceInfos();
        for (ClasspathInfo classpathResourceInfo : classpathResoruceInfos) {
            try {
                List<File> jsFiles = FileUtil.findFiles("js", classpathResourceInfo.createResourceHandler().getFullLocation(), null, null);
                classFiles.addAll(jsFiles);
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
