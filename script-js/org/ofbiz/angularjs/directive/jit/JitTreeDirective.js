package org.ofbiz.angularjs.directive.jit;
/**
 * Jit Tree Directive
 * http://philogb.github.io/jit/static/v20/Jit/Examples/Spacetree/example1.html
 */
function JitTreeDirective($compile) {

    this.controller = function($scope, $element, $attrs, $transclude, $http, HttpService) {
        var width = $attrs.width;
        var height = $attrs.height;
        var nodeTemplateUrl = $attrs.nodeTemplateUrl;
        var levelDistance = $attrs.levelDistance;
        var nodeHeight = $attrs.nodeHeight;
        var nodeWidth = $attrs.nodeWidth;

        if (width == null) {
            width = "100%";
        }
        if (height == null) {
            height = "400px";
        }

        if(_.isEmpty(levelDistance)) {
            levelDistance = 100;
        } else {
            levelDistance = parseInt(levelDistance);
        }

        if(_.isEmpty(nodeHeight)) {
            nodeHeight = 100;
        } else {
            nodeHeight = parseInt(nodeHeight);
        }

        if(_.isEmpty(nodeWidth)) {
            nodeWidth = 100;
        } else {
            nodeWidth = parseInt(nodeWidth);
        }

        // console.log(levelDistance + ", " + nodeHeight + ", " + nodeWidth);

        var id = _.uniqueId("jitTree");
        $element.attr("id", id);
        $element.css("width", width);
        $element.css("height", height);
        $element.css("margin", "auto");
        $element.css("position", "relative");
        $element.css("overflow", "hidden");


        var spaceTree = new $jit.ST({
            //id of viz container element
            injectInto: id,
            //set duration for the animation
            duration: 800,
            //set animation transition type
            transition: $jit.Trans.Quart.easeInOut,
            //set distance between node and its children
            levelDistance: levelDistance,
            //enable panning
            Navigation: {
              enable:true,
              panning:true
            },
            //set node and edge styles
            //set overridable=true for styling individual
            //nodes or edges
            Node: {
                height: nodeHeight,
                width: nodeWidth,
                type: 'rectangle',
                //color: '#aaa',
                overridable: true
            },

            Edge: {
                type: 'bezier',
                overridable: true
            },

            onBeforeCompute: function(node){
                // console.log("loading " + node.name);
            },

            onAfterCompute: function(){
                // console.log("done");
            },

            //This method is called on DOM label creation.
            //Use this method to add event handlers and styles to
            //your node.
            onCreateLabel: function(label, node){
                label.id = node.id;
                label.onclick = function(){
                    /*
                    if(normal.checked) {
                      spaceTree.onClick(node.id);
                    } else {
                    spaceTree.setRoot(node.id, 'animate');
                    }
                    */

                    // spaceTree.setRoot(node.id, 'animate');
                    spaceTree.onClick(node.id);
                    $scope.nodeClick({node: node});
                };

                if (!_.isEmpty(nodeTemplateUrl)) {
                    var parameters = _.clone(node.data);
                    parameters.id = node.id;

                    // copy node parameters
                    if ($scope.nodeParameters != null && _.isObject($scope.nodeParameters)) {
                        var keys = _.keys($scope.nodeParameters);
                        _.each(keys, function(key) {
                            parameters[key] = $scope.nodeParameters[key];
                        });
                    }

                    HttpService.post(nodeTemplateUrl, parameters)
                        .success(function(data) {
                            var element = angular.element(label);
                            element.html(data);
                            $compile(element.contents())($scope);
                        });
                } else {
                    label.innerHTML = node.name;
                    //set label styles
                    var style = label.style;
                    style.width = 60 + 'px';
                    style.height = 70 + 'px';
                    style.cursor = 'pointer';
                    style.color = '#333';
                    style.fontSize = '0.8em';
                    style.textAlign= 'center';
                    style.paddingTop = '3px';
                }
            },

            //This method is called right before plotting
            //a node. It's useful for changing an individual node
            //style properties before plotting it.
            //The data properties prefixed with a dollar
            //sign will override the global node style properties.
            onBeforePlotNode: function(node){
                //add some color to the nodes in the path between the
                //root node and the selected node.
                if (node.selected) {
                    node.data.$color = "#ff7";
                }
                else {
                    delete node.data.$color;
                    //if the node belongs to the last plotted level
                    if(!node.anySubnode("exist")) {
                        //count children number
                        var count = 0;
                        node.eachSubnode(function(n) { count++; });
                        //assign a node color based on
                        //how many children it has
                        node.data.$color = ['#aaa', '#C00000', '#C00000', '#C00000', '#C00000', '#C00000', '#C00000', '#C00000', '#C00000', '#C00000', '#C00000'][count];
                    }
                }
            },

            //This method is called right before plotting
            //an edge. It's useful for changing an individual edge
            //style properties before plotting it.
            //Edge data proprties prefixed with a dollar sign will
            //override the Edge global style properties.
            onBeforePlotLine: function(adj){
                if (adj.nodeFrom.selected && adj.nodeTo.selected) {
                    adj.data.$color = "#eed";
                    adj.data.$lineWidth = 3;
                }
                else {
                    delete adj.data.$color;
                    delete adj.data.$lineWidth;
                }
            }
        });

        $scope.$watch("ngModel", function(model) {
            if (model != null) {
                loadNodes(spaceTree, model);
            }
        })
    };

    this.compile = function() {
        return {
            pre: function() {

            },
            post: function() {

            }
        };
    };

    this.link = function() {

    };

    function loadNodes(spaceTree, model) {
        //load json data
        spaceTree.loadJSON(model);
        //compute node positions and layout
        spaceTree.compute();
        //optional: make a translation of the tree
        spaceTree.geom.translate(new $jit.Complex(-200, 0), "current");
        //emulate a click on the root node.
        spaceTree.onClick(spaceTree.root);
    }
}
