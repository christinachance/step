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

/**
 * Adds a random greeting to the page.
 */

var slideIndex = 0;
showSlides();


function showSlides(){
    var i;
    var slides = document.getElementsByClassName("mySlides");
    for(i=0; i<slides.length;i++){
        slides[i].style.display="none";
    }
    console.log(slides.length);
    slideIndex++;
    if(slideIndex> slides.length){
        slideIndex=1;
    }
    if(slideIndex<1){
        slideIndex=slides.length;
    }
    slides[slideIndex-1].style.display = "block";
    setTimeout(showSlides, 2000);
}

