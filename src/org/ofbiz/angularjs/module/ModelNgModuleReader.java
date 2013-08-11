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
package org.ofbiz.angularjs.module;

import java.util.List;
import java.util.Map;

import org.ofbiz.angularjs.model.AbstractModelNg;
import org.ofbiz.angularjs.model.AbstractModelNgReader;
import org.ofbiz.base.config.ResourceHandler;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class ModelNgModuleReader extends AbstractModelNgReader {

    public final static String module = ModelNgModuleReader.class.getName();
    
    protected ModelNgModuleReader(ResourceHandler handler) {
        super("ng-module", handler);
    }
    
    public static Map<String, ModelNgModule> getModelNgModuleMap(ResourceHandler handler) {
        ModelNgModuleReader reader = new ModelNgModuleReader(handler);
        return UtilGenerics.cast(reader.getModelNgs());
    }

    @Override
    protected AbstractModelNg createModelNg(Element element,
            String resourceLocation) {
        ModelNgModule ngModule = new ModelNgModule();
        ngModule.name = UtilXml.checkEmpty(element.getAttribute("name")).intern();
        ngModule.location = UtilXml.checkEmpty(element.getAttribute("location")).intern();
        ngModule.invoke = UtilXml.checkEmpty(element.getAttribute("invoke")).intern();
        
        // read javaScript elements
        List<? extends Element> javaScriptElements = UtilXml.childElementList(element, "javaScript");
        for (Element javaScriptElement : javaScriptElements) {
            String path = UtilXml.elementAttribute(javaScriptElement, "path", null);
            boolean fullPath = Boolean.valueOf(UtilXml.elementAttribute(javaScriptElement, "full-path", "false"));
            if (UtilValidate.isNotEmpty(path)) {
                ModelNgModule.ModelJavaScript modelJavaScript = new ModelNgModule.ModelJavaScript();
                modelJavaScript.path = path;
                modelJavaScript.fullPath = fullPath;
                ngModule.modelJavaScripts.add(modelJavaScript);
            }
        }
        
        // read styleSheet elements
        List<? extends Element> styleSheetElements = UtilXml.childElementList(element, "styleSheet");
        for (Element styleSheetElement : styleSheetElements) {
            String path = UtilXml.elementAttribute(styleSheetElement, "path", null);
            boolean fullPath = Boolean.valueOf(UtilXml.elementAttribute(styleSheetElement, "full-path", "false"));
            if (UtilValidate.isNotEmpty(path)) {
                ModelNgModule.ModelStyleSheet modelStyleSheet = new ModelNgModule.ModelStyleSheet();
                modelStyleSheet.path = path;
                modelStyleSheet.fullPath = fullPath;
                ngModule.modelStyleSheets.add(modelStyleSheet);
            }
        }
        
        return ngModule;
    }

}
