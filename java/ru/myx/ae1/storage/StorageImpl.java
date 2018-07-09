/*
 * Created on 22.06.2004
 */
package ru.myx.ae1.storage;

import java.sql.Connection;
import java.util.Enumeration;

import ru.myx.ae1.know.Server;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.cache.CacheL2;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.jdbc.lock.LockManager;

/** @author myx */
public interface StorageImpl {

	/** @return boolean */
	public boolean areAliasesSupported();

	/** @return boolean */
	public boolean areLinksSupported();

	/** @return boolean */
	public boolean areLocksSupported();

	/** @return boolean */
	public boolean areObjectHistoriesSupported();

	/** @return boolean */
	public boolean areObjectVersionsSupported();

	/** @return boolean */
	public boolean areSchedulesSupported();

	/** @return boolean */
	public boolean areSoftDeletionsSupported();

	/** @return boolean */
	public boolean areSynchronizationsSupported();

	/** @return map */
	public BaseObject commitPrivateSettings();

	/** @return map */
	public BaseObject commitProtectedSettings();

	/** @param command
	 * @param arguments
	 * @return object */
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments);

	/** Storage common commands
	 *
	 * @return commandset */
	public ControlCommandset getCommands();

	/** @return connection source */
	public Enumeration<Connection> getConnectionSource();

	/** Storage commands for entry. key is guid.
	 *
	 * @param key
	 * @return commandset */
	public ControlCommandset getContentCommands(final String key);

	/** @return interface */
	public ModuleInterface getInterface();

	/** @return */
	public String getLocationControl();

	/** @return locker */
	public LockManager getLocker();

	/** @return string */
	public String getMnemonicName();

	/** Same as Server's cache
	 *
	 * @param <T>
	 *
	 * @return cache */
	public <T> CacheL2<T> getObjectCache();

	/** @return recycler */
	public ModuleRecycler getRecycler();

	/** @return scheduler */
	public ModuleSchedule getScheduling();

	/** @return server */
	public Server getServer();

	/** @return map */
	public BaseObject getSettingsPrivate();

	/** @return map */
	public BaseObject getSettingsProtected();

	/** @return interface */
	public ModuleInterface getStorage();

	/** @return synchronizer */
	public ModuleSynchronizer getSynchronizer();

	/** @return ready connection */
	public Connection nextConnection();
}
