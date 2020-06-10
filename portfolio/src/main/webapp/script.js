// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


var slideIndex = 0;

function showSlides(){
    var currentImgIndex;
    var slides = document.getElementsByClassName("mySlides");
    for(currentImgIndex=0; currentImgIndex<slides.length;currentImgIndex++){
        slides[currentImgIndex].style.display="none";
    }
    slideIndex++;
    if (slideIndex> slides.length) {
        slideIndex=1;
    }
    if (slideIndex<1) {
        slideIndex=slides.length;
    }
    slides[slideIndex-1].style.display = "block";
    setTimeout(showSlides, 3000);
}

window.onscroll = function() {myFunction()};

var navbar = document.getElementById("nav");
var sticky = navbar.offsetTop;

function myFunction() {
  if (window.pageYOffset >= sticky) {
    navbar.classList.add("sticky");
  } else {
    navbar.classList.remove("sticky");
  }
}

function getUserLogin() {
    fetch('/login').then(response => response.json()).then((userInfo) => {
        const link = document.getElementById('loginButton');
        link.href = userInfo[3];
        if (userInfo[0] == "true") {
            link.innerText="Sign Out";
        } else {
            link.innerText="Sign In";
        }
    });
}

var map;
var markers = [];

function initMap(){
    var hartford = {lat: 41.78, lng: -72.69};
    map = new google.maps.Map(
        document.getElementById('map'),
        {center: hartford, zoom: 5, mapTypeId: 'terrain'});
    var atlanta = {lat: 33.79, lng: -84.32};
    var jamaica = {lat: 18.39, lng: -77.39};
    var munnar = {lat: 10.079, lng: 77.062};
    var hightstown = {lat: 40.267, lng: -74.52};
    var disney = {lat: 28.38, lng: -81.56};
    var sunnyvale = {lat: 37.403, lng: -122.03};
    var manhattan = {lat: 40.71, lng: -74.002};
    var dc = {lat: 38.92, lng: -77.02};
    var niagra = {lat:43.25, lng:-79.079};
    addMarker(hartford);
    addMarker(atlanta);
    addMarker(jamaica);
    addMarker(munnar);
    addMarker(hightstown);
    addMarker(disney);
    addMarker(sunnyvale);
    addMarker(manhattan);
    addMarker(dc);
    addMarker(niagra);

    google.maps.event.addListener(map, 'click', function(event) {
            addMarker(event.latLng, "purple");
    });
}

function addMarker(location, color="blue"){
    var marker = new google.maps.Marker({
        position: location,
        map:map,
        icon:{ url: "http://maps.google.com/mapfiles/ms/icons/"+color+"-dot.png"}
    });
    markers.push(marker);
}

function setMapOnAll(map) {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(map);
    }
}

function clearMarkers() {
    setMapOnAll(null);
}

function showMarkers() {
    setMapOnAll(map);
}

function deleteMarkers() {
    clearMarkers();
    markers = [];
}

google.maps.event.addDomListener(window, 'load', initMap);
