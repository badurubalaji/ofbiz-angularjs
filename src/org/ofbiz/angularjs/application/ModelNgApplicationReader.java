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
package org.ofbiz.angularjs.application;

import java.util.List;
import java.util.Map;

import org.ofbiz.angularjs.model.AbstractModelNg;
import org.ofbiz.angularjs.model.AbstractModelNgReader;
import org.ofbiz.base.config.ResourceHandler;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class ModelNgApplicationReader extends AbstractModelNgReader {

    protected ModelNgApplicationReader(ResourceHandler handler) {
        super("ng-application", handler);
    }
    
    public static Map<String , ModelNgApplication> getModelNgApplicationMap(ResourceHandler handler) {
        ModelNgApplicationReader reader = new ModelNgApplicationReader(handler);
        return UtilGenerics.cast(reader.getModelNgs());
    }

    @Override
    protected AbstractModelNg createModelNg(Element element,
            String resourceLocation) {
        ModelNgApplication ngApplication = new ModelNgApplication();
        ngApplication.name = UtilXml.checkEmpty(element.getAttribute("name")).intern();
        ngApplication.defaultPath = UtilXml.checkEmpty(element.getAttribute("default-path")).intern();
        List<? extends Element> stateElements = UtilXml.childElementList(element, "state");
        for (Element stateElement : stateElements) {
            String stateName = UtilXml.elementAttribute(stateElement, "name", null);
            String url = UtilXml.elementAttribute(stateElement, "url", null);
            
            ModelNgState modelNgState = new ModelNgState();
            modelNgState.name = stateName;
            modelNgState.url = url;

            List<? extends Element> viewElements = UtilXml.childElementList(stateElement, "view");
            for (Element viewElement : viewElements) {
                String viewName = UtilXml.elementAttribute(viewElement, "name", null);
                String target = UtilXml.elementAttribute(viewElement, "target", null);
                String controller = UtilXml.elementAttribute(viewElement, "controller", null);
                
                ModelNgView modelNgView = new ModelNgView();
                modelNgView.name  = viewName;
                modelNgView.target = target;
                modelNgView.controller = controller;
                
                modelNgState.addView(modelNgView);
            }
            
            ngApplication.addState(modelNgState);
        }
        return ngApplication;
    }

}
