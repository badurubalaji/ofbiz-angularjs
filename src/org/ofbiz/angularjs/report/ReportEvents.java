package org.ofbiz.angularjs.report;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastMap;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.birt.BirtFactory;
import org.ofbiz.birt.BirtWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.LocalDispatcher;
import org.xml.sax.SAXException;

public class ReportEvents {
    
    public final static String module = ReportEvents.class.getName();
    
    public static String serveReport(HttpServletRequest request,
            HttpServletResponse response) {
        ServletContext servletContext = request.getServletContext();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request
                .getAttribute("dispatcher");
        HttpSession session = request.getSession();
        Locale locale = UtilHttp.getLocale(request);
        Map<String, Object> parametersMap = UtilHttp.getParameterMap(request);
        
        String location = (String) parametersMap.get("__location");
        
        if (UtilValidate.isEmpty(location)) {
            String errMsg = "Location could not be empty.";
            Debug.logError(errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        
        String contentType = "text/html";
        String parametersParam = (String) parametersMap.get("__parameters");
        Map<String, String> reportParametersMap = new HashMap<String, String>();
        if (UtilValidate.isNotEmpty(parametersParam)) {
            List<String> paramPairs = StringUtil.split(parametersParam, ",");
            for (String paramPair : paramPairs) {
                if (UtilValidate.isNotEmpty(paramPair)) {
                    List<String> paramTokens = StringUtil.split(paramPair,
                            "_EQS_");
                    String paramName = paramTokens.get(0);
                    String paramValue = paramTokens.get(1);
                    reportParametersMap.put(paramName, paramValue);
                }
            }
        }
        
        try {
            IReportEngine engine = org.ofbiz.birt.BirtFactory.getReportEngine();
            // open report design
            IReportRunnable design = null;
            if (location.startsWith("component://")) {
                InputStream reportInputStream = BirtFactory
                        .getReportInputStreamFromLocation(location);
                design = engine.openReportDesign(reportInputStream);
            } else {
                design = engine.openReportDesign(servletContext
                        .getRealPath(location));
            }
            
            Map<String, Object> appContext = UtilGenerics.cast(engine
                    .getConfig().getAppContext());
            BirtWorker.setWebContextObjects(appContext, request, response);
            
            Map<String, Object> context = FastMap.newInstance();
            context.put(BirtWorker.BIRT_PARAMETERS, reportParametersMap);
            
            // set output file name
            String outputFileName = (String) request
                    .getAttribute(BirtWorker.BIRT_OUTPUT_FILE_NAME);
            if (UtilValidate.isNotEmpty(outputFileName)) {
                response.setHeader("Content-Disposition",
                        "attachment; filename=" + outputFileName);
            }
            
            context.put(BirtWorker.BIRT_LOCALE, locale);
            String birtImageDirectory = UtilProperties.getPropertyValue("birt",
                    "birt.html.image.directory");
            context.put(BirtWorker.BIRT_IMAGE_DIRECTORY, birtImageDirectory);
            BirtWorker.exportReport(design, context, contentType,
                    response.getOutputStream());
        } catch (BirtException e) {
            String errMsg = "Birt Error create engine: " + e.toString();
            Debug.logError(e, errMsg, module);
        } catch (IOException e) {
            String errMsg = "Error in the response writer/output stream: "
                    + e.toString();
            Debug.logError(e, errMsg, module);
        } catch (SQLException e) {
            String errMsg = "get connection error: " + e.toString();
            Debug.logError(e, errMsg, module);
        } catch (GenericEntityException e) {
            String errMsg = "Generic entity error: " + e.toString();
            Debug.logError(e, errMsg, module);
        } catch (GeneralException e) {
            String errMsg = "General error: " + e.toString();
            Debug.logError(e, errMsg, module);
        } catch (SAXException se) {
            String errMsg = "Error SAX rendering " + location
                    + " view handler: " + se.toString();
            Debug.logError(se, errMsg, module);
        } catch (ParserConfigurationException pe) {
            String errMsg = "Error parser rendering " + location
                    + " view handler: " + pe.toString();
            Debug.logError(pe, errMsg, module);
        }
        
        return "success";
    }
}
