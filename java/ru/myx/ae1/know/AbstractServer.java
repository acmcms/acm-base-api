/*
 * Created on 05.05.2006
 */
package ru.myx.ae1.know;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.control.FilterNode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.control.status.NodeStatusProvider;
import ru.myx.ae1.messaging.Messaging;
import ru.myx.ae1.messaging.MessagingManager;
import ru.myx.ae1.provide.ProvideStatus;
import ru.myx.ae1.types.TypeRegistry;
import ru.myx.ae2.TemporaryStorage;
import ru.myx.ae3.Engine;
import ru.myx.ae3.access.AccessPermission;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseJoined;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.cache.Cache;
import ru.myx.ae3.cache.CacheFactory;
import ru.myx.ae3.cache.CacheL2;
import ru.myx.ae3.cache.CacheL3;
import ru.myx.ae3.cache.CacheType;
import ru.myx.ae3.control.ControlActor;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.eval.LanguageImpl;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.Instruction;
import ru.myx.ae3.exec.ProgramAssembly;
import ru.myx.ae3.exec.ProgramPart;
import ru.myx.ae3.help.Create;
import ru.myx.ae3.know.Language;
import ru.myx.ae3.produce.Produce;
import ru.myx.ae3.report.LogReceiver;
import ru.myx.ae3.report.ReceiverMultiple;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.skinner.SkinScanner;
import ru.myx.ae3.skinner.Skinner;
import ru.myx.ae3.status.StatusProviderFiller;
import ru.myx.ae3.status.StatusRegistry;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.EntryContainer;
import ru.myx.ae3.vfs.Storage;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.filesystem.StorageImplFilesystem;
import ru.myx.ae3.vfs.ram.StorageImplMemory;
import ru.myx.ae3.vfs.status.StorageImplStatus;

/** @author myx */
public abstract class AbstractServer implements Server {

	private static final String[] defaultLanguages = {
			"en"
	};

	private static final Object STR_NODE_TITLE = MultivariantString.getString("Server status", Collections.singletonMap("ru", "Статус сервера"));

	static {
		Produce.registerFactory(new CommandCreateFactory());
	}

	/**
	 *
	 */
	protected final long created = Engine.fastTime();

	private final ReceiverMultiple eventRegistryAudit;

	private final ReceiverMultiple eventRegistryLog;

	/**
	 *
	 */
	protected File systemRoot;

	AccessManager accessManager = Access.DEFAULT_MANAGER;

	MessagingManager messagingManager = Messaging.DEFAULT_MANAGER;

	TemporaryStorage storage = null;

	final ThreadGroup threadGroup;

	private CacheL2<Object> cache;

	private CacheL3<Object> cacheL3 = null;

	private final Set<Function<String, ControlActor<?>>> commonActors = new HashSet<>();

	private final List<AccessPermission> commonPermissions = new ArrayList<>();

	private final ControlNode<?> controlRoot;

	private String defaultRendererName = null;

	/**
	 *
	 */
	protected final String domainId;

	/**
	 *
	 */
	protected final String zoneId;

	/**
	 *
	 */
	protected final BaseObject lookups;

	private final Properties properties;

	private final Map<String, Function<Void, Object>> registrySignals;

	/**
	 *
	 */
	protected final Entry vfsRoot;

	private final ExecProcess rootProcess;

	private final StatusRegistry statusRegistry;

	/** @param id
	 * @param mainDomain
	 * @param context
	 */
	protected AbstractServer(final String id, final String mainDomain, final ExecProcess context) {

		this.properties = new Properties(Context.defaultProperties());

		assert id != null : "NULL server id";
		assert id.length() > 0 : "Empty server id";
		assert this.isRootServer() == "DEFAULT".equals(id) : "Only root server can and must have DEFAULT id!";

		this.zoneId = id;
		this.domainId = mainDomain;

		this.rootProcess = Exec.createProcess(context, "Server, id=" + id + ", domain=" + mainDomain);

		// final BaseObject global = this.rootProcess.ri10GV =
		// BaseMap.create(null).putAppendAllOwnProperties(ExecProcess.GLOBAL);

		final BaseObject global = this.rootProcess.ri10GV = new BaseJoined(null, BaseMap.create(null), ExecProcess.GLOBAL);

		this.rootProcess.rb7FV = BaseMap.create(global);

		Context.replaceServer(this.rootProcess, this);

		this.threadGroup = this.createRootThreadGroup();

		{
			this.registrySignals = Create.tempMap();
			this.registrySignals.put("clear_cache", new SignalClearCache());
		}

		{
			final BaseNativeObject lookups = new BaseNativeObject();
			this.lookups = lookups//
					.putAppend("System.AllLookups", lookups)//
					.putAppend("System.Languages", Know.SYSTEM_LANGUAGES)//
					.putAppend("System.AllLanguages", Language.ALL)//
					.putAppend("System.AllGroups", Access.GROUPS)//
					.putAppend("System.AllHmUsers", Access.HM_USERS)//
					.putAppend("System.AllPools", Know.ALL_POOLS);
		}

		{
			final boolean rootServer = this.isRootServer() || "DEFAULT".equals(id);

			final ReceiverMultiple recieverLog = new ReceiverMultiple(
					Report.createReceiver(
							rootServer
								? null
								: id + "-log"));
			this.eventRegistryLog = recieverLog;

			final ReceiverMultiple recieverAudit = new ReceiverMultiple(
					Report.createReceiver(
							rootServer
								? null
								: id + "-audit"));
			this.eventRegistryAudit = recieverAudit;

			recieverLog.register(new ErrorInformer(this));
			recieverAudit.event("SERVER", "START", "server (id=" + id + ", domainId=" + mainDomain + ", idc=" + System.identityHashCode(this) + ", audit) is started");
			if (recieverLog != recieverAudit) {
				recieverLog.event("SERVER", "START", "server (id=" + id + ", domainId=" + mainDomain + ", idc=" + System.identityHashCode(this) + ", log) is started");
			}
			final BaseObject baseAudit = Base.forUnknown(recieverAudit);
			final BaseObject baseLog = Base.forUnknown(recieverLog);

			global.baseDefine("$reportAudit", baseAudit, BaseProperty.ATTRS_MASK_NNN);
			global.baseDefine("$reportLog", baseLog, BaseProperty.ATTRS_MASK_NNN);
			assert global.baseGet("$reportAudit", BaseObject.UNDEFINED) == baseAudit : "Can't install logger!";
			assert global.baseGet("$reportLog", BaseObject.UNDEFINED) == baseLog : "Can't install logger!";
		}

		this.statusRegistry = new StatusRegistry(id);
		this.vfsRoot = this.createVfsRoot(id);
		this.controlRoot = new NodeControlRoot(this);

		{
			final StatusProviderFiller status = new StatusProviderFiller(//
					id, //
					"Server: " + this,
					this.statusRegistry//
			);
			StatusRegistry.ROOT_REGISTRY.register(status);

			global.baseDefine(ProvideStatus.REGISTRY_CONTEXT_KEY, Base.forUnknown(this.statusRegistry));

			this.getControlRoot().bind(
					new NodeStatusProvider(//
							new StatusProviderFiller("srvstate", AbstractServer.STR_NODE_TITLE, this.statusRegistry)//
					)//
			);
		}
	}

	@Override
	public Class<? extends ServeRequest> accepts() {

		return ServeRequest.class;
	}

	@Override
	public void close() {

		// ignore
	}

	@Override
	public final ProgramPart createRenderer(final String identity, final Object source) {

		if (source == null || source == BaseObject.UNDEFINED) {
			return null;
		}
		if (source instanceof ProgramPart) {
			return (ProgramPart) source;
		}
		if (source instanceof Instruction) {
			final ProgramAssembly assembly = new ProgramAssembly();
			try {
				assembly.addInstruction((Instruction) source);
			} catch (final RuntimeException e) {
				throw e;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			return assembly.toProgram(0);
		}
		final String sourceCode = String.valueOf(source).trim();
		if (sourceCode.length() == 0) {
			return null;
		}
		final LanguageImpl plugin = Evaluate.getLanguageImpl(this.defaultRendererName);
		if (plugin == null) {
			Report.warning("SRV/RENDER", "No default renderer plugin found for a renderer object creation request.");
			return null;
		}
		try {
			return Evaluate.compileProgram(plugin, identity, sourceCode);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private final ThreadGroup createRootThreadGroup() {

		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		for (; tg.getParent() != null; tg = tg.getParent()) {
			// ignore
		}
		return new ThreadGroup(tg, "ACM-SRV / " + this.zoneId + "/" + this.domainId);
	}

	private Entry createVfsRoot(final String id) {

		final Entry vfsRoot = Storage.createRoot(StorageImplMemory.create(id));
		/** FIXME: when HIDDEN reference won't get to DAV listings, etc... <code>
		{
			final Entry internal = vfs.relative( ".internal", TreeLinkType.HIDDEN_TREE_REFERENCE );
			assert internal != null : "Shouldn't be equal to NULL!";
			Storage.mount( internal, "root", TreeLinkType.HIDDEN_TREE_REFERENCE, Storage.ROOT );
		}
		 * </code> */
		if (this.getRootContext() != Exec.getRootProcess()) {
			final Entry storage = Storage.getRoot(null).relative("storage", null);
			if (storage != null) {
				assert storage.isExist() : "Must exist, value is not NULL, but requested with NULL default tree mode, entry=" + storage + ", class=" + storage.getClass().getName();
				final EntryContainer zone = storage.relativeFolderEnsure("zones/" + id);
				Storage.mount(vfsRoot, "storage", TreeLinkType.PUBLIC_TREE_REFERENCE, zone);
			} else {
				Report.warning("ACM-SRV", "vfs /storage is not available, zone is not mounted!");
			}
		}
		{
			final Entry acm = vfsRoot.relativeFolderEnsure("acm");
			assert acm != null : "Should not return NULL when mode is not NULL";
			Storage.mount(acm, "public", TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.createRoot(new StorageImplFilesystem(Engine.PATH_PUBLIC, true)));
		}
		{
			Storage.mount(vfsRoot, "status", TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.createRoot(new StorageImplStatus(this.statusRegistry)));
		}
		return vfsRoot;
	}

	@Override
	public AccessUser<?> ensureAuthorization(final int level/* , final BaseObject parameters */) {

		final ExecProcess process = Exec.currentProcess();
		return Context.getContext(process).ensureAuthorization(level/* , parameters */);
	}

	@Override
	public String fixLocation(final ExecProcess ctx, final String path, final boolean absolute) {

		return path;
	}

	@Override
	public String fixUrl(final String url) {

		return url;
	}

	@Override
	public String fixUrl(final String url, final String language) {

		return url;
	}

	@Override
	public AccessManager getAccessManager() {

		return this.accessManager;
	}

	/** @return cache */
	@Override
	public final CacheL3<Object> getCache() {

		if (this.cacheL3 != null) {
			return this.cacheL3;
		}
		synchronized (this) {
			return this.cacheL3 == null
				? this.cacheL3 = CacheFactory.createL3()
				: this.cacheL3;
		}
	}

	@Override
	public final ControlActor<?>[] getCommonActors(final String path) {

		final List<ControlActor<?>> result = new ArrayList<>();
		for (final Function<String, ControlActor<?>> provider : this.commonActors) {
			try {
				final ControlActor<?> actor = provider.apply(path);
				if (actor != null) {
					result.add(actor);
				}
			} catch (final Throwable t) {
				Report.exception("ACM-SRV", "error obtaining common container", t);
			}
		}
		return result.toArray(new ControlActor<?>[result.size()]);
	}

	/** @return permissions */
	@Override
	public final AccessPermission[] getCommonPermissions() {

		return this.commonPermissions.isEmpty()
			? null
			: (AccessPermission[]) this.commonPermissions.toArray(new AccessPermission[this.commonPermissions.size()]);
	}

	@Override
	public Map<String, Enumeration<Connection>> getConnections() {

		return null;
	}

	@Override
	public String getControlBase() {

		return "/";
	}

	@Override
	public ControlActor<?> getControlQuickActor() {

		return null;
	}

	@Override
	public ControlNode<?> getControlRoot() {

		return this.controlRoot;
	}

	@Override
	public String[] getControlSharePoints() {

		return null;
	}

	@Override
	public ControlNode<?> getControlShareRoot() {

		final ControlNode<?> root = new AbstractNode() {

			{
				this.setAttributeIntern("id", "root");
				this.setAttributeIntern("title", "/");
			}

			@Override
			public final String getLocationControl() {

				return "/";
			}
		};
		final String[] paths = this.getControlSharePoints();
		if (paths != null) {
			final ControlNode<?> controlRoot = this.getControlRoot();
			for (final String path : paths) {
				if (path.length() <= 1) {
					continue;
				}
				final ControlNode<?> share = Control.relativeNode(controlRoot, path);
				if (share != null) {
					root.bind(new FilterNode<FilterNode<?>>(share) {

						@Override
						public String getLocationControl() {

							return path;
						}
					});
				}
			}
		}
		return root;
	}

	@Override
	public String getControlShareRootLocationForControlLocation(final String location) {

		final String[] paths = this.getControlSharePoints();
		if (paths != null) {
			final ControlNode<?> controlRoot = this.getControlRoot();
			String found = null;
			for (final String path : paths) {
				if (path.length() <= 1) {
					continue;
				}
				if (!location.startsWith(path)) {
					continue;
				}
				final ControlNode<?> share = Control.relativeNode(controlRoot, path);
				if (share != null) {
					final String candidate = '/' + share.getKey() + '/' + location.substring(path.length());
					if (found == null || found.length() < candidate.length()) {
						found = candidate;
					}
				}
			}
			if (found != null) {
				return found;
			}
		}
		return location;
	}

	@Override
	public final String getDomainId() {

		return this.domainId;
	}

	@Override
	public String getLanguage(final String hint) {

		if (hint == null) {
			return this.getLanguageDefault();
		}
		final String[] languages = this.getLanguages();
		if (languages == null) {
			return this.getLanguageDefault();
		}
		for (int i = languages.length - 1; i >= 0; --i) {
			if (hint.equals(languages[i])) {
				return hint;
			}
		}
		return this.getLanguageDefault();
	}

	@Override
	public String getLanguageDefault() {

		return "en";
	}

	@Override
	public String[] getLanguages() {

		return AbstractServer.defaultLanguages;
	}

	@Override
	public final BaseObject getLookups() {

		return this.lookups;
	}

	@Override
	public MessagingManager getMessagingManager() {

		return this.messagingManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T> CacheL2<T> getObjectCache() {

		if (this.cache == null) {
			synchronized (this) {
				if (this.cache == null) {
					/** Just a cheat (dead code) */
					this.getCache();
					if (this.cacheL3 != null) {
						return (CacheL2<T>) (this.cache = this.cacheL3.getCacheL2("rendered"));
					}
					/**
					 *
					 */
					this.cache = Cache.createL2(this.getZoneId() + "-rendered", CacheType.FAST_JAVA_SOFT);
				}
			}
		}
		return (CacheL2<T>) this.cache;
	}

	@Override
	public final Properties getProperties() {

		return this.properties;
	}

	@Override
	public final String getProperty(final String key, final String defaultValue) {

		return this.getProperties().getProperty(key, defaultValue);
	}

	@Override
	public final String getRendererDefault() {

		return this.defaultRendererName;
	}

	@Override
	public final ExecProcess getRootContext() {

		return this.rootProcess;
	}

	@Override
	public Connection getServerConnection(final String alias) {

		final Map<String, Enumeration<Connection>> connections = this.getConnections();
		if (connections == null) {
			return null;
		}
		final Enumeration<Connection> source = connections.get(alias);
		return source == null
			? null
			: source.nextElement();
	}

	@Override
	public final long getServerStartTime() {

		return this.created;
	}

	@Override
	public Skinner getSkinner(final String name) {

		return SkinScanner.getSystemSkinner(name);
	}

	@Override
	public Collection<String> getSkinnerNames() {

		final Set<String> names = Create.tempSet();
		return SkinScanner.getSystemSkinnerNames(names);
	}

	@Override
	public TemporaryStorage getStorage() {

		return this.storage;
	}

	@Override
	public final File getSystemRoot() {

		if (this.systemRoot != null) {
			return this.systemRoot;
		}
		synchronized (this) {
			if (this.systemRoot == null) {
				this.systemRoot = new File(new File(System.getProperty("serve.root")), this.getZoneId());
			}
			return this.systemRoot;
		}
	}

	@Override
	public TypeRegistry getTypes() {

		return null;
	}

	@Override
	public Entry getVfsRootEntry() {

		return this.vfsRoot;
	}

	@Override
	public final String getZoneId() {

		return this.zoneId;
	}

	/** @return */
	@SuppressWarnings("static-method")
	protected boolean isRootServer() {

		return false;
	}

	@Override
	public void logQuickTaskUsage(final String task, final BaseObject arguments) {

		// empty
	}

	@Override
	public final void registerCommonActor(final Function<String, ControlActor<?>> actorProvider) {

		this.commonActors.add(actorProvider);
	}

	/** @param permission
	 */
	@Override
	public final void registerCommonPermission(final AccessPermission permission) {

		this.commonPermissions.add(permission);
	}

	@Override
	public void registerEventReciever(final LogReceiver reciever) {

		this.eventRegistryLog.register(reciever);
	}

	@Override
	public void registerEventRecieverAudit(final LogReceiver reciever) {

		this.eventRegistryAudit.register(reciever);
	}

	@Override
	public final void registerRendererDefault(final String rendererName) {

		this.defaultRendererName = rendererName;
	}

	@Override
	public String registerUser(final String guid, final String login, final String lowerCase, final String passwordToUse, final BaseObject data) throws Exception {

		throw new UnsupportedOperationException("Oops!");
	}

	@Override
	public Map<String, Function<Void, Object>> registrySignals() {

		return this.registrySignals;
	}

	@Override
	public AccessManager setAccessManager(final AccessManager manager) {

		if (this.accessManager == Access.DEFAULT_MANAGER) {
			this.accessManager = manager;
			return manager;
		}
		throw new IllegalStateException("Access manager is already set!");
	}

	@Override
	public MessagingManager setMessagingManager(final MessagingManager manager) {

		if (this.messagingManager == Messaging.DEFAULT_MANAGER) {
			this.messagingManager = manager;
			return manager;
		}
		throw new IllegalStateException("Messaging manager is already set!");
	}

	@Override
	public TemporaryStorage setStorage(final TemporaryStorage storage) {

		if (this.storage == null) {
			return this.storage = storage;
		}
		throw new IllegalStateException("Storage is already set, zoneId=" + this.zoneId + "!");
	}

	@Override
	public String toString() {

		return "SRVCTX(" + this.zoneId + ')';
	}

}
