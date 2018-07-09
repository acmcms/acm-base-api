package ru.myx.ae1.handle;

import ru.myx.ae1.know.Server;

/**
 * @author myx
 * 
 */
public interface ServerManagerImpl {
	
	/**
	 * @param name
	 * @return server
	 */
	public Server check(final String name);
	
	/**
	 * @param name
	 * @param server
	 */
	public void register(final String name, final Server server);
	
	/**
	 * @param name
	 * @return server
	 */
	public Server server(final String name);
}
