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

import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

public class JavaScriptPackage {

    public final static String module = JavaScriptPackage.class.getName();

    protected JavaScriptPackage parent = null;
    protected List<JavaScriptPackage> children = new LinkedList<JavaScriptPackage>();
    protected String name = null;
    protected List<JavaScriptClass> javaScriptClasses = new LinkedList<JavaScriptClass>();
    
    public JavaScriptPackage(String name) {
        this.name = name;
    }
    
    public void addJavaScriptClass(Element element) {
        String className = UtilXml.elementAttribute(element, "name", null);
        JavaScriptClass javaScriptClass = new JavaScriptClass(this, className);
        List<? extends Element> javaScriptMethodElements = UtilXml.childElementList(element, "javascript-method");
        for (Element javaScriptMethodElement : javaScriptMethodElements) {
            javaScriptClass.addJavaScriptMethod(javaScriptMethodElement);
        }
    }
}
