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
