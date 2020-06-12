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



function getComments(){
    var currUserEmail = null; 
    var isAdmin = "false";
    fetch('/login').then(response => response.json()).then((userInfo) =>{
        currUserEmail = userInfo[1];
        isAdmin = userInfo[2];
    });
    fetch('/comments').then(response => response.json()).then((comments) =>{
        comments.forEach((comment) => {
            const commentHistory = document.getElementById(comment.postId);
            commentHistory.appendChild(createCommentElement(comment, currUserEmail, isAdmin));
        })
    });
}

function createCommentElement(comment, currUserEmail, isAdmin){
    const commentElement = document.createElement('li');
    commentElement.className = 'comment';

    const textElement = document.createElement('p');
    textElement.innerText = comment.text;

    const dateElement = document.createElement('h6');
    dateElement.innerText = comment.date;

    const userEmailElement = document.createElement('h5');
    userEmailElement.innerText = comment.userEmail;

    commentElement.appendChild(userEmailElement);
    commentElement.appendChild(dateElement);

    if(comment.imageKey!=null){
        const imageElement = document.createElement('img');
        fetch('/serveBlob?imageKey='+comment.imageKey).then((image)=>{
            imageElement.src = image.url;
        });
        commentElement.appendChild(imageElement);

    }

    commentElement.appendChild(textElement);

    if (currUserEmail == comment.userEmail || isAdmin == "true") {
        const deleteButtonElement = document.createElement('button');
        deleteButtonElement.innerText = 'Delete';
        deleteButtonElement.addEventListener('click', () => {
            deleteComment(comment);
            commentElement.remove();
        });
        commentElement.appendChild(deleteButtonElement);

    }
    return commentElement;
}

async function deleteComment(comment){
    (async () =>{
        const params = new URLSearchParams();
        params.append('commentId', comment.commentId);
        fetch('/delete-comment', {method: 'POST', body:params});
    })();
}

function  getPostFunctionality(){
    fetch('/login').then(response => response.json()).then((userInfo) =>{
        if (userInfo[0] == "true") {
            console.log('User is signed in and should see post option');
            var forms = document.getElementsByTagName("form");
            for(let formIndex=0; formIndex<forms.length; formIndex++){
               forms[formIndex].style.display="block";
            }
        } else {
         console.log('user not login and should not see post option');
        }
    })
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

function fetchBlobstoreUrlAndShowForm() {
  fetch('/blobstore-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const messageForm = document.getElementsByClassName('my-form');
        for(let formIndex=0; formIndex<messageForm.length; formIndex++){
            console.log(imageUploadUrl);
            messageForm[formIndex].action=imageUploadUrl;
        }
      });
}

// Sticky NavBar Functions
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
