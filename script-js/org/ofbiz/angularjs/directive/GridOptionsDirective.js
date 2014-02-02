package org.ofbiz.angularjs.directive;

/**
 * Grid Options Directive
 */
function GridOptionsDirective(HttpService, $timeout) {
    
    /**
     * Controller
     */
    this.controller = function($scope, $element, $attrs, $transclude) {
        var modelName = $attrs.model;
        var rowHeight = $attrs.rowHeight;
        var selectTarget = $attrs.selectTarget;
        var selectParameters = $scope.$eval($attrs.selectParameters);
        var listName = $attrs.listName;
        var columnDefs = $scope.$eval($attrs.columnDefs);
        var pageSizes = $scope.$eval($attrs.pageSizes);
        var pageSize = $scope.$eval($attrs.pageSize);
        var sortInfo = null;
        if ($attrs.sortInfo) {
            sortInfo = $scope.$eval($attrs.sortInfo);
        }
        
        var onBeforeSelectionChanged = $scope[$attrs.onBeforeSelectionChanged]; // function (rowItem, event) {}
        var onAfterSelectionChanged = $scope[$attrs.onAfterSelectionChanged]; // function (rowItem, event) { return true; }
        var onRowDoubleClicked = $scope[$attrs.onRowDoubleClicked]; // function (rowItem, event) {}
        
        $scope.filterOptions = {
            filterText: "",
            useExternalFilter: true
        };
        
        var enablePaging = true;
        var showFooter = true;
        
        if (!pageSizes) {
            pageSizes = [20, 30, 50, 100, 200];
        }
        
        if (!pageSize) {
            pageSize = 20;
        }
        
        $scope.totalServerItems = 0;
        
        $scope.pagingOptions = {
            pageSizes: pageSizes,
            pageSize: pageSize,
            currentPage: 1
        };
        
        if (sortInfo) {
            $scope.sortInfo = sortInfo;
        }
        
        $scope.data = [];
        
        /**
         * Set Paging Data
         */
        $scope.setPagingData = function(data, viewSize, viewIndex, listSize) {
            if (!viewIndex) {
                viewIndex = 0;
            }
            var pageSize = viewSize;
            var currentPage = viewIndex + 1;
            
            // var pagedData = data.slice((page - 1) * pageSize, page * pageSize);
            $scope.data = data; // set data for the first loaded
            $scope.$parent.data = data; // set data for other loaded
            $scope.totalServerItems = listSize;
            /*
            $scope.pagingOptions = {
                pageSizes: pageSizes,
                pageSize: pageSize,
                currentPage: currentPage
            };
            */
            if (!$scope.$$phase) {
                $scope.$apply();
            }
        };
        
        /**
         * Get Paged Data Async
         */
        $scope.getPagedDataAsync = function (selectTarget, listName, viewSize, viewIndex, parameters) {
            setTimeout(function () {
                var data;
                var postData = {viewSize: viewSize, viewIndex: viewIndex};
                if ($scope.orderBy) {
                    postData.orderBy = $scope.orderBy;
                }
                
                // add select parameters
                if (selectParameters) {
                    for (key in selectParameters) {
                        var value = selectParameters[key];
                        postData[key] = value;
                    }
                }
                
                if (parameters) {
                    for (key in parameters) {
                        var value = parameters[key];
                        postData[key] = value;
                    }
                    HttpService.post(selectTarget, postData).success(function (response) {
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
                            $scope.setPagingData(data, viewSize, viewIndex, listSize);
                        }
                    });
                } else {
                    HttpService.post(selectTarget, postData).success(function (response) {
                        var listSize = response.listSize;
                        if (listSize > 0) {
                            var data = response[listName];
                            if (data != null) {
                                $scope.$parent.data = data;
                                $scope.setPagingData(data, viewSize, viewIndex, listSize);
                            }
                        }
                    });
                }
            }, 100);
        };
        
        var viewSize = $scope.pagingOptions.pageSize;
        var viewIndex = $scope.pagingOptions.currentPage - 1;
        $scope.getPagedDataAsync(selectTarget, listName, viewSize, viewIndex, null);
        
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
            
            $scope.parameters = args.parameters;

            var viewSize = pageSize;
            var viewIndex = currentPage - 1;
            $scope.getPagedDataAsync(selectTarget, listName, viewSize, viewIndex, $scope.parameters);
        });
        
        if (sortInfo) {
            $scope.$watch('sortInfo', function (newVal, oldVal) {
                var fieldName = newVal.fields[0];
                var sortDirection = newVal.directions[0];
                if ("asc" == sortDirection) {
                    $scope.orderBy = "-" + fieldName;
                } else if ("desc" == sortDirection) {
                    $scope.orderBy = fieldName;
                }
                $scope.getPagedDataAsync(selectTarget, listName, viewSize, viewIndex, $scope.parameters);
            }, true);
        }
        
        $scope.$watch('pagingOptions', function (newVal, oldVal) {
            if (newVal !== oldVal) {
                var viewIndex = 0;
                var viewSize = newVal.pageSize;
                if (newVal.currentPage !== oldVal.currentPage) {
                    if (!isNaN(newVal.currentPage)) {
                        viewIndex = newVal.currentPage - 1;
                    }
                }
               
                var parameters = $scope.parameters;
                if (parameters == null) {
                    parameters = {};
                }
                $scope.getPagedDataAsync(selectTarget, listName, viewSize, viewIndex, parameters);
              
            }
        }, true);
        
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
                    $timeout(function() {
                        var selectedItem = self.$scope.selectedItems[0];
                        onRowDoubleClicked(adjustFieldNames(selectedItem));
                    }, 100)
                }
            };
        }
        
        /**
         * Adjust Field Names
         * @param rowItem
         * @returns
         */
        function adjustFieldNames(rowItem) {
            var keys = _.keys(rowItem);
            _.each(columnDefs, function(columnDef) {
                if (!_.contains(keys, columnDef.name)) {
                    var value = rowItem[columnDef.field];
                    rowItem[columnDef.name] = value;
                    delete rowItem[columnDef.field];
                }
            });
            return rowItem;
        }

        $scope.grid = {
            data: "data"
            , multiSelect: false
            , enablePaging: enablePaging
            , pagingOptions: $scope.pagingOptions
            , showFooter: showFooter
            , totalServerItems: "totalServerItems"
            , filterOptions: $scope.filterOptions
            , enableCellSelection: false
            , enableRowSelection: true
            , enableCellEditOnFocus: true
            , enablePinning: true
            , useExternalSorting: true
            , columnDefs: columnDefs
            , afterSelectionChange: onAfterSelectionChanged
            , beforeSelectionChange: onBeforeSelectionChanged
            , onRowDoubleClicked: onRowDoubleClicked
            , plugins: [ngGridDoubleClick]
        };
        
        if (sortInfo) {
            $scope.grid.sortInfo = sortInfo;
        }
        
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
