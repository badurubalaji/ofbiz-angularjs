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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.angularjs.component.NgComponentConfig;
import org.ofbiz.angularjs.component.NgComponentConfig.DirectiveResourceInfo;

@SuppressWarnings("serial")
public class NgD‏irectiveDispatchContext implements Serializable {

    public final static String module = NgD‏irectiveDispatchContext.class.getName();
    
    public static List<ModelNgDirective> getAllModelNgDirectives() {
        List<ModelNgDirective> modelNgDirectives = new LinkedList<ModelNgDirective>();
        for (DirectiveResourceInfo directiveResourceInfo : NgComponentConfig.getAllDirectiveResourceInfos()) {
            Map<String, ModelNgDirective> modelNgDirectiveMap = ModelNgDirectiveReader.getModelNgDirectiveMap("ng-directive", directiveResourceInfo.createResourceHandler());
            modelNgDirectives.addAll(modelNgDirectiveMap.values());
        }
        return modelNgDirectives;
    }
}
