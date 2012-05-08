package edu.gac.mcs270.messageboard.server;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gac.mcs270.messageboard.shared.BlobService;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/* This is extracted from code in the BlobStore Tutorial written by the blogger "fishbone":
 *  http://www.fishbonecloud.com/2010/12/tutorial-gwt-application-for-storing.html */

@SuppressWarnings("serial")
public class BlobServiceImpl extends RemoteServiceServlet implements
    BlobService {

  //Start a GAE BlobstoreService session and persistence manager
  BlobstoreService blobstoreService = BlobstoreServiceFactory
      .getBlobstoreService();
  PersistenceManager pm = PMF.get().getPersistenceManager();
  
  //Generate a Blobstore Upload URL from the GAE BlobstoreService
  @Override
  public String getBlobStoreUploadUrl() {

    //Map the UploadURL to the uploadservice which will be called by
    //submitting the FormPanel
    return blobstoreService
        .createUploadUrl("/messageboard/uploadservice");
  }

  //Override doGet to serve blobs.  This will be called when an uploaded file is viewed
  //in the client
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

        BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
        blobstoreService.serve(blobKey, resp);

  }
}