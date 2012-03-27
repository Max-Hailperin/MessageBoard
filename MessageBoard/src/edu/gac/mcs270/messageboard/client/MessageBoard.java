package edu.gac.mcs270.messageboard.client;

import java.util.List;

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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User interface for the message board.
 * @author max
 */
public class MessageBoard implements EntryPoint {
	// The interval in milliseconds between periodic updates.
	private static final int UPDATE_INTERVAL_MS = 10000;
	private final MessageStoreAsync messageStore = GWT
			.create(MessageStore.class);
	private VerticalPanel messagesPanel;
	private Label updatingLabel;
	private Label failureLabel;
	private Long nextID = 1L; // minimum ID for next retrieval of Messages

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final VerticalPanel mainPanel = new VerticalPanel();
		final HorizontalPanel entryPanel = new HorizontalPanel();
		entryPanel.addStyleName("entryPanel");
		mainPanel.add(entryPanel);

		final Label authorLabel = new Label("Author:");
		final TextBox authorField = new TextBox();
		entryPanel.add(authorLabel);
		entryPanel.add(authorField);

		final Label textLabel = new Label("Text:");
		final TextBox textField = new TextBox();
		entryPanel.add(textLabel);
		entryPanel.add(textField);

		final Button postButton = new Button("Post");
		entryPanel.add(postButton);

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
				messageStore.storeMessage(
						new Message(
								authorField.getText(),
								textField.getText()),
								new AsyncCallback<Void>(){
							@Override
							public void onFailure(Throwable caught) {
								failureLabel.setVisible(true);
							}

							@Override
							public void onSuccess(Void result) {
								failureLabel.setVisible(false);
								updateMessages();
							}
						});
				authorField.setText("");
				textField.setText("");
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
					Label body = new Label(m.getText());
					body.addStyleName("messageBody");
					messagesPanel.insert(body, position++);
					Label date = new Label("Posted at: " + m.getDate().toString());
					date.addStyleName("messageDate");
					messagesPanel.insert(date,position++);

				} 
				if(!result.isEmpty()){
					nextID = result.get(0).getId() + 1;
				}
			}

		});
	}
}
