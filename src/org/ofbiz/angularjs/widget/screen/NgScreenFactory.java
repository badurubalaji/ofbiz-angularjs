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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class NgScreenFactory {
    
    public final static String module = NgScreenFactory.class.getName();
    
    public static ModelNgScreen getNgScreenFromLocation(String resourceName, String ngScreenName)
            throws IOException, SAXException, ParserConfigurationException {
        Map<String, ModelNgScreen> modelNgScreenMap = getNgScreensFromLocation(resourceName);
        ModelNgScreen modelNgScreen = modelNgScreenMap.get(ngScreenName);
        if (UtilValidate.isEmpty(modelNgScreen)) {
            throw new IllegalArgumentException("Could not find ng-screen with name [" + ngScreenName + "] in class resource [" + resourceName + "]");
        }
        return modelNgScreen;
    }
    
    public static Map<String, ModelNgScreen> getNgScreensFromLocation(String resourceName)
            throws IOException, SAXException, ParserConfigurationException {

        Map<String, ModelNgScreen> modelNgScreenMap = new HashMap<String, ModelNgScreen>();
        synchronized (NgScreenFactory.class) {
            if (UtilValidate.isEmpty(modelNgScreenMap)) {
                long startTime = System.currentTimeMillis();
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if (loader == null) {
                    loader = NgScreenFactory.class.getClassLoader();
                }
    
                URL screenFileUrl = null;
                screenFileUrl = FlexibleLocation.resolveLocation(resourceName, loader);
                if (screenFileUrl == null) {
                    throw new IllegalArgumentException("Could not resolve location to URL: " + resourceName);
                }
                Document screenFileDoc = UtilXml.readXmlDocument(screenFileUrl, true, true);
                modelNgScreenMap = readNgScreenDocument(screenFileDoc, resourceName);
                double totalSeconds = (System.currentTimeMillis() - startTime)/1000.0;
                Debug.logInfo("Got ng-screen in " + totalSeconds + "s from: " + screenFileUrl.toExternalForm(), module);
            }
        }
        
        if (UtilValidate.isEmpty(modelNgScreenMap)) {
            throw new IllegalArgumentException("Could not find ng-screen file with name [" + resourceName + "]");
        }
        return modelNgScreenMap;
    }
    
    public static Map<String, ModelNgScreen> readNgScreenDocument(Document ngScreenFileDocument, String sourceLocation) {
        Map<String, ModelNgScreen> modelNgScreenMap = new HashMap<String, ModelNgScreen>();
        if (UtilValidate.isNotEmpty(ngScreenFileDocument)) {
            Element rootElement = ngScreenFileDocument.getDocumentElement();
            List<? extends Element> ngScreenElements = UtilXml.childElementList(rootElement, "ng-screen");
            for (Element ngScreenElement : ngScreenElements) {
                ModelNgScreen modelNgScreen = new ModelNgScreen(ngScreenElement, modelNgScreenMap, sourceLocation);
                modelNgScreenMap.put(modelNgScreen.getName(), modelNgScreen);
            }
        }
        return modelNgScreenMap;
    }
}
