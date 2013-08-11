<html>
    <head>
        <#if layoutSettings.javaScripts?has_content>
            <#--layoutSettings.javaScripts is a list of java scripts. -->
            <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
            <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
            <#list layoutSettings.javaScripts as javaScript>
                <#if javaScriptsSet.contains(javaScript)>
                    <#assign nothing = javaScriptsSet.remove(javaScript)/>
                    <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
                </#if>
            </#list>
        </#if>
        
        <#list layoutSettings.moduleJavaScripts as moduleJavaScript>
            <#if moduleJavaScript.fullPath == true>
                <#assign protocol = "http"/>
                <#if request.isSecure() == true>
                    <#assign protocol = "https"/>
                </#if>
                <script src="${protocol}://${StringUtil.wrapString(moduleJavaScript.path)}" type="text/javascript"></script>
            <#else>
                <script src="<@ofbizContentUrl>${StringUtil.wrapString(moduleJavaScript.path)}</@ofbizContentUrl>" type="text/javascript"></script>
            </#if>
        </#list>
        
        <#if layoutSettings.styleSheets?has_content>
            <#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
            <#list layoutSettings.styleSheets as styleSheet>
                <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
            </#list>
        </#if>
        <#list layoutSettings.moduleStyleSheets as moduleStyleSheet>
            <#if moduleStyleSheet.fullPath == true>
                <#assign protocol = "http"/>
                <#if request.isSecure() == true>
                    <#assign protocol = "https"/>
                </#if>
                <link rel="stylesheet" href="${protocol}://${StringUtil.wrapString(moduleStyleSheet.path)}" type="text/css">
            <#else>
                <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(moduleStyleSheet.path)}</@ofbizContentUrl>" type="text/css">
            </#if>
        </#list>
    </head>
    <body>
