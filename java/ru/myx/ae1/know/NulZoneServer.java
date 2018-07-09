/**
 *
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
import ru.myx.ae1.sharing.Share;
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

final class NulZoneServer implements ZoneServer {
	
	
	@Override
	public boolean absorb(final ServeRequest request) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Class<? extends ServeRequest> accepts() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public void close() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public ProgramPart createRenderer(final String identity, final Object source) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public AccessUser<?>
			ensureAuthorization(final int state/*
												 * , final BaseObject parameters
												 */) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Share<?>[] filterAllowedShares(final Share<?>[] sharings) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String fixLocation(final ExecProcess process, final String path, final boolean absolute) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String fixUrl(final String url) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String fixUrl(final String url, final String language) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public AccessManager getAccessManager() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public CacheL3<Object> getCache() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public ControlActor<?>[] getCommonActors(final String path) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public AccessPermission[] getCommonPermissions() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Map<String, Enumeration<Connection>> getConnections() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String getControlBase() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public ControlNode<?> getControlNodeForShare(final String share) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public ControlActor<?> getControlQuickActor() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public ControlNode<?> getControlRoot() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String[] getControlSharePoints() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public ControlNode<?> getControlShareRoot() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String getControlShareRootLocationForControlLocation(final String location) {
		
		return location;
	}

	@Override
	public String getDomainId() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String getLanguage(final String language) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String getLanguageDefault() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String[] getLanguages() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public BaseObject getLookups() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public MessagingManager getMessagingManager() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public <T> CacheL2<T> getObjectCache() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Properties getProperties() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String getProperty(final String key, final String defaultValue) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public ExecProcess getRootContext() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Connection getServerConnection(final String alias) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public long getServerStartTime() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Share<?>[] getSharings() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Skinner getSkinner(final String name) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Collection<String> getSkinnerNames() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public TemporaryStorage getStorage() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public File getSystemRoot() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public TypeRegistry getTypes() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Entry getVfsRootEntry() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Entry getVfsZoneEntry() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Entry getVfsZoneLibEntry() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String getZoneId() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public void logQuickTaskUsage(final String task, final BaseObject arguments) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public void registerCommonActor(final Function<String, ControlActor<?>> actorProvider) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public void registerCommonPermission(final AccessPermission permission) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public void registerEventReciever(final LogReceiver reciever) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public void registerEventRecieverAudit(final LogReceiver reciever) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}
	
	@Override
	public final String getRendererDefault() {
		
		
		return null;
	}

	@Override
	public void registerRendererDefault(final String rendererName) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public String registerUser(final String guid, final String login, final String lowerCase, final String passwordToUse, final BaseObject data) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Map<String, Function<Void, Object>> registrySignals() {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Entry requireResolveVfsModule(final String moduleName) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public Entry requireResolveVfsEntry(final String pathName) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public AccessManager setAccessManager(final AccessManager manager) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public MessagingManager setMessagingManager(final MessagingManager manager) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}

	@Override
	public TemporaryStorage setStorage(final TemporaryStorage storage) {
		
		throw new UnsupportedOperationException("NULL server is used only as a flag!");
	}
}
