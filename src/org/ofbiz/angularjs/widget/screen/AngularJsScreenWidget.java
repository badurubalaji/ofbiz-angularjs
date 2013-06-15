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
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.widget.screen.ModelScreen;
import org.ofbiz.widget.screen.ModelScreenWidget;
import org.ofbiz.widget.screen.ScreenStringRenderer;
import org.w3c.dom.Element;

public class AngularJsScreenWidget {

    public final static String module = AngularJsScreenWidget.class.getName();
    
    @SuppressWarnings("serial")
    public static class Application extends ModelScreenWidget {
        public static final String TAG_NAME = "application";
        
        protected FlexibleStringExpander nameExdr;
        protected List<ModelScreenWidget> subWidgets;

        public Application(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name"));
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml.childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div ng-app=\"" + this.nameExdr.getOriginal() + "\">";
        }
        
    }

    @SuppressWarnings("serial")
    public static class Controller extends ModelScreenWidget {
        public static final String TAG_NAME = "controller";

        protected FlexibleStringExpander nameExdr;
        protected List<ModelScreenWidget> subWidgets;

        public Controller(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name"));
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml.childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div ng-controller=\"" + this.nameExdr.getOriginal() + "\">";
        }
        
    }
    
    @SuppressWarnings("serial")
    public static class CurrentTime extends ModelScreenWidget {
        public static final String TAG_NAME = "current-time";

        public CurrentTime(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
        }

        @Override
        public String rawString() {
            return "<span current-time=\"\"></span>";
        }
        
    }

    @SuppressWarnings("serial")
    public static class NgList extends ModelScreenWidget {
        public static final String TAG_NAME = "list";

        protected FlexibleStringExpander repeatExdr;
        protected List<ModelScreenWidget> subWidgets;

        public NgList(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.repeatExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("repeat"));
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml.childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);
            writer.append("</li></ul>");
        }

        @Override
        public String rawString() {
            return "<ul><li ng-repeat=\"" + repeatExdr.getOriginal() + "\">";
        }
    }
}
