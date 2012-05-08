/**
 * 
 */
package edu.gac.mcs270.messageboard.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The asynchronous interface to a MessageStore.
 * @author max
 */
public interface MessageStoreAsync {

	/**
	 * 
	 * @see edu.gac.mcs270.messageboard.shared.MessageStore#getMessages(long)
	 */
	void getMessages(Long minimumID, AsyncCallback<List<Message>> callback);

}
