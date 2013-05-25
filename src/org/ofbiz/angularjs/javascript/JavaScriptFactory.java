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

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

public class JavaScriptFactory {

    public final static String module = JavaScriptFactory.class.getName();
    
    protected static Map<String, JavaScriptPackage> javaScriptPackages = new LinkedHashMap<String, JavaScriptPackage>();
    protected static List<JavaScriptPackage> rootJavaScriptPackages = new LinkedList<JavaScriptPackage>();
    
    private JavaScriptFactory() {}
    
    public static JavaScriptPackage getJavaScriptPackage(String packagePath) {
        if (UtilValidate.isEmpty(packagePath)) return null;
        
        JavaScriptPackage javaScriptPackage = javaScriptPackages.get(packagePath);
        if (UtilValidate.isEmpty(javaScriptPackage)) {
            javaScriptPackage = new JavaScriptPackage(packagePath);
            javaScriptPackages.put(packagePath, javaScriptPackage);
        }
        return javaScriptPackage;
    }
    
    public static void addJavaScriptClass(Element element) {
        String packagePath = UtilXml.elementAttribute(element, "package", null);
        JavaScriptPackage javaScriptPackage = JavaScriptFactory.getJavaScriptPackage(packagePath);
        javaScriptPackage.addJavaScriptClass(element);
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
