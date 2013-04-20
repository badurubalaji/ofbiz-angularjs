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

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;

public class AngularJsContainer implements Container {
    
    public final static String module = AngularJsContainer.class.getName();
    
    protected String name = null;
    protected String configFile = null;

    @Override
    public void init(String[] args, String name, String configFile)
            throws ContainerException {
        this.name = name;
        this.configFile = configFile;
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

}
