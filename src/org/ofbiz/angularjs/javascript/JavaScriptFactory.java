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

import java.util.LinkedList;
import java.util.List;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

public class JavaScriptFactory {

    public final static String module = JavaScriptFactory.class.getName();
    
    protected static List<JavaScriptPackage> javaScriptPackages = new LinkedList<JavaScriptPackage>();
    
    private JavaScriptFactory() {}
    
    private static JavaScriptPackage getJavaScriptPackage(String packagePath) {
        List<String> packageTokens = StringUtil.split(packagePath, ".");
        String packageName = packageTokens.get(packageTokens.size() - 1);
        JavaScriptPackage javaScriptPackage = new JavaScriptPackage(packageName);
        return javaScriptPackage;
    }
    
    public static void addJavaScriptClass(Element element) {
        String packagePath = UtilXml.elementAttribute(element, "package", null);
        JavaScriptPackage javaScriptPackage = JavaScriptFactory.getJavaScriptPackage(packagePath);
        javaScriptPackage.addJavaScriptClass(element);
        javaScriptPackages.add(javaScriptPackage);
    }
    
    public static List<JavaScriptPackage> getRootJavaScriptPackages() {
        List<JavaScriptPackage> rootJavaScriptPackages = new LinkedList<JavaScriptPackage>();
        return rootJavaScriptPackages;
    }
}
