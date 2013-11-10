package org.ofbiz.angularjs.directive;

/**
 * Grid Options Directive
 */
function GridOptionsDirective() {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude, $http, appBusy) {
        appBusy.set();
        var modelName = $attrs.model;
        var rowHeight = $attrs.rowHeight;
        var selectTarget = $attrs.selectTarget;
        var listName = $attrs.listName;
        var columnDefs = $scope.$eval($attrs.columnDefs);
        var pageSizes = $scope.$eval($attrs.pageSizes);
        var pageSize = $scope.$eval($attrs.pageSize);
        
        var onBeforeSelectionChanged = $scope[$attrs.onBeforeSelectionChanged]; // function (rowItem, event) {}
        var onAfterSelectionChanged = $scope[$attrs.onAfterSelectionChanged]; // function (rowItem, event) { return true; }
        var onRowDoubleClicked = $scope[$attrs.onRowDoubleClicked]; // function (rowItem, event) {}
        
        var enablePaging = true;
        var showFooter = true;
        
        if (!pageSizes) {
            pageSizes = [20, 30, 50, 100, 200];
        }
        
        if (!pageSize) {
            pageSize = 20;
        }
        
        $scope.data = [];
        
        /**
         * Set Paging Data
         */
        $scope.setPagingData = function(data, page, pageSize, viewIndex, listSize) {
            if (!viewIndex) {
                viewIndex = 0;
            }
            
            // var pagedData = data.slice((page - 1) * pageSize, page * pageSize);
            $scope.data = data; // set data for the first loaded
            $scope.$parent.data = data; // set data for other loaded
            $scope.totalServerItems = data.length;
            $scope.pagingOptions = {
                pageSizes: pageSizes,
                pageSize: pageSize,
                currentPage: viewIndex + 1
            };
            if (!$scope.$$phase) {
                $scope.$apply();
            }
        };
        
        /**
         * Get Paged Data Async
         */
        $scope.getPagedDataAsync = function (selectTarget, listName, pageSize, page, parameters) {
            setTimeout(function () {
                var data;
                var postData = {viewSize: pageSize};
                if (parameters) {
                    for (key in parameters) {
                        var value = parameters[key];
                        postData[key] = value;
                    }
                    appBusy.set();
                    $http.post(selectTarget, postData).success(function (response) {
                        var listSize = response.listSize;
                        var viewIndex = response.viewIndex;
                        var viewSize = response.viewSize;
                        if (listSize > 0) {
                            
                            // The data has already been filtered from server.
                            /*
                            data = response[listName].filter(function(item) {
                                return JSON.stringify(item).toLowerCase().indexOf(ft) != -1;
                            });
                            */
                            
                            data = response[listName]
                        } else {
                            data = [];
                        }
                        
                        if (data != null) {
                            $scope.setPagingData(data, page, pageSize, viewIndex, listSize);
                        }
                        appBusy.set(false);
                    });
                } else {
                    appBusy.set();
                    $http.post(selectTarget, postData).success(function (response) {
                        var listSize = response.listSize;
                        if (listSize > 0) {
                            var data = response[listName];
                            if (data != null) {
                                $scope.$parent.data = data;
                                $scope.setPagingData(data,page,pageSize);
                            }
                        }
                        appBusy.set(false);
                    });
                }
            }, 100);
        };
        
        $scope.totalServerItems = 0;
        
        $scope.pagingOptions = {
            pageSizes: pageSizes,
            pageSize: pageSize,
            currentPage: 1
        };
        
        $scope.filterOptions = {
            filterText: "",
            useExternalFilter: true
        };
        
        $scope.getPagedDataAsync(selectTarget, listName, $scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, null);
        
        $scope.$on(modelName, function(event, args) {
            var pageSize = args.pageSize;
            var currentPage = args.currentPage;
            var filterText = args.filterText;
            if (!pageSize) {
                pageSize = $scope.pagingOptions.pageSize;
            }
            if (!currentPage) {
                currentPage = $scope.pagingOptions.currentPage;
            }
            $scope.getPagedDataAsync(selectTarget, listName, pageSize, currentPage, args.parameters);
        });
        
        var rowSize = 10
        
        if (rowSize > 20) {
            enablePaging = true;
            showFooter = true;
        }
        
        /* 
         DoubleClick row plugin
         http://developer.the-hideout.de/?p=113
        */
        function ngGridDoubleClick() {
            var self = this;
            self.$scope = null;
            self.myGrid = null;
         
            // The init method gets called during the ng-grid directive execution.
            self.init = function(scope, grid, services) {
                // The directive passes in the grid scope and the grid object which
                // we will want to save for manipulation later.
                self.$scope = scope;
                self.myGrid = grid;
                // In this example we want to assign grid events.
                self.assignEvents();
            };
            self.assignEvents = function() {
                // Here we set the double-click event handler to the header container.
                self.myGrid.$viewport.on('dblclick', self.onDoubleClick);
            };
            // double-click function
            self.onDoubleClick = function(event) {
                var onRowDoubleClicked = self.myGrid.config.onRowDoubleClicked;
                if (onRowDoubleClicked) {
                    onRowDoubleClicked(self.$scope.selectedItems[0]);
                }
            };
        }

        $scope.grid = {
            data: "data"
            , multiSelect: false
            , enablePaging: enablePaging
            , showFooter: showFooter
            , totalServerItems: "totalServerItems"
            , filterOptions: $scope.filterOptions
            , enableCellSelection: false
            , enableRowSelection: true
            , enableCellEditOnFocus: true
            , enablePinning: true
            , columnDefs: columnDefs
            , afterSelectionChange: onAfterSelectionChanged
            , beforeSelectionChange: onBeforeSelectionChanged
            , onRowDoubleClicked: onRowDoubleClicked
            , plugins: [ngGridDoubleClick]
        };
        
        angular.element($element).css("height", (rowSize * rowHeight) + "px");
    }

    /**
     * Compile
     */
    this.compile = function() {
        return {
            pre: function() {
            
            },
            post: function($scope, $element, $attrs, controller) {
                
            }
        };
    };
}
