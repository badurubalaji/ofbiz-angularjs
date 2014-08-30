package org.ofbiz.angularjs.directive;

/**
 * Grid Directive
 *
 */
function GridDirective(HttpService, $timeout, $parse, $compile) {

    this.link = function($scope, $element, $attrs, controller) {
        var selectedItemsSetter = $parse($attrs.selectedItems).assign;
        var style = $attrs.style;
        var rowHeight = $attrs.rowHeight;
        var selectTarget = $attrs.selectTarget;
        var listName = $attrs.listName;
        var columnDefs = $scope.$eval($attrs.columnDefs);
        var pageSizes = $scope.$eval($attrs.pageSizes);
        var pageSize = $scope.$eval($attrs.pageSize);
        var multiSelect = Boolean($scope.$eval($attrs.multiSelect));
        var showSelectionCheckbox = $scope.$eval($attrs.showSelectionCheckbox);
        var checkboxHeaderTemplate = null;
        var sortInfo = null;

        $scope.$watch("selectParameters", function (newVal, oldVal) {
            onParametersChanged({parameters: newVal});
        });

        if ($attrs.sortInfo) {
            sortInfo = $scope.$eval($attrs.sortInfo);
        }

        if (showSelectionCheckbox) {
            checkboxHeaderTemplate = "<input class=\"ngSelectionHeader\" type=\"checkbox\" ng-show=\"multiSelect\" ng-model=\"allSelected\" ng-change=\"toggleSelectAll(allSelected, true)\"/>";
        }

        var onBeforeSelectionChanged = $scope[$attrs.onBeforeSelectionChanged]; // function (rowItem, event) {}
        var onAfterSelectionChanged = $scope[$attrs.onAfterSelectionChanged]; // function (rowItem, event) { return true; }

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
        $scope.selectedItems = [];
        $scope.selectedItemsToDispatch = [];

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
            $scope.data = adjustDataFieldNames(data); // set data for the first loaded
            $scope.$parent.data = $scope.data; // set data for other loaded
            $scope.totalServerItems = listSize;

            /*
            $scope.pagingOptions = {
                pageSizes: pageSizes,
                pageSize: pageSize,
                currentPage: currentPage
            };
            */
            if (!$scope.$$phase) {
                // This causes an error
                //$scope.$apply();
            }

            $scope.afterDataLoad();
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
                if ($scope.selectParameters) {
                    for (key in $scope.selectParameters) {
                        var value = $scope.selectParameters[key];
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

        function onParametersChanged(args) {
            if (args != null) {
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
            }
        }

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

        if (selectedItemsSetter != null) {
            $scope.$watch("selectedItemsToDispatch", function (newVal, oldVal) {
                selectedItemsSetter($scope.$parent, newVal);
            });
        }

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
                        var extendedSelectedItem = {};
                        _.extend(extendedSelectedItem, selectedItem) ;
                        onRowDoubleClicked(event, extendedSelectedItem);
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

        /**
         * Adjust Data Field Names
         * @param data
         * @returns Adjusted Data Field Names
         */
        function adjustDataFieldNames(data) {
            var adjustedData = [];
            _.each(data, function(rowItem) {
                var adjustedRowItem = adjustFieldNames(rowItem);
                adjustedData.push(adjustedRowItem);
            });
            return adjustedData;
        }

        /**
         * Adjust Column Defs
         * @param columnDefs
         * @returns Adjusted Column Defs
         */
        function adjustColumnDefs(columnDefs) {
            var adjustedColumnDefs = [];
            _.each(columnDefs, function(columnDef) {
                var adjustedColumnDef = _.clone(columnDef);
                adjustedColumnDef.field = columnDef.name;
                adjustedColumnDefs.push(adjustedColumnDef);
            });
            return adjustedColumnDefs;
        }

        // configure grid
        $scope.grid = {
            data: "data"
            , multiSelect: multiSelect
            , enablePaging: enablePaging
            , pagingOptions: $scope.pagingOptions
            , showFooter: showFooter
            , totalServerItems: "totalServerItems"
            , selectedItems: $scope.selectedItems
            , filterOptions: $scope.filterOptions
            , showSelectionCheckbox: showSelectionCheckbox
            , checkboxHeaderTemplate: checkboxHeaderTemplate
            //, selectWithCheckboxOnly: true
            , enableCellSelection: false
            , enableRowSelection: true
            , enableCellEditOnFocus: true
            , enablePinning: true
            , useExternalSorting: true
            , columnDefs: adjustColumnDefs(columnDefs)
            , init:function(grid,$scope) {
                // ng-grid does not take 100% width on page load but shows fine on resize
                // http://stackoverflow.com/questions/15523959/ng-grid-does-not-take-100-width-on-page-load-but-shows-fine-on-resize
                setTimeout(function() {
                    $scope.grid.$gridServices.DomUtilityService.RebuildGrid(
                        $scope.grid.$gridScope,
                        $scope.grid.ngGrid
                    );
                },500);
            }
            , afterSelectionChange: function(rowItem, event) {
                var selectedItemsToDispatch = [];
                _.each($scope.selectedItems, function (selectedItem) {
                    var selectedItemToDispatch = {};
                    _.extend(selectedItemToDispatch, selectedItem);
                    selectedItemsToDispatch.push(selectedItemToDispatch);
                });
                $scope.selectedItemsToDispatch = selectedItemsToDispatch;
                if (typeof(onAfterSelectionChanged) == "function") {
                    onAfterSelectionChanged(rowItem, event);
                }
            }
            , beforeSelectionChange: onBeforeSelectionChanged
            , onRowDoubleClicked: function(event, rowItem) {
                $scope.onRowDoubleClicked({"rowItem": rowItem});
            }
            , plugins: [ngGridDoubleClick]
        };

        if (sortInfo) {
            $scope.grid.sortInfo = sortInfo;
        }

        var divElement = angular.element("<div class='" + style + "' ng-grid='grid'></div>");
        angular.element(divElement).css("height", (rowSize * rowHeight) + "px");
        $element.html(divElement);
        $compile($element.contents())($scope);
    }
}
