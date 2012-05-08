package edu.gac.mcs270.messageboard.shared;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * A contribution to the message board.
 * @author max
 */
@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class Message implements Serializable{
	private static final long serialVersionUID = 2L;

	@Persistent
	private String author;

	@Persistent
	private String text;
	
	@Persistent
	private String url;

	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Long id;

	/**
	 * @param author the name of the Message's author
	 * @param text the body of the Message
	 * @param url the URL of the uploaded file
	 */
	public Message(String author, String text, String url) {
		this.author = author;
		this.text = text;
		this.url = url;
	}

	/**
	 * @return the name of this Message's author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return the body of this Message
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the URL of the file uploaded with this Message
	 */
	public String getURL() {
		return url;
	}

	/**
	 * @return the id of this Message
	 */
	public Long getId() {
		return id;
	}
	
	// To keep DataNucleus happy we need this:
	@SuppressWarnings("unused")
	private Message(){}
}
