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
			query.setRange(0, MessageStore.INITIAL_LIMIT);
		}
		@SuppressWarnings("unchecked")
		List<Message> messages = (List<Message>) query.execute(minimumID);
		return new ArrayList<Message>(messages);
	}
}
