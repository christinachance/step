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
    fetch('/comments').then(response => response.json()).then((comments) =>{
        const numCommentSections = document.getElementsByClassName('oldComments').length;
        for(var postId=1; postId<=numCommentSections; postId++){
            const commentHistory = document.getElementById(postId);
            comments.forEach((comment) => {
                if(postId == comment.postId){
                    commentHistory.appendChild(createCommentElement(comment));
                }
            })
        }
    });
}

// function getComments(){
//     fetch('/comments').then(response => response.json()).then((comments) =>{
//         comments.forEach((comment) => {
//             const commentHistory = document.getElementById(comment.postId);
//             commentHistory.appendChild(createCommentElement(comment));
//         })
//     });
// }


function createCommentElement(comment){
    const commentElement = document.createElement('li');
    commentElement.className = 'comment';

    const textElement = document.createElement('p');
    textElement.innerText = comment.text;

    const dateElement = document.createElement('h5');
    dateElement.innerText = comment.date;

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.addEventListener('click', () => {
        deleteComment(comment);
        commentElement.remove();
    });

    commentElement.appendChild(dateElement);
    commentElement.appendChild(textElement);
    commentElement.appendChild(deleteButtonElement);
    return commentElement;
}

function deleteComment(comment){
    const params = new URLSearchParams();
    params.append('id', comment.id);
    fetch('/delete-comment', {method: 'POST', body:params});
}
