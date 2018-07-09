/*
 * Created on 05.05.2006
 */
package ru.myx.ae1.know;

import java.io.File;
import java.sql.Connection;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.messaging.MessagingManager;
import ru.myx.ae1.types.TypeRegistry;
import ru.myx.ae2.TemporaryStorage;
import ru.myx.ae3.access.AccessPermission;
import java.util.function.Function;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.cache.CacheL2;
import ru.myx.ae3.cache.CacheL3;
import ru.myx.ae3.control.ControlActor;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ProgramPart;
import ru.myx.ae3.flow.ObjectTarget;
import ru.myx.ae3.report.LogReceiver;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.skinner.Skinner;
import ru.myx.ae3.vfs.Entry;

/**
 * @author myx
 */
public interface Server extends ObjectTarget<ServeRequest> {

	/**
	 * NULL Server - non usable - kinda flag. It is not supposed to be default
	 * or something.
	 */
	static final Server NULL_SERVER = new NulServer();

	@Override
	boolean absorb(final ServeRequest request);

	/**
	 * @param identity
	 * @param source
	 * @return renderer
	 */
	ProgramPart createRenderer(final String identity, final Object source);

	/**
	 *
	 * AuthLevels.AL_AUTHORIZED_AUTOMATICALLY
	 *
	 * AuthLevels.AL_AUTHORIZED_3RDPARTY
	 *
	 * AuthLevels.AL_AUTHORIZED_NORMAL
	 *
	 * AuthLevels.AL_AUTHORIZED_HIGH
	 *
	 *
	 * @param state
	 * @param parameters
	 * @return non NULL user instance
	 */
	AccessUser<?>
			ensureAuthorization(final int state/*
												 * , final BaseObject parameters
												 */);

	/**
	 *
	 * Converts control tree location to an external URL considering share
	 * setups, access types and so on.
	 *
	 * @param process
	 *            current context
	 * @param path
	 *            control tree location (/aa/bb/vvxxv/....)
	 * @param absolute
	 *            true: doesn't consider current context location - same result
	 *            for any share used for current request.<br>
	 *            false: tries to make location on the same share as share used
	 *            for current request.
	 *
	 * @return string
	 */
	String fixLocation(final ExecProcess process, final String path, boolean absolute);

	/**
	 * @param url
	 * @return url
	 */
	String fixUrl(final String url);

	/**
	 * @param url
	 * @param language
	 * @return url
	 */
	String fixUrl(final String url, final String language);

	/**
	 * @return access manager
	 */
	AccessManager getAccessManager();

	/**
	 * @return
	 */
	CacheL3<Object> getCache();

	/**
	 * @param path
	 * @return actors
	 */
	ControlActor<?>[] getCommonActors(final String path);

	/**
	 * @return permissions
	 */
	AccessPermission[] getCommonPermissions();

	/**
	 * @return all jdbc connections
	 */
	Map<String, Enumeration<Connection>> getConnections();

	/**
	 * @return control base
	 */
	String getControlBase();

	/**
	 * @return actor
	 */
	ControlActor<?> getControlQuickActor();

	/**
	 * @return root node for whole domain
	 */
	ControlNode<?> getControlRoot();

	/**
	 * @return share points
	 */
	String[] getControlSharePoints();

	/**
	 * @return root node for share
	 */
	ControlNode<?> getControlShareRoot();

	/**
	 * @param location
	 * @return root location for ControlShareRoot tree found from location in
	 *         Root tree
	 */
	String getControlShareRootLocationForControlLocation(final String location);

	/**
	 * @return identifier
	 */
	String getDomainId();

	/**
	 * @param language
	 * @return valid language
	 */
	String getLanguage(final String language);

	/**
	 * @return default language
	 */
	String getLanguageDefault();

	/**
	 * @return active languages
	 */
	String[] getLanguages();

	/**
	 * @return lookups
	 */
	BaseObject getLookups();

	/**
	 * @return messaging manager
	 */
	MessagingManager getMessagingManager();

	/**
	 * @param <T>
	 *
	 * @return cache rendered
	 */
	<T> CacheL2<T> getObjectCache();

	/**
	 * @return properties
	 */
	Properties getProperties();

	/**
	 * @param key
	 * @param defaultValue
	 * @return property
	 */
	String getProperty(final String key, final String defaultValue);

	/**
	 * @return root context
	 */
	ExecProcess getRootContext();

	/**
	 * @param alias
	 * @return jdbc connection
	 */
	Connection getServerConnection(final String alias);

	/**
	 * @return date
	 */
	long getServerStartTime();

	/**
	 * @param name
	 * @return
	 */
	Skinner getSkinner(final String name);

	/**
	 * @return list
	 */
	Collection<String> getSkinnerNames();

	/**
	 * @return storage
	 */
	TemporaryStorage getStorage();

	/**
	 * @return root folder
	 */
	File getSystemRoot();

	/**
	 * @return type registry
	 */
	TypeRegistry getTypes();

	/**
	 * Domain-private vfs folder. 'zone' folder.
	 *
	 * @return
	 */
	Entry getVfsRootEntry();

	/**
	 * @return identifier
	 */
	String getZoneId();

	/**
	 * @param task
	 * @param arguments
	 */
	void logQuickTaskUsage(final String task, final BaseObject arguments);

	/**
	 * @param actorProvider
	 */
	void registerCommonActor(final Function<String, ControlActor<?>> actorProvider);

	/**
	 * @param permission
	 */
	void registerCommonPermission(final AccessPermission permission);

	/**
	 * @param reciever
	 */
	void registerEventReciever(LogReceiver reciever);

	/**
	 * @param reciever
	 */
	void registerEventRecieverAudit(LogReceiver reciever);

	/**
	 *
	 * @return
	 */
	String getRendererDefault();

	/**
	 * @param rendererName
	 */
	void registerRendererDefault(final String rendererName);

	/**
	 * TODO: move it exclusively to AcmZone, then to ru.acmcms.internal
	 * commonJs.
	 *
	 * @param guid
	 * @param login
	 * @param lowerCase
	 * @param passwordToUse
	 * @param data
	 * @return
	 * @throws Exception
	 */
	String registerUser(String guid, String login, String lowerCase, String passwordToUse, BaseObject data) throws Exception;

	/**
	 * @return signals
	 */
	Map<String, Function<Void, Object>> registrySignals();

	/**
	 * @param manager
	 * @return access manager
	 */
	AccessManager setAccessManager(AccessManager manager);

	/**
	 * @param manager
	 * @return messaging manager
	 */
	MessagingManager setMessagingManager(MessagingManager manager);

	/**
	 * @param storage
	 * @return storage
	 */
	TemporaryStorage setStorage(final TemporaryStorage storage);
}
