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
import java.net.URL;
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
public class ModelNgDirectiveReader implements Serializable {

    public final static String module = ModelNgDirectiveReader.class.getName();
    
    protected boolean isFromURL;
    protected URL readerURL = null;
    protected ResourceHandler handler = null;
    
    public static Map<String, ModelNgDirective> getModelNgDirectiveMap(ResourceHandler handler) {
        ModelNgDirectiveReader reader = new ModelNgDirectiveReader(false, null, handler);
        return reader.getModelNgDirectives();
    }
    
    private ModelNgDirectiveReader(boolean isFromURL, URL readerURL, ResourceHandler handler) {
        this.isFromURL = isFromURL;
        this.readerURL = readerURL;
        this.handler = handler;
    }
    
    private Map<String, ModelNgDirective> getModelNgDirectives() {
        UtilTimer utilTimer = new UtilTimer();
        Document document = null;
        Element docElement = document.getDocumentElement();
        if (docElement == null) {
            return null;
        }
        
        
        Map<String, ModelNgDirective> modelNgDirectives = new LinkedHashMap<String, ModelNgDirective>();
        
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
            if (this.isFromURL) {
                utilTimer.timerString("Before start of ngDirectives loop in file " + readerURL);
            } else {
                utilTimer.timerString("Before start of ngDirectives loop in " + handler);
            }

            do {
                if (curChild.getNodeType() == Node.ELEMENT_NODE && "ng-directive".equals(curChild.getNodeName())) {
                    i++;
                    Element curNgDirectiveElement = (Element) curChild;
                    String ngDirectiveName = UtilXml.checkEmpty(curNgDirectiveElement.getAttribute("name"));

                    // check to see if NgDirevtive with same name has already been read
                    if (modelNgDirectives.containsKey(ngDirectiveName)) {
                        Debug.logWarning("WARNING: NgDirective " + ngDirectiveName + " is defined more than once, " +
                            "most recent will over-write previous definition(s)", module);
                    }

                    ModelNgDirective ngDirective = createModelNgDirective(curNgDirectiveElement, resourceLocation);

                    if (ngDirective != null) {
                    } else {
                        Debug.logWarning(
                            "-- -- NgDirective ERROR:getModelNgDirective: Could not create ngDirective for : ngDirectiveName" +
                            ngDirectiveName, module);
                    }

                }
            } while ((curChild = curChild.getNextSibling()) != null);
        } else {
            Debug.logWarning("No child nodes found.", module);
        }
        
        if (this.isFromURL) {
            utilTimer.timerString("Finished file " + readerURL + " - Total NgDirectives: " + i + " FINISHED");
            Debug.logImportant("Loaded [" + StringUtil.leftPad(Integer.toString(i), 3) + "] NgDirectives from " + readerURL, module);
        } else {
            utilTimer.timerString("Finished document in " + handler + " - Total NgDirectives: " + i + " FINISHED");
            if (Debug.importantOn()) {
                Debug.logImportant("Loaded [" + StringUtil.leftPad(Integer.toString(i), 3) + "] NgDirectives from " + resourceLocation, module);
            }
        }
        
        return modelNgDirectives;
    }
    
    private ModelNgDirective createModelNgDirective(Element ngDirectiveElement, String resourceLocation) {
        ModelNgDirective ngDirective = new ModelNgDirective();
        
        return ngDirective;
    }
}
