package org.ofbiz.angularjs.common.util;

/**
 * Scope Util
 */
function ScopeUtil($rootScope) {
    
    /**
     * Set top scope property
     */
    this.setTopScopeProperty = function($currentScope, propertyName, propertyValue) {
        var topScopeIndex = -1;
        var scopeIndex = 0;
        var tempParentScopes = [];
        var tempParentScope = $currentScope.$parent;
        while (tempParentScope != null) {
            tempParentScopes.push(tempParentScope);
            if (tempParentScope == $rootScope) {
                topScopeIndex = scopeIndex - 1;
            }
            scopeIndex ++;
            tempParentScope = tempParentScope.$parent;
        }
        
        var topScope = tempParentScopes[topScopeIndex];
        topScope[propertyName] = propertyValue;
    };
}
