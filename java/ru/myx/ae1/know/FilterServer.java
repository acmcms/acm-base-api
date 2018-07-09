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
import ru.myx.ae3.report.LogReceiver;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.skinner.Skinner;
import ru.myx.ae3.vfs.Entry;

/**
 * @author myx
 *
 */
public class FilterServer implements Server {

	/**
	 *
	 */
	protected final Server parent;
	
	/**
	 * @param parent
	 * @throws NullPointerException
	 */
	public FilterServer(final Server parent) throws NullPointerException {
		this.parent = parent;
	}
	
	@Override
	public boolean absorb(final ServeRequest request) {

		return this.parent.absorb(request);
	}
	
	@Override
	public Class<? extends ServeRequest> accepts() {

		return this.parent.accepts();
	}
	
	@Override
	public void close() {

		this.parent.close();
	}
	
	@Override
	public final ProgramPart createRenderer(final String identity, final Object source) {

		return this.parent.createRenderer(identity, source);
	}
	
	@Override
	public AccessUser<?>
			ensureAuthorization(final int state/*
												 * , final BaseObject parameters
												 */) {

		return this.parent.ensureAuthorization(state/* , parameters */);
	}
	
	@Override
	public String fixLocation(final ExecProcess ctx, final String path, final boolean absolute) {

		return this.parent.fixLocation(ctx, path, absolute);
	}
	
	@Override
	public String fixUrl(final String url) {

		return this.parent.fixUrl(url);
	}
	
	@Override
	public String fixUrl(final String url, final String language) {

		return this.parent.fixUrl(url, language);
	}
	
	@Override
	public AccessManager getAccessManager() {

		return this.parent.getAccessManager();
	}
	
	@Override
	public final CacheL3<Object> getCache() {

		return this.parent.getCache();
	}
	
	@Override
	public final ControlActor<?>[] getCommonActors(final String path) {

		return this.parent.getCommonActors(path);
	}
	
	@Override
	public AccessPermission[] getCommonPermissions() {

		return this.parent.getCommonPermissions();
	}
	
	@Override
	public Map<String, Enumeration<Connection>> getConnections() {

		return this.parent.getConnections();
	}
	
	@Override
	public String getControlBase() {

		return this.parent.getControlBase();
	}
	
	@Override
	public ControlActor<?> getControlQuickActor() {

		return this.parent.getControlQuickActor();
	}
	
	@Override
	public ControlNode<?> getControlRoot() {

		return this.parent.getControlRoot();
	}
	
	@Override
	public String[] getControlSharePoints() {

		return this.parent.getControlSharePoints();
	}
	
	@Override
	public ControlNode<?> getControlShareRoot() {

		return this.parent.getControlShareRoot();
	}
	
	@Override
	public String getControlShareRootLocationForControlLocation(final String location) {

		return this.parent.getControlShareRootLocationForControlLocation(location);
	}
	
	@Override
	public String getDomainId() {

		return this.parent.getDomainId();
	}
	
	@Override
	public String getLanguage(final String language) {

		return this.parent.getLanguage(language);
	}
	
	@Override
	public String getLanguageDefault() {

		return this.parent.getLanguageDefault();
	}
	
	@Override
	public String[] getLanguages() {

		return this.parent.getLanguages();
	}
	
	@Override
	public final BaseObject getLookups() {

		return this.parent.getLookups();
	}
	
	@Override
	public MessagingManager getMessagingManager() {

		return this.parent.getMessagingManager();
	}
	
	@Override
	public <T> CacheL2<T> getObjectCache() {

		return this.parent.getObjectCache();
	}
	
	@Override
	public Properties getProperties() {

		return this.parent.getProperties();
	}
	
	@Override
	public String getProperty(final String key, final String defaultValue) {

		return this.parent.getProperty(key, defaultValue);
	}
	
	@Override
	public ExecProcess getRootContext() {

		return this.parent.getRootContext();
	}
	
	@Override
	public Connection getServerConnection(final String alias) {

		return this.parent.getServerConnection(alias);
	}
	
	@Override
	public long getServerStartTime() {

		return this.parent.getServerStartTime();
	}
	
	@Override
	public Skinner getSkinner(final String name) {

		return this.parent.getSkinner(name);
	}
	
	@Override
	public Collection<String> getSkinnerNames() {

		return this.parent.getSkinnerNames();
	}
	
	@Override
	public TemporaryStorage getStorage() {

		return this.parent.getStorage();
	}
	
	@Override
	public File getSystemRoot() {

		return this.parent.getSystemRoot();
	}
	
	@Override
	public TypeRegistry getTypes() {

		return this.parent.getTypes();
	}
	
	@Override
	public Entry getVfsRootEntry() {

		return this.parent.getVfsRootEntry();
	}
	
	@Override
	public String getZoneId() {

		return this.parent.getZoneId();
	}
	
	@Override
	public void logQuickTaskUsage(final String task, final BaseObject arguments) {

		this.parent.logQuickTaskUsage(task, arguments);
	}
	
	@Override
	public final void registerCommonActor(final Function<String, ControlActor<?>> actorProvider) {

		this.parent.registerCommonActor(actorProvider);
	}
	
	@Override
	public void registerCommonPermission(final AccessPermission permission) {

		this.parent.registerCommonPermission(permission);
	}
	
	@Override
	public void registerEventReciever(final LogReceiver reciever) {

		this.parent.registerEventReciever(reciever);
	}
	
	@Override
	public void registerEventRecieverAudit(final LogReceiver reciever) {

		this.parent.registerEventRecieverAudit(reciever);
	}
	
	@Override
	public final String getRendererDefault() {

		return this.parent.getRendererDefault();
	}
	
	@Override
	public final void registerRendererDefault(final String rendererName) {

		this.parent.registerRendererDefault(rendererName);
	}
	
	@Override
	public String registerUser(final String guid, final String login, final String lowerCase, final String passwordToUse, final BaseObject data) throws Exception {

		return this.parent.registerUser(guid, login, lowerCase, passwordToUse, data);
	}
	
	@Override
	public Map<String, Function<Void, Object>> registrySignals() {

		return this.parent.registrySignals();
	}
	
	@Override
	public AccessManager setAccessManager(final AccessManager manager) {

		return this.parent.setAccessManager(manager);
	}
	
	@Override
	public MessagingManager setMessagingManager(final MessagingManager manager) {

		return this.parent.setMessagingManager(manager);
	}
	
	@Override
	public TemporaryStorage setStorage(final TemporaryStorage storage) {

		return this.parent.setStorage(storage);
	}
}
