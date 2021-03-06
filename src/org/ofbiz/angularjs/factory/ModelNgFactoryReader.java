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
package org.ofbiz.angularjs.factory;

import java.util.Map;

import org.ofbiz.angularjs.model.AbstractModelNg;
import org.ofbiz.angularjs.model.AbstractModelNgReader;
import org.ofbiz.base.config.ResourceHandler;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class ModelNgFactoryReader extends AbstractModelNgReader {

    protected ModelNgFactoryReader(ResourceHandler handler) {
        super("ng-factory", handler);
    }
    
    public static Map<String, ModelNgFactory> getModelNgFactoryMap(ResourceHandler handler) {
        ModelNgFactoryReader reader = new ModelNgFactoryReader(handler);
        return UtilGenerics.cast(reader.getModelNgs());
    }

    @Override
    protected AbstractModelNg createModelNg(Element element,
            String resourceLocation) {
        ModelNgFactory ngFactory = new ModelNgFactory();
        ngFactory.name = UtilXml.checkEmpty(element.getAttribute("name")).intern();
        ngFactory.location = UtilXml.checkEmpty(element.getAttribute("location")).intern();
        ngFactory.invoke = UtilXml.checkEmpty(element.getAttribute("invoke")).intern();
        return ngFactory;
    }

}
