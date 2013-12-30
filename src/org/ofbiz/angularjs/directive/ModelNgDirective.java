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
package org.ofbiz.angularjs.directive;

import org.ofbiz.angularjs.model.AbstractModelNg;

@SuppressWarnings("serial")
public class ModelNgDirective extends AbstractModelNg {
    
    public final static String module = ModelNgDirective.class.getName();
    public final static String SIMPLE_TYPE = "simple";
    public final static String JAVASCRIPT_TYPE = "javascript";
    
    public int priority;
    public boolean replace;
    public boolean transclude;
    public String require;
    public String restrict;
    public String scope;
    public String controllerName;
    public String preCompileName;
    public String postCompileName;
    public String linkName;
    
    public String getType() {
        if (location.endsWith(".js")) {
            return JAVASCRIPT_TYPE;
        } else {
            return SIMPLE_TYPE;
        }
    }

    @Override
    public String toString() {
        return "Directive[" + name + "]";
    }
}
