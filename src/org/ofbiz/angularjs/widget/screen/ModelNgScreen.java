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
package org.ofbiz.angularjs.widget.screen;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.widget.screen.ModelScreen;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class ModelNgScreen extends ModelScreen {
    
    public final static String module = ModelNgScreen.class.getName();
    
    protected String name = null;
    protected Map<String, ModelNgScreen> modelNgScreenMap = null;
    protected String sourceLocation = null;
    protected Element ngScreenElement = null;
    protected boolean useTransaction;
    protected boolean useCache;
    
    private Transformer transformer = null;
    
    public ModelNgScreen(Element ngScreenElement, Map<String, ModelNgScreen> modelNgScreenMap, String sourceLocation) {
        this.ngScreenElement = ngScreenElement;
        this.modelNgScreenMap = modelNgScreenMap;
        this.sourceLocation = sourceLocation;
        this.name = ngScreenElement.getAttribute("name");
        
     // read in the section, which will read all sub-widgets too
        Element sectionElement = UtilXml.firstChildElement(ngScreenElement, "section");
        if (sectionElement == null) {
            throw new IllegalArgumentException("No section found for the ng-screen definition with name: " + this.name);
        }
        // this.section = ?
        
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (Exception e) {
            Debug.logError(e, module);
        }
    }

    public String getName() {
        return name;
    }
    
    public void renderNgScreenString(Appendable writer, Map<String, Object> context, NgScreenStringRenderer ngScreenStringRenderer) {
        try {
            List<? extends Element> childElements = UtilXml.childElementList(ngScreenElement);
            for (Element childElement : childElements) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                StreamResult result = new StreamResult(buffer);
                DOMSource domSource = new DOMSource(childElement);
                transformer.transform(domSource, result);
                writer.append(buffer.toString());
            }
        } catch (Exception e) {
            Debug.logError(e, module);
        }
    }
}
