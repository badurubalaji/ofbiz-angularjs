package org.ofbiz.angularjs.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class LookupServices {

    public final static String module = LookupServices.class.getName();

    public static Map<String, Object> performLookupJsonList(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        HttpServletRequest request = (HttpServletRequest) context.remove("request");
        String entityName = (String) context.remove("entityName");
        String searchFields = (String) context.remove("searchFields");
        String displayFields = (String) context.remove("displayFields");
        boolean searchDistinct = Boolean.valueOf((String) context.remove("searchDistinct"));
        String term = (String) context.remove("term");
        String searchValueFieldName = (String) context.remove("searchValueFieldName");
        String searchType = (String) context.remove("searchType");
        List<String> conditionFields = (List) context.remove("conditionFields");
        EntityCondition andCondition = (EntityCondition) context.remove("andCondition");
        Integer viewSize = (Integer) context.remove("viewSize");
        
        if (UtilValidate.isEmpty(displayFields)) {
            displayFields = searchFields;
        }
        
        String searchValue = term;
        String fieldValue = null;
        boolean showDescription = false;
        String returnField = null;
        
        if (UtilValidate.isNotEmpty(searchValue)) {
            fieldValue = searchValue;
        } else if (UtilValidate.isNotEmpty(searchValueFieldName)) {
            fieldValue = (String) context.get(searchValueFieldName);
            showDescription = true;
        }
        
        Set<String> displayFieldSet = null;
        List<EntityCondition> orExprs = new LinkedList<EntityCondition>();
        List<EntityCondition> mainAndConds = new LinkedList<EntityCondition>();
        
        if (UtilValidate.isNotEmpty(searchFields) && UtilValidate.isNotEmpty(fieldValue)) {
            List<String> searchFieldsList = StringUtil.toList(searchFields);
            displayFieldSet = StringUtil.toSet(displayFields);
            
            if (showDescription && fieldValue instanceof String) {
                returnField = searchValueFieldName;
            } else {
                returnField = searchFieldsList.get(0);
                displayFieldSet.add(returnField);
            }
            
            if ("STARTS_WITH".equals(searchType)) {
                searchValue = fieldValue.toUpperCase() + "%";
            } else if ("EQUALS".equals(searchType)) {
                searchValue = fieldValue;
            } else {
                searchValue = "%" + fieldValue.toUpperCase() + "%";
            }
            
            for (String fieldName : searchFieldsList) {
                if ("EQUALS".equals(searchType)) {
                    orExprs.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(searchFieldsList.get(0)), EntityOperator.EQUALS, searchValue));
                } else {
                    orExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue(fieldName)), EntityOperator.LIKE, searchValue));
                }
            }
        }
        
        if (UtilValidate.isNotEmpty(conditionFields)) {
            // TODO: add additional conditions
        }
        
        if (UtilValidate.isNotEmpty(orExprs) && UtilValidate.isNotEmpty(entityName) && UtilValidate.isNotEmpty(displayFields)) {
            mainAndConds.add(EntityCondition.makeCondition(orExprs, EntityOperator.OR));
            
            if (UtilValidate.isNotEmpty(andCondition)) {
                mainAndConds.add(andCondition);
            }
            
            EntityConditionList<EntityCondition> entityConditionList = EntityCondition.makeCondition(mainAndConds, EntityOperator.AND);
            
            if (UtilValidate.isEmpty(viewSize)) {
                viewSize = new Integer(UtilProperties.getPropertyValue("widget", "widget.autocompleter.defaultViewSize"));
            }
            
            Integer autocompleterViewSize = UtilValidate.isNotEmpty(viewSize) ? viewSize : 10;
            EntityFindOptions findOptions = new EntityFindOptions();
            findOptions.setMaxRows(autocompleterViewSize);
            findOptions.setDistinct(searchDistinct);
            
            try {
                List<GenericValue> autocompleteOptions = delegator.findList(entityName, entityConditionList, displayFieldSet, StringUtil.toList(displayFields), findOptions, false);
                request.setAttribute("options", autocompleteOptions);
                return ServiceUtil.returnSuccess();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("Could not find options because: " + e.getMessage());
            }
        } else {
            return ServiceUtil.returnError("Could not find entity name or display fields.");
        }
    }
}
