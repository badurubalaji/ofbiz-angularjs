<html ng-app>
    <head>
        <script type="text/javascript" src="/angularjs/images/js/angular.min.js"></script>
        <script type="text/javascript" src="/angularjs/control/controllers.js"></script>
    </head>
    <body ng-controller="PhoneController">
        <h1>Sample</h1>
        <ul>
            <li ng-repeat="phone in phones">
                <b>{{phone.name}}</b>
                <p>{{phone.snippet}}</p>
            </li>
        </ul>
    </body>
</html>
