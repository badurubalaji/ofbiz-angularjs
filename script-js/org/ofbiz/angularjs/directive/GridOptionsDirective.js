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
        
        var enablePaging = true;
        var showFooter = true;
        
        if (!pageSizes) {
            pageSizes = [250, 500, 1000];
        }
        
        if (!pageSize) {
            pageSize = 250;
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
        $scope.getPagedDataAsync = function (selectTarget, listName, pageSize, page, searchText) {
            setTimeout(function () {
                var data;
                var postData = {viewSize: pageSize};
                if (searchText) {
                    var ft = searchText.toLowerCase();
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
                        
                        $scope.setPagingData(data, page, pageSize, viewIndex, listSize);
                        appBusy.set(false);
                    });
                } else {
                    $http.post(selectTarget, postData).success(function (response) {
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
            pageSizes: pageSizes,
            pageSize: pageSize,
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
