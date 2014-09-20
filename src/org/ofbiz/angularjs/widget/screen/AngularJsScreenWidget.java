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

import org.ofbiz.base.util.BshUtil;
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

import bsh.EvalError;
import bsh.Interpreter;

public class AngularJsScreenWidget {

    public final static String module = AngularJsScreenWidget.class.getName();

    private static Interpreter getBshInterpreter(Map<String, Object> context)
            throws EvalError {
        Interpreter bsh = (Interpreter) context.get("bshInterpreter");
        if (bsh == null) {
            bsh = BshUtil.makeInterpreter(context);
            context.put("bshInterpreter", bsh);
        }
        return bsh;
    }

    @SuppressWarnings("serial")
    public static class Accordion extends ModelScreenWidget {
        public static final String TAG_NAME = "accordion";

        protected FlexibleStringExpander closeOthersExdr;
        protected List<? extends Element> accordionGroupElementList;

        public Accordion(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.closeOthersExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("close-others"));
            accordionGroupElementList = UtilXml.childElementList(widgetElement,
                    "accordion-group");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<accordion close-others=\""
                    + this.closeOthersExdr.expandString(context) + "\">");
            for (Element accordionGroupElement : accordionGroupElementList) {
                FlexibleStringExpander headingExdr = FlexibleStringExpander
                        .getInstance(accordionGroupElement
                                .getAttribute("heading"));
                FlexibleStringExpander repeatExdr = FlexibleStringExpander
                        .getInstance(accordionGroupElement
                                .getAttribute("repeat"));
                writer.append("<accordion-group heading=\""
                        + headingExdr.getOriginal() + "\"");
                if (UtilValidate.isNotEmpty(repeatExdr.getOriginal())) {
                    writer.append(" ng-repeat=\"" + repeatExdr.getOriginal()
                            + "\"");
                }
                writer.append(">");
                // read sub-widgets
                List<? extends Element> subElementList = UtilXml
                        .childElementList(accordionGroupElement);
                List<ModelScreenWidget> subWidgets = ModelScreenWidget
                        .readSubWidgets(this.modelScreen, subElementList);
                renderSubWidgetsString(subWidgets, writer, context,
                        screenStringRenderer);
                writer.append("</accordion-group>");
            }
            writer.append("</accordion>");
        }

        @Override
        public String rawString() {
            return "<accordion/>";
        }

    }

    @SuppressWarnings("serial")
    public static class ActionPanel extends ModelScreenWidget {

        public static final String TAG_NAME = "action-panel";

        protected FlexibleStringExpander useWhenExdr;
        protected List<ModelScreenWidget> subWidgets;

        public ActionPanel(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.useWhenExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("use-when"));
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {

            boolean usewhen = true;
            String useWhenStr = useWhenExdr.expandString(context);
            if (UtilValidate.isEmpty(useWhenStr)) {
                usewhen = true;
            } else {
                try {
                    Interpreter bsh = getBshInterpreter(context);
                    Object retVal = bsh.eval(StringUtil
                            .convertOperatorSubstitutions(useWhenStr));
                    boolean condTrue = false;
                    // retVal should be a Boolean, if not something weird is
                    // up...
                    if (retVal instanceof Boolean) {
                        Boolean boolVal = (Boolean) retVal;
                        condTrue = boolVal.booleanValue();
                    } else {
                        throw new IllegalArgumentException(
                                "Return value from use-when condition eval was not a Boolean: "
                                        + (retVal != null ? retVal.getClass()
                                                .getName() : "null") + " ["
                                        + retVal + "] on the field "
                                        + this.name);
                    }

                    usewhen = condTrue;
                } catch (EvalError e) {
                    String errMsg = "Error evaluating BeanShell use-when condition ["
                            + useWhenStr
                            + "] on the field "
                            + this.name
                            + ": "
                            + e.toString();
                    Debug.logError(e, errMsg, module);
                    // Debug.logError("For use-when eval error context is: " +
                    // context, module);
                    throw new IllegalArgumentException(errMsg);
                }
            }

            if (usewhen) {
                writer.append(this.rawString());
                renderSubWidgetsString(this.subWidgets, writer, context,
                        screenStringRenderer);
                writer.append("</div>");
            }
        }

        @Override
        public String rawString() {
            return "<div class=\"form-actions\">";
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
            this.repeatExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("repeat"));
            this.typeExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("type"));
            this.closeExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("close"));
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("text"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<alert ng-cloak ng-repeat=\""
                    + this.repeatExdr.expandString(context) + "\" type=\""
                    + typeExdr.expandString(context) + "\" close=\""
                    + closeExdr.expandString(context) + "\">");
            writer.append(textExdr.expandString(context));
            writer.append("</alert>");
        }

        @Override
        public String rawString() {
            return "<alert/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Application extends ModelScreenWidget {
        public static final String TAG_NAME = "application";

        protected FlexibleStringExpander nameExdr;
        protected List<ModelScreenWidget> subWidgets;

        public Application(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div ng-app=\""
                    + this.nameExdr.expandString(context) + "\">");
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div ng-app/>";
        }

    }

    @SuppressWarnings("serial")
    public static class Button extends ModelScreenWidget {
        public static final String TAG_NAME = "button";

        protected FlexibleStringExpander textExdr;
        protected FlexibleStringExpander onClickExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander clickConfirmMessageExdr;

        public Button(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("text"));
            this.onClickExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("on-click"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
            this.clickConfirmMessageExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("click-confirm-message"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {

            String classAttributeName = "class";
            String style = styleExdr.expandString(context);

            if (UtilValidate.isNotEmpty(style) && !style.startsWith("btn ")
                    && !style.endsWith(")")) {
                style = "btn " + style;
            } else {
                classAttributeName = "ng-" + classAttributeName;
            }

            writer.append("<button " + classAttributeName + "=\"" + style
                    + "\" ng-click=\"" + this.onClickExdr.expandString(context) + "\"");

            if (UtilValidate.isNotEmpty(clickConfirmMessageExdr.getOriginal())) {
                writer.append(" click-confirm-message=\"" + clickConfirmMessageExdr.expandString(context) + "\"");
            }

            writer.append(">" + this.textExdr.expandString(context) + "</button>");
        }

        @Override
        public String rawString() {
            return "<button/>";
        }

    }

    @SuppressWarnings("serial")
    public static class Calendar extends ModelScreenWidget {
        public static final String TAG_NAME = "calendar";

        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander optionsExdr;
        protected FlexibleStringExpander modelExdr;

        public Calendar(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
            this.optionsExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("options"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div ui-calendar=\""
                    + optionsExdr.expandString(context) + "\"");
            if (UtilValidate.isNotEmpty(nameExdr.getOriginal())) {
                writer.append(" calendar=\""
                        + this.nameExdr.expandString(context) + "\"");
            }
            if (UtilValidate.isNotEmpty(modelExdr.getOriginal())) {
                writer.append(" ng-model=\""
                        + this.modelExdr.expandString(context) + "\"");
            }
            writer.append(">");
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div ui-calendar/>";
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
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("text"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<label class=\"checkbox\">");
            writer.append("<input type=\"checkbox\" class=\""
                    + this.styleExdr.expandString(context) + "\"");
            if (UtilValidate.isNotEmpty(modelExdr.getOriginal())) {
                writer.append(" ng-model=\""
                        + this.modelExdr.expandString(context) + "\"");
            }
            writer.append("/>");
            writer.append(this.textExdr.expandString(context));
            writer.append("</label>");
        }

        @Override
        public String rawString() {
            return "<input checkbox/>";
        }

    }

    @SuppressWarnings("serial")
    public static class Collapse extends ModelScreenWidget {
        public static final String TAG_NAME = "collapse";

        protected FlexibleStringExpander collapseExdr;
        protected List<ModelScreenWidget> subWidgets;

        public Collapse(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.collapseExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("collapse"));
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div collapse=\""
                    + this.collapseExdr.expandString(context) + "\">");
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div collapse/>";
        }
    }

    @SuppressWarnings("serial")
    public static class CommentsPanel extends ModelScreenWidget {
        public static final String TAG_NAME = "comments-panel";

        protected FlexibleStringExpander contentIdExdr;
        protected FlexibleStringExpander onCreateSuccessExdr;

        public CommentsPanel(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.contentIdExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("content-id"));
            this.onCreateSuccessExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("on-create-success"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<comments-panel");
            writer.append(" content-id=\""
                    + contentIdExdr.expandString(context) + "\"");
            writer.append(" on-create-success=\""
                    + onCreateSuccessExdr.expandString(context) + "\"");
            writer.append("/>");
        }

        @Override
        public String rawString() {
            return "<comments-panel/>";
        }

    }

    @SuppressWarnings("serial")
    public static class ContainerFluid extends ModelScreenWidget {
        public static final String TAG_NAME = "container-fluid";

        protected List<ModelScreenWidget> subWidgets;

        public ContainerFluid(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
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

        protected FlexibleStringExpander targetExdr = null;
        protected FlexibleStringExpander parametersExdr = null;
        protected FlexibleStringExpander modelExdr = null;
        protected FlexibleStringExpander fieldExdr = null;
        protected List<ModelScreenWidget> subWidgets;

        public Context(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.targetExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("target"));
            this.parametersExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("parameters"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.fieldExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("field"));
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<context target=\""
                    + targetExdr.expandString(context) + "\" parameters=\""
                    + parametersExdr.expandString(context) + "\" ng-model=\""
                    + modelExdr.expandString(context) + "\" field=\""
                    + fieldExdr.expandString(context) + "\" ng-transclude>");
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
            writer.append("</context>");
        }

        @Override
        public String rawString() {
            return "<context/>";
        }
    }

    @SuppressWarnings("serial")
    public static class ControlGroup extends ModelScreenWidget {
        public static final String TAG_NAME = "control-group";

        protected FlexibleStringExpander rowExdr = null;
        protected Element widgetElement = null;

        public ControlGroup(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.widgetElement = widgetElement;
            this.rowExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("row"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            List<? extends Element> subElements = UtilXml
                    .childElementList(this.widgetElement);
            for (Element subElement : subElements) {
                if ("control-label".equals(subElement.getNodeName())) {
                    String text = UtilXml.elementAttribute(subElement, "text",
                            null);
                    String forAttr = UtilXml.elementAttribute(subElement,
                            "for", null);
                    writer.append("<label class=\"control-label\" for=\""
                            + forAttr + "\">");
                    writer.append(text);
                    writer.append("</label>");
                } else if ("controls".equals(subElement.getNodeName())) {
                    List<? extends Element> controlElements = UtilXml
                            .childElementList(subElement);
                    for (Element controlElement : controlElements) {
                        // read sub-widget
                        List<Element> cElementList = new LinkedList<Element>();
                        cElementList.add(controlElement);
                        List<ModelScreenWidget> controlWidgets = ModelScreenWidget
                                .readSubWidgets(this.modelScreen, cElementList);
                        renderSubWidgetsString(controlWidgets, writer, context,
                                screenStringRenderer);
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
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div ng-controller=\""
                    + this.nameExdr.expandString(context) + "\">");
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div ng-controller/>";
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
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.showWeeksExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("show-weeks"));
            this.startingDayExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("starting-day"));
            this.dateDisabledExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("date-disabled"));
            this.minExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("min"));
            this.maxExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("max"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<datepicker ng-model=\""
                    + modelExdr.expandString(context) + "\" show-weeks=\""
                    + showWeeksExdr.expandString(context) + "\""
                    + " starting-day=\""
                    + startingDayExdr.expandString(context)
                    + "\" date-disabled=\""
                    + dateDisabledExdr.expandString(context) + "\"" + " min=\""
                    + minExdr.expandString(context) + "\" max=\""
                    + maxExdr.expandString(context) + "\">");
            writer.append("</datepicker>");
        }

        @Override
        public String rawString() {
            return "<datepicker/>";
        }
    }

    @SuppressWarnings("serial")
    public static class DateTime extends ModelScreenWidget {
        public static final String TAG_NAME = "date-time";

        protected FlexibleStringExpander formatExdr = null;
        protected FlexibleStringExpander modelExdr = null;
        protected FlexibleStringExpander styleExdr = null;

        public DateTime(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.formatExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("format"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {

            String format = null;
            String style = null;

            if (UtilValidate.isEmpty(this.formatExdr.getOriginal())) {
                format = "MMM d, yyyy h:mm:ss a";
            } else {
                format = this.formatExdr.expandString(context);
            }

            if (UtilValidate.isEmpty(this.styleExdr.getOriginal())) {
                style = "input-large";
            } else {
                style = this.styleExdr.expandString(context);
            }

            writer.append("<date-time ng-model=\"" + modelExdr.expandString(context) + "\" style=\"" + style + "\" format=\"" + format + "\"/>");
        }

        @Override
        public String rawString() {
            return "<date-time/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Dropdown extends ModelScreenWidget {
        public static final String TAG_NAME = "dropdown";

        protected FlexibleStringExpander nameExdr = null;
        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander targetExdr;
        protected FlexibleStringExpander parametersExdr;
        protected FlexibleStringExpander dependentParameterNamesExdr;
        protected FlexibleStringExpander placeholderExdr;
        protected FlexibleStringExpander fieldNameExdr = null;
        protected FlexibleStringExpander descriptionFieldNameExdr = null;
        protected FlexibleStringExpander defaultValueExdr = null;

        public Dropdown(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.targetExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("target"));
            this.parametersExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("parameters"));
            this.dependentParameterNamesExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("dependent-parameter-names"));
            this.placeholderExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("placeholder"));
            this.fieldNameExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("field-name"));
            this.descriptionFieldNameExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("description-field-name"));
            this.defaultValueExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("default-value"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<dropdown");
            if (UtilValidate.isNotEmpty(modelExdr.getOriginal())) {
                writer.append(" model=\"" + modelExdr.expandString(context)
                        + "\"");
            }
            if (UtilValidate.isNotEmpty(targetExdr.getOriginal())) {
                writer.append(" target=\"" + targetExdr.expandString(context)
                        + "\"");
            }
            if (UtilValidate.isNotEmpty(parametersExdr.getOriginal())) {
                writer.append(" parameters=\""
                        + parametersExdr.expandString(context) + "\"");
            }
            if (UtilValidate.isNotEmpty(dependentParameterNamesExdr.getOriginal())) {
                writer.append(" dependent-parameter-names=\""
                        + dependentParameterNamesExdr.expandString(context) + "\"");
            }
            writer.append(" description-field-name=\""
                    + descriptionFieldNameExdr.expandString(context)
                    + "\" field-name=\"" + fieldNameExdr.expandString(context)
                    + "\"");
            writer.append(" default-value=\""
                    + defaultValueExdr.expandString(context) + "\"");
            writer.append(" placeholder=\""
                    + placeholderExdr.expandString(context)
                    + "\" class=\"form-control\"");
            writer.append(">");
            writer.append("</dropdown>");
        }

        @Override
        public String rawString() {
            return "<data/>";
        }
    }

    @SuppressWarnings("serial")
    public static class DropdownToggle extends ModelScreenWidget {
        public static final String TAG_NAME = "dropdown-toggle";

        protected FlexibleStringExpander textExdr;
        protected List<? extends Element> optionElementList;

        public DropdownToggle(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("text"));
            optionElementList = UtilXml.childElementList(widgetElement,
                    "option");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append("<a class=\"dropdown-toggle\">"
                    + textExdr.expandString(context) + "</a>");
            writer.append("<ul class=\"dropdown-menu\">");
            for (Element optionElement : optionElementList) {
                FlexibleStringExpander textExdr = FlexibleStringExpander
                        .getInstance(optionElement.getAttribute("text"));
                FlexibleStringExpander repeatExdr = FlexibleStringExpander
                        .getInstance(optionElement.getAttribute("repeat"));
                writer.append("<li");
                if (UtilValidate.isNotEmpty(repeatExdr.expandString(context))) {
                    writer.append(" ng-repeat=\""
                            + repeatExdr.expandString(context) + "\"");
                }
                writer.append(">");
                writer.append("<a>" + textExdr.expandString(context) + "</a>");
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

        protected FlexibleStringExpander textContentExpr = null;
        protected List<? extends Element> subElementList = null;

        public EmphasizedText(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            textContentExpr = FlexibleStringExpander.getInstance(widgetElement
                    .getTextContent());
            subElementList = UtilXml.childElementList(widgetElement);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append(textContentExpr.expandString(context));
            // read sub-widgets
            List<ModelScreenWidget> subWidgets = ModelScreenWidget
                    .readSubWidgets(this.modelScreen, subElementList);
            renderSubWidgetsString(subWidgets, writer, context,
                    screenStringRenderer);
            writer.append("</em>");
        }

        @Override
        public String rawString() {
            return "<em>";
        }
    }

    /**
     * Service http://twilson63.github.io/ngUpload/
     *
     * @author chatree
     *
     */
    @SuppressWarnings("serial")
    public static class File extends ModelScreenWidget {
        public static final String TAG_NAME = "file";

        protected FlexibleStringExpander modelExdr;

        public File(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<file-wrapper");
            writer.append(" ng-model=\"" + modelExdr.expandString(context));
            writer.append("\">");
            writer.append("</file-wrapper>");
        }

        @Override
        public String rawString() {
            return "<input type=\"file\"/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Field extends ModelScreenWidget {
        public static final String TAG_NAME = "field";

        protected FlexibleStringExpander titleExdr;
        protected FlexibleStringExpander useWhenExdr;
        protected FlexibleStringExpander helpTextExdr;
        protected List<ModelScreenWidget> subWidgets;

        public Field(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.titleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("title"));
            this.useWhenExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("use-when"));
            this.helpTextExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("help-text"));
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {

            boolean usewhen = true;
            String useWhenStr = useWhenExdr.expandString(context);
            if (UtilValidate.isEmpty(useWhenStr)) {
                usewhen = true;
            } else {
                try {
                    Interpreter bsh = getBshInterpreter(context);
                    Object retVal = bsh.eval(StringUtil
                            .convertOperatorSubstitutions(useWhenStr));
                    boolean condTrue = false;
                    // retVal should be a Boolean, if not something weird is
                    // up...
                    if (retVal instanceof Boolean) {
                        Boolean boolVal = (Boolean) retVal;
                        condTrue = boolVal.booleanValue();
                    } else {
                        throw new IllegalArgumentException(
                                "Return value from use-when condition eval was not a Boolean: "
                                        + (retVal != null ? retVal.getClass()
                                                .getName() : "null") + " ["
                                        + retVal + "] on the field "
                                        + this.name);
                    }

                    usewhen = condTrue;
                } catch (EvalError e) {
                    String errMsg = "Error evaluating BeanShell use-when condition ["
                            + useWhenStr
                            + "] on the field "
                            + this.name
                            + ": "
                            + e.toString();
                    Debug.logError(e, errMsg, module);
                    // Debug.logError("For use-when eval error context is: " +
                    // context, module);
                    throw new IllegalArgumentException(errMsg);
                }
            }

            if (usewhen) {
                writer.append(rawString());
                if (UtilValidate.isNotEmpty(titleExdr.getOriginal())) {
                    writer.append("<label class=\"control-label\">"
                            + titleExdr.expandString(context) + "</label>");
                }
                writer.append("<div class=\"controls\">");
                renderSubWidgetsString(this.subWidgets, writer, context,
                        screenStringRenderer);
                if (UtilValidate.isNotEmpty(helpTextExdr.getOriginal())) {
                    writer.append("<p class=\"help-block\">");
                    writer.append(helpTextExdr.expandString(context));
                    writer.append("</p>");
                }
                writer.append("</div>");
                writer.append("</div>");
            }
        }

        @Override
        public String rawString() {
            return "<div class=\"control-group\">";
        }

    }

    /**
     * http://getbootstrap.com/css/#forms
     * http://www.w3resource.com/twitter-bootstrap/forms-tutorial.php
     *
     * @author chatree
     *
     */
    @SuppressWarnings("serial")
    public static class Form extends ModelScreenWidget {
        public static final String TAG_NAME = "form";

        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander typeExdr;
        protected FlexibleStringExpander legendExdr;
        protected FlexibleStringExpander validatedExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander onSubmitExdr = null;
        protected List<ModelScreenWidget> subWidgets;

        public Form(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
            this.typeExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("type"));
            this.legendExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("legend"));
            this.validatedExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("validated"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
            this.onSubmitExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("on-submit"));

            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {

            String formStyle = "form-" + typeExdr.expandString(context);
            boolean validated = Boolean.valueOf(validatedExdr
                    .expandString(context));

            writer.append("<form name=\"" + name + "\" role=\"form\" class=\""
                    + formStyle + " ");
            if (UtilValidate.isNotEmpty(styleExdr.getOriginal())) {
                writer.append(styleExdr.expandString(context));
            }
            writer.append("\"");

            if (!validated) {
                writer.append("novalidate ");
            }

            writer.append("ng-submit=\"" + onSubmitExdr.expandString(context)
                    + "\" ");

            writer.append(" form-options=\"\" ng-transclude>");
            writer.append("<fieldset>");
            if (UtilValidate.isNotEmpty(legendExdr.getOriginal())) {
                writer.append("<legend>" + legendExdr.expandString(context)
                        + "</legend>");
            }
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
            writer.append("</fieldset>");
            writer.append("</form>");
        }

        @Override
        public String rawString() {
            return "<form/>";
        }
    }

    @SuppressWarnings("serial")
    public static class GoogleChart extends ModelScreenWidget {
        public static final String TAG_NAME = "google-chart";

        protected FlexibleStringExpander styleExdr = null;
        protected FlexibleStringExpander typeExdr = null;
        protected FlexibleStringExpander displayedExdr = null;
        protected FlexibleStringExpander titleExdr = null;
        protected FlexibleStringExpander isStackedExdr = null;
        protected FlexibleStringExpander fillExdr = null;
        protected FlexibleStringExpander displayExactValuesExdr = null;
        protected FlexibleStringExpander vTitleExdr = null;
        protected FlexibleStringExpander hTitleExdr = null;
        protected FlexibleStringExpander vGridLinesCountExdr = null;
        protected FlexibleStringExpander targetExdr = null;
        protected FlexibleStringExpander modelExdr = null;

        public GoogleChart(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
            this.typeExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("type"));
            this.displayedExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("displayed"));
            this.titleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("title"));
            this.isStackedExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("is-stacked"));
            this.fillExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("fill"));
            this.displayExactValuesExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("style"));
            this.vTitleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("v-title"));
            this.hTitleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("h-title"));
            this.vGridLinesCountExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("v-grid-lines-count"));
            this.targetExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("target"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            int fill = 20;
            int vGridLinesCount = 18;

            if (UtilValidate.isNotEmpty(fillExdr.getOriginal())) {
                fill = Integer.valueOf(fillExdr.expandString(context));
            }

            if (UtilValidate.isNotEmpty(vGridLinesCountExdr.getOriginal())) {
                vGridLinesCount = Integer.valueOf(vGridLinesCountExdr
                        .expandString(context));
            }

            writer.append("<div google-chart chart=\""
                    + modelExdr.expandString(context) + "\" style=\""
                    + styleExdr.expandString(context) + "\" type=\""
                    + typeExdr.expandString(context) + "\"" + " displayed=\""
                    + displayedExdr.expandString(context) + "\" title=\""
                    + titleExdr.expandString(context) + "\" is-stacked=\""
                    + isStackedExdr.expandString(context) + "\"" + " fill=\""
                    + fill + "\" diplay-exact-valules=\""
                    + displayExactValuesExdr.expandString(context) + "\""
                    + " v-title=\"" + vTitleExdr.expandString(context)
                    + "\" h-title=\"" + hTitleExdr.expandString(context)
                    + "\" v-grid-lines-count=\"" + vGridLinesCount + "\""
                    + " target=\"" + targetExdr.expandString(context)
                    + "\" google-chart-options=\"\"/>");
        }

        @Override
        public String rawString() {
            return "<div google-chart/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Grid extends ModelScreenWidget {
        public static final String TAG_NAME = "grid";

        protected FlexibleStringExpander selectTargetExdr;
        protected FlexibleStringExpander selectParametersExdr;
        protected FlexibleStringExpander selectedItemsExdr;
        protected FlexibleStringExpander listNameExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander rowHeightExdr;
        protected FlexibleStringExpander multiSelectExdr;
        protected FlexibleStringExpander showSelectionCheckboxExdr;
        protected FlexibleStringExpander onBeforeSelectionChangedExdr = null;
        protected FlexibleStringExpander onAfterSelectionChangedExdr = null;
        protected FlexibleStringExpander onRowDoubleClickedExdr = null;
        protected FlexibleStringExpander afterDataLoadExdr = null;
        protected List<? extends Element> fieldElements;

        public Grid(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.selectTargetExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("select-target"));
            this.selectParametersExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("select-parameters"));
            this.selectedItemsExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("selected-items"));
            this.listNameExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("list-name"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
            this.onBeforeSelectionChangedExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("on-before-selection-changed"));
            this.onAfterSelectionChangedExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("on-after-selection-changed"));
            this.onRowDoubleClickedExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("on-row-double-clicked"));
            this.afterDataLoadExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("after-data-load"));
            this.rowHeightExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("row-height"));
            this.multiSelectExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("multi-select"));
            this.showSelectionCheckboxExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("show-selection-checkbox"));
            this.fieldElements = UtilXml.childElementList(widgetElement,
                    "field");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            int defaultRowHeight = 40;
            int rowHeight = 20;

            try {
                rowHeight = Integer.parseInt(rowHeightExdr
                        .expandString(context));
            } catch (Exception e) {
            }

            if (UtilValidate.isEmpty(rowHeightExdr.getOriginal())) {
                rowHeight = defaultRowHeight;
            }

            StringBuilder sortInfoBuilder = new StringBuilder();
            List<Map<String, String>> sortDirections = new LinkedList<Map<String, String>>();
            StringBuilder columnDefsBuilder = new StringBuilder();
            columnDefsBuilder.append("[");
            if (UtilValidate.isNotEmpty(fieldElements)) {
                StringBuilder fieldsBuilder = new StringBuilder();
                for (Element fieldElement : fieldElements) {
                    fieldsBuilder.append("{");
                    String name = UtilXml.elementAttribute(fieldElement,
                            "name", null);
                    String fieldName = UtilXml.elementAttribute(fieldElement,
                            "field-name", null);
                    String title = UtilXml.elementAttribute(fieldElement,
                            "title", null);
                    String editable = UtilXml.elementAttribute(fieldElement,
                            "editable", "false");
                    String headerCellTemplateUri = UtilXml.elementAttribute(
                            fieldElement, "header-cell-template-uri", null);
                    String cellTemplateUri = UtilXml.elementAttribute(
                            fieldElement, "cell-template-uri", null);
                    String editableCellTemplateUri = UtilXml.elementAttribute(
                            fieldElement, "editable-cell-template-uri", null);
                    String sortDirection = UtilXml.elementAttribute(
                            fieldElement, "sort-direction", null);

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
                        fieldsBuilder.append(",headerCellTemplate:'"
                                + headerCellTemplateUri + "'");
                    }
                    if (UtilValidate.isNotEmpty(cellTemplateUri)) {
                        fieldsBuilder.append(",cellTemplate:'"
                                + cellTemplateUri + "'");
                    }
                    if (UtilValidate.isNotEmpty(editableCellTemplateUri)) {
                        fieldsBuilder.append(",editableCellTemplate:'"
                                + editableCellTemplateUri + "'");
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
                    fieldString = fieldString.substring(0,
                            fieldsBuilder.length() - 1);
                }

                if (UtilValidate.isNotEmpty(sortDirections)) {
                    List<String> sortFields = new LinkedList<String>();
                    List<String> directions = new LinkedList<String>();
                    for (Map<String, String> sortDirectionMap : sortDirections) {
                        String sortField = sortDirectionMap.get("sortField");
                        String sortDirection = sortDirectionMap
                                .get("sortDirection");
                        sortFields.add(sortField);
                        directions.add(sortDirection);
                    }

                    if (UtilValidate.isNotEmpty(sortFields)) {
                        sortInfoBuilder.append("{");
                        sortInfoBuilder.append("fields: ["
                                + StringUtil.join(sortFields, ",") + "],");
                        sortInfoBuilder.append("directions: ["
                                + StringUtil.join(directions, ",") + "]");
                        sortInfoBuilder.append("}");
                    }
                }

                columnDefsBuilder.append(fieldString);
            }
            columnDefsBuilder.append("]");

            writer.append("<grid style=\""
                    + styleExdr.expandString(context)
                    + "\" row-height=\""
                    + rowHeight
                    + "\" select-target=\""
                    + selectTargetExdr.expandString(context)
                    + "\" select-parameters=\""
                    + selectParametersExdr.expandString(context)
                    + "\" selected-items=\""
                    + selectedItemsExdr.expandString(context)
                    + "\" list-name=\""
                    + listNameExdr.expandString(context)
                    + "\" column-defs=\""
                    + columnDefsBuilder.toString()
                    + "\" on-before-selection-changed=\""
                    + onBeforeSelectionChangedExdr.expandString(context)
                    + "\" on-after-selection-changed=\""
                    + onAfterSelectionChangedExdr.expandString(context)
                    + "\" on-row-double-clicked=\""
                    + onRowDoubleClickedExdr.expandString(context)
                    + "\" after-data-load=\""
                    + afterDataLoadExdr.expandString(context)
                    + "\" multi-select=\""
                    + multiSelectExdr.expandString(context)
                    + "\" show-selection-checkbox=\""
                    + showSelectionCheckboxExdr.expandString(context)
                    + "\" "
                    + (UtilValidate.isNotEmpty(sortInfoBuilder.toString()) ? "sort-info=\""
                            + sortInfoBuilder.toString() + "\""
                            : "") + "></grid>");
        }

        @Override
        public String rawString() {
            return "<div ng-grid/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Hidden extends ModelScreenWidget {
        public static final String TAG_NAME = "hidden";

        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander valueExdr;

        public Hidden(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
            this.valueExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("value"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<input type=\"hidden\" name=\""
                    + nameExdr.expandString(context) + "\" value=\""
                    + valueExdr.expandString(context) + "\"/>");
        }

        @Override
        public String rawString() {
            return "<input type=\"hidden\"/>";
        }

    }

    @SuppressWarnings("serial")
    public static class Html extends ModelScreenWidget {
        public static final String TAG_NAME = "html";

        private FlexibleStringExpander modelExdr;

        public Html(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div html ng-model=\""
                    + modelExdr.expandString(context) + "\"></div>");
        }

        @Override
        public String rawString() {
            return "<div html/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Image extends ModelScreenWidget.Image {

        public Image(ModelScreen modelScreen, Element imageElement) {
            super(modelScreen, imageElement);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer) {
            try {
                if (UtilValidate.isNotEmpty(srcExdr.getOriginal())) {
                    writer.append("<img");
                    if (UtilValidate.isNotEmpty(idExdr.getOriginal())) {
                        writer.append(" id=\"" + idExdr.expandString(context) + "\"");
                    }
                    if (UtilValidate.isNotEmpty(styleExdr.getOriginal())) {
                    writer.append(" class=\"" + styleExdr.expandString(context) + "\"");
                    }
                    if (UtilValidate.isNotEmpty(widthExdr.getOriginal())) {
                    writer.append(" width=\"" + widthExdr.expandString(context) + "\"");
                    }
                    if (UtilValidate.isNotEmpty(heightExdr.getOriginal())) {
                    writer.append(" height=\"" + heightExdr.expandString(context) + "\"");
                    }
                    if (UtilValidate.isNotEmpty(borderExdr.getOriginal())) {
                    writer.append(" border=\"" + borderExdr.expandString(context) + "\"");
                    }
                    if (UtilValidate.isNotEmpty(alt.getOriginal())) {
                    writer.append(" alt=\"" + alt.expandString(context) + "\"");
                    }
                    if (UtilValidate.isNotEmpty(srcExdr.getOriginal())) {
                    writer.append(" ng-src=\"" + srcExdr.expandString(context) + "\"");
                    }
                    writer.append("/>");
                }
            } catch (IOException e) {
                Debug.logError(e, module);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class JitTree extends ModelScreenWidget {
        public static final String TAG_NAME = "jit-tree";

        protected FlexibleStringExpander typeExdr = null;
        protected FlexibleStringExpander modelExdr = null;
        protected FlexibleStringExpander nodeTemplateUrlExdr = null;
        protected FlexibleStringExpander levelDistanceExdr = null;
        protected FlexibleStringExpander nodeHeightExdr = null;
        protected FlexibleStringExpander nodeWidthExdr = null;
        protected FlexibleStringExpander nodeClickExdr = null;
        protected FlexibleStringExpander nodeParametersExdr = null;

        public JitTree(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.typeExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("type"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.nodeTemplateUrlExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("node-template-url"));
            this.levelDistanceExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("level-distance"));
            this.nodeHeightExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("node-height"));
            this.nodeWidthExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("node-width"));
            this.nodeClickExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("node-click"));
            this.nodeParametersExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("node-parameters"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div jit-tree type=\""
                    + typeExdr.expandString(context) + "\" ng-model=\""
                    + modelExdr.expandString(context)
                    + "\" node-template-url=\""
                    + nodeTemplateUrlExdr.expandString(context) + "\" "
                    + "level-distanc=\""
                    + levelDistanceExdr.expandString(context)
                    + "\" node-height=\""
                    + nodeHeightExdr.expandString(context) + "\" node-width=\""
                    + nodeWidthExdr.expandString(context) + "\" node-click=\""
                    + nodeClickExdr.expandString(context)
                    + "\" node-parameters=\""
                    + nodeParametersExdr.expandString(context) + "\">");
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div jit-tree/>";
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

    /**
     * http://ivaynberg.github.io/select2/ see "Loading Remote Data"
     *
     * @author chatree
     *
     */
    @SuppressWarnings("serial")
    public static class Lookup extends ModelScreenWidget {
        public static final String TAG_NAME = "lookup";

        protected FlexibleStringExpander targetExdr = null;
        protected FlexibleStringExpander modelExdr = null;
        protected FlexibleStringExpander fieldNameExdr = null;
        protected FlexibleStringExpander descriptionFieldNameExdr = null;
        protected FlexibleStringExpander parametersExdr;
        protected FlexibleStringExpander dependentParameterNamesExdr;
        protected FlexibleStringExpander placeholderExdr;
        protected FlexibleStringExpander defaultValueExdr;

        public Lookup(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.targetExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("target"));
            this.parametersExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("parameters"));
            this.dependentParameterNamesExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("dependent-parameter-names"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.fieldNameExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("field-name"));
            this.descriptionFieldNameExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("description-field-name"));
            this.placeholderExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("placeholder"));
            this.defaultValueExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("default-value"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<lookup target=\""
                    + targetExdr.expandString(context) + "\" ng-model=\""
                    + modelExdr.expandString(context)
                    + "\" description-field-name=\""
                    + descriptionFieldNameExdr.expandString(context)
                    + "\" field-name=\"" + fieldNameExdr.expandString(context)
                    + "\"");
            if (UtilValidate.isNotEmpty(placeholderExdr.getOriginal())) {
                writer.append(" placeholder=\""
                        + placeholderExdr.expandString(context) + "\"");
            }
            if (UtilValidate.isNotEmpty(parametersExdr.getOriginal())) {
                writer.append(" parameters=\""
                        + parametersExdr.expandString(context) + "\"");
            }
            if (UtilValidate.isNotEmpty(dependentParameterNamesExdr.getOriginal())) {
                writer.append(" dependent-parameter-names=\""
                        + dependentParameterNamesExdr.expandString(context) + "\"");
            }
            if (UtilValidate.isNotEmpty(defaultValueExdr.getOriginal())) {
                writer.append(" default-value=\""
                        + defaultValueExdr.expandString(context) + "\"");
            }
            writer.append(">");
            writer.append("</lookup>");
        }

        @Override
        public String rawString() {
            return "<data/>";
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

        protected FlexibleStringExpander titleExdr = null;
        protected FlexibleStringExpander targetExdr = null;
        protected FlexibleStringExpander styleExdr = null;
        protected List<? extends Element> itemElementList = null;

        public MenuBar(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            titleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("title"));
            targetExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("target"));
            styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
            itemElementList = UtilXml.childElementList(widgetElement,
                    "menu-item");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div class=\"navbar\" "
                    + styleExdr.expandString(context) + ">");
            writer.append("<div class=\"navbar-inner\">");
            writer.append("<div class=\"container\">");
            writer.append("<a class=\"brand\" ui-sref=\""
                    + targetExdr.expandString(context) + "\">"
                    + titleExdr.expandString(context) + "</a>");
            writer.append("<ul class=\"nav\">");

            for (Element itemElement : itemElementList) {
                String target = UtilXml.elementAttribute(itemElement, "target",
                        null);
                String text = UtilXml.elementAttribute(itemElement, "text",
                        null);
                String activeState = UtilXml.elementAttribute(itemElement,
                        "active-state", null);

                if (UtilValidate.isEmpty(activeState)) {
                    activeState = target;
                }

                writer.append("<li ng-class=\"{ active: $state.includes('"
                        + activeState + "') }\"><a ui-sref=\"" + target + "\">"
                        + text + "</a></li>");
            }
            writer.append("</ul>");
            writer.append("</div>");
            writer.append("</div>");
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div navbar/>";
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
            this.shouldBeOpenExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("should-be-open"));
            this.closeExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("close"));
            this.optionsExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("options"));
            this.modalHeaderElement = UtilXml.firstChildElement(widgetElement,
                    "modal-header");
            this.modalBodyElement = UtilXml.firstChildElement(widgetElement,
                    "modal-body");
            this.modalFooterElement = UtilXml.firstChildElement(widgetElement,
                    "modal-footer");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div modal=\""
                    + shouldBeOpenExdr.expandString(context) + "\" close=\""
                    + closeExdr.expandString(context) + "\" options=\""
                    + optionsExdr.expandString(context) + "\">");
            if (UtilValidate.isNotEmpty(modalHeaderElement)) {
                writer.append("<div class=\"modal-header\">");
                // read header sub-widgets
                List<? extends Element> subElementList = UtilXml
                        .childElementList(modalHeaderElement);
                List<ModelScreenWidget> subWidgets = ModelScreenWidget
                        .readSubWidgets(this.modelScreen, subElementList);
                renderSubWidgetsString(subWidgets, writer, context,
                        screenStringRenderer);
                writer.append("</div>");
            }
            if (UtilValidate.isNotEmpty(modalBodyElement)) {
                writer.append("<div class=\"modal-body\">");
                // read body sub-widgets
                List<? extends Element> subElementList = UtilXml
                        .childElementList(modalBodyElement);
                List<ModelScreenWidget> subWidgets = ModelScreenWidget
                        .readSubWidgets(this.modelScreen, subElementList);
                renderSubWidgetsString(subWidgets, writer, context,
                        screenStringRenderer);
                writer.append("</div>");
            }
            if (UtilValidate.isNotEmpty(modalFooterElement)) {
                writer.append("<div class=\"modal-footer\">");
                // read footer sub-widgets
                List<? extends Element> subElementList = UtilXml
                        .childElementList(modalFooterElement);
                List<ModelScreenWidget> subWidgets = ModelScreenWidget
                        .readSubWidgets(this.modelScreen, subElementList);
                renderSubWidgetsString(subWidgets, writer, context,
                        screenStringRenderer);
                writer.append("</div>");
            }
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div modal/>";
        }
    }

    @SuppressWarnings("serial")
    public static class NgList extends ModelScreenWidget {
        public static final String TAG_NAME = "list";

        protected List<? extends Element> listItemElementList;

        public NgList(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            listItemElementList = UtilXml.childElementList(widgetElement,
                    "list-item");
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            for (Element listItemElement : listItemElementList) {
                writer.append("<li");
                FlexibleStringExpander styleExdr = FlexibleStringExpander
                        .getInstance(listItemElement.getAttribute("style"));
                FlexibleStringExpander repeatExdr = FlexibleStringExpander
                        .getInstance(listItemElement.getAttribute("repeat"));
                writer.append(" class=\"" + styleExdr.expandString(context)
                        + "\"");
                if (UtilValidate.isNotEmpty(repeatExdr.getOriginal())) {
                    writer.append(" ng-repeat=\""
                            + repeatExdr.expandString(context) + "\"");
                }
                writer.append(">");
                // read sub-widgets
                List<? extends Element> subElementList = UtilXml
                        .childElementList(listItemElement);
                List<ModelScreenWidget> subWidgets = ModelScreenWidget
                        .readSubWidgets(this.modelScreen, subElementList);
                renderSubWidgetsString(subWidgets, writer, context,
                        screenStringRenderer);
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

        protected FlexibleStringExpander nameExdr = null;
        protected FlexibleStringExpander typeExdr = null;
        protected FlexibleStringExpander modelExdr = null;
        protected FlexibleStringExpander minExdr = null;
        protected FlexibleStringExpander maxExdr = null;

        public Number(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
            this.typeExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("type"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.minExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("min"));
            this.maxExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("max"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<input type=\"number\" ng-model=\""
                    + modelExdr.expandString(context) + "\" name=\""
                    + nameExdr.expandString(context) + "\" min=\""
                    + minExdr.expandString(context) + "\" max=\""
                    + maxExdr.expandString(context) + "\" "
                    + typeExdr.expandString(context) + "/>");
        }

        @Override
        public String rawString() {
            return "<input type=\"number\"/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Panel extends ModelScreenWidget {
        public static final String TAG_NAME = "panel";

        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander headerTextExdr;
        protected FlexibleStringExpander hideExdr;
        protected List<ModelScreenWidget> subWidgets;

        public Panel(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
            this.hideExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("hide"));
            this.headerTextExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("header-text"));

            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div class=\"panel "
                    + styleExdr.expandString(context) + "\"");

            if (UtilValidate.isNotEmpty(hideExdr.getOriginal())) {
                writer.append(" ng-hide=\"" + hideExdr.expandString(context)
                        + "\"");
            }
            writer.append(">");
            if (UtilValidate.isNotEmpty(headerTextExdr.getOriginal())) {
                writer.append("<div class=\"panel-heading\">");
                writer.append(headerTextExdr.expandString(context));
                writer.append("</div>");
            }
            writer.append("<div class=\"panel-body\">");
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
            writer.append("</div>");
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div class=\"panel\"/>";
        }
    }

    @SuppressWarnings("serial")
    public static class PreformattedText extends ModelScreenWidget {
        public static final String TAG_NAME = "preformatted-text";

        protected List<? extends Element> subElementList = null;
        protected FlexibleStringExpander textContentExdr = null;

        public PreformattedText(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            textContentExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getTextContent());
            subElementList = UtilXml.childElementList(widgetElement);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            writer.append(this.textContentExdr.expandString(context));

            // TODO reader text and element by order
            /*
             * for (Element subElement : subElementList) { short nodeType =
             * subElement.getNodeType(); if (nodeType == Node.TEXT_NODE) {
             * writer.append(subElement.getTextContent()); } else {
             * List<Element> elements = new LinkedList<Element>();
             * elements.add(subElement); List<ModelScreenWidget> subWidgets =
             * ModelScreenWidget.readSubWidgets(this.modelScreen, elements);
             * renderSubWidgetsString(subWidgets, writer, context,
             * screenStringRenderer); } }
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
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("text"));
            this.valueExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("value"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<label class=\"radio\">");
            writer.append("<input type=\"radio\" class=\""
                    + this.styleExdr.expandString(context) + "\" ng-model=\""
                    + this.modelExdr.expandString(context) + "\" value=\""
                    + valueExdr.expandString(context) + "\"/>");
            writer.append(this.textExdr.expandString(context));
            writer.append("</label>");
        }

        @Override
        public String rawString() {
            return "<label/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Report extends ModelScreenWidget {
        public static final String TAG_NAME = "report";

        protected FlexibleStringExpander locationExdr;
        protected FlexibleStringExpander formatExdr;
        protected FlexibleStringExpander widthExdr;
        protected FlexibleStringExpander heightExdr;
        protected FlexibleStringExpander parametersExdr;

        public Report(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.locationExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("location"));
            this.formatExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("format"));
            this.widthExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("width"));
            this.heightExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("height"));
            this.parametersExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("parameters"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div report location=\""
                    + locationExdr.expandString(context) + "\" format=\""
                    + formatExdr.expandString(context) + "\" width=\""
                    + widthExdr.expandString(context) + "\" height=\""
                    + heightExdr.expandString(context) + "\" parameters=\""
                    + parametersExdr.expandString(context) + "\"></div>");
        }

        @Override
        public String rawString() {
            return "<report>";
        }
    }

    @SuppressWarnings("serial")
    public static class Row extends ModelScreenWidget {
        public static final String TAG_NAME = "row";

        protected List<ModelScreenWidget> subWidgets;

        public Row(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
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
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append(this.rawString());
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
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

        protected FlexibleStringExpander titleExdr = null;
        protected List<ModelScreenWidget> subWidgets = null;

        public Screenlet(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.titleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("title"));
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div class=\"panel panel-default\">");
            writer.append("<div class=\"panel-heading\">");
            writer.append("<h5 class=\"panel-title\">");
            writer.append(titleExdr.expandString(context));
            writer.append("</h5>");
            writer.append("</div>");
            writer.append("<div class=\"panel-body\">");
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
            writer.append("</div>");
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div screenlet/>";
        }
    }

    @SuppressWarnings("serial")
    public static class Submit extends ModelScreenWidget {
        public static final String TAG_NAME = "submit";

        protected FlexibleStringExpander textExdr;
        protected FlexibleStringExpander styleExdr;

        public Submit(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.textExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("text"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<input type=\"submit\" class=\"btn "
                    + styleExdr.expandString(context) + "\" value=\""
                    + textExdr.expandString(context) + "\"");
            writer.append("/>");
        }

        @Override
        public String rawString() {
            return "<input type=\"submit\"/>";
        }
    }

    @SuppressWarnings("serial")
    public static class TabBar extends ModelScreenWidget {
        public static final String TAG_NAME = "tab-bar";

        protected List<? extends Element> tabItemElements;
        protected Element tabContentElement;

        public TabBar(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.tabItemElements = UtilXml.childElementList(widgetElement,
                    "tab-item");
            this.tabContentElement = UtilXml.firstChildElement(widgetElement,
                    "tab-content");
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
                    String text = FlexibleStringExpander.getInstance(
                            tabItemElement.getAttribute("text")).expandString(
                            context);
                    String target = FlexibleStringExpander.getInstance(
                            tabItemElement.getAttribute("target"))
                            .expandString(context);
                    String activeState = FlexibleStringExpander.getInstance(
                            tabItemElement.getAttribute("active-state"))
                            .expandString(context);
                    String style = FlexibleStringExpander.getInstance(
                            tabItemElement.getAttribute("style")).expandString(
                            context);
                    String onSelect = FlexibleStringExpander.getInstance(
                            tabItemElement.getAttribute("on-select"))
                            .expandString(context);

                    if (UtilValidate.isEmpty(activeState)) {
                        activeState = target;
                    }

                    writer.append("<li class=\"" + style + "\" ");
                    if (UtilValidate.isNotEmpty(target)) {
                        writer.append("ng-class=\"{active: $state.includes('"
                                + activeState + "')}\"");
                    }
                    writer.append(">");
                    if (UtilValidate.isNotEmpty(target)) {
                        writer.append("<a ui-sref=\"" + target + "\">" + text
                                + "</a>");
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
                String viewName = FlexibleStringExpander.getInstance(
                        tabContentElement.getAttribute("view-name"))
                        .getOriginal();
                writer.append("<div class=\"tab-content\" ui-view=\""
                        + viewName + "\"></div>");
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
     *
     * @author chatree
     *
     */
    @SuppressWarnings("serial")
    public static class Tabs extends ModelScreenWidget {
        public static final String TAG_NAME = "tabs";

        protected List<? extends Element> tabElements;
        protected FlexibleStringExpander verticalExdr;
        protected FlexibleStringExpander typeExdr;
        protected FlexibleStringExpander directionExdr;

        public Tabs(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.tabElements = UtilXml.childElementList(widgetElement, "tab");
            this.verticalExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("vertical"));
            this.typeExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("type"));
            this.directionExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("direction"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<tabset tab-set-options ");
            writer.append("vertical=\""
                    + Boolean.valueOf(verticalExdr.expandString(context))
                    + "\"");
            if (UtilValidate.isNotEmpty(typeExdr.getOriginal())) {
                writer.append("type=\"" + typeExdr.expandString(context) + "\"");
            }
            if (UtilValidate.isNotEmpty(directionExdr.getOriginal())) {
                writer.append("direction=\""
                        + directionExdr.expandString(context) + "\"");
            }
            writer.append(">");
            if (UtilValidate.isNotEmpty(tabElements)) {
                // tabs
                for (Element tabElement : tabElements) {
                    StringWriter tabWriter = new StringWriter();
                    String repeat = FlexibleStringExpander.getInstance(
                            tabElement.getAttribute("repeat")).expandString(
                            context);
                    String heading = FlexibleStringExpander.getInstance(
                            tabElement.getAttribute("heading")).expandString(
                            context);
                    String active = FlexibleStringExpander.getInstance(
                            tabElement.getAttribute("active")).expandString(
                            context);
                    String disabled = FlexibleStringExpander.getInstance(
                            tabElement.getAttribute("disabled")).expandString(
                            context);
                    String onSelect = FlexibleStringExpander.getInstance(
                            tabElement.getAttribute("on-select")).expandString(
                            context);
                    String targetUri = FlexibleStringExpander.getInstance(
                            tabElement.getAttribute("target-uri"))
                            .expandString(context);
                    String targetParameters = FlexibleStringExpander
                            .getInstance(
                                    tabElement
                                            .getAttribute("target-parameters"))
                            .expandString(context);
                    String targetContentModel = FlexibleStringExpander
                            .getInstance(
                                    tabElement
                                            .getAttribute("target-content-model"))
                            .expandString(context);
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
                        tabWriter.append("target-content-model=\""
                                + targetContentModel + "\" ");
                    }
                    if (UtilValidate.isNotEmpty(targetParameters)) {
                        tabWriter.append("target-parameters=\""
                                + targetParameters + "\" ");
                    }
                    tabWriter.append("tab-options=\"\" ");
                    tabWriter.append(">");

                    // tab heading
                    Element tabHeadingElement = UtilXml.firstChildElement(
                            tabElement, "tab-heading");
                    if (UtilValidate.isNotEmpty(tabHeadingElement)) {
                        tabWriter.append("<tab-heading>");
                        // read tab heading sub widgets
                        List<? extends Element> tabHeadingSubWidgetElements = UtilXml
                                .childElementList(tabHeadingElement);
                        List<ModelScreenWidget> tabHeadingSubWidgets = ModelScreenWidget
                                .readSubWidgets(this.modelScreen,
                                        tabHeadingSubWidgetElements);
                        renderSubWidgetsString(tabHeadingSubWidgets, tabWriter,
                                context, screenStringRenderer);
                        tabWriter.append("</tab-heading>");
                    }

                    // read tab sub widgets
                    List<? extends Element> tabSubWidgetElements = UtilXml
                            .childElementList(tabElement);
                    if (UtilValidate.isNotEmpty(tabHeadingElement)) {
                        // remove tab heading element
                        tabSubWidgetElements.remove(0);
                    }
                    List<ModelScreenWidget> tabSubWidgets = ModelScreenWidget
                            .readSubWidgets(this.modelScreen,
                                    tabSubWidgetElements);
                    renderSubWidgetsString(tabSubWidgets, tabWriter, context,
                            screenStringRenderer);

                    tabWriter.append("</tab>");
                    writer.append(tabWriter.toString());
                }
            }
            writer.append("</tabset>");
        }

        @Override
        public String rawString() {
            return "<tabset/>";
        }

    }

    @SuppressWarnings("serial")
    public static class Text extends ModelScreenWidget {
        public static final String TAG_NAME = "text";

        protected FlexibleStringExpander typeExdr;
        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander placeholderExdr;

        public Text(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.typeExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("type"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
            this.placeholderExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("placeholder"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            String type = typeExdr.expandString(context);
            String model = modelExdr.expandString(context);
            String style = styleExdr.expandString(context);
            String placeholder = placeholderExdr.expandString(context);
            if (UtilValidate.isEmpty(type)) {
                type = "text";
            }

            writer.append("<input type=\"" + type + "\" class=\"form-control "
                    + style + "\"");
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

        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander placeholderExdr;
        protected FlexibleStringExpander visualEditorEnableExdr;

        public TextArea(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
            this.placeholderExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("placeholder"));
            this.visualEditorEnableExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("visual-editor-enable"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<textarea-wrapper name=\"" + this.name
                    + "\" style=\""
                    + this.styleExdr.expandString(context) + "\"");
            if (UtilValidate.isNotEmpty(placeholderExdr.getOriginal())) {
                writer.append(" placeholder=\""
                        + this.placeholderExdr.expandString(context) + "\"");
            }
            if (UtilValidate.isNotEmpty(modelExdr.getOriginal())) {
                writer.append(" ng-model=\""
                        + this.modelExdr.expandString(context) + "\"");
            }
            if (Boolean.valueOf(visualEditorEnableExdr.expandString(context))) {
                writer.append(" visual-editor-enable=\"" + this.visualEditorEnableExdr.expandString(context) + "\"");
            }
            writer.append("></textarea-wrapper>");
        }

        @Override
        public String rawString() {
            return "<textarea/>";
        }

    }

    @SuppressWarnings("serial")
    public static class Tree extends ModelScreenWidget {
        public static final String TAG_NAME = "tree";

        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander nodeChildrenFieldNameExdr;
        protected FlexibleStringExpander nodeIdFieldNameExdr;
        protected FlexibleStringExpander nodeLabelFieldNameExdr;

        public Tree(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.nodeChildrenFieldNameExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("node-children-field-name"));
            this.nodeIdFieldNameExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("node-id-field-name"));
            this.nodeLabelFieldNameExdr = FlexibleStringExpander
                    .getInstance(widgetElement
                            .getAttribute("node-label-field-name"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div");
            writer.append(" data-angular-treeview=\"true\"");
            writer.append(" data-tree-model=\""
                    + modelExdr.expandString(context) + "\"");
            writer.append(" data-node-id=\""
                    + nodeIdFieldNameExdr.expandString(context) + "\"");
            writer.append(" data-node-label=\""
                    + nodeLabelFieldNameExdr.expandString(context) + "\"");
            writer.append(" data-node-children=\""
                    + nodeChildrenFieldNameExdr.expandString(context) + "\"");
            writer.append(" tree-options=\"\"");
            writer.append("></div>");
        }

        @Override
        public String rawString() {
            return "<div data-angular-treeview/>";
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

        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander heightExdr;
        protected FlexibleStringExpander eventExdr;
        protected FlexibleStringExpander optionsExdr;

        public UiMap(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
            this.styleExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("style"));
            this.heightExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("height"));
            this.eventExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("event"));
            this.optionsExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("options"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div ui-map-options=\"\" ui-map=\"" + name
                    + "\" height=\"" + heightExdr.expandString(context)
                    + "\" class=\"" + styleExdr.expandString(context)
                    + "\" ui-event=\"" + eventExdr.expandString(context)
                    + "\" ui-options=\"" + optionsExdr.expandString(context)
                    + "\">");
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div ui-map/>";
        }
    }

    @SuppressWarnings("serial")
    public static class UiMapInfoWindow extends ModelScreenWidget {
        public static final String TAG_NAME = "map-info-window";

        protected FlexibleStringExpander nameExdr;
        protected List<ModelScreenWidget> subWidgets;

        public UiMapInfoWindow(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
            // read sub-widgets
            List<? extends Element> subElementList = UtilXml
                    .childElementList(widgetElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(
                    this.modelScreen, subElementList);
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div ui-map-info-window=\""
                    + nameExdr.expandString(context) + "\">");
            renderSubWidgetsString(this.subWidgets, writer, context,
                    screenStringRenderer);
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div ui-map-info-window/>";
        }
    }

    @SuppressWarnings("serial")
    public static class UiMapMarker extends ModelScreenWidget {
        public static final String TAG_NAME = "map-marker";

        protected FlexibleStringExpander repeatExdr;
        protected FlexibleStringExpander valueExdr;
        protected FlexibleStringExpander eventExdr;

        public UiMapMarker(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.repeatExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("repeat"));
            this.valueExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("value"));
            this.eventExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("event"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div ng-repeat=\""
                    + repeatExdr.expandString(context) + "\" ui-map-marker=\""
                    + valueExdr.expandString(context) + "\" ui-event=\""
                    + eventExdr.expandString(context) + "\">");
            writer.append("</div>");
        }

        @Override
        public String rawString() {
            return "<div ng-repeat/>";
        }
    }

    @SuppressWarnings("serial")
    public static class TimePicker extends ModelScreenWidget {
        public static final String TAG_NAME = "time-picker";

        protected FlexibleStringExpander modelExdr;
        protected FlexibleStringExpander hourStepExdr;
        protected FlexibleStringExpander minuteStepExdr;
        protected FlexibleStringExpander showMeridianExdr;

        public TimePicker(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.modelExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("model"));
            this.hourStepExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("hour-step"));
            this.minuteStepExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("minute-step"));
            this.showMeridianExdr = FlexibleStringExpander
                    .getInstance(widgetElement.getAttribute("show-meridian"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<timepicker");
            if (UtilValidate.isNotEmpty(modelExdr.getOriginal())) {
                writer.append(" ng-model=\""
                        + this.modelExdr.expandString(context) + "\"");
            }
            if (UtilValidate.isNotEmpty(hourStepExdr.getOriginal())) {
                writer.append(" hour-step=\""
                        + this.hourStepExdr.expandString(context) + "\"");
            }
            if (UtilValidate.isNotEmpty(minuteStepExdr.getOriginal())) {
                writer.append(" minute-step=\""
                        + this.minuteStepExdr.expandString(context) + "\"");
            }
            if (UtilValidate.isNotEmpty(showMeridianExdr.getOriginal())) {
                writer.append(" show-meridian=\""
                        + this.showMeridianExdr.expandString(context) + "\"");
            }
            writer.append("></timepicker>");
        }

        @Override
        public String rawString() {
            return "<timepicker/>";
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

        protected FlexibleStringExpander nameExdr = null;

        public View(ModelScreen modelScreen, Element widgetElement) {
            super(modelScreen, widgetElement);
            this.nameExdr = FlexibleStringExpander.getInstance(widgetElement
                    .getAttribute("name"));
        }

        @Override
        public void renderWidgetString(Appendable writer,
                Map<String, Object> context,
                ScreenStringRenderer screenStringRenderer)
                throws GeneralException, IOException {
            writer.append("<div ui-view=\"" + nameExdr.expandString(context)
                    + "\"></div>");
        }

        @Override
        public String rawString() {
            return "<div ui-view/>";
        }
    }
}
