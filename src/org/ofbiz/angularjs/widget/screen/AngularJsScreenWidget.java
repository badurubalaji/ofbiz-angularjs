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
import java.io.StringWriter;
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
    public static class Alert extends ModelScreenWidget {
        public static final String TAG_NAME = "alert";
        
        protected FlexibleStringExpander repeatExdr;
        protected FlexibleStringExpander typeExdr;
        protected FlexibleStringExpander closeExdr;
        protected FlexibleStringExpander textExdr;

        public Alert(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.repeatExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("repeat"));
            this.typeExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("type"));
            this.closeExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("close"));
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("text"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append(textExdr.getOriginal());
            writer.append("</alert>");
        }

        @Override
        public String rawString() {
            return "<alert ng-repeat=\"" + this.repeatExdr.getOriginal() + "\" type=\"" + typeExdr.getOriginal() + "\" close=\"" + closeExdr.getOriginal() + "\">";
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
            String style = this.styleExdr.getOriginal();
            if (UtilValidate.isEmpty(style)) {
                style= "btn";
            }
            return "<button class=\"" + style + "\" ng-click=\"" + this.onClickExdr.getOriginal() + "\">" + this.textExdr.getOriginal() + "</button>";
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
    public static class Datepicker extends ModelScreenWidget {
        public static final String TAG_NAME = "datepicker";

        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander showWeeksExdr;
        protected FlexibleStringExpander startingDayExdr;
        protected FlexibleStringExpander dateDisabledExdr;
        protected FlexibleStringExpander minExdr;
        protected FlexibleStringExpander maxExdr;
        
        public Datepicker(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model"));
            this.showWeeksExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("show-weeks"));
            this.startingDayExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("starting-day"));
            this.dateDisabledExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("date-disabled"));
            this.minExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("min"));
            this.maxExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("max"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append("</datepicker>");
        }

        @Override
        public String rawString() {
            return "<datepicker ng-model=\"" + modelExdr.getOriginal() + "\" show-weeks=\"" + showWeeksExdr.getOriginal() + "\""
                    + " starting-day=\"" + startingDayExdr.getOriginal() + "\" date-disabled=\"" + dateDisabledExdr.getOriginal() + "\""
                    + " min=\"" + minExdr.getOriginal() + "\" max=\"" + maxExdr.getOriginal() + "\">";
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
    public static class EmphasizedText extends ModelScreenWidget {
        public static final String TAG_NAME = "emphasized-text";
        
        protected String textContent = null;
        protected List<? extends Element> subElementList = null;
        
        public EmphasizedText(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            textContent = widgetElement.getTextContent();
            subElementList = UtilXml.childElementList(widgetElement);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append(textContent);
            // read sub-widgets
            List<ModelScreenWidget> subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
            renderSubWidgetsString(subWidgets, writer, context, screenStringRenderer);
            writer.append("</em>");
        }

        @Override
        public String rawString() {
            return "<em>";
        }
    }

    /**
     * Service http://twilson63.github.io/ngUpload/
     * @author chatree
     *
     */
    @SuppressWarnings("serial")
    public static class File extends ModelScreenWidget {
        public static final String TAG_NAME = "file";
        
        protected FlexibleStringExpander nameExdr;
        
        public File(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name"));
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
            return "<input type=\"file\" name=\"" + nameExdr.getOriginal() + "\"/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Form extends ModelScreenWidget {
        public static final String TAG_NAME = "form";

        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander targetExdr;
        protected FlexibleStringExpander validatedExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander uploadExdr;
        protected List<ModelScreenWidget> subWidgets;
        
        public Form(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name"));
            this.targetExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("target"));
            this.validatedExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("validated"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style"));
            this.uploadExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("upload"));
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
            boolean upload = Boolean.valueOf(uploadExdr.getOriginal());
            boolean validated = Boolean.valueOf(validatedExdr.getOriginal());
            
            StringBuilder builder = new StringBuilder();
            builder.append("<form name=\"" + nameExdr.getOriginal() + "\" action=\"" + targetExdr.getOriginal() + "\" class=\"" + styleExdr.getOriginal() + "\" ");
            if (!validated) {
                builder.append("novalidate ");
            }
            if (upload) {
                builder.append("ng-upload ");
            }
            builder.append(">");
            return builder.toString();
        }
    }

    @SuppressWarnings("serial")
    public static class Grid extends ModelScreenWidget {
        public static final String TAG_NAME = "grid";

        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander rowHeightExdr;
        
        public Grid(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style"));
            this.rowHeightExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("row-height"));
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
            int rowHeight = 40;
            
            if (UtilValidate.isNotEmpty(rowHeightExdr.getOriginal())) {
                rowHeight = Integer.parseInt(rowHeightExdr.getOriginal());
            }
            
            return "<div class=\"" + styleExdr.getOriginal() + "\" ng-grid=\"grid\" grid-options=\"\" row-height=\"" + rowHeight + "\"></div>";
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
    public static class Modal extends ModelScreenWidget {
        public static final String TAG_NAME = "modal";

        protected FlexibleStringExpander shouldBeOpenExdr;
        protected FlexibleStringExpander closeExdr;
        protected FlexibleStringExpander optionsExdr;
        protected Element modalHeaderElement;
        protected Element modalBodyElement;
        protected Element modalFooterElement;
        
        public Modal(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.shouldBeOpenExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("should-be-open"));
            this.closeExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("close"));
            this.optionsExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("options"));
            this.modalHeaderElement = UtilXml.firstChildElement(widgetElement, "modal-header");
            this.modalBodyElement = UtilXml.firstChildElement(widgetElement, "modal-body");
            this.modalFooterElement = UtilXml.firstChildElement(widgetElement, "modal-footer");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            if (UtilValidate.isNotEmpty(modalHeaderElement)) {
                writer.append("<div class=\"modal-header\">");
                // read header sub-widgets
                List<? extends Element> subElementList = UtilXml.childElementList(modalHeaderElement);
                List<ModelScreenWidget> subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
                renderSubWidgetsString(subWidgets, writer, context, screenStringRenderer);
                writer.append("</div>");
            }
            if (UtilValidate.isNotEmpty(modalBodyElement)) {
                writer.append("<div class=\"modal-body\">");
                // read body sub-widgets
                List<? extends Element> subElementList = UtilXml.childElementList(modalBodyElement);
                List<ModelScreenWidget> subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
                renderSubWidgetsString(subWidgets, writer, context, screenStringRenderer);
                writer.append("</div>");
            }
            if (UtilValidate.isNotEmpty(modalFooterElement)) {
                writer.append("<div class=\"modal-footer\">");
                // read footer sub-widgets
                List<? extends Element> subElementList = UtilXml.childElementList(modalFooterElement);
                List<ModelScreenWidget> subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
                renderSubWidgetsString(subWidgets, writer, context, screenStringRenderer);
                writer.append("</div>");
            }
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div modal=\"" + shouldBeOpenExdr.getOriginal() + "\" close=\"" + closeExdr.getOriginal() + "\" options=\"" + optionsExdr.getOriginal() + "\">";
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
    public static class PreformattedText extends ModelScreenWidget {
        public static final String TAG_NAME = "preformatted-text";
        
        protected String textContent = null;
        protected List<? extends Element> subElementList = null;
        
        public PreformattedText(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            textContent = widgetElement.getTextContent();
            subElementList = UtilXml.childElementList(widgetElement);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append(textContent);
            // read sub-widgets
            List<ModelScreenWidget> subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
            renderSubWidgetsString(subWidgets, writer, context, screenStringRenderer);
            writer.append("</pre>");
        }

        @Override
        public String rawString() {
            return "<pre>";
        }
    }

    @SuppressWarnings("serial")
    public static class Radio extends ModelScreenWidget {
        public static final String TAG_NAME = "radio";

        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander textExdr;
        protected FlexibleStringExpander valueExdr;
        protected FlexibleStringExpander styleExdr;
        
        public Radio(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model"));
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("text"));
            this.valueExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("value"));
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
            builder.append("<label class=\"radio\">");
            builder.append("<input type=\"radio\" class=\"" + this.styleExdr.getOriginal() + "\" ng-model=\"" + this.modelExdr.getOriginal() + "\" value=\"" + valueExdr.getOriginal() + "\"/>");
            builder.append(this.textExdr.getOriginal());
            builder.append("</label>");
            return builder.toString();
        }
    }

    @SuppressWarnings("serial")
    public static class Submit extends ModelScreenWidget {
        public static final String TAG_NAME = "submit";

        protected FlexibleStringExpander textExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander onUploadExdr;
        
        public Submit(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("text"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style"));
            this.onUploadExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("on-upload"));
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
            return "<input type=\"submit\" class=\"" + styleExdr.getOriginal() + "\" value=\"" + textExdr.getOriginal() + "\" upload-submit=\"" + onUploadExdr.getOriginal() + "\"/>";
        }
    }
    
    @SuppressWarnings("serial")
    public static class Tabs extends ModelScreenWidget {
        public static final String TAG_NAME = "tabs";

        protected List<? extends Element> tabElements;
        protected FlexibleStringExpander verticalExdr;
        protected FlexibleStringExpander typeExdr;

        public Tabs(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.tabElements = UtilXml.childElementList(widgetElement, "tab");
            this.verticalExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("vertical"));
            this.typeExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("type"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            if (UtilValidate.isNotEmpty(tabElements)) {
                // tabs
                for (Element tabElement : tabElements) {
                    StringWriter tabWriter = new StringWriter();
                    FlexibleStringExpander repeatExdr = FlexibleStringExpander.getInstance(tabElement.getAttribute("repeat"));
                    FlexibleStringExpander headingExdr = FlexibleStringExpander.getInstance(tabElement.getAttribute("heading"));
                    FlexibleStringExpander activeExdr = FlexibleStringExpander.getInstance(tabElement.getAttribute("active"));
                    FlexibleStringExpander disabledExdr = FlexibleStringExpander.getInstance(tabElement.getAttribute("disabled"));
                    FlexibleStringExpander onSelectExdr = FlexibleStringExpander.getInstance(tabElement.getAttribute("on-select"));
                    tabWriter.append("<tab ");
                    if (UtilValidate.isNotEmpty(repeatExdr.getOriginal())) {
                        tabWriter.append("ng-repeat=\"" + repeatExdr.getOriginal() + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(headingExdr.getOriginal())) {
                        tabWriter.append("heading=\"" + headingExdr.getOriginal() + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(activeExdr.getOriginal())) {
                        tabWriter.append("active=\"" + activeExdr.getOriginal() + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(disabledExdr.getOriginal())) {
                        tabWriter.append("disabled=\"" + disabledExdr.getOriginal() + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(onSelectExdr.getOriginal())) {
                        tabWriter.append("select=\"" + onSelectExdr.getOriginal() + "\" ");
                    }
                    tabWriter.append(">");
                    
                    // tab heading
                    Element tabHeadingElement = UtilXml.firstChildElement(tabElement, "tab-heading");
                    if (UtilValidate.isNotEmpty(tabHeadingElement)) {
                        tabWriter.append("<tab-heading>");
                        // read tab heading sub widgets
                        List<? extends Element> tabHeadingSubWidgetElements = UtilXml.childElementList(tabHeadingElement);
                        List<ModelScreenWidget> tabHeadingSubWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, tabHeadingSubWidgetElements);
                        renderSubWidgetsString(tabHeadingSubWidgets, tabWriter, context, screenStringRenderer);
                        tabWriter.append("</tab-heading>");
                    }

                    // read tab sub widgets
                    List<? extends Element> tabSubWidgetElements = UtilXml.childElementList(tabElement);
                    if (UtilValidate.isNotEmpty(tabHeadingElement)) {
                        // remove tab heading element
                        tabSubWidgetElements.remove(0);
                    }
                    List<ModelScreenWidget> tabSubWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, tabSubWidgetElements);
                    renderSubWidgetsString(tabSubWidgets, tabWriter, context, screenStringRenderer);

                    tabWriter.append("</tab>");
                    writer.append(tabWriter.toString());
                }
            }
            writer.append("</tabset>");
        }

        @Override
        public String rawString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<tabset ");
            if (UtilValidate.isNotEmpty(verticalExdr.getOriginal())) {
                builder.append("vertical=\"" + verticalExdr.getOriginal() + "\"");
            }
            if (UtilValidate.isNotEmpty(typeExdr.getOriginal())) {
                builder.append("type=\"" + typeExdr.getOriginal() + "\"");
            }
            builder.append(">");
            return builder.toString();
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
