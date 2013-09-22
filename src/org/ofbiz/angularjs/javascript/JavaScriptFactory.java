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
package org.ofbiz.angularjs.javascript;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;

public class JavaScriptFactory {

    public final static String module = JavaScriptFactory.class.getName();
    
    protected static Map<String, JavaScriptPackage> javaScriptPackages = new LinkedHashMap<String, JavaScriptPackage>();
    protected static List<JavaScriptPackage> rootJavaScriptPackages = new LinkedList<JavaScriptPackage>();

    private JavaScriptFactory() {}
    
    public static JavaScriptPackage getJavaScriptPackage(String packageName) {
        if (UtilValidate.isEmpty(packageName)) return null;
        
        JavaScriptPackage javaScriptPackage = javaScriptPackages.get(packageName);
        if (UtilValidate.isEmpty(javaScriptPackage)) {
            javaScriptPackage = new JavaScriptPackage(packageName);
            javaScriptPackages.put(packageName, javaScriptPackage);
        }
        return javaScriptPackage;
    }
    
    public static JavaScriptClass getJavaScriptClass(String fullClassName) {
    	List<String> tokens = StringUtil.split(fullClassName, ".");
        String className = tokens.remove(tokens.size() - 1);
        String packageName = StringUtil.join(tokens, ".");
        JavaScriptPackage javaScriptPackage = getJavaScriptPackage(packageName);
        JavaScriptClass javaScriptClass = javaScriptPackage.getJavaScriptClass(className);
        return javaScriptClass;
    }
    
    public static void addJavaScriptClass(String packageName, String className, Function classFunction, Context context) {
        JavaScriptPackage javaScriptPackage = JavaScriptFactory.getJavaScriptPackage(packageName);
        javaScriptPackage.addJavaScriptClass(className, classFunction, context);
    }
    
    public static void addRootJavaScriptPackage(JavaScriptPackage javaScriptPackage) {
        rootJavaScriptPackages.add(javaScriptPackage);
    }
    
    public static List<JavaScriptPackage> getRootJavaScriptPackages() {
        return rootJavaScriptPackages;
    }
    
    public static void clear() {
        javaScriptPackages.clear();
        rootJavaScriptPackages.clear();
    }
}
