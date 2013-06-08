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

import java.util.LinkedList;
import java.util.List;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.minilang.MiniLangElement;
import org.w3c.dom.Element;

public class JavaScriptMethod extends MiniLangElement {
    
    public static final String module = JavaScriptMethod.class.getName();
    
    protected JavaScriptClass javaScriptClass = null;
    protected String name = null;
    protected String body = "";
    protected boolean isStatic = false;
    protected List<MethodAttribute> attributes = new LinkedList<MethodAttribute>();

    public JavaScriptMethod(JavaScriptClass javaScriptClass, Element element) {
        super(element, null);
        this.javaScriptClass = javaScriptClass;
        name = UtilXml.elementAttribute(element, "name", null);
        isStatic = "true".equals(UtilXml.elementAttribute(element, "is-static", null));
        Element attributesElement = UtilXml.firstChildElement(element, "attributes");
        if (UtilValidate.isNotEmpty(attributesElement)) {
            List<? extends Element> attributeElements = UtilXml.childElementList(attributesElement, "attribute");
            if (UtilValidate.isNotEmpty(attributeElements)) {
                for (Element attributeElement : attributeElements) {
                    MethodAttribute attribute = new MethodAttribute(attributeElement);
                    attributes.add(attribute);
                }
            }
        }
        
        Element bodyElement = UtilXml.firstChildElement(element, "body");
        if (UtilValidate.isNotEmpty(bodyElement)) {
            body = UtilXml.elementValue(bodyElement);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getBody() {
        return body;
    }
    
    public boolean isStatic() {
        return isStatic;
    }
    
    public String getInAttributeDeclaration() {
        List<String> attrNames = new LinkedList<String>();
        for (MethodAttribute attribute : attributes) {
            if ("IN".equals(attribute.mode)) {
                attrNames.add(attribute.name);
            }
        }
        
        String declarationStr = StringUtil.join(attrNames, ",");
        return UtilValidate.isNotEmpty(declarationStr) ? declarationStr : "";
    }
    
    public String rawString() {
        String rawString = "";
        
        if (isStatic) {
            rawString += javaScriptClass.getFullName() + "." + name + " = function(" + getInAttributeDeclaration() + ") {\n";
            rawString += body;
            rawString += "\n};\n";
        } else {
            rawString += "this." + name + " = function(" + getInAttributeDeclaration() + ") {\n";
            rawString += body;
            rawString += "\n}\n";
        }
        
        return rawString;
    }
    
    public class MethodAttribute {
        public String name;
        public String type;
        public String mode;
        
        public MethodAttribute(Element element) {
            this.name = UtilXml.elementAttribute(element, "name", null);
            this.type = UtilXml.elementAttribute(element, "type", null);
            this.mode = UtilXml.elementAttribute(element, "mode", null);
        }
    }

}
