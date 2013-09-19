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
        var rowHeight = $attrs.rowHeight;
        var targetUri = $attrs.targetUri;
        var listName = $attrs.listName;
        var columnDefs = $scope.$eval($attrs.columnDefs);
        
        var enablePaging = true;
        var showFooter = true;
        
        $scope.data = [];
        
        $scope.setPagingData = function(data, page, pageSize){  
            var pagedData = data.slice((page - 1) * pageSize, page * pageSize);
            $scope.data = pagedData;
            $scope.totalServerItems = data.length;
            if (!$scope.$$phase) {
                $scope.$apply();
            }
        };
        
        $scope.getPagedDataAsync = function (targetUri, listName, pageSize, page, searchText) {
            setTimeout(function () {
                var data;
                if (searchText) {
                    var ft = searchText.toLowerCase();
                    $http.get(targetUri).success(function (response) {
                        var listSize = response.listSize;
                        if (listSize > 0) {
                            data = response[listName].filter(function(item) {
                                return JSON.stringify(item).toLowerCase().indexOf(ft) != -1;
                            });
                            $scope.setPagingData(data,page,pageSize);
                        }
                        appBusy.set(false);
                    });
                } else {
                    $http.get(targetUri).success(function (response) {
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
        
        $scope.getPagedDataAsync(targetUri, listName, $scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, null, $scope);
        
        $scope.$watch('pagingOptions', function (newVal, oldVal) {
            if (newVal !== oldVal && newVal.currentPage !== oldVal.currentPage) {
                $scope.getPagedDataAsync(targetUri, listName, $scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
            }
        }, true);
        $scope.$watch('filterOptions', function (newVal, oldVal) {
            if (newVal !== oldVal) {
              $scope.getPagedDataAsync(targetUri, listName, $scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
            }
        }, true);
        
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
}
