package org.ofbiz.angularjs.directive;
/**
 * Tree Directive
 * http://philogb.github.io/jit/static/v20/Jit/Examples/Spacetree/example1.html
 */
function TreeDirective() {

    this.controller = function($scope, $element, $attrs, $transclude, $http) {
        var width = $attrs.width;
        var height = $attrs.height;
        
        if (width == null) {
            width = "100%";
        }
        if (height == null) {
            height = "400px";
        }
        
        var container = $element.find("div");
        var id = container.attr("id");
        container.css("width", width);
        container.css("height", height);
        container.css("margin", "auto");
        container.css("position", "relative");
        container.css("overflow", "hidden");
        
        
        var st = new $jit.ST({
            //id of viz container element
            injectInto: id,
            //set duration for the animation
            duration: 800,
            //set animation transition type
            transition: $jit.Trans.Quart.easeInOut,
            //set distance between node and its children
            levelDistance: 50,
            //enable panning
            Navigation: {
              enable:true,
              panning:true
            },
            //set node and edge styles
            //set overridable=true for styling individual
            //nodes or edges
            Node: {
                height: 20,
                width: 60,
                type: 'rectangle',
                color: '#aaa',
                overridable: true
            },
            
            Edge: {
                type: 'bezier',
                overridable: true
            },
            
            onBeforeCompute: function(node){
                console.log("loading " + node.name);
            },
            
            onAfterCompute: function(){
                console.log("done");
            },
            
            //This method is called on DOM label creation.
            //Use this method to add event handlers and styles to
            //your node.
            onCreateLabel: function(label, node){
                label.id = node.id;            
                label.innerHTML = node.name;
                label.onclick = function(){
                    /*
                    if(normal.checked) {
                      st.onClick(node.id);
                    } else {
                    st.setRoot(node.id, 'animate');
                    }
                    */
                    
                    //st.setRoot(node.id, 'animate');
                    st.onClick(node.id);
                };
                //set label styles
                var style = label.style;
                style.width = 60 + 'px';
                style.height = 17 + 'px';            
                style.cursor = 'pointer';
                style.color = '#333';
                style.fontSize = '0.8em';
                style.textAlign= 'center';
                style.paddingTop = '3px';
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
                        node.data.$color = ['#aaa', '#baa', '#caa', '#daa', '#eaa', '#faa'][count];                    
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
        
        var model = $scope[$attrs.model];
        
        //load json data
        st.loadJSON(model);
        //compute node positions and layout
        st.compute();
        //optional: make a translation of the tree
        st.geom.translate(new $jit.Complex(-200, 0), "current");
        //emulate a click on the root node.
        st.onClick(st.root);
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
}
