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
package org.ofbiz.angularjs.service;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ofbiz.base.config.ResourceHandler;

@SuppressWarnings("serial")
public class ModelNgServiceReader implements Serializable {

    public static final String module = ModelNgServiceReader.class.getName();
    
    public static Map<String, ModelNgService> getModelNgServiceMap() {
        ModelNgServiceReader reader = new ModelNgServiceReader(null);
        return reader.getModelNgServices();
    }
    
    private ModelNgServiceReader(ResourceHandler handler) {
        
    }
    
    private Map<String, ModelNgService> getModelNgServices() {
        Map<String, ModelNgService> modelNgServices = new LinkedHashMap<String, ModelNgService>();
        return modelNgServices;
    }
}
