<#--
<div ng-controller="org.ofbiz.angularjs.demo.controller.CommentsPanelDemoController">
    <comments-panel content-id="contentId" on-create-success="onCommentCreated(newComment)"/>
</div>
-->

<div ng-controller="org.ofbiz.angularjs.demo.controller.DateTimeDemoController">

    <div class="ade-editable" ade-date='{"className":"input-large","format":"MMM d, yyyy h:mm:ss a","absolute":true}' ng-model="date">{{date | validDate:['MMM d, yyyy h:mm:ss a']}}</div>
</div>

