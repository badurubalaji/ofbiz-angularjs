package org.ofbiz.angularjs.filter;

/**
 * Capitalize Filter
 * 
 */
function CapitalizeFilter() {
    return function(input, scope) {
        return input.substring(0,1).toUpperCase() + input.substring(1);
    }
}
