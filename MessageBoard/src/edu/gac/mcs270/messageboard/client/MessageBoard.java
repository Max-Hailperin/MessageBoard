package edu.gac.mcs270.messageboard.client;

import java.util.List;

import edu.gac.mcs270.messageboard.shared.BlobService;
import edu.gac.mcs270.messageboard.shared.BlobServiceAsync;
import edu.gac.mcs270.messageboard.shared.Message;
import edu.gac.mcs270.messageboard.shared.MessageStore;
import edu.gac.mcs270.messageboard.shared.MessageStoreAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

/**
 * User interface for the message board.
 * The feature to store images in Blobstore uses code taken from the BlobStore Tutorial
 * written by the blogger "fishbone":
 *  http://www.fishbonecloud.com/2010/12/tutorial-gwt-application-for-storing.html
 * @author max
 */
public class MessageBoard implements EntryPoint {
	// The interval in milliseconds between periodic updates.
	private static final int UPDATE_INTERVAL_MS = 10000;
	private final MessageStoreAsync messageStore = GWT
			.create(MessageStore.class);
	private final BlobServiceAsync blobService = GWT
			.create(BlobService.class);
	private VerticalPanel messagesPanel;
	private Label updatingLabel;
	private Label failureLabel;
	private Long nextID = 1L; // minimum ID for next retrieval of Messages

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final VerticalPanel mainPanel = new VerticalPanel();
		final FormPanel uploadForm = new FormPanel();
		final HorizontalPanel entryPanel = new HorizontalPanel();
		entryPanel.addStyleName("entryPanel");
		mainPanel.add(uploadForm);
		uploadForm.add(entryPanel);

		final Label authorLabel = new Label("Author:");
		final TextBox authorField = new TextBox();
		entryPanel.add(authorLabel);
		entryPanel.add(authorField);
		
		final Label textLabel = new Label("Text:");
		final TextBox textField = new TextBox();
		entryPanel.add(textLabel);
		entryPanel.add(textField);

		final FileUpload upload = new FileUpload();
		entryPanel.add(upload);

		final Button postButton = new Button("Post");
		entryPanel.add(postButton);

		// The upload form, when submitted, will trigger an HTTP call to the
		// servlet.  The following parameters must be set
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);

		// Set Names for the text boxes so that they can be retrieved from the
		// HTTP call as parameters
		authorField.setName("author");
		textField.setName("text");
		upload.setName("upload");

		final HorizontalPanel statusPanel = new HorizontalPanel();
		statusPanel.setHeight("3em");
		updatingLabel = new Label("Updating...");
		updatingLabel.setVisible(false);
		failureLabel = new Label("Lost connection to server.");
		failureLabel.setVisible(false);
		statusPanel.add(updatingLabel);
		statusPanel.add(failureLabel);
		mainPanel.add(statusPanel);
		
		messagesPanel = new VerticalPanel();
		mainPanel.add(messagesPanel);
		RootPanel.get("appContent").add(mainPanel);
		
		postButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {

				blobService
				.getBlobStoreUploadUrl(new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {
						// Set the form action to the newly created
						// blobstore upload URL
						uploadForm.setAction(result.toString());

						// Submit the form to complete the upload
						uploadForm.submit();
					}

					@Override
					public void onFailure(Throwable caught) {
						failureLabel.setVisible(true);
					}
				});

			}
		});

		uploadForm.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				// This event is fired just before the form is submitted. We can take
				// this opportunity to perform validation.
				if (authorField.getText().length() == 0) {
					Window.alert("The author is required.");
					event.cancel();
				}
				if (textField.getText().length() == 0) {
					Window.alert("The text is required.");
					event.cancel();
				}
				if (upload.getFilename().length() == 0) {
					Window.alert("The upload file is required.");
					event.cancel();
				}
			}
		});


		uploadForm
		.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				failureLabel.setVisible(false);
				updateMessages();
				uploadForm.reset();
				authorField.setFocus(true);
			}

		});

		updateMessages();
		new Timer(){
			@Override
			public void run() {
				updateMessages();
			}
		}.scheduleRepeating(UPDATE_INTERVAL_MS);
		
		Element loadingMessage = DOM.getElementById("loadingMessage");
		loadingMessage.getParentNode().removeChild(loadingMessage);
		
		authorField.setFocus(true);
	}
	
	private void updateMessages(){
		if(updatingLabel.isVisible()){
			return;
		}
		updatingLabel.setVisible(true);
		failureLabel.setVisible(false);
		messageStore.getMessages(nextID,
				new AsyncCallback<List<Message>>(){

					@Override
					public void onFailure(Throwable caught) {
						updatingLabel.setVisible(false);
						failureLabel.setVisible(true);
					}

					@Override
					public void onSuccess(List<Message> result) {
						updatingLabel.setVisible(false);
						int position = 0;
						for(Message m : result){
							Label heading = new Label(m.getAuthor());
							heading.addStyleName("messageHeading");
							messagesPanel.insert(heading, position++);
							Anchor link = new Anchor("uploaded file", m.getURL());
							link.setTarget("_blank");
							messagesPanel.insert(link, position++);
							Label body = new Label(m.getText());
							body.addStyleName("messageBody");
							messagesPanel.insert(body, position++);
						}
						if(!result.isEmpty()){
							nextID = result.get(0).getId() + 1;
						}
					}
			
		});
	}
}
