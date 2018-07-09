/**
 *
 */
package ru.myx.ae1.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class PluginsList {

	private final Set<StorageImpl> vPlugins = new HashSet<>();

	private final Map<String, StorageImpl> hPlugins = new HashMap<>();

	final void add(final StorageImpl plugin) {

		this.vPlugins.add(plugin);
		this.hPlugins.put(plugin.getMnemonicName(), plugin);
	}

	final StorageImpl getPlugin(final Object key) {

		return this.hPlugins.get(key);
	}

	final Set<StorageImpl> getPlugins() {

		return this.vPlugins;
	}

	final void remove(final StorageImpl plugin) {

		this.vPlugins.remove(plugin);
		this.hPlugins.remove(plugin.getMnemonicName());
	}
}
