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
package org.ofbiz.angularjs.widget;

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

public class ViewFactory {
    
    public final static String module = ViewFactory.class.getName();
    
    public static ModelView getViewFromLocation(String resourceName, String viewName)
            throws IOException, SAXException, ParserConfigurationException {
        Map<String, ModelView> modelViewMap = getViewsFromLocation(resourceName);
        ModelView modelView = modelViewMap.get(viewName);
        if (UtilValidate.isEmpty(modelView)) {
            throw new IllegalArgumentException("Could not find vew with name [" + viewName + "] in class resource [" + resourceName + "]");
        }
        return modelView;
    }
    
    public static Map<String, ModelView> getViewsFromLocation(String resourceName)
            throws IOException, SAXException, ParserConfigurationException {

        Map<String, ModelView> modelViewMap = new HashMap<String, ModelView>();
        synchronized (ViewFactory.class) {
            if (UtilValidate.isEmpty(modelViewMap)) {
                long startTime = System.currentTimeMillis();
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if (loader == null) {
                    loader = ViewFactory.class.getClassLoader();
                }
    
                URL screenFileUrl = null;
                screenFileUrl = FlexibleLocation.resolveLocation(resourceName, loader);
                if (screenFileUrl == null) {
                    throw new IllegalArgumentException("Could not resolve location to URL: " + resourceName);
                }
                Document screenFileDoc = UtilXml.readXmlDocument(screenFileUrl, true, true);
                modelViewMap = readViewDocument(screenFileDoc, resourceName);
                double totalSeconds = (System.currentTimeMillis() - startTime)/1000.0;
                Debug.logInfo("Got view in " + totalSeconds + "s from: " + screenFileUrl.toExternalForm(), module);
            }
        }
        
        if (UtilValidate.isEmpty(modelViewMap)) {
            throw new IllegalArgumentException("Could not find vew file with name [" + resourceName + "]");
        }
        return modelViewMap;
    }
    
    public static Map<String, ModelView> readViewDocument(Document viewFileDocument, String sourceLocation) {
        Map<String, ModelView> modelViewMap = new HashMap<String, ModelView>();
        if (UtilValidate.isNotEmpty(viewFileDocument)) {
            Element rootElement = viewFileDocument.getDocumentElement();
            List<? extends Element> viewElements = UtilXml.childElementList(rootElement, "ng-view");
            for (Element viewElement : viewElements) {
                ModelView modelView = new ModelView(viewElement, modelViewMap, sourceLocation);
                modelViewMap.put(modelView.getName(), modelView);
            }
        }
        return modelViewMap;
    }
}
