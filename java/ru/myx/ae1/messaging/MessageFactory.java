/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.messaging;

import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlForm;

/**
 * @author myx
 * 
 */
public interface MessageFactory {
	/**
	 * @param message
	 * @return map
	 */
	public BaseObject createExternalMessage(final Message message);
	
	/**
	 * @param message
	 * @return form
	 */
	public ControlForm<?> createMessageForm(final Message message);
	
	/**
	 * @param message
	 * @return string
	 */
	public String getMessageTitle(final Message message);
	
	/**
	 * @return string
	 */
	public String getTitle();
	
	/**
	 * @return boolean
	 */
	public boolean isExternalSupported();
	
	/**
	 * @return boolean
	 */
	public boolean isFormSupported();
}
