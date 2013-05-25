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

import org.ofbiz.base.util.UtilValidate;
import org.w3c.dom.Element;

public class JavaScriptClass {

    public final static String module = JavaScriptClass.class.getName();

    protected JavaScriptPackage javaScriptPackage = null;
    protected String name = null;
    protected JavaScriptMethod constructorMethod = null;
    protected List<JavaScriptMethod> javaScriptMethods = new LinkedList<JavaScriptMethod>();
    
    public JavaScriptClass(JavaScriptPackage javaScriptPackage, String name) {
        this.javaScriptPackage = javaScriptPackage;
        this.name = name;
    }
    
    public void addJavaScriptMethod(Element javaScriptMethodElement) {
        JavaScriptMethod javaScriptMethod = new JavaScriptMethod(this, javaScriptMethodElement);
        if (name.equals(javaScriptMethod.getName())) {
            constructorMethod = javaScriptMethod;
        } else {
            javaScriptMethods.add(javaScriptMethod);
            
            if (javaScriptMethod.isStatic()) {
                JavaScriptFactory.addStaticJavaScriptMethod(javaScriptMethod);
            }
        }
    }
    
    public String getFullName() {
        return javaScriptPackage.getPackagePath() + "." + name;
    }
    
    public String rawString() {
        String rawString = "function " + name + "() {\n";
        
        // render methods
        for (JavaScriptMethod javaScriptMethod : javaScriptMethods) {
            if (!javaScriptMethod.isStatic()) {
                rawString += javaScriptMethod.rawString();
            }
        }
        
        // render constructor method
        if (UtilValidate.isNotEmpty(constructorMethod)) {
            rawString += constructorMethod.getBody();
        }
        
        rawString += "\n}\n";
        return rawString;
    }
}
