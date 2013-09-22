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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;

public class JavaScriptClass {

    public final static String module = JavaScriptClass.class.getName();

    protected JavaScriptPackage javaScriptPackage = null;
    protected String name = null;
    protected Function function = null;
    protected Context context = null;
    protected String rawFunction = null;
    protected String rawBody = null;
    protected String constructorArgument = "";
    
    public JavaScriptClass(JavaScriptPackage javaScriptPackage, String name, Function function, Context context) {
        this.javaScriptPackage = javaScriptPackage;
        this.name = name;
        this.function = function;
        this.context = context;
        this.rawFunction = context.decompileFunction(function, 4);
        this.rawBody = context.decompileFunctionBody(function, 4);
        
        // get constructor argument
        Pattern pattern = Pattern.compile(".*?function.*?" + this.name + ".*?(\\()(.*?)(\\))");
        Matcher matcher = pattern.matcher(rawFunction);
        while (matcher.find()) {
            constructorArgument = matcher.group(2);
            break;
        }
    }
    
    public String getFullName() {
        return javaScriptPackage.getPackagePath() + "." + name;
    }
    
    public String getConstructorArgument() {
        return constructorArgument;
    }
    
    public String rawString() {
        String rawString = this.name + ": function(" + constructorArgument + ") {" + rawBody + "},\n";
        return rawString;
    }
}
