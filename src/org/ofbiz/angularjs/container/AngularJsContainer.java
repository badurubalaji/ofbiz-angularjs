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
package org.ofbiz.angularjs.container;

import java.util.Collection;

import org.ofbiz.angularjs.component.NgComponentConfig;
import org.ofbiz.angularjs.component.NgComponentException;
import org.ofbiz.base.component.AlreadyLoadedException;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;

public class AngularJsContainer implements Container {
    
    public final static String module = AngularJsContainer.class.getName();
    
    protected String name = null;
    protected String configFile = null;

    @Override
    public void init(String[] args, String name, String configFile)
            throws ContainerException {
        this.name = name;
        this.configFile = configFile;

        // load the components
        try {
            loadComponents();
        } catch (AlreadyLoadedException e) {
            throw new ContainerException(e);
        } catch (NgComponentException e) {
            throw new ContainerException(e);
        }
    }

    @Override
    public boolean start() throws ContainerException {
        return false;
    }

    @Override
    public void stop() throws ContainerException {

    }

    @Override
    public String getName() {
        return name;
    }


    public synchronized void loadComponents() throws AlreadyLoadedException, NgComponentException {
        Collection<ComponentConfig> components = ComponentConfig.getAllComponents();
        for (ComponentConfig component : components) {
            component.getRootLocation();
            NgComponentConfig ngComponentConfig = NgComponentConfig.getNgComponentConfig(component.getComponentName(), component.getRootLocation());
            loadComponent(ngComponentConfig);
        }
    }
    
    private void loadComponent(NgComponentConfig ngComponentConfig) {
        Debug.logInfo("Loading Ng component : [" + ngComponentConfig.getGlobalName() + "]", module);
    }
}
