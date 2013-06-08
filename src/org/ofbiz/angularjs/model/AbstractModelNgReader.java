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
package org.ofbiz.angularjs.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ofbiz.base.config.GenericConfigException;
import org.ofbiz.base.config.ResourceHandler;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilTimer;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import freemarker.template.utility.StringUtil;

@SuppressWarnings("serial")
public abstract class AbstractModelNgReader implements Serializable {

    public final static String module= AbstractModelNgReader.class.getName();
    
    private String tagName = null;
    private ResourceHandler handler = null;
    
    protected AbstractModelNgReader(String tagName, ResourceHandler handler) {
        this.tagName = tagName;
        this.handler = handler;
    }
    
    protected Map<String, AbstractModelNg> getModelNgs() {
        UtilTimer utilTimer = new UtilTimer();
        Document document = null;
        // utilTimer.timerString("Before getDocument in " + handler);
        try {
            document = handler.getDocument();
        } catch (GenericConfigException e) {
            Debug.logError(e, "Error getting XML document from resource", module);
            return null;
        }
        
        Map<String, AbstractModelNg> modelNgs = new LinkedHashMap<String, AbstractModelNg>();

        Element docElement = document.getDocumentElement();
        if (docElement == null) {
            return null;
        }
        
        docElement.normalize();
        
        String resourceLocation = handler.getLocation();
        try {
            resourceLocation = handler.getURL().toExternalForm();
        } catch (GenericConfigException e) {
            Debug.logError(e, "Could not get resource URL", module);
        }
        
        int i = 0;
        Node curChild = docElement.getFirstChild();
        if (curChild != null) {
            utilTimer.timerString("Before start of " + tagName + "s loop in " + handler);

            do {
                if (curChild.getNodeType() == Node.ELEMENT_NODE && tagName.equals(curChild.getNodeName())) {
                    i++;
                    Element curNgElement = (Element) curChild;
                    String name = UtilXml.checkEmpty(curNgElement.getAttribute("name"));

                    // check to see if ng model with same name has already been read
                    if (modelNgs.containsKey(name)) {
                        Debug.logWarning("WARNING: " + tagName + " " + name + " is defined more than once, " +
                            "most recent will over-write previous definition(s)", module);
                    }

                    AbstractModelNg modelNg = createModelNg(curNgElement, resourceLocation);

                    if (modelNg != null) {
                        modelNgs.put(name, modelNg);
                    } else {
                        Debug.logWarning("-- -- ERROR: Could not create " + tagName + " for : name" + name, module);
                    }

                }
            } while ((curChild = curChild.getNextSibling()) != null);
        } else {
            Debug.logWarning("No child nodes found.", module);
        }
        
        utilTimer.timerString("Finished document in " + handler + " - Total " + tagName + "s: " + i + " FINISHED");
        if (Debug.importantOn()) {
            Debug.logImportant("Loaded [" + StringUtil.leftPad(Integer.toString(i), 3) + "] " + tagName + "s from " + resourceLocation, module);
        }
        
        return modelNgs;
    }
    
    protected abstract AbstractModelNg createModelNg(Element element, String resourceLocation);
}
