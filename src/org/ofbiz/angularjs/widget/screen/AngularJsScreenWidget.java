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
import java.util.LinkedList;
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
            return "<button class=\"btn " + style + "\" ng-click=\"" + this.onClickExdr.getOriginal() + "\">" + this.textExdr.getOriginal() + "</button>";
        }
        
    }
    
    @SuppressWarnings("serial")
    public static class Calendar extends ModelScreenWidget {
        public static final String TAG_NAME = "calendar";

        protected String name;
        protected String options;
        protected String model;

        public Calendar(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.name = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name")).getOriginal();
            this.options = FlexibleStringExpander.getInstance(widgetElement.getAttribute("options")).getOriginal();
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<div ui-calendar=\"" + options + "\"");
            if (UtilValidate.isNotEmpty(name)) {
                builder.append(" calendar=\"" + this.name + "\"");
            }
            if (UtilValidate.isNotEmpty(model)) {
                builder.append(" ng-model=\"" + this.model + "\"");
            }
            builder.append(">");
            return builder.toString();
        }
        
    }
    
    @SuppressWarnings("serial")
    public static class Checkbox extends ModelScreenWidget {
        public static final String TAG_NAME = "checkbox";

        protected String text;
        protected String model;
        protected String style;

        public Checkbox(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.text = FlexibleStringExpander.getInstance(widgetElement.getAttribute("text")).getOriginal();
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            this.style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
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
            builder.append("<input type=\"checkbox\" class=\"" + this.style + "\"");
            if (UtilValidate.isNotEmpty(model)) {
                builder.append(" ng-model=\"" + this.model + "\"");
            }
            builder.append("/>");
            builder.append(this.text);
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
    public static class ContainerFluid extends ModelScreenWidget {
        public static final String TAG_NAME = "container-fluid";
        
        protected List<ModelScreenWidget> subWidgets;
        
        public ContainerFluid(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
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
            return "<div class=\"container-fluid\">";
        }
    }

    @SuppressWarnings("serial")
    public static class ControlGroup extends ModelScreenWidget {
        public static final String TAG_NAME = "control-group";
        
        protected boolean row = false;
        protected Element widgetElement = null;
        
        public ControlGroup(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.widgetElement = widgetElement;
            this.row = Boolean.valueOf(FlexibleStringExpander.getInstance(widgetElement.getAttribute("row")).getOriginal());
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            List<? extends Element> subElements = UtilXml.childElementList(this.widgetElement);
            for (Element subElement : subElements) {
                if ("control-label".equals(subElement.getNodeName())) {
                    String text = UtilXml.elementAttribute(subElement, "text", null);
                    String forAttr = UtilXml.elementAttribute(subElement, "for", null);
                    writer.append("<label class=\"control-label\" for=\"" + forAttr + "\">");
                    writer.append(text);
                    writer.append("</label>");
                } else if ("controls".equals(subElement.getNodeName())) {
                    List<? extends Element> controlElements = UtilXml.childElementList(subElement);
                    for (Element controlElement : controlElements) {
                        // read sub-widget
                        List<Element> cElementList = new LinkedList<Element>();
                        cElementList.add(controlElement);
                        List<ModelScreenWidget> controlWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, cElementList);
                        renderSubWidgetsString(controlWidgets, writer, context, screenStringRenderer);
                    }
                }
            }
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div class=\"control-group\">";
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
    public static class DatePicker extends ModelScreenWidget {
        public static final String TAG_NAME = "date-picker";

        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander showWeeksExdr;
        protected FlexibleStringExpander startingDayExdr;
        protected FlexibleStringExpander dateDisabledExdr;
        protected FlexibleStringExpander minExdr;
        protected FlexibleStringExpander maxExdr;
        
        public DatePicker(ModelScreen modelScreen, Element widgetElement) {
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
    public static class DateTime extends ModelScreenWidget {
        public static final String TAG_NAME = "date-time";

        protected String format = null;
        protected String model = null;
        protected String style = null;
        
        public DateTime(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.format = FlexibleStringExpander.getInstance(widgetElement.getAttribute("format")).getOriginal();
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            this.style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
            
            if (UtilValidate.isEmpty(this.format)) {
                this.format = "\"format\":\"MMM d, yyyy h:mm:ss a\"";
            }
            if (UtilValidate.isEmpty(this.style)) {
                this.style = "input-large";
            }
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
            return "<input ade-calpop='{" + format + "}' ng-model=\"" + model + "\" type=\"text\" class=\"" + style + "\" />";
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
        
        protected List<? extends Element> subElementList = null;
        protected String textContent = null;
        
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
            writer.append(this.textContent);
            
            //TODO reader text and element by order
            /*
            for (Element subElement : subElementList) {
                short nodeType = subElement.getNodeType();
                if (nodeType == Node.TEXT_NODE) {
                    writer.append(subElement.getTextContent());
                } else {
                    List<Element> elements = new LinkedList<Element>();
                    elements.add(subElement);
                    List<ModelScreenWidget> subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, elements);
                    renderSubWidgetsString(subWidgets, writer, context, screenStringRenderer);
                }
            }
            */
            
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
    public static class Row extends ModelScreenWidget {
        public static final String TAG_NAME = "row";
        
        protected List<ModelScreenWidget> subWidgets;
        
        public Row(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
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
            return "<div class=\"row\">";
        }
    }

    @SuppressWarnings("serial")
    public static class RowFluid extends ModelScreenWidget {
        public static final String TAG_NAME = "row-fluid";
        
        protected List<ModelScreenWidget> subWidgets;
        
        public RowFluid(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
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
            return "<div class=\"row-fluid\">";
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

        protected String model;
        protected String style;
        protected String placeholder;

        public Text(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            this.style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
            this.placeholder = FlexibleStringExpander.getInstance(widgetElement.getAttribute("placeholder")).getOriginal();
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
            builder.append("<input type=\"text\" class=\"" + this.style + "\"");
            if (UtilValidate.isNotEmpty(placeholder)) {
                builder.append(" placeholder=\"" + this.placeholder + "\"");
            }
            if (UtilValidate.isNotEmpty(model)) {
                builder.append(" ng-model=\"" + this.model + "\"");
            }
            builder.append("/>");
            
            return builder.toString();
        }
        
    }
    
    @SuppressWarnings("serial")
    public static class TextArea extends ModelScreenWidget {
        public static final String TAG_NAME = "textarea";

        protected String model;
        protected String style;
        protected String placeholder;
        protected boolean visualEditorEnable = false;

        public TextArea(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            this.style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
            this.placeholder = FlexibleStringExpander.getInstance(widgetElement.getAttribute("placeholder")).getOriginal();
            this.visualEditorEnable = Boolean.valueOf(FlexibleStringExpander.getInstance(widgetElement.getAttribute("visual-editor-enable")).getOriginal());
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
            builder.append("<textarea class=\"" + this.style + "\"");
            if (UtilValidate.isNotEmpty(placeholder)) {
                builder.append(" placeholder=\"" + this.placeholder + "\"");
            }
            if (UtilValidate.isNotEmpty(model)) {
                builder.append(" ng-model=\"" + this.model + "\"");
            }
            if (visualEditorEnable) {
                builder.append(" ui-tinymce");
            }
            builder.append("></textarea>");
            
            return builder.toString();
        }
        
    }
    
    /**
     * 
     * @author chatree
     *
     */
    @SuppressWarnings("serial")
    public static class UiMap extends ModelScreenWidget {
        public static final String TAG_NAME = "map";

        protected String name;
        protected String style;
        protected String height;
        protected String event;
        protected String options;
        
        public UiMap(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.name = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name")).getOriginal();
            this.style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
            this.height = FlexibleStringExpander.getInstance(widgetElement.getAttribute("height")).getOriginal();
            this.event = FlexibleStringExpander.getInstance(widgetElement.getAttribute("event")).getOriginal();
            this.options = FlexibleStringExpander.getInstance(widgetElement.getAttribute("options")).getOriginal();
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div ui-map-options=\"\" ui-map=\"" + name + "\" height=\"" + height + "\" class=\"" + style + "\" ui-event=\"" + event + "\" ui-options=\"" + options + "\">" ;
        }
    }
    
    @SuppressWarnings("serial")
    public static class UiMapInfoWindow extends ModelScreenWidget {
        public static final String TAG_NAME = "map-info-window";

        protected String name;
        protected List<ModelScreenWidget> subWidgets;
        
        public UiMapInfoWindow(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.name = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name")).getOriginal();
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
            return "<div ui-map-info-window=\"" + name + "\">" ;
        }
    }
    @SuppressWarnings("serial")
    public static class UiMapMarker extends ModelScreenWidget {
        public static final String TAG_NAME = "map-marker";

        protected String repeat;
        protected String value;
        protected String event;
        
        public UiMapMarker(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.repeat = FlexibleStringExpander.getInstance(widgetElement.getAttribute("repeat")).getOriginal();
            this.value = FlexibleStringExpander.getInstance(widgetElement.getAttribute("value")).getOriginal();
            this.event = FlexibleStringExpander.getInstance(widgetElement.getAttribute("event")).getOriginal();
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div ng-repeat=\"" + repeat + "\" ui-map-marker=\"" + value + "\" ui-event=\"" + event + "\">" ;
        }
    }
    
    @SuppressWarnings("serial")
    public static class TimePicker extends ModelScreenWidget {
        public static final String TAG_NAME = "time-picker";

        protected String model;
        protected String hourStep;
        protected String minuteStep;
        protected String showMeridian;

        public TimePicker(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            this.hourStep = FlexibleStringExpander.getInstance(widgetElement.getAttribute("hour-step")).getOriginal();
            this.minuteStep = FlexibleStringExpander.getInstance(widgetElement.getAttribute("minute-step")).getOriginal();
            this.showMeridian = FlexibleStringExpander.getInstance(widgetElement.getAttribute("show-meridian")).getOriginal();
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
            builder.append("<timepicker");
            if (UtilValidate.isNotEmpty(model)) {
                builder.append(" ng-model=\"" + this.model + "\"");
            }
            if (UtilValidate.isNotEmpty(hourStep)) {
                builder.append(" hour-step=\"" + this.hourStep + "\"");
            }
            if (UtilValidate.isNotEmpty(minuteStep)) {
                builder.append(" minute-step=\"" + this.minuteStep + "\"");
            }
            if (UtilValidate.isNotEmpty(showMeridian)) {
                builder.append(" show-meridian=\"" + this.showMeridian + "\"");
            }
            builder.append("></timepicker>");
            
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
