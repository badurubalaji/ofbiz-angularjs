package org.ofbiz.angularjs.common.service;

/**
 * Notification Service
 */
function NotificationService($rootScope) {

    this.showToast = function(text) {
        alert(text);
    };
}
