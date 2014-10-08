
<div ng-controller="org.ofbiz.angularjs.demo.controller.DateTimeDemoController">
    <div>
        <label>Selected date: {{date}}</label>
        <!--
        <date-time ng-model="date" format="MMM d, yyyy h:mm:ss a"/>
        -->
    </div>

    <hr/>
    <context target="getExample" parameters="getExampleParameters" ng-model="example" field="example" ng-transclude>
        <div>
            <form>
                <div class="controls">
                    <label>Created stamp: {{example.createdStamp}}</label>
                    <div>
                    <date-time ng-model="example.createdStamp" format="MMM d, yyyy h:mm:ss a"/>
                    </div>
                    <div>
                    <date-time ng-model="example.createdStamp" format="MMM d, yyyy h:mm:ss a" read-only="true"/>
                    </div>
                </div>
            </form>
        </div>
    </context>
</div>



