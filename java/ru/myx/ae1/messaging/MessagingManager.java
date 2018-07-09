/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.messaging;

import ru.myx.ae3.base.BaseObject;

/**
 * @author myx
 * 
 */
public interface MessagingManager {
	/**
	 * @param messageFactoryId
	 * @param messageFactoryParameters
	 * @return message
	 */
	public MessageBlank createBlankMessage(final String messageFactoryId, final BaseObject messageFactoryParameters);
	
	/**
	 * @param subject
	 * @param body
	 * @return message
	 */
	public MessageBlank createBlankMessageText(final String subject, final String body);
	
	/**
	 * @param key
	 */
	public void deleteInbox(final int key);
	
	/**
	 * @param keys
	 */
	public void deleteInbox(final int[] keys);
	
	/**
	 * @param key
	 */
	public void deleteSent(final int key);
	
	/**
	 * @param keys
	 */
	public void deleteSent(final int[] keys);
	
	/**
	 * @return messages
	 */
	public Message[] getInbox();
	
	/**
	 * @param key
	 * @return message
	 */
	public Message getInbox(final int key);
	
	/**
	 * @return messages
	 */
	public Message[] getSent();
	
	/**
	 * @param key
	 * @return message
	 */
	public Message getSent(final int key);
	
	/**
	 * @return boolean
	 */
	public boolean hasInbox();
	
	/**
	 * @return boolean
	 */
	public boolean hasSent();
}
