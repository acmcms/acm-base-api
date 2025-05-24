package ru.myx.ae1.handle;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.myx.ae1.know.Server;

/** @author myx */
public final class ServerManagerDefault implements ServerManagerImpl {

	private final Map<String, Server> servers = new HashMap<>();
	
	@Override
	public Server check(final String name) {

		return this.servers.get(name);
	}
	
	@Override
	public Collection<String> knownDomainNames() {

		return Collections.emptyList();
	}
	
	@Override
	public Collection<String> knownServerNames() {

		return Collections.unmodifiableSet(this.servers.keySet());
	}
	
	@Override
	public void register(final String name, final Server server) {

		synchronized (this) {
			this.servers.put(name, server);
		}
	}
	
	@Override
	public Server server(final String name) {

		return this.servers.get(name);
	}
}
