package org.ofbiz.angularjs.common.util;

/**
 * Date Time Util
 */
function DateTimeUtil($rootScope, $filter) {

    this.getTime = function(s) {
        var date = new Date(s);
        var milliseconds = date.getTime();
        var seconds = milliseconds / 1000;
        return seconds;
    };

    this.toTimestamp = function(seconds) {
        var milliseconds = 0;
        if (_.isArray(seconds)) {
            milliseconds = seconds[0] * 1000;
        } else {
            milliseconds = seconds * 1000;
        }
        return $filter("date")(milliseconds, "yyyy-MM-dd HH:mm:ss");
    };
}
