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
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.widget.screen.ModelScreen;
import org.ofbiz.widget.screen.ModelScreenWidget;
import org.ofbiz.widget.screen.ScreenStringRenderer;
import org.w3c.dom.Element;

public class AngularJsScreenWidget {

    public final static String module = AngularJsScreenWidget.class.getName();
    
    @SuppressWarnings("serial")
    public static class Accordion extends ModelScreenWidget {
        public static final String TAG_NAME = "accordion";
        
        protected FlexibleStringExpander closeOthersExdr;
        protected List<? extends Element> accordionGroupElementList;

        public Accordion(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.closeOthersExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("close-others"));
            accordionGroupElementList = UtilXml.childElementList(widgetElement, "accordion-group");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            for (Element accordionGroupElement : accordionGroupElementList) {
                FlexibleStringExpander headingExdr = FlexibleStringExpander.getInstance(accordionGroupElement.getAttribute("heading"));
                FlexibleStringExpander repeatExdr = FlexibleStringExpander.getInstance(accordionGroupElement.getAttribute("repeat"));
                writer.append("<accordion-group heading=\"" + headingExdr.getOriginal() + "\"");
                if (UtilValidate.isNotEmpty(repeatExdr.getOriginal())) {
                    writer.append(" ng-repeat=\"" + repeatExdr.getOriginal() + "\"");
                }
                writer.append(">");
                // read sub-widgets
                List<? extends Element> subElementList = UtilXml.childElementList(accordionGroupElement);
                List<ModelScreenWidget> subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
                renderSubWidgetsString(subWidgets, writer, context, screenStringRenderer);
                writer.append("</accordion-group>");
            }
            writer.append("</accordion>");
        }

        @Override
        public String rawString() {
            return "<accordion close-others=\"" + this.closeOthersExdr.getOriginal() + "\">";
        }
        
    }
    
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
    public static class Button extends ModelScreenWidget {
        public static final String TAG_NAME = "button";

        protected FlexibleStringExpander textExdr;
        protected FlexibleStringExpander onClickExdr;
        protected FlexibleStringExpander styleExdr;

        public Button(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("text"));
            this.onClickExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("on-click"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style"));
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
            return "<button class=\"" + this.styleExdr.getOriginal() + "\" ng-click=\"" + this.onClickExdr.getOriginal() + "\">" + this.textExdr.getOriginal() + "</button>";
        }
        
    }
    
    @SuppressWarnings("serial")
    public static class Checkbox extends ModelScreenWidget {
        public static final String TAG_NAME = "checkbox";

        protected FlexibleStringExpander textExdr;
        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander styleExdr;

        public Checkbox(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("text"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style"));
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
            StringBuilder builder = new StringBuilder();
            builder.append("<label class=\"checkbox\">");
            builder.append("<input type=\"checkbox\" class=\"" + this.styleExdr.getOriginal() + "\" ng-model=\"" + this.modelExdr.getOriginal() + "\"/>");
            builder.append(this.textExdr.getOriginal());
            builder.append("</label>");
            return builder.toString();
        }
        
    }

    @SuppressWarnings("serial")
    public static class Collapse extends ModelScreenWidget {
        public static final String TAG_NAME = "collapse";

        protected FlexibleStringExpander collapseExdr;
        protected List<ModelScreenWidget> subWidgets;
        
        public Collapse(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.collapseExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("collapse"));
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
            return "<div collapse=\"" + this.collapseExdr.getOriginal() + "\">";
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
    public static class Dropdown extends ModelScreenWidget {
        public static final String TAG_NAME = "dropdown";

        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander optionsExdr;
        protected FlexibleStringExpander defaultTextExdr;
        
        public Dropdown(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model"));
            this.optionsExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("options"));
            this.defaultTextExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("default-text"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            if (UtilValidate.isNotEmpty(defaultTextExdr.getOriginal())) {
                writer.append("<option value=\"\">" + defaultTextExdr.getOriginal() + "</option>");
            }
            writer.append("</select>");
        }

        @Override
        public String rawString() {
            return "<select ng-model=\"" + modelExdr.getOriginal() + "\" ng-options=\"" + optionsExdr.getOriginal() + "\">";
        }
    }
    
    @SuppressWarnings("serial")
    public static class DropdownToggle extends ModelScreenWidget {
        public static final String TAG_NAME = "dropdown-toggle";
        
        protected FlexibleStringExpander textExdr;
        protected List<? extends Element> optionElementList;

        public DropdownToggle(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("text"));
            optionElementList = UtilXml.childElementList(widgetElement, "option");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append("<a class=\"dropdown-toggle\">" + textExdr.getOriginal() + "</a>");
            writer.append("<ul class=\"dropdown-menu\">");
            for (Element optionElement : optionElementList) {
                FlexibleStringExpander textExdr = FlexibleStringExpander.getInstance(optionElement.getAttribute("text"));
                FlexibleStringExpander repeatExdr = FlexibleStringExpander.getInstance(optionElement.getAttribute("repeat"));
                writer.append("<li");
                if (UtilValidate.isNotEmpty(repeatExdr.getOriginal())) {
                    writer.append(" ng-repeat=\"" + repeatExdr.getOriginal() + "\"");
                }
                writer.append(">");
                writer.append("<a>" + textExdr.getOriginal() + "</a>");
                writer.append("</li>");
            }
            writer.append("</ul>");
            writer.append("</li>");
        }

        @Override
        public String rawString() {
            return "<li class=\"dropdown\">";
        }
        
    }

    @SuppressWarnings("serial")
    public static class Form extends ModelScreenWidget {
        public static final String TAG_NAME = "form";

        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander validatedExdr;
        protected FlexibleStringExpander styleExdr;
        protected List<ModelScreenWidget> subWidgets;
        
        public Form(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name"));
            this.validatedExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("validated"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style"));
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
            writer.append("</form>");
        }

        @Override
        public String rawString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<form name=\"" + nameExdr.getOriginal() + "\" class=\"" + styleExdr.getOriginal() + "\" ");
            boolean validated = Boolean.valueOf(validatedExdr.getOriginal());
            if (!validated) {
                builder.append("novalidate ");
            }
            builder.append(">");
            return builder.toString();
        }
    }

    @SuppressWarnings("serial")
    public static class Grid extends ModelScreenWidget {
        public static final String TAG_NAME = "grid";

        protected FlexibleStringExpander optionExdr;
        protected FlexibleStringExpander styleExdr;
        
        public Grid(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.optionExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("option"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style"));
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
            StringBuilder option = new StringBuilder();
            option.append("{");
            option.append("}");
            return "<div class=\"" + styleExdr.getOriginal() + "\" ng-grid=\"" + optionExdr.getOriginal() + "\" grid-option=\"" + option.toString() + "\"></div>";
        }
    }

    @SuppressWarnings("serial")
    public static class HorizontalRule extends ModelScreenWidget {
        public static final String TAG_NAME = "horizontal-rule";
        
        public HorizontalRule(ModelScreen modelScreen, Element widgetElement) {
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
            return "<hr/>";
        }
    }
    
    @SuppressWarnings("serial")
    public static class LineBreak extends ModelScreenWidget {
        public static final String TAG_NAME = "line-break";
        
        public LineBreak(ModelScreen modelScreen, Element widgetElement) {
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
            return "<br/>";
        }
    }
    
    @SuppressWarnings("serial")
    public static class PreformattedText extends ModelScreenWidget {
        public static final String TAG_NAME = "preformatted-text";
        
        protected String textContent = null;
        
        public PreformattedText(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            textContent = widgetElement.getTextContent();
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append(textContent);
            writer.append("</pre>");
        }

        @Override
        public String rawString() {
            return "<pre>";
        }
    }

    @SuppressWarnings("serial")
    public static class NgList extends ModelScreenWidget {
        public static final String TAG_NAME = "list";
        
        protected List<? extends Element> listItemElementList;

        public NgList(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            listItemElementList = UtilXml.childElementList(widgetElement, "list-item");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            for (Element listItemElement : listItemElementList) {
                writer.append("<li");
                FlexibleStringExpander styleExdr = FlexibleStringExpander.getInstance(listItemElement.getAttribute("style"));
                FlexibleStringExpander repeatExdr = FlexibleStringExpander.getInstance(listItemElement.getAttribute("repeat"));
                writer.append(" class=\"" + styleExdr.getOriginal() + "\"");
                if (UtilValidate.isNotEmpty(repeatExdr.getOriginal())) {
                    writer.append(" ng-repeat=\"" + repeatExdr.getOriginal() + "\"");
                }
                writer.append(">");
                // read sub-widgets
                List<? extends Element> subElementList = UtilXml.childElementList(listItemElement);
                List<ModelScreenWidget> subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
                renderSubWidgetsString(subWidgets, writer, context, screenStringRenderer);
                writer.append("</li>");
            }
            writer.append("</ul>");
        }

        @Override
        public String rawString() {
            return "<ul>";
        }
    }

    @SuppressWarnings("serial")
    public static class Radio extends ModelScreenWidget {
        public static final String TAG_NAME = "radio";

        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander valueExdr;
        
        public Radio(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model"));
            this.valueExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("value"));
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
            return "<input type=\"radio\" ng-model=\"" + modelExdr.getOriginal() + "\" value=\"" + valueExdr.getOriginal() + "\"/>";
        }
    }
    
    @SuppressWarnings("serial")
    public static class Text extends ModelScreenWidget {
        public static final String TAG_NAME = "text";

        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander styleExdr;

        public Text(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style"));
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
            StringBuilder builder = new StringBuilder();
            builder.append("<input type=\"text\" class=\"" + this.styleExdr.getOriginal() + "\" ng-model=\"" + this.modelExdr.getOriginal() + "\"/>");
            return builder.toString();
        }
        
    }

    @SuppressWarnings("serial")
    public static class Upload extends ModelScreenWidget {
        public static final String TAG_NAME = "upload";

        public Upload(ModelScreen modelScreen, Element widgetElement) {
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
            return "<div ng-upload=\"\"></div>";
        }
    }

    @SuppressWarnings("serial")
    public static class View extends ModelScreenWidget {
        public static final String TAG_NAME = "view";

        public View(ModelScreen modelScreen, Element widgetElement) {
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
            return "<div ng-view=\"\"></div>";
        }
    }
}
