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

import java.util.Map;

import org.ofbiz.angularjs.model.AbstractModelNg;
import org.ofbiz.angularjs.model.AbstractModelNgReader;
import org.ofbiz.base.config.ResourceHandler;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class ModelNgDirectiveReader extends AbstractModelNgReader {

    public final static String module = ModelNgDirectiveReader.class.getName();
    
    public ModelNgDirectiveReader(ResourceHandler handler) {
        super("ng-directive", handler);
    }

    public static Map<String, ModelNgDirective> getModelNgDirectiveMap(ResourceHandler handler) {
        ModelNgDirectiveReader reader = new ModelNgDirectiveReader(handler);
        return UtilGenerics.cast(reader.getModelNgs());
    }

    @Override
    protected AbstractModelNg createModelNg(Element element, String resourceLocation) {
            ModelNgDirective ngDirective = new ModelNgDirective();
            ngDirective.name = UtilXml.checkEmpty(element.getAttribute("name")).intern();
            ngDirective.location = UtilXml.checkEmpty(element.getAttribute("location")).intern();
            ngDirective.invoke = UtilXml.checkEmpty(element.getAttribute("invoke")).intern();
            ngDirective.priority = Integer.valueOf(UtilXml.checkEmpty(element.getAttribute("priority")).intern());
            ngDirective.replace = Boolean.valueOf(UtilXml.checkEmpty(element.getAttribute("replace")).intern());
            ngDirective.transclude = Boolean.valueOf(UtilXml.checkEmpty(element.getAttribute("transclude")).intern());
            ngDirective.require = UtilXml.checkEmpty(element.getAttribute("require")).intern();
            ngDirective.restrict = UtilXml.checkEmpty(element.getAttribute("restrict")).intern();
            ngDirective.scope = UtilXml.checkEmpty(element.getAttribute("scope")).intern();
            ngDirective.controllerName = UtilXml.checkEmpty(element.getAttribute("controller-name")).intern();
            ngDirective.preCompileName = UtilXml.checkEmpty(element.getAttribute("pre-compile-name")).intern();
            ngDirective.postCompileName = UtilXml.checkEmpty(element.getAttribute("post-compile-name")).intern();
            ngDirective.linkName = UtilXml.checkEmpty(element.getAttribute("link-name")).intern();
            return ngDirective;
    }
}
