package org.ofbiz.angularjs.common.service;

/**
 * Form Service
 */
function FormService($http, HttpService, $upload, appBusy) {
    
    /**
     * Post
     */
    this.post = function(target, parameters, successHandler, errorHandler) {
        HttpService.post(target, parameters, {"Content-Type": "application/x-www-form-urlencoded"})
        .success(function(data, status, headers, config) {
            if (data._ERROR_MESSAGE_ != undefined || data._ERROR_MESSAGE_LIST_ != undefined) {
                if (typeof(errorHandler) == "function") {
                    errorHandler(data, status, headers, config);
                }
            } else {
                if (typeof(successHandler) == "function") {
                    successHandler(data, status, headers, config);
                }
            }
        })
        .error(function(data, status, headers, config) {
            if (typeof(errorHandler) == "function") {
                errorHandler(data, status, headers, config);
            }
        });
    }
    
    this.upload = function(target, files, parameters, onProgress, onSuccess) {
        //$files: an array of files selected, each file has name, size, and type.
        for (var i = 0; i < files.length; i++) {
          var file = files[i];
          $upload.upload({
            url: target, //upload.php script, node.js route, or servlet url
            // method: POST or PUT,
            // headers: {'headerKey': 'headerValue'},
            // withCredentials: true,
            data: parameters,
            file: file,
            // file: $files, //upload multiple files, this feature only works in HTML5 FromData browsers
            /* set file formData name for 'Content-Desposition' header. Default: 'file' */
            //fileFormDataName: myFile, //OR for HTML5 multiple upload only a list: ['name1', 'name2', ...]
            /* customize how data is added to formData. See #40#issuecomment-28612000 for example */
            //formDataAppender: function(formData, key, val){} //#40#issuecomment-28612000
          }).progress(function(evt) {
            if (onProgress != null) {
                var percent = parseInt(100.0 * evt.loaded / evt.total);
                onProgress(percent)
            }
          }).success(function(data, status, headers, config) {
            // file is uploaded successfully
              if (onSuccess != null) {
                  onSuccess(data);
              }
          });
          //.error(...)
          //.then(success, error, progress); 
        }
    }
    
    /**
     * Post Multi
     */
    this.postMulti = function(target, data, rowItems, successHandler, errorHandler) {
        var rowItemsQueryString = "&";
        var rowItemIndex = 0;
        _.each(rowItems, function(rowItem) {
            var pairs = _.pairs(rowItem)
            _.each(pairs, function(pair) {
                var key = pair[0];
                var value = pair[1];
                if (value != null) {
                    var multiKey = key + "_o_" + rowItemIndex;
                    data[multiKey] = value;
                    rowItemsQueryString += (multiKey + "=" + value + "&");
                }
            });
            
            rowItemIndex ++;
        });
        this.post(target, data + rowItemsQueryString, successHandler, errorHandler);
    }
}
