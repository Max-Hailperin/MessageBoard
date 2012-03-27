package edu.gac.mcs270.messageboard.server;

import java.util.ArrayList;
import java.util.List;

import edu.gac.mcs270.messageboard.shared.Message;
import edu.gac.mcs270.messageboard.shared.MessageStore;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;


/**
 * The server side implementation of the RPC service.
 * @author max
 */
public class MessageStoreImpl extends RemoteServiceServlet implements
MessageStore {

	private static final long serialVersionUID = 7367373321119740703L;
	
	private static final PersistenceManagerFactory pmf =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");

	@Override
	public void storeMessage(Message msg) {
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.makePersistent(msg);
	}

	@Override
	public List<Message> getMessages(Long minimumID) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Query query = pm.newQuery(Message.class);
		query.declareParameters("Long minimumID");
		query.setFilter("id >= minimumID");
		query.setOrdering("id descending");
		if(minimumID == 1){
			// Keeps initial updates fast
			query.setRange(0, MessageStore.INITIAL_LIMIT);
		}
		@SuppressWarnings("unchecked")
		// Limit to new messages
		List<Message> messages = (List<Message>) query.execute(minimumID);
		return new ArrayList<Message>(messages);
	}
	
	public List<Message> getOldMessages(Long minID, Long maxID) throws Exception {
		PersistenceManager pm = pmf.getPersistenceManager();
		Query query = pm.newQuery(Message.class);
		query.declareParameters("Long minID, Long maxID");
		query.setFilter("(id >= minID) && (id < maxID)");
		query.setOrdering("id descending");
		if(minID < 0){
			minID = 0L;
		} else if (minID > maxID) {
			throw new Exception("Error: minID cannot be greater than maxID");
		}
		query.setRange(minID, maxID);
		@SuppressWarnings("unchecked")
		// Limit to new messages
		List<Message> messages = (List<Message>) query.execute(minID);
		return new ArrayList<Message>(messages);
	}
}
