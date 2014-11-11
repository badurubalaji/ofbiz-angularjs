package org.ofbiz.angularjs.common.factory;

function ExceptionHandlerFactory() {
    return function (exception, cause) {
        if (exception.message.indexOf("[$rootScope:inprog]") > -1) {
            console.warn("Action already in progress.");
        } else {
            exception.message += ' (caused by "' + cause + '")';
            //throw exception;
            console.error(exception.message);
        }
      };
}
