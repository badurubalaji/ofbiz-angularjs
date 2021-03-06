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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.angularjs.application.ModelNgApplication;
import org.ofbiz.angularjs.application.ModelNgApplicationReader;
import org.ofbiz.angularjs.component.NgComponentConfig;
import org.ofbiz.angularjs.component.NgComponentConfig.ApplicationResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.DirectiveResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.FactoryResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.FilterResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.ModuleResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.ProviderResourceInfo;
import org.ofbiz.angularjs.component.NgComponentConfig.ServiceResourceInfo;
import org.ofbiz.angularjs.directive.ModelNgDirective;
import org.ofbiz.angularjs.directive.ModelNgDirectiveReader;
import org.ofbiz.angularjs.factory.ModelNgFactory;
import org.ofbiz.angularjs.factory.ModelNgFactoryReader;
import org.ofbiz.angularjs.filter.ModelNgFilter;
import org.ofbiz.angularjs.filter.ModelNgFilterReader;
import org.ofbiz.angularjs.module.ModelNgModule;
import org.ofbiz.angularjs.module.ModelNgModuleReader;
import org.ofbiz.angularjs.provider.ModelNgProvider;
import org.ofbiz.angularjs.provider.ModelNgProviderReader;
import org.ofbiz.angularjs.service.ModelNgService;
import org.ofbiz.angularjs.service.ModelNgServiceReader;
import org.ofbiz.base.util.UtilValidate;

public class NgModelDispatcherContext {

    public final static String module = NgModelDispatcherContext.class.getName();

    public static List<ModelNgDirective> getAllModelNgDirectives() {
        List<ModelNgDirective> modelNgDirectives = new LinkedList<ModelNgDirective>();
        for (DirectiveResourceInfo directiveResourceInfo : NgComponentConfig.getAllDirectiveResourceInfos()) {
            Map<String, ModelNgDirective> modelNgDirectiveMap = ModelNgDirectiveReader.getModelNgDirectiveMap(directiveResourceInfo.createResourceHandler());
            modelNgDirectives.addAll(modelNgDirectiveMap.values());
        }
        return modelNgDirectives;
    }

    public static List<ModelNgService> getAllModelNgServices() {
        List<ModelNgService> modelNgServices = new LinkedList<ModelNgService>();
        for (ServiceResourceInfo serviceResourceInfo : NgComponentConfig.getAllServiceResoruceInfos()) {
            Map<String, ModelNgService> modelNgServiceMap = ModelNgServiceReader.getModelNgServiceMap(serviceResourceInfo.createResourceHandler());
            modelNgServices.addAll(modelNgServiceMap.values());
        }
        return modelNgServices;
    }

    public static List<ModelNgFilter> getAllModelNgFilters() {
        List<ModelNgFilter> modelNgFilters = new LinkedList<ModelNgFilter>();
        for (FilterResourceInfo filterResourceInfo : NgComponentConfig.getAllFilterResourceInfos()) {
            Map<String, ModelNgFilter> modelNgFilterMap = ModelNgFilterReader.getModelNgFilterMap(filterResourceInfo.createResourceHandler());
            modelNgFilters.addAll(modelNgFilterMap.values());
        }
        return modelNgFilters;
    }

    public static List<ModelNgProvider> getAllModelNgProviders() {
        List<ModelNgProvider> modelNgProviders = new LinkedList<ModelNgProvider>();
        for (ProviderResourceInfo providerResourceInfo : NgComponentConfig.getAllProviderResourceInfos()) {
            Map<String, ModelNgProvider> modelNgProviderMap = ModelNgProviderReader.getModelNgProviderMap(providerResourceInfo.createResourceHandler());
            modelNgProviders.addAll(modelNgProviderMap.values());
        }
        return modelNgProviders;
    }

    public static List<ModelNgFactory> getAllModelNgFactories() {
        List<ModelNgFactory> modelNgFactories = new LinkedList<ModelNgFactory>();
        for (FactoryResourceInfo factoryResourceInfo : NgComponentConfig.getAllFactoryResourceInfos()) {
            Map<String, ModelNgFactory> modelNgFactoryMap = ModelNgFactoryReader.getModelNgFactoryMap(factoryResourceInfo.createResourceHandler());
            modelNgFactories.addAll(modelNgFactoryMap.values());
        }
        return modelNgFactories;
    }

    public static List<ModelNgModule> getAllModelNgModules() {
        List<ModelNgModule> modelNgModules = new LinkedList<ModelNgModule>();
        for (ModuleResourceInfo moduleResourceInfo : NgComponentConfig.getAllModuleResourceInfos()) {
            Map<String, ModelNgModule> modelNgModuleMap = ModelNgModuleReader.getModelNgModuleMap(moduleResourceInfo.createResourceHandler());
            if (UtilValidate.isNotEmpty(modelNgModuleMap)) {
                modelNgModules.addAll(modelNgModuleMap.values());
            }
        }
        return modelNgModules;
    }

    public static List<ModelNgApplication> getAllModelNgApplications() {
        List<ModelNgApplication> modelNgApplications = new LinkedList<ModelNgApplication>();
        for (ApplicationResourceInfo applicationResourceInfo : NgComponentConfig.getAllApplicationResourceInfos()) {
            Map<String, ModelNgApplication> applicationNgModuleMap = ModelNgApplicationReader.getModelNgApplicationMap(applicationResourceInfo.createResourceHandler());
            modelNgApplications.addAll(applicationNgModuleMap.values());
        }
        return modelNgApplications;
    }
}
