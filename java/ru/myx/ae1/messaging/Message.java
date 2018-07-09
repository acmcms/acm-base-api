/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.messaging;

import ru.myx.ae3.base.BaseObject;

/**
 * @author myx
 * 
 */
public interface Message {
	/**
	 * @param messageFactoryId
	 * @param messageFactoryParameters
	 * @return bank message
	 */
	public MessageBlank createReply(final String messageFactoryId, final BaseObject messageFactoryParameters);
	
	/**
     * 
     */
	public void deleteAll();
	
	/**
     * 
     */
	public void doneRead();
	
	/**
	 * @return date
	 */
	public long getMessageDate();
	
	/**
	 * @return string
	 */
	public String getMessageFactoryId();
	
	/**
	 * @return map
	 */
	public BaseObject getMessageFactoryParams();
	
	/**
	 * @return string
	 */
	public String getMessageId();
	
	/**
	 * @return int
	 */
	public int getMessageLuid();
	
	/**
	 * @return boolean
	 */
	public boolean getMessageRead();
	
	/**
	 * @return string
	 */
	public String getMessageSender();
	
	/**
	 * @return string
	 */
	public String getMessageTarget();
}
