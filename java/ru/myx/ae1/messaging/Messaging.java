/*
 * Created on 07.10.2004
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.ae1.messaging;

import ru.myx.ae3.produce.Produce;

/**
 * @author myx
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public final class Messaging {
	/**
     * 
     */
	public static final MessagingManager	DEFAULT_MANAGER	= new DefaultManager();
	
	private static final MessageFactory		DEFAULT_FACTORY	= new DefaultFactory();
	
	/**
	 * @param factoryId
	 * @return factory
	 */
	public static final MessageFactory getMessageFactory(final String factoryId) {
		final Object registered = Produce.object( MessageFactory.class, factoryId, null, null );
		if (registered == null) {
			return Messaging.DEFAULT_FACTORY;
		}
		return (MessageFactory) registered;
	}
}
