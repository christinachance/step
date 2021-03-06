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

package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/comments")
public class DataServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
      String imageKey = getUploadedFileUrl(request, "image");
      String text = request.getParameter("comment");
      long timestamp = System.currentTimeMillis();
      SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");    
      Date resultdate = new Date(timestamp);
      String date = sdf.format(resultdate);
      long postId = Long.parseLong(request.getParameter("id"));
      String userEmail = null;

      UserService userService = UserServiceFactory.getUserService();
      if (userService.isUserLoggedIn()) {
        userEmail = userService.getCurrentUser().getEmail();
      } else {
        String urlToRedirectToAfterUserLogsIn = "/";
        String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
        response.sendRedirect(loginUrl);
      }

      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("text", text);
      commentEntity.setProperty("imageKey", imageKey);
      commentEntity.setProperty("timestamp", timestamp);
      commentEntity.setProperty("date", date);
      commentEntity.setProperty("postId", postId);
      commentEntity.setProperty("userEmail", userEmail);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);

      response.sendRedirect("/blog.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
      Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);


      List<Comment> comments = new ArrayList<>();
      for(Entity entity:results.asIterable()){
          long commentId = entity.getKey().getId();
          String imageKey =  (String) entity.getProperty("imageKey");
          String text = (String) entity.getProperty("text");
          String date = (String) entity.getProperty("date");
          long timestamp = (long) entity.getProperty("timestamp");
          long postId = (long) entity.getProperty("postId");
          String userEmail = (String) entity.getProperty("userEmail"); 

          Comment comment = new Comment(commentId, text, timestamp, date, postId, userEmail, imageKey);
          comments.add(comment);
      }

      Gson gson = new Gson();
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(comments));
  }

  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    if (blobKeys == null || blobKeys.isEmpty()) {
        return null;
    }

    BlobKey blobKey = blobKeys.get(0);

    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
        blobstoreService.delete(blobKey);
        return null;
    } else {
        return blobKey.getKeyString();
    }

  }
}
