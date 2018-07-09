package ru.myx.ae1.handle;

import java.util.HashMap;
import java.util.Map;

import ru.myx.ae1.know.Server;

/**
 * @author myx
 * 
 */
public final class ServerManagerDefault implements ServerManagerImpl {
	private final Map<String, Server>	servers	= new HashMap<>();
	
	@Override
	public Server check(final String name) {
		return this.servers.get( name );
	}
	
	@Override
	public void register(final String name, final Server server) {
		synchronized (this) {
			this.servers.put( name, server );
		}
	}
	
	@Override
	public Server server(final String name) {
		return this.servers.get( name );
	}
}
