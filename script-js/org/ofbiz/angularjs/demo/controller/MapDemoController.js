package org.ofbiz.angularjs.demo.controller;

/**
 * Map Demo Controller
 * @param $scope
 */
function MapDemoController($scope) {
    try {
        $scope.myMarkers = [];

        $scope.mapOptions = {
          center: new google.maps.LatLng(35.784, -78.670),
          zoom: 15,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };
         
        $scope.addMarker = function($event, $params) {
          var latLng = $params[0].latLng;
          $scope.myMarkers.push(new google.maps.Marker({
            map: $scope.myMap,
            position: latLng
          }));
        };
         
        $scope.setZoomMessage = function(zoom) {
          $scope.zoomMessage = 'You just zoomed to '+zoom+'!';
          console.log(zoom,'zoomed')
        };
         
        $scope.openMarkerInfo = function(marker) {
          $scope.currentMarker = marker;
          $scope.currentMarkerLat = marker.getPosition().lat();
          $scope.currentMarkerLng = marker.getPosition().lng();
          $scope.myInfoWindow.open($scope.myMap, marker);
        };
         
        $scope.setMarkerPosition = function(marker, lat, lng) {
          marker.setPosition(new google.maps.LatLng(lat, lng));
        };
        
        $scope.panTo = function(marker) {
            $scope.myMap.panTo(marker.getPosition());
        };
    } catch (e) {
        console.error("Could not render map demo. " + e);
    }
}
