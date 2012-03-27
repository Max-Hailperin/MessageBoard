package edu.gac.mcs270.messageboard.shared;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.Date;
/**
 * A contribution to the message board.
 * @author max
 */



@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class Message implements Serializable{
	private static final long serialVersionUID = 1L;

	@Persistent
	private String author;

	@Persistent
	private String text;
	
	@Persistent
	private Date date;

	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Long id;

	/**
	 * @param author the name of the Message's author
	 * @param text the body of the Message
	 */
	public Message(String author, String text) {
		this.author = author;
		this.text = text;
		this.date = new Date();
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
	
	public Date getDate() {
		return this.date;
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
