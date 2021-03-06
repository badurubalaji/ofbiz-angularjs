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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.ofbiz.angularjs.application.ModelNgApplication;
import org.ofbiz.angularjs.application.ModelNgController;
import org.ofbiz.angularjs.application.ModelNgState;
import org.ofbiz.angularjs.application.ModelNgView;
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
import org.ofbiz.angularjs.module.ModelNgModule.ModelJavaScript;
import org.ofbiz.angularjs.provider.ModelNgProvider;
import org.ofbiz.angularjs.service.ModelNgService;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.control.LoginWorker;

public class AngularJsEvents {

    public final static String module = AngularJsEvents.class.getName();
    public final static String NG_APPS_INIT_PATH = "/WEB-INF/ng-apps.xml";

    private static String checkPath(String path, boolean fullPath,
            HttpServletRequest request) {
        String checkedPath = null;
        if (fullPath) {
            String protocol = "http";
            if (request.isSecure()) {
                protocol = "https";
            }
            checkedPath = protocol + "://" + path;
        } else {
            checkedPath = path;
        }
        return checkedPath;
    }

    private static void buildJsClasses(List<File> files, StringBuilder builder)
            throws IOException {
        JavaScriptFactory.clear();

        Context context = Context.enter();
        for (File file : files) {
            String packageName = null;
            String className = null;
            try {
                if (file.getAbsolutePath().endsWith(".js")) {
                    Scriptable scope = context.initStandardObjects();
                    BufferedReader reader = new BufferedReader(new FileReader(
                            file));
                    String packageLine = reader.readLine();

                    // find package name
                    Pattern packagePattern = Pattern
                            .compile(".*?package.*?(.*?);");
                    Matcher packageMatcher = packagePattern
                            .matcher(packageLine);
                    while (packageMatcher.find()) {
                        packageName = packageMatcher.group(1).trim();
                    }

                    context.evaluateReader(scope, reader, null, 2, null);
                    className = file.getName().replace(".js", "").trim();
                    Function classFunction = (Function) scope.get(className,
                            scope);
                    JavaScriptFactory.addJavaScriptClass(packageName,
                            className, classFunction, context);
                }
            } catch (Exception e) {
                Debug.logError(e, "Could not compile " + packageName + "."
                        + className + " class", module);
            }
        }
        Context.exit();

        JavaScriptRenderer renderer = new JavaScriptRenderer(builder);
        renderer.render(JavaScriptFactory.getRootJavaScriptPackages());
    }

    private static void buildCombindAllModule(StringBuilder builder) {
        builder.append("\nangular.module('combine.all', [");

        // modules
        List<String> moduleNames = new LinkedList<String>();
        for (ModelNgModule modelModule : NgModelDispatcherContext
                .getAllModelNgModules()) {
            moduleNames.add("'" + modelModule.name + "'");
        }

        if (UtilValidate.isNotEmpty(moduleNames)) {
            builder.append(StringUtil.join(moduleNames, ","));
        }
        builder.append("])\n");

        // directives
        String emptyJsFunction = "function() {}";
        for (ModelNgDirective modelNgDirective : NgModelDispatcherContext
                .getAllModelNgDirectives()) {
            String directiveJsFunction = createDirectiveJsFunction(modelNgDirective);
            if (UtilValidate.isEmpty(directiveJsFunction)) {
                directiveJsFunction = emptyJsFunction;
            }
            builder.append(".directive('" + modelNgDirective.name + "', "
                    + directiveJsFunction + ")\n");
        }

        // filters
        for (ModelNgFilter modelNgFilter : NgModelDispatcherContext
                .getAllModelNgFilters()) {
            builder.append(".filter('" + modelNgFilter.name + "', "
                    + modelNgFilter.location + ")\n");
        }

        // services
        for (ModelNgService modelNgService : NgModelDispatcherContext
                .getAllModelNgServices()) {
            builder.append(".service('" + modelNgService.name + "', "
                    + modelNgService.location + ")\n");
        }

        // providers
        for (ModelNgProvider modelNgProvider : NgModelDispatcherContext
                .getAllModelNgProviders()) {
            builder.append(".provider('" + modelNgProvider.name + "', "
                    + modelNgProvider.location + "." + modelNgProvider.invoke
                    + ")\n");
        }

        // factories
        for (ModelNgFactory modelNgFactory : NgModelDispatcherContext
                .getAllModelNgFactories()) {
            builder.append(".factory('" + modelNgFactory.name + "', "
                    + modelNgFactory.location + ")\n");
        }

        builder.append(";");
    }

    private static void buildAppJsFunction(String name, String defaultState,
            boolean disableAutoScrolling, List<? extends ModelNgController> modelNgControllers,
            List<? extends ModelNgState> modelNgStates, StringBuilder builder) {
        builder.append("\nangular.module('" + name + "', ['combine.all'])");

        // controllers
        StringBuilder controllersBuilder = new StringBuilder();
        if (UtilValidate.isNotEmpty(modelNgControllers)) {
            for (ModelNgController modelNgController : modelNgControllers) {
                String controllerName = modelNgController.name;
                if (UtilValidate.isEmpty(controllerName)) {
                    controllerName = modelNgController.path;
                }

                if (UtilValidate.isNotEmpty(controllerName)) {
                    controllersBuilder.append("\n.controller(\"" + controllerName + "\", " + modelNgController.path + ")");
                }
            }
        }
        builder.append(controllersBuilder);

        builder.append("\n.config(function($stateProvider, $urlRouterProvider, $anchorScrollProvider) {\n");

        String defaultUrl = "";
        List<String> defaultStateTokens = StringUtil.split(defaultState, ".");

        // states
        StringBuilder statesBuilder = new StringBuilder();
        if (UtilValidate.isNotEmpty(modelNgStates)) {
            statesBuilder.append("\n$stateProvider");
            for (ModelNgState modelNgState : modelNgStates) {

                // append default URL
                String defaultStateTokenTail = "";
                for (String defaultStateToken : defaultStateTokens) {
                    defaultStateTokenTail += defaultStateToken;

                    if (UtilValidate.isNotEmpty(defaultStateTokenTail)
                            && defaultStateTokenTail.equals(modelNgState.name)) {
                        defaultUrl += modelNgState.url;
                        break;
                    }

                    defaultStateTokenTail += ".";
                }

                statesBuilder.append("\n//- State[" + modelNgState.name
                        + "] is abstract[" + modelNgState.isAbstract
                        + "] with URL[" + modelNgState.url + "]\n");
                statesBuilder.append(".state(\"" + modelNgState.name + "\", {");
                statesBuilder.append("abstract: " + modelNgState.isAbstract
                        + ",");
                statesBuilder.append("url: \"" + modelNgState.url + "\",");
                statesBuilder.append("views: {");

                List<String> viewDefs = new LinkedList<String>();
                for (ModelNgView modelNgView : modelNgState.getModelNgViews()) {
                    statesBuilder.append("\n//-- View[" + modelNgView.name
                            + "] with template URL[" + modelNgView.target
                            + "] and controller[" + modelNgView.controller
                            + "]\n");
                    viewDefs.add("\"" + modelNgView.name
                            + "\": { templateUrl: \"" + modelNgView.target
                            + "\", controller: " + modelNgView.controller
                            + ", controllerClassName: \"" + modelNgView.controller
                            + "\"}");
                }
                statesBuilder.append(StringUtil.join(viewDefs, ","));

                statesBuilder.append("}})");
            }
        }

        // default path (One page can have only one default path)
        if (UtilValidate.isNotEmpty(defaultUrl)) {
            builder.append("//- Default URL[" + defaultUrl + "] of state["
                    + defaultState + "]\n\n");
            builder.append("\n$urlRouterProvider.otherwise(\"" + defaultUrl
                    + "\");");
        }

        builder.append(statesBuilder);
        builder.append(";");

        builder.append("})\n");

        // disable auto scrolling
        if (disableAutoScrolling) {
            builder.append(".value('$anchorScroll', angular.noop)");
        }

        // run
        builder.append("\n.run(function($rootScope, $state, $stateParams, $templateFactory, $templateCache, $http) {");
        builder.append("\n$rootScope.$state = $state;");
        builder.append("\n$rootScope.$stateParams = $stateParams;");

        // override $templateFactory.fromUrl() function and add X-Requested-With header
        builder.append("\n$templateFactory.fromUrl = function (url, params) {");
        builder.append("\n    if (angular.isFunction(url)) url = url(params);");
        builder.append("\n    if (url == null) return null;");
        builder.append("\n    else{");
        builder.append("\n        return $http");
        builder.append("\n            .get(url, { cache: $templateCache, headers: { Accept: 'text/html', 'X-Requested-With': 'XMLHttpRequest' }})");
        builder.append("\n            .then(function(response) {");
        builder.append("\n                return response.data;");
        builder.append("\n            });");
        builder.append("\n   }");
        builder.append("\n};");

        builder.append("})");

        builder.append(";");
    }

    private static String createDirectiveJsFunction(
            ModelNgDirective modelNgDirective) {
        JavaScriptClass javaScriptClass = JavaScriptFactory
                .getJavaScriptClass(modelNgDirective.location);
        if (UtilValidate.isNotEmpty(javaScriptClass)) {
            // object builder
            StringBuilder objectBuilder = new StringBuilder();
            objectBuilder.append("var " + modelNgDirective.name + " = new "
                    + modelNgDirective.location + "("
                    + javaScriptClass.getConstructorArgument() + ");");
            if (UtilValidate.isNotEmpty(modelNgDirective.replace)) {
                objectBuilder.append(modelNgDirective.name + ".replace = "
                        + modelNgDirective.replace + ";");
            }
            if (UtilValidate.isNotEmpty(modelNgDirective.transclude)) {
                objectBuilder.append(modelNgDirective.name + ".transclude = "
                        + modelNgDirective.transclude + ";");
            }
            if (UtilValidate.isNotEmpty(modelNgDirective.scope)) {
                objectBuilder.append(modelNgDirective.name + ".scope = "
                        + modelNgDirective.scope + ";");
            }
            if (UtilValidate.isNotEmpty(modelNgDirective.priority)) {
                objectBuilder.append(modelNgDirective.name + ".priority = "
                        + modelNgDirective.priority + ";");
            }
            if (UtilValidate.isNotEmpty(modelNgDirective.require)) {
                objectBuilder.append(modelNgDirective.name + ".require = \""
                        + modelNgDirective.require + "\";");
            }
            if (UtilValidate.isNotEmpty(modelNgDirective.restrict)) {
                objectBuilder.append(modelNgDirective.name + ".restrict = \""
                        + modelNgDirective.restrict + "\";");
            }

            StringBuilder builder = new StringBuilder();
            builder.append("function("
                    + javaScriptClass.getConstructorArgument() + ") {");
            builder.append(objectBuilder.toString());
            builder.append(" return " + modelNgDirective.name + ";");
            builder.append("}");
            return builder.toString();
        } else {
            return "";
        }
    }

    public static String buildMainJs(HttpServletRequest request,
            HttpServletResponse response) {

        StringBuilder builder = new StringBuilder();

        try {
            synchronized (AngularJsEvents.class) {
                // classes
                List<File> classFiles = new LinkedList<File>();
                List<ClasspathInfo> classpathResoruceInfos = NgComponentConfig
                        .getAllClasspathResourceInfos();
                for (ClasspathInfo classpathResourceInfo : classpathResoruceInfos) {
                    try {
                        List<File> jsFiles = FileUtil.findFiles("js",
                                classpathResourceInfo.createResourceHandler()
                                        .getFullLocation(), null, null);
                        classFiles.addAll(jsFiles);
                    } catch (Exception e) {
                        Debug.logWarning(e, module);
                    }
                }
                buildJsClasses(classFiles, builder);
            }

            // require
            builder.append("require.config({");
            builder.append("\nbaseUrl: \"/angularjs/control\",");
            builder.append("\nwaitSeconds: 15,");
            builder.append("\n});");
            builder.append("\nrequire([\n");

            // modules
            List<ModelNgModule> ngModules = NgModelDispatcherContext
                    .getAllModelNgModules();
            List<String> moduleJsPaths = new LinkedList<String>();
            List<String> javaScriptPaths = new LinkedList<String>();

            for (ModelNgModule ngModule : ngModules) {
                String modulePath = checkPath(ngModule.location, false, request);
                for (ModelJavaScript modelJavaScript : ngModule.modelJavaScripts) {
                    String javaScriptPath = checkPath(modelJavaScript.path,
                            modelJavaScript.fullPath, request);
                    javaScriptPaths.add("\n'" + javaScriptPath + "'");
                }
                moduleJsPaths.add("\n'" + modulePath + "'");
            }

            String moduleJsList = StringUtil.join(moduleJsPaths, ",");
            String javaScriptJsList = StringUtil.join(javaScriptPaths, ",");

            builder.append(moduleJsList);
            builder.append("], function() {");

            builder.append("\nrequire([\n");
            builder.append(javaScriptJsList);
            builder.append("\n], function() {");

            buildCombindAllModule(builder);

            builder.append("/*\n");
            builder.append("*\n");
            builder.append("* ============== Applications =================");
            builder.append("*\n");
            builder.append("*/\n");

            // apps
            for (ModelNgApplication modelNgApplication : NgModelDispatcherContext
                    .getAllModelNgApplications()) {
                builder.append("\n// Application [" + modelNgApplication.name
                        + "] with default state["
                        + modelNgApplication.defaultState
                        + "] and disable auto scrolling["
                        + modelNgApplication.disableAutoScrolling + "]\n");
                buildAppJsFunction(modelNgApplication.name,
                        modelNgApplication.defaultState,
                        modelNgApplication.disableAutoScrolling,
                        modelNgApplication.getModelNgControllers(),
                        modelNgApplication.getModelNgStates(), builder);
            }

            // bootstrap
            for (ModelNgApplication modelNgApplication : NgModelDispatcherContext
                    .getAllModelNgApplications()) {
                String elementName = "appElement_" + modelNgApplication.name;
                String elementId = modelNgApplication.name + "-app";
                builder.append("\nvar " + elementName + " = $('#" + elementId
                        + "');");
                builder.append("\nif(" + elementName + ".length > 0) {");
                builder.append("\nangular.bootstrap(" + elementName + ", ['"
                        + modelNgApplication.name + "']);");
                builder.append("\n}");
            }

            builder.append("\n});");

            builder.append("\n});");

            // return the JavaScript String
            String javaScriptString = builder.toString();
            response.setContentType("application/x-javascript; charset=UTF-8");

            response.setContentLength(javaScriptString.getBytes("UTF8").length);

            Writer out;
            try {
                out = response.getWriter();
                out.write(javaScriptString);
                out.flush();
            } catch (IOException e) {
                Debug.logError(e, module);
            }
        } catch (UnsupportedEncodingException e) {
            Debug.logError(e, "Problems with JavaScript encoding.", module);
        } catch (IOException e) {
            Debug.logError(e, "Problems with IO.", module);
        }

        return "success";
    }

    public static String error(HttpServletRequest request,
            HttpServletResponse response) {
        List<String> responseMessageList = new LinkedList<String>();
        String errorMessage = UtilGenerics.cast(request
                .getAttribute("_ERROR_MESSAGE_"));
        List<String> errorMessageList = UtilGenerics.cast(request
                .getAttribute("_ERROR_MESSAGE_LIST_"));
        if (UtilValidate.isNotEmpty(errorMessage)) {
            responseMessageList.add(StringEscapeUtils.unescapeHtml(errorMessage));
        }
        if (UtilValidate.isNotEmpty(errorMessageList)) {
            responseMessageList.addAll(errorMessageList);
        }

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("_ERROR_MESSAGE_LIST_", responseMessageList);

        // set the X-JSON content type
        response.setContentType("application/x-json");
        try {
            JSON json = JSON.from(results);
            String jsonStr = json.toString();
            // jsonStr.length is not reliable for unicode characters
            response.setContentLength(jsonStr.getBytes("UTF8").length);

            // return the JSON String
            Writer out;
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with Json encoding: " + e, module);
        } catch (IOException e) {
            Debug.logError(e, module);
        }

        return "success";
    }

    public static String ajaxCheckLogin(HttpServletRequest request, HttpServletResponse response) {
        //TODO show AJAX login popup
        String result = LoginWorker.checkLogin(request, response);
        if ("error".equals(result)) {
            request.setAttribute("MESSAGE", "Please Login");
        } else {
            request.setAttribute("SUCCESS", "Please Login");
        }
        return "success";
    }
}
