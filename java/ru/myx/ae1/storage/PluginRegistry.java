/*
 * Created on 27.06.2004
 */
package ru.myx.ae1.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.myx.ae1.know.Server;

/** @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments */
public final class PluginRegistry {

	static final Map<String, PluginsList> RUNTIMES = new HashMap<>();

	/** @param plugin
	 */
	public static final void addPlugin(final StorageImpl plugin) {

		PluginsList result = PluginRegistry.RUNTIMES.get(plugin.getServer().getZoneId());
		if (result == null) {
			result = new PluginsList();
			PluginRegistry.RUNTIMES.put(plugin.getServer().getZoneId(), result);
		}
		result.add(plugin);
	}

	/** @return storage array */
	public static final StorageImpl[] allPlugins() {

		final PluginsList[] lists = PluginRegistry.RUNTIMES.values().toArray(new PluginsList[PluginRegistry.RUNTIMES.size()]);
		if (lists.length == 0) {
			return null;
		}
		final List<StorageImpl> result = new ArrayList<>();
		for (int i = lists.length - 1; i >= 0; --i) {
			final PluginsList current = lists[i];
			if (current != null) {
				result.addAll(current.getPlugins());
			}
		}
		return result.toArray(new StorageImpl[result.size()]);
	}

	/** @param server
	 * @param key
	 * @return storage */
	public static final StorageImpl getPlugin(final Server server, final String key) {

		final PluginsList result = PluginRegistry.RUNTIMES.get(server.getZoneId());
		return result == null
			? null
			: result.getPlugin(key);
	}

	/** @param server
	 * @return collection */
	public static final Set<StorageImpl> getPlugins(final Server server) {

		final PluginsList result = PluginRegistry.RUNTIMES.get(server.getZoneId());
		return result == null
			? null
			: result.getPlugins();
	}

	/** @param plugin
	 */
	public static final void removePlugin(final StorageImpl plugin) {

		final PluginsList result = PluginRegistry.RUNTIMES.get(plugin.getServer().getZoneId());
		if (result != null) {
			result.remove(plugin);
		}
	}
}
