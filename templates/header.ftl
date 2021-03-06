<html>
    <head>
        <#if layoutSettings.javaScripts?has_content>
            <#--layoutSettings.javaScripts is a list of java scripts. -->
            <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
            <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
            <#list layoutSettings.javaScripts as javaScript>
                <#if javaScriptsSet.contains(javaScript)>
                    <#assign nothing = javaScriptsSet.remove(javaScript)/>
                    <#if request.isSecure()>
                        <#if javaScript.contains("http://")>
                            <#assign newJavaScript = Static["org.ofbiz.base.util.StringUtil"].replaceString(javaScript, "http://", "https://")/>
                            <script src="<@ofbizContentUrl>${StringUtil.wrapString(newJavaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
                        <#else>
                            <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
                        </#if>
                    <#else>
                        <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
                    </#if>
                </#if>
            </#list>
        </#if>
        
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
