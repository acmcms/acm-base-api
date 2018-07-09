/**
 * Created on 27.11.2002
 * 
 * myx - barachta */
package ru.myx.ae1.handle;

import ru.myx.ae1.know.Server;

/**
 * @author barachta
 * 
 * myx - barachta 
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public final class Handle {
	static ServerManagerImpl	serverManager	= new ServerManagerDefault();
	
	private static boolean		managerLock		= false;
	
	/**
	 * @param name
	 * @return server
	 */
	public static final Server checkServer(final String name) {
		return name == null
				? null
				: Handle.serverManager.check( name );
	}
	
	/**
	 * @param name
	 * @return server
	 */
	public static final Server getServer(final String name) {
		return name == null
				? null
				: Handle.serverManager.server( name );
	}
	
	/**
	 * @param impl
	 */
	public static final void managerImpl(final ServerManagerImpl impl) {
		if (Handle.managerLock) {
			throw new IllegalStateException( "Locked!" );
		}
		Handle.serverManager = impl;
	}
	
	/**
	 * 
	 */
	public static final void managerLock() {
		Handle.managerLock = true;
	}
	
	/**
	 * @param name
	 * @param server
	 */
	public static final void registerServer(final String name, final Server server) {
		Handle.serverManager.register( name, server );
	}
	
	private Handle() {
		// empty
	}
}
