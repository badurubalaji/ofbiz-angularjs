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

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.minilang.MiniLangElement;
import org.w3c.dom.Element;

public class JavaScriptMethod extends MiniLangElement {
    
    public static final String module = JavaScriptMethod.class.getName();
    
    protected JavaScriptClass javaScriptClass = null;
    protected String name = null;
    protected boolean isStatic = false;

    public JavaScriptMethod(Element element) {
        super(element, null);
        name = UtilXml.elementAttribute(element, "name", null);
        isStatic = "true".equals(UtilXml.elementAttribute(element, "is-static", null));
        Element attributesElement = UtilXml.firstChildElement(element, "attributes");
        List<? extends Element> attributeElements = UtilXml.childElementList(attributesElement, "attributes");
        for (Element attributeElement : attributeElements) {
            String attributeName = UtilXml.elementAttribute(attributeElement, "name", null);
            Debug.logInfo("-*- Attribute: " + attributeName, module);
        }
        
        Element bodyElement = UtilXml.firstChildElement(element, "body");
        String script = UtilXml.elementValue(bodyElement);
        Debug.logInfo("// Script: " + script, module);
    }

}
