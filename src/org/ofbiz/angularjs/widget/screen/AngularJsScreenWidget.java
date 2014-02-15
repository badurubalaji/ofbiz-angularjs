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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
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

        protected String text;
        protected String onClick;
        protected String style;

        public Button(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.text = FlexibleStringExpander.getInstance(widgetElement.getAttribute("text")).getOriginal();
            this.onClick = FlexibleStringExpander.getInstance(widgetElement.getAttribute("on-click")).getOriginal();
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
            return "<button class=\"btn " + style + "\" ng-click=\"" + this.onClick + "\">" + this.text + "</button>";
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
    public static class Context extends ModelScreenWidget {

        public static final String TAG_NAME = "context";
        
        protected String target = null;
        protected String parameters = null;
        protected String model = null;
        protected String field = null;
        protected List<ModelScreenWidget> subWidgets;
        
        public Context(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.target = FlexibleStringExpander.getInstance(widgetElement.getAttribute("target")).getOriginal();
            this.parameters = FlexibleStringExpander.getInstance(widgetElement.getAttribute("parameters")).getOriginal();
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            this.field = FlexibleStringExpander.getInstance(widgetElement.getAttribute("field")).getOriginal();
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
            writer.append("</context>");
        }

        @Override
        public String rawString() {
            return "<context target=\"" + target + "\" parameters=\"" + parameters + "\" model=\"" + model + "\" field=\"" + field + "\">";
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

        protected String model;
        protected String options;
        protected String defaultText;
        
        public Dropdown(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            this.options = FlexibleStringExpander.getInstance(widgetElement.getAttribute("options")).getOriginal();
            this.defaultText = FlexibleStringExpander.getInstance(widgetElement.getAttribute("default-text")).getOriginal();
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            if (UtilValidate.isNotEmpty(defaultText)) {
                writer.append("<option value=\"\">" + defaultText + "</option>");
            }
            writer.append("</select>");
        }

        @Override
        public String rawString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<select ");
            if (UtilValidate.isNotEmpty(model)) {
                builder.append(" ng-model=\"" + model + "\"");
            }
            if (UtilValidate.isNotEmpty(options)) {
                builder.append(" ng-options=\"" + options + "\"");
            }
            builder.append(" class=\"form-control\"");
            builder.append(">");
            return builder.toString();
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
    public static class Field extends ModelScreenWidget {
        public static final String TAG_NAME = "field";
        
        protected String title;
        protected List<ModelScreenWidget> subWidgets;
        
        public Field(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml.childElementList(widgetElement);
            this.title = FlexibleStringExpander.getInstance(widgetElement.getAttribute("title")).getOriginal();
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(rawString());
            if (UtilValidate.isNotEmpty(title)) {
                writer.append("<label class=\"control-label\">" + title + "</label>");
            }
            writer.append("<div class=\"controls\">");
            renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);
            writer.append("</div>");
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div class=\"control-group\">";
        }

    }

    /**
     * http://getbootstrap.com/css/#forms
     * @author chatree
     *
     */
    @SuppressWarnings("serial")
    public static class Form extends ModelScreenWidget {
        public static final String TAG_NAME = "form";

        protected String name;
        protected String type;
        protected String target;
        protected boolean validated;
        protected String style;
        protected boolean upload;
        protected String onSubmitSuccess = null;
        protected String onSubmitError = null;
        protected List<ModelScreenWidget> subWidgets;
        
        public Form(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.name = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name")).getOriginal();
            this.type = FlexibleStringExpander.getInstance(widgetElement.getAttribute("type")).getOriginal();
            this.target = FlexibleStringExpander.getInstance(widgetElement.getAttribute("target")).getOriginal();
            this.validated = Boolean.valueOf(FlexibleStringExpander.getInstance(widgetElement.getAttribute("validated")).getOriginal());
            this.style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
            this.upload = Boolean.valueOf(FlexibleStringExpander.getInstance(widgetElement.getAttribute("upload")).getOriginal());
            this.onSubmitSuccess = FlexibleStringExpander.getInstance(widgetElement.getAttribute("on-submit-success")).getOriginal();
            this.onSubmitError = FlexibleStringExpander.getInstance(widgetElement.getAttribute("on-submit-error")).getOriginal();
            
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
            writer.append("<fieldset>");
            renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);
            writer.append("</fieldset>");
            writer.append("</form>");
        }

        @Override
        public String rawString() {
            StringBuilder builder = new StringBuilder();
            String formStyle = "form-" + type;
            builder.append("<form name=\"" + name + "\" role=\"form\" class=\"" + formStyle + " ");
            if (UtilValidate.isNotEmpty(style)) {
                builder.append(style);
            }
            builder.append("\"");
            
            if (!validated) {
                builder.append("novalidate ");
            }
            if (upload) {
                builder.append("ng-upload ");
                builder.append("action=\"" + target + "\"");
            } else {
                builder.append("target=\"" + target + "\" ");
                builder.append("on-submit-success=\"" + onSubmitSuccess + "\" ");
                builder.append("on-submit-error=\"" + onSubmitError + "\" ");
            }
            
            builder.append(" form-options=\"\">");
            return builder.toString();
        }
    }

    @SuppressWarnings("serial")
    public static class GoogleChart extends ModelScreenWidget {
        public static final String TAG_NAME = "google-chart";
        
        protected String style = null;
        protected String type = null;
        protected boolean displayed = true;
        protected String title =  null;
        protected boolean isStacked = true;
        protected int fill = 20;
        protected boolean displayExactValues = true;
        protected String vTitle = null;
        protected String hTitle = null;
        protected int vGridLinesCount = 18;
        protected String target = null;
        protected String model = null;
        
        public GoogleChart(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
            this.type = FlexibleStringExpander.getInstance(widgetElement.getAttribute("type")).getOriginal();
            this.displayed = Boolean.valueOf(FlexibleStringExpander.getInstance(widgetElement.getAttribute("displayed")).getOriginal());
            this.title = FlexibleStringExpander.getInstance(widgetElement.getAttribute("title")).getOriginal();
            this.isStacked = Boolean.valueOf(FlexibleStringExpander.getInstance(widgetElement.getAttribute("is-stacked")).getOriginal());
            this.fill = Integer.parseInt(FlexibleStringExpander.getInstance(widgetElement.getAttribute("fill")).getOriginal());
            this.displayExactValues = Boolean.valueOf(FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal());
            this.vTitle = FlexibleStringExpander.getInstance(widgetElement.getAttribute("v-title")).getOriginal();
            this.hTitle = FlexibleStringExpander.getInstance(widgetElement.getAttribute("h-title")).getOriginal();
            this.vGridLinesCount = Integer.parseInt(FlexibleStringExpander.getInstance(widgetElement.getAttribute("v-grid-lines-count")).getOriginal());
            this.target = FlexibleStringExpander.getInstance(widgetElement.getAttribute("target")).getOriginal();
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
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
            return "<div google-chart chart=\"" + model + "\" style=\"" + style + "\" type=\"" + type + "\""
                    + " displayed=\"" + displayed + "\" title=\"" + title + "\" is-stacked=\"" + isStacked + "\""
                    + " fill=\"" + fill + "\" diplay-exact-valules=\"" + displayExactValues + "\""
                    + " v-title=\"" + vTitle + "\" h-title=\"" + hTitle + "\" v-grid-lines-count=\"" + vGridLinesCount + "\""
                    + " target=\"" + target + "\" google-chart-options=\"\"/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Grid extends ModelScreenWidget {
        public static final String TAG_NAME = "grid";

        protected String model;
        protected String selectTarget;
        protected String selectParameters;
        protected String listName;
        protected String style;
        protected int rowHeight;
        protected String onBeforeSelectionChanged = null;
        protected String onAfterSelectionChanged = null;
        protected String onRowDoubleClicked = null;
        protected List<? extends Element> fieldElements;
        
        public Grid(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            this.selectTarget = FlexibleStringExpander.getInstance(widgetElement.getAttribute("select-target")).getOriginal();
            this.selectParameters = FlexibleStringExpander.getInstance(widgetElement.getAttribute("select-parameters")).getOriginal();
            this.listName = FlexibleStringExpander.getInstance(widgetElement.getAttribute("list-name")).getOriginal();
            this.style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
            this.onBeforeSelectionChanged = FlexibleStringExpander.getInstance(widgetElement.getAttribute("on-before-selection-changed")).getOriginal();
            this.onAfterSelectionChanged = FlexibleStringExpander.getInstance(widgetElement.getAttribute("on-after-selection-changed")).getOriginal();
            this.onRowDoubleClicked = FlexibleStringExpander.getInstance(widgetElement.getAttribute("on-row-double-clicked")).getOriginal();
            try {
                this.rowHeight = Integer.valueOf(FlexibleStringExpander.getInstance(widgetElement.getAttribute("row-height")).getOriginal());
            } catch (Exception e) {
                this.rowHeight = 20;
            }
            this.fieldElements = UtilXml.childElementList(widgetElement, "field");
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
            int defaultRowHeight = 40;
            
            if (UtilValidate.isEmpty(rowHeight)) {
                rowHeight = defaultRowHeight;
            }
            
            StringBuilder sortInfoBuilder = new StringBuilder();
            List<Map<String, String>> sortDirections = new LinkedList<Map<String,String>>();
            StringBuilder columnDefsBuilder = new StringBuilder();
            columnDefsBuilder.append("[");
            if (UtilValidate.isNotEmpty(fieldElements)) {
                StringBuilder fieldsBuilder = new StringBuilder();
                for (Element fieldElement : fieldElements) {
                    fieldsBuilder.append("{");
                    String name = UtilXml.elementAttribute(fieldElement, "name", null);
                    String fieldName = UtilXml.elementAttribute(fieldElement, "field-name", null);
                    String title = UtilXml.elementAttribute(fieldElement, "title", null);
                    String editable = UtilXml.elementAttribute(fieldElement, "editable", null);
                    String headerCellTemplateUri = UtilXml.elementAttribute(fieldElement, "header-cell-template-uri", null);
                    String cellTemplateUri = UtilXml.elementAttribute(fieldElement, "cell-template-uri", null);
                    String editableCellTemplateUri = UtilXml.elementAttribute(fieldElement, "editable-cell-template-uri", null);
                    String sortDirection = UtilXml.elementAttribute(fieldElement, "sort-direction", null);

                    fieldsBuilder.append("name:'" + name + "'");
                    if (UtilValidate.isNotEmpty(fieldName)) {
                        fieldsBuilder.append(",field:'" + fieldName + "'");
                    } else {
                        fieldsBuilder.append(",field:'" + name + "'");
                    }
                    if (UtilValidate.isNotEmpty(title)) {
                        fieldsBuilder.append(",displayName:'" + title + "'");
                    }
                    if (UtilValidate.isNotEmpty(editable)) {
                        fieldsBuilder.append(",enableCellEdit:" + editable);
                    }
                    if (UtilValidate.isNotEmpty(headerCellTemplateUri)) {
                        fieldsBuilder.append(",headerCellTemplate:'" + headerCellTemplateUri + "'");
                    }
                    if (UtilValidate.isNotEmpty(cellTemplateUri)) {
                        fieldsBuilder.append(",cellTemplate:'" + cellTemplateUri + "'");
                    }
                    if (UtilValidate.isNotEmpty(editableCellTemplateUri)) {
                        fieldsBuilder.append(",editableCellTemplate:'" + editableCellTemplateUri + "'");
                    }
                    
                    fieldsBuilder.append("},");
                    
                    if (UtilValidate.isNotEmpty(sortDirection)) {
                        Map<String, String> sortDirectionMap = new HashMap<String, String>();
                        sortDirectionMap.put("sortField", name);
                        sortDirectionMap.put("sortDirection", sortDirection);
                        sortDirections.add(sortDirectionMap);
                    }
                }
                
                String fieldString = fieldsBuilder.toString();
                if (fieldString.endsWith(",")) {
                    fieldString = fieldString.substring(0, fieldsBuilder.length() - 1);
                }
                
                if (UtilValidate.isNotEmpty(sortDirections)) {
                    List<String> sortFields = new LinkedList<String>();
                    List<String> directions = new LinkedList<String>();
                    for (Map<String, String> sortDirectionMap : sortDirections) {
                        String sortField = sortDirectionMap.get("sortField");
                        String sortDirection = sortDirectionMap.get("sortDirection");
                        sortFields.add(sortField);
                        directions.add(sortDirection);
                    }
                    
                    if (UtilValidate.isNotEmpty(sortFields)) {
                        sortInfoBuilder.append("{");
                        sortInfoBuilder.append("fields: [" + StringUtil.join(sortFields, ",") + "],");
                        sortInfoBuilder.append("directions: [" + StringUtil.join(directions, ",") + "]");
                        sortInfoBuilder.append("}");
                    }
                }
                
                columnDefsBuilder.append(fieldString);
            }
            columnDefsBuilder.append("]");
            
            return "<div model=\"" + model + "\" class=\"" + style + "\" ng-grid=\"grid\" grid-options=\"\" row-height=\"" + rowHeight
                    + "\" select-target=\"" + selectTarget + "\" select-parameters=\"" + selectParameters + "\" list-name=\"" + listName + "\" column-defs=\"" + columnDefsBuilder.toString()
                    + "\" on-before-selection-changed=\"" + onBeforeSelectionChanged + "\" on-after-selection-changed=\"" + onAfterSelectionChanged
                    + "\" on-row-double-clicked=\"" + onRowDoubleClicked
                    + "\" " + (UtilValidate.isNotEmpty(sortInfoBuilder.toString()) ? "sort-info=\"" + sortInfoBuilder.toString() + "\"" : "")
                    + "></div>";
        }
    }

    @SuppressWarnings("serial")
    public static class Hidden extends ModelScreenWidget {
        public static final String TAG_NAME = "hidden";
        
        protected String name;
        protected String value;
        
        public Hidden(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.name = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name")).getOriginal();
            this.value = FlexibleStringExpander.getInstance(widgetElement.getAttribute("value")).getOriginal();
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(rawString());
        }

        @Override
        public String rawString() {
            return "<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>";
        }

    }

    @SuppressWarnings("serial")
    public static class Html extends ModelScreenWidget {
        public static final String TAG_NAME = "html";
        
        private String model;
        
        public Html(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(rawString());
        }

        @Override
        public String rawString() {
            return "<div compile=\"" + model + "\"></div>";
        }
    }
    
    @SuppressWarnings("serial")
    public static class JitTree extends ModelScreenWidget {
        public static final String TAG_NAME = "jit-tree";

        protected String type = null;
        protected String model = null;

        public JitTree(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.type = FlexibleStringExpander.getInstance(widgetElement.getAttribute("type")).getOriginal();
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
            return "<div jit-tree type=\"" + type + "\" model=\"" + model + "\">";
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
    public static class Lookup extends ModelScreenWidget {
        public static final String TAG_NAME = "lookup";
        
        protected String target = null;
        protected String model = null;
        protected String descriptionFieldName = null;
        
        public Lookup(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            target = FlexibleStringExpander.getInstance(widgetElement.getAttribute("target")).getOriginal();
            model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            descriptionFieldName = FlexibleStringExpander.getInstance(widgetElement.getAttribute("description-field-name")).getOriginal();
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
            return "<input type=\"text\" target=\"" + target + "\" ng-model=\"" + model + "\" typeahead=\"option as getOptionDescription(option) for option in getOptions($viewValue)\" description-field-name=\"" + descriptionFieldName + "\" lookup/>";
        }
    }

    /**
     * http://mgcrea.github.io/angular-strap/
     * https://github.com/angular-ui/ui-router
     * 
     * @author chatree
     *
     */
    @SuppressWarnings("serial")
    public static class MenuBar extends ModelScreenWidget {
        public static final String TAG_NAME = "menu-bar";
        
        protected String title = null;
        protected String target = null;
        protected String style = "";
        protected List<? extends Element> itemElementList = null;
        
        public MenuBar(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            title = FlexibleStringExpander.getInstance(widgetElement.getAttribute("title")).getOriginal();
            target = FlexibleStringExpander.getInstance(widgetElement.getAttribute("target")).getOriginal();
            style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
            itemElementList = UtilXml.childElementList(widgetElement, "menu-item");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            for (Element itemElement : itemElementList) {
                String target = UtilXml.elementAttribute(itemElement, "target", null);
                String text = UtilXml.elementAttribute(itemElement, "text", null);
                String activeState = UtilXml.elementAttribute(itemElement, "active-state", null);
                
                if (UtilValidate.isEmpty(activeState)) {
                    activeState = target;
                }
                
                writer.append("<li ng-class=\"{ active: $state.includes('" + activeState + "') }\"><a ui-sref=\"" + target + "\">" + text + "</a></li>");
            }
            writer.append("</ul>");
            writer.append("</div>");
            writer.append("</div>");
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<div class=\"navbar\" " + style + ">");
            builder.append("<div class=\"navbar-inner\">");
            builder.append("<div class=\"container\">");
            builder.append("<a class=\"brand\" ui-sref=\"" + target + "\">" + title + "</a>");
            builder.append("<ul class=\"nav\">");
            return builder.toString();
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
    public static class Number extends ModelScreenWidget {
        public static final String TAG_NAME = "number";

        protected String name = null;
        protected String type = null;
        protected String model = null;
        protected String min = null;
        protected String max = null;

        public Number(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.name = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name")).getOriginal();
            this.type = FlexibleStringExpander.getInstance(widgetElement.getAttribute("type")).getOriginal();
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            this.min = FlexibleStringExpander.getInstance(widgetElement.getAttribute("min")).getOriginal();
            this.max = FlexibleStringExpander.getInstance(widgetElement.getAttribute("max")).getOriginal();
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
            return "<input type=\"number\" ng-model=\"" + model + "\" name=\"" + name + "\" min=\"" + min + "\" max=\"" + max + "\" " + type + "/>";
        }
    }
    
    @SuppressWarnings("serial")
    public static class Panel extends ModelScreenWidget {
        public static final String TAG_NAME = "panel";
        
        protected String style;
        protected String headerText;
        protected List<ModelScreenWidget> subWidgets;
        
        public Panel(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
            this.headerText = FlexibleStringExpander.getInstance(widgetElement.getAttribute("header-text")).getOriginal();

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
            if (UtilValidate.isNotEmpty(headerText)) {
                writer.append("<div class=\"panel-heading\">");
                writer.append(headerText);
                writer.append("</div>");
            }
            writer.append("<div class=\"panel-body\">");
            renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);
            writer.append("</div>");
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div class=\"panel " + style + "\">";
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
    public static class Screenlet extends ModelScreenWidget {
        public static final String TAG_NAME = "screenlet";
        
        protected String title = null;
        protected List<ModelScreenWidget> subWidgets = null;
        
        public Screenlet(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.title = FlexibleStringExpander.getInstance(widgetElement.getAttribute("title")).getOriginal();
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
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<div class=\"panel panel-default\">");
            builder.append("<div class=\"panel-heading\">");
            builder.append("<h5 class=\"panel-title\">");
            builder.append(title);
            builder.append("</h5>");
            builder.append("</div>");
            builder.append("<div class=\"panel-body\">");
            return builder.toString();
        }
    }

    @SuppressWarnings("serial")
    public static class Submit extends ModelScreenWidget {
        public static final String TAG_NAME = "submit";

        protected String text;
        protected String style;
        protected String onUpload;
        
        public Submit(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.text = FlexibleStringExpander.getInstance(widgetElement.getAttribute("text")).getOriginal();
            this.style = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style")).getOriginal();
            this.onUpload = FlexibleStringExpander.getInstance(widgetElement.getAttribute("on-upload")).getOriginal();
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
            builder.append("<input type=\"submit\" class=\"btn " + style + "\" value=\"" + text + "\"");
            if (UtilValidate.isNotEmpty(onUpload)) {
                builder.append(" upload-submit=\"" + onUpload + "\"");
            }
            builder.append("/>");
            return  builder.toString();
        }
    }
    
    @SuppressWarnings("serial")
    public static class TabBar extends ModelScreenWidget {
        public static final String TAG_NAME = "tab-bar";

        protected List<? extends Element> tabItemElements;
        protected Element tabContentElement;
        
        public TabBar(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.tabItemElements = UtilXml.childElementList(widgetElement, "tab-item");
            this.tabContentElement = UtilXml.firstChildElement(widgetElement, "tab-content");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append("<ul class=\"nav nav-tabs\">");
            if (UtilValidate.isNotEmpty(tabItemElements)) {
                for (Element tabItemElement : tabItemElements) {
                    String text = FlexibleStringExpander.getInstance(tabItemElement.getAttribute("text")).getOriginal();
                    String target = FlexibleStringExpander.getInstance(tabItemElement.getAttribute("target")).getOriginal();
                    String activeState = FlexibleStringExpander.getInstance(tabItemElement.getAttribute("active-state")).getOriginal();
                    String style = FlexibleStringExpander.getInstance(tabItemElement.getAttribute("style")).getOriginal();
                    String onSelect = FlexibleStringExpander.getInstance(tabItemElement.getAttribute("on-select")).getOriginal();
                    
                    if (UtilValidate.isEmpty(activeState)) {
                        activeState = target;
                    }
                    
                    writer.append("<li class=\"" + style + "\" ");
                    if (UtilValidate.isNotEmpty(target)) {
                        writer.append("ng-class=\"{active: $state.includes('" + activeState + "')}\"");
                    }
                    writer.append(">");
                    if (UtilValidate.isNotEmpty(target)) {
                        writer.append("<a ui-sref=\"" + target + "\">" + text + "</a>");
                    } else {
                        writer.append("<a ");
                        if (UtilValidate.isNotEmpty(onSelect)) {
                            writer.append("ng-click=\"" + onSelect + "\"");
                        }
                        writer.append(">" + text + "</a>");
                    }
                    writer.append("</li>");
                }
            }
            writer.append("</div>");
            
            if (UtilValidate.isNotEmpty(tabContentElement)) {
                String viewName = FlexibleStringExpander.getInstance(tabContentElement.getAttribute("view-name")).getOriginal();
                writer.append("<div class=\"tab-content\" ui-view=\"" + viewName + "\"></div>");
            }
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div class=\"tabbable\">";
        }

    }
    
    /**
     * http://plnkr.co/edit/g8nIqe37HEjvNOQz5z0p?p=preview
     * @author chatree
     *
     */
    @SuppressWarnings("serial")
    public static class Tabs extends ModelScreenWidget {
        public static final String TAG_NAME = "tabs";

        protected List<? extends Element> tabElements;
        protected boolean vertical;
        protected String type;
        protected String direction;

        public Tabs(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.tabElements = UtilXml.childElementList(widgetElement, "tab");
            this.vertical = Boolean.valueOf(FlexibleStringExpander.getInstance(widgetElement.getAttribute("vertical")).getOriginal());
            this.type = FlexibleStringExpander.getInstance(widgetElement.getAttribute("type")).getOriginal();
            this.direction = FlexibleStringExpander.getInstance(widgetElement.getAttribute("direction")).getOriginal();
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
                    String repeat = FlexibleStringExpander.getInstance(tabElement.getAttribute("repeat")).getOriginal();
                    String heading = FlexibleStringExpander.getInstance(tabElement.getAttribute("heading")).getOriginal();
                    String active = FlexibleStringExpander.getInstance(tabElement.getAttribute("active")).getOriginal();
                    String disabled = FlexibleStringExpander.getInstance(tabElement.getAttribute("disabled")).getOriginal();
                    String onSelect = FlexibleStringExpander.getInstance(tabElement.getAttribute("on-select")).getOriginal();
                    String targetUri = FlexibleStringExpander.getInstance(tabElement.getAttribute("target-uri")).getOriginal();
                    String targetParameters = FlexibleStringExpander.getInstance(tabElement.getAttribute("target-parameters")).getOriginal();
                    String targetContentModel = FlexibleStringExpander.getInstance(tabElement.getAttribute("target-content-model")).getOriginal();
                    tabWriter.append("<tab ");
                    if (UtilValidate.isNotEmpty(repeat)) {
                        tabWriter.append("ng-repeat=\"" + repeat + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(heading)) {
                        tabWriter.append("heading=\"" + heading + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(active)) {
                        tabWriter.append("active=\"" + active + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(disabled)) {
                        tabWriter.append("disabled=\"" + disabled + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(onSelect)) {
                        tabWriter.append("select=\"" + onSelect + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(onSelect)) {
                        tabWriter.append("select=\"" + onSelect + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(targetUri)) {
                        tabWriter.append("target-uri=\"" + targetUri + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(targetContentModel)) {
                        tabWriter.append("target-content-model=\"" + targetContentModel + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(targetParameters)) {
                        tabWriter.append("target-parameters=\"" + targetParameters + "\" ");
                    }
                    tabWriter.append("tab-options=\"\" ");
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
            builder.append("<tabset tab-set-options ");
            builder.append("vertical=\"" + vertical + "\"");
            if (UtilValidate.isNotEmpty(type)) {
                builder.append("type=\"" + type + "\"");
            }
            if (UtilValidate.isNotEmpty(direction)) {
                builder.append("direction=\"" + direction + "\"");
            }
            builder.append(">");
            return builder.toString();
        }
        
    }
    
    @SuppressWarnings("serial")
    public static class Text extends ModelScreenWidget {
        public static final String TAG_NAME = "text";

        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander typeExdr;
        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander placeholderExdr;

        public Text(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name"));
            this.typeExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("type"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("style"));
            this.placeholderExdr = FlexibleStringExpander.getInstance(widgetElement.getAttribute("placeholder"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            String name = nameExdr.expandString(context);
            String type = typeExdr.expandString(context);
            String model = modelExdr.expandString(context);
            String style = styleExdr.expandString(context);
            String placeholder = placeholderExdr.expandString(context);
            if (UtilValidate.isEmpty(type)) {
                type = "text";
            }
            
            writer.append("<input type=\"" + type + "\" name=\"" + name + "\" class=\"form-control " + style + "\"");
            if (UtilValidate.isNotEmpty(placeholder)) {
                writer.append(" placeholder=\"" + placeholder + "\"");
            }
            if (UtilValidate.isNotEmpty(model)) {
                writer.append(" ng-model=\"" + model + "\"");
            }
            writer.append("/>");
        }

        @Override
        public String rawString() {
            return "<input/>";
        }
        
    }
    
    @SuppressWarnings("serial")
    public static class TextArea extends ModelScreenWidget {
        public static final String TAG_NAME = "textarea";

        protected String name;
        protected String model;
        protected String style;
        protected String placeholder;
        protected boolean visualEditorEnable = false;

        public TextArea(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.name = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name")).getOriginal();
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
            builder.append("<textarea name=\"" + this.name + "\" class=\"form-control " + this.style + "\"");
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
    
    @SuppressWarnings("serial")
    public static class Tree extends ModelScreenWidget {
        public static final String TAG_NAME = "tree";

        protected String model;
        protected String nodeChildrenFieldName;
        protected String nodeIdFieldName;
        protected String nodeLabelFieldName;

        public Tree(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.model = FlexibleStringExpander.getInstance(widgetElement.getAttribute("model")).getOriginal();
            this.nodeChildrenFieldName = FlexibleStringExpander.getInstance(widgetElement.getAttribute("node-children-field-name")).getOriginal();
            this.nodeIdFieldName = FlexibleStringExpander.getInstance(widgetElement.getAttribute("node-id-field-name")).getOriginal();
            this.nodeLabelFieldName = FlexibleStringExpander.getInstance(widgetElement.getAttribute("node-label-field-name")).getOriginal();
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
            builder.append("<div");
            builder.append(" data-angular-treeview=\"true\"");
            builder.append(" data-tree-model=\"" + model + "\"");
            builder.append(" data-node-id=\"" + nodeIdFieldName + "\"");
            builder.append(" data-node-label=\"" + nodeLabelFieldName + "\"");
            builder.append(" data-node-children=\"" + nodeChildrenFieldName + "\"");
            builder.append(" tree-options=\"\"");
            builder.append("></div>");
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
        
        protected String name = null;

        public View(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.name = FlexibleStringExpander.getInstance(widgetElement.getAttribute("name")).getOriginal();
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
            return "<div ui-view=\"" + name + "\"></div>";
        }
    }
}
