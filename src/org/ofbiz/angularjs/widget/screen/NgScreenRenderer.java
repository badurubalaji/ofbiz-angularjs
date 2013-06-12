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

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.widget.screen.ScreenFactory;
import org.xml.sax.SAXException;

public class NgScreenRenderer {
    
    protected Appendable writer;
    protected MapStack<String> context;
    protected NgScreenStringRenderer ngScreenStringRenderer;
    
    public NgScreenRenderer(Appendable writer, MapStack<String> context, NgScreenStringRenderer ngScreenStringRenderer) {
        this.writer = writer;
        this.context = context;
        if (this.context == null) this.context = MapStack.create();
        this.ngScreenStringRenderer = ngScreenStringRenderer;
    }

    public String render(String combinedName) throws GeneralException, IOException, SAXException, ParserConfigurationException {
        String resourceName = ScreenFactory.getResourceNameFromCombined(combinedName);
        String ngScreenName = ScreenFactory.getScreenNameFromCombined(combinedName);
        this.render(resourceName, ngScreenName);
        return "";
    }

    public String render(String resourceName, String ngScreenName) throws GeneralException, IOException, SAXException, ParserConfigurationException {
        ModelNgScreen modelNgScreen = NgScreenFactory.getNgScreenFromLocation(resourceName, ngScreenName);
        modelNgScreen.renderNgScreenString(writer, context, ngScreenStringRenderer);
        return "";
    }
}
