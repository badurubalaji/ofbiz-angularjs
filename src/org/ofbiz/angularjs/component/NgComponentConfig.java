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
package org.ofbiz.angularjs.component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastList;

import org.ofbiz.angularjs.component.NgComponentException;
import org.ofbiz.base.component.ComponentResourceHandler;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class NgComponentConfig {
    
    public static final String module = NgComponentConfig.class.getName();
    public static final String NG_COMPONENT_XML_FILENAME = "ng-component.xml";
    
    protected static Map<String, NgComponentConfig> ngComponentConfigs = new LinkedHashMap<String, NgComponentConfig>();
    
    protected List<DirectiveResourceInfo> directiveResourceInfos = new LinkedList<NgComponentConfig.DirectiveResourceInfo>();
    protected List<FilterResourceInfo> filterResourceInfos = new LinkedList<NgComponentConfig.FilterResourceInfo>();
    protected List<ServiceResourceInfo> serviceResourceInfos = new LinkedList<NgComponentConfig.ServiceResourceInfo>();
    protected List<FactoryResourceInfo> factoryResourceInfos = new LinkedList<NgComponentConfig.FactoryResourceInfo>();
    protected List<ProviderResourceInfo> providerResourceInfos = new LinkedList<NgComponentConfig.ProviderResourceInfo>();
    protected List<ControllerResourceInfo> controllerResourceInfos = new LinkedList<NgComponentConfig.ControllerResourceInfo>();
    
    protected String globalName = null;
    protected String rootLocation = null;
    protected boolean enabled = true;
    
    protected NgComponentConfig (String globalName, String rootLocation) throws NgComponentException {
        this.globalName = globalName;
        if (!rootLocation.endsWith("/")) {
            rootLocation = rootLocation + "/";
        }
        this.rootLocation = rootLocation.replace('\\', '/');

        File rootLocationDir = new File(rootLocation);
        if (!rootLocationDir.exists()) {
            throw new NgComponentException("The component root location does not exist: " + rootLocation);
        }
        if (!rootLocationDir.isDirectory()) {
            throw new NgComponentException("The component root location is not a directory: " + rootLocation);
        }

        String xmlFilename = rootLocation + "/" + NG_COMPONENT_XML_FILENAME;
        URL xmlUrl = UtilURL.fromFilename(xmlFilename);
        if (UtilValidate.isEmpty(xmlUrl)) {
            Debug.logWarning("Could not find the " + NG_COMPONENT_XML_FILENAME + " configuration file in the component root location: " + rootLocation, module);
            return;
        }

        Document ngComponentDocument = null;
        try {
            ngComponentDocument = UtilXml.readXmlDocument(xmlUrl, true);
        } catch (SAXException e) {
            throw new NgComponentException("Error reading the component config file: " + xmlUrl, e);
        } catch (ParserConfigurationException e) {
            throw new NgComponentException("Error reading the component config file: " + xmlUrl, e);
        } catch (IOException e) {
            throw new NgComponentException("Error reading the component config file: " + xmlUrl, e);
        }

        Element ngComponentElement = ngComponentDocument.getDocumentElement();
        this.enabled = "true".equalsIgnoreCase(ngComponentElement.getAttribute("enabled"));
        
        // directive-resource - directiveResourceInfos
        for (Element curElement: UtilXml.childElementList(ngComponentElement, "directive-resource")) {
            DirectiveResourceInfo directiveResourceInfo = new DirectiveResourceInfo(this, curElement);
            this.directiveResourceInfos.add(directiveResourceInfo);
        }
        
        // filter-resource - filterResourceInfos
        for (Element curElement: UtilXml.childElementList(ngComponentElement, "filter-resource")) {
            FilterResourceInfo filterResourceInfo = new FilterResourceInfo(this, curElement);
            this.filterResourceInfos.add(filterResourceInfo);
        }
        
        // service-resource - serviceResourceInfos
        for (Element curElement: UtilXml.childElementList(ngComponentElement, "service-resource")) {
            ServiceResourceInfo serviceResourceInfo = new ServiceResourceInfo(this, curElement);
            this.serviceResourceInfos.add(serviceResourceInfo);
        }
        
        // factory-resource - factoryResourceInfos
        for (Element curElement: UtilXml.childElementList(ngComponentElement, "factory-resource")) {
            FactoryResourceInfo factoryResourceInfo = new FactoryResourceInfo(this, curElement);
            this.factoryResourceInfos.add(factoryResourceInfo);
        }
        
        // provider-resource - providerResourceInfos
        for (Element curElement: UtilXml.childElementList(ngComponentElement, "provider-resource")) {
            ProviderResourceInfo providerResourceInfo = new ProviderResourceInfo(this, curElement);
            this.providerResourceInfos.add(providerResourceInfo);
        }
        
        // controller-resource - controllerResourceInfos
        for (Element curElement: UtilXml.childElementList(ngComponentElement, "controller-resource")) {
            ControllerResourceInfo controllerResourceInfo = new ControllerResourceInfo(this, curElement);
            this.controllerResourceInfos.add(controllerResourceInfo);
        }
    }

    public static NgComponentConfig getNgComponentConfig(String globalName, String rootLocation) throws NgComponentException {
        NgComponentConfig ngComponentConfig = null;
        if (UtilValidate.isNotEmpty(globalName)) {
            ngComponentConfig = ngComponentConfigs.get(globalName);
        }
        if (UtilValidate.isEmpty(ngComponentConfig)) {
            if (UtilValidate.isNotEmpty(rootLocation)) {
                synchronized (NgComponentConfig.class) {
                    if (UtilValidate.isNotEmpty(globalName)) {
                        ngComponentConfig = ngComponentConfigs.get(globalName);
                    }
                    if (UtilValidate.isEmpty(ngComponentConfig)) {
                        ngComponentConfig = new NgComponentConfig(globalName, rootLocation);
                        if (ngComponentConfigs.containsKey(ngComponentConfig.getGlobalName())) {
                            Debug.logWarning("WARNING: Loading ng-component using a global name that already exists, will over-write: " + ngComponentConfig.getGlobalName(), module);
                        }
                        if (ngComponentConfig.enabled()) {
                            ngComponentConfigs.put(ngComponentConfig.getGlobalName(), ngComponentConfig);
                        }
                    }
                }
            }
        }
        
        return ngComponentConfig;
    }
    
    public static Collection<NgComponentConfig> getAllNgComponents() {
        Collection<NgComponentConfig> values = ngComponentConfigs.values();
        if (UtilValidate.isNotEmpty(values)) {
            return values;
        } else {
            Debug.logWarning("No ng components were found.", module);
            return FastList.newInstance();
        }
    }

    public String getGlobalName() {
        return this.globalName;
    }
    
    public boolean enabled() {
        return this.enabled;
    }
    
    public static List<DirectiveResourceInfo> getAllDirectiveResourceInfos() {
        List<DirectiveResourceInfo> directiveResourceInfos = new LinkedList<NgComponentConfig.DirectiveResourceInfo>();
        for (NgComponentConfig ngcc : getAllNgComponents()) {
            directiveResourceInfos.addAll(ngcc.getDirectiveResourceInfos());
        }
        return directiveResourceInfos;
    }
    
    public static List<FilterResourceInfo> getAllFilterResourceInfos() {
        List<FilterResourceInfo> filterResourceInfos = new LinkedList<NgComponentConfig.FilterResourceInfo>();
        for (NgComponentConfig ngcc : getAllNgComponents()) {
            filterResourceInfos.addAll(ngcc.getFilterResourceInfos());
        }
        return filterResourceInfos;
    }
    
    public static List<ServiceResourceInfo> getAllServiceResoruceInfos() {
        List<ServiceResourceInfo> serviceResourceInfos = new LinkedList<NgComponentConfig.ServiceResourceInfo>();
        for (NgComponentConfig ngcc : getAllNgComponents()) {
            serviceResourceInfos.addAll(ngcc.getServiceResourceInfos());
        }
        return serviceResourceInfos;
    }
    
    public static List<FactoryResourceInfo> getAllFactoryResourceInfos() {
        List<FactoryResourceInfo> factoryResourceInfos = new LinkedList<NgComponentConfig.FactoryResourceInfo>();
        for (NgComponentConfig ngcc : getAllNgComponents()) {
            factoryResourceInfos.addAll(ngcc.getFactoryResourceInfos());
        }
        return factoryResourceInfos;
    }
    
    public static List<ProviderResourceInfo> getAllProviderResourceInfos() {
        List<ProviderResourceInfo> providerResourceInfos = new LinkedList<NgComponentConfig.ProviderResourceInfo>();
        for (NgComponentConfig ngcc : getAllNgComponents()) {
            providerResourceInfos.addAll(ngcc.getProviderResourceInfos());
        }
        return providerResourceInfos;
    }
    
    public static List<ControllerResourceInfo> getAllControllerResourceInfos() {
        List<ControllerResourceInfo> controllerResourceInfos = new LinkedList<NgComponentConfig.ControllerResourceInfo>();
        for (NgComponentConfig ngcc : getAllNgComponents()) {
            controllerResourceInfos.addAll(ngcc.getControllerResourceInfos());
        }
        return controllerResourceInfos;
    }
    
    public List<DirectiveResourceInfo> getDirectiveResourceInfos() {
        return this.directiveResourceInfos;
    }
    
    public List<FilterResourceInfo> getFilterResourceInfos() {
        return this.filterResourceInfos;
    }
    
    public List<ServiceResourceInfo> getServiceResourceInfos() {
        return this.serviceResourceInfos;
    }
    
    public List<FactoryResourceInfo> getFactoryResourceInfos() {
        return this.factoryResourceInfos;
    }
    
    public List<ProviderResourceInfo> getProviderResourceInfos() {
        return this.providerResourceInfos;
    }
    
    public List<ControllerResourceInfo> getControllerResourceInfos() {
        return this.controllerResourceInfos;
    }
    
    public static class ResourceInfo {
        public NgComponentConfig ngComponentConfig;
        public String location;
        
        public ResourceInfo(NgComponentConfig ngComponentConfig, Element element) {
            this.ngComponentConfig = ngComponentConfig;
            this.location = element.getAttribute("location");
        }
        
        public ComponentResourceHandler createResourceHandler() {
            return new ComponentResourceHandler(ngComponentConfig.getGlobalName(), "main", location);
        }
        
        public String getLocation() {
            return location;
        }
    }
    
    public static class DirectiveResourceInfo extends ResourceInfo {

        public DirectiveResourceInfo(NgComponentConfig ngComponentConfig,
                Element element) {
            super(ngComponentConfig, element);
        }
        
    }
    
    public static class FilterResourceInfo extends ResourceInfo {

        public FilterResourceInfo(NgComponentConfig ngComponentConfig,
                Element element) {
            super(ngComponentConfig, element);
        }
        
    }
    
    public static class ServiceResourceInfo extends ResourceInfo {

        public ServiceResourceInfo(NgComponentConfig ngComponentConfig,
                Element element) {
            super(ngComponentConfig, element);
        }
        
    }
    
    public static class FactoryResourceInfo extends ResourceInfo {

        public FactoryResourceInfo(NgComponentConfig ngComponentConfig,
                Element element) {
            super(ngComponentConfig, element);
        }
        
    }
    
    public static class ProviderResourceInfo extends ResourceInfo {

        public ProviderResourceInfo(NgComponentConfig ngComponentConfig,
                Element element) {
            super(ngComponentConfig, element);
        }
        
    }
    
    public static class ControllerResourceInfo extends ResourceInfo {

        public ControllerResourceInfo(NgComponentConfig ngComponentConfig,
                Element element) {
            super(ngComponentConfig, element);
        }
        
    }
}
