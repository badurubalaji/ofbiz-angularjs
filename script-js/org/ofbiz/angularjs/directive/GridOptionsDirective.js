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
        
        var enablePaging = true;
        var showFooter = true;
        
        $scope.data = [];
        
        /**
         * Set Paging Data
         */
        $scope.setPagingData = function(data, page, pageSize) {
            var pagedData = data.slice((page - 1) * pageSize, page * pageSize);
            $scope.data = pagedData;
            $scope.totalServerItems = data.length;
            $scope.$parent.data = data;
            if (!$scope.$$phase) {
                $scope.$apply();
            }
        };
        
        /**
         * Get Paged Data Async
         */
        $scope.getPagedDataAsync = function (selectTarget, listName, pageSize, page, searchText) {
            setTimeout(function () {
                var data;
                if (searchText) {
                    var ft = searchText.toLowerCase();
                    $http.get(selectTarget + "?" + searchText).success(function (response) {
                        var listSize = response.listSize;
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
                        
                        $scope.setPagingData(data,page,pageSize);
                        appBusy.set(false);
                    });
                } else {
                    $http.get(selectTarget).success(function (response) {
                        var listSize = response.listSize;
                        if (listSize > 0) {
                            $scope.$parent.data = response[listName];
                            $scope.setPagingData(response[listName],page,pageSize);
                        }
                        appBusy.set(false);
                    });
                }
            }, 100);
        };
        
        $scope.totalServerItems = 0;
        
        $scope.pagingOptions = {
            pageSizes: [250, 500, 1000],
            pageSize: 250,
            currentPage: 1
        };
        
        $scope.filterOptions = {
            filterText: "",
            useExternalFilter: true
        };
        
        $scope.getPagedDataAsync(selectTarget, listName, $scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, null, $scope);
        
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
            if (!filterText) {
                filterText = $scope.filterOptions.filterText;
            }
            $scope.getPagedDataAsync(selectTarget, listName, pageSize, currentPage, filterText);
        });
        
        var rowSize = 10
        
        if (rowSize > 20) {
            enablePaging = true;
            showFooter = true;
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
