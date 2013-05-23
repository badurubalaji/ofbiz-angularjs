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
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

public class JavaScriptPackage {

    public final static String module = JavaScriptPackage.class.getName();

    protected JavaScriptPackage parent = null;
    protected List<JavaScriptPackage> children = new LinkedList<JavaScriptPackage>();
    protected String name = null;
    protected List<JavaScriptClass> javaScriptClasses = new LinkedList<JavaScriptClass>();
    
    public JavaScriptPackage(String packagePath) {
        List<String> packageTokens = StringUtil.split(packagePath, ".");
            if (UtilValidate.isNotEmpty(packageTokens)) {
            this.name = packageTokens.remove(packageTokens.size() - 1);
            String parentPackagePath = StringUtil.join(packageTokens, ".");
            this.parent = JavaScriptFactory.getJavaScriptPackage(parentPackagePath);
            
            if (UtilValidate.isNotEmpty(this.parent)) {
                this.parent.getChildren().add(this);
            }
            
            if (packagePath.indexOf(".") < 0) {
                JavaScriptFactory.addRootJavaScriptPackage(this);
            }
        } else {
            this.name = packagePath;
            JavaScriptFactory.addRootJavaScriptPackage(this);
        }
    }
    
    public void addJavaScriptClass(Element element) {
        String className = UtilXml.elementAttribute(element, "name", null);
        JavaScriptClass javaScriptClass = new JavaScriptClass(this, className);
        List<? extends Element> javaScriptMethodElements = UtilXml.childElementList(element, "javascript-method");
        for (Element javaScriptMethodElement : javaScriptMethodElements) {
            javaScriptClass.addJavaScriptMethod(javaScriptMethodElement);
        }
    }
    
    public List<JavaScriptPackage> getChildren() {
        return children;
    }
    
    public String rawString() {
        String rawString = name + " {\n";
        for (JavaScriptPackage javaScriptPackage : getChildren()) {
            rawString += javaScriptPackage.rawString();
        }
        rawString += "\n}\n";
        return rawString;
    }
}
