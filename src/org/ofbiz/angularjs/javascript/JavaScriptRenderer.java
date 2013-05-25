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

import java.io.IOException;
import java.util.List;

public class JavaScriptRenderer {

    public final static String module = JavaScriptRenderer.class.getName();
    
    protected Appendable writer;
    
    public JavaScriptRenderer(Appendable writer) {
        this.writer = writer;
    }
    
    public void render(List<JavaScriptPackage> javaScriptPackages) throws IOException {
        // render packages
        StringBuilder packageBuilder = new StringBuilder();
        for (JavaScriptPackage javaScriptPackage : javaScriptPackages) {
            packageBuilder.append(javaScriptPackage.rawString());
        }
        // remove the last comma ','
        writer.append(packageBuilder.toString().subSequence(0, packageBuilder.toString().length() - 2));
        writer.append("\n");
        
        // render static methods
        for (JavaScriptMethod javaScriptMethod : JavaScriptFactory.getStaticJavaScriptMethods()) {
            writer.append(javaScriptMethod.rawString());
        }
    }
}
