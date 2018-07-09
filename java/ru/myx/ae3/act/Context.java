package ru.myx.ae3.act;

import java.util.Properties;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.access.PasswordType;
import ru.myx.ae1.handle.FactoryDispatcher;
import ru.myx.ae1.know.Server;
import ru.myx.ae1.know.ZoneServer;
import ru.myx.ae1.session.SessionManager;
import ru.myx.ae3.Engine;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.auth.InvalidCredentials;
import ru.myx.ae3.auth.LoginCheckContextBean;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseFunction;
import ru.myx.ae3.base.BaseHostEmpty;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecProcessVariable;
import ru.myx.ae3.exec.ExecStateCode;
import ru.myx.ae3.exec.ResultHandler;
import ru.myx.ae3.know.Language;
import ru.myx.ae3.produce.Produce;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.Request;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.auth.AuthUtils;

/**
 * @author myx
 */
public final class Context extends BaseHostEmpty {
	
	
	private static final ExecProcessVariable<Context> CONTEXT;

	private static final ZoneServer ROOT_SERVER;

	static final Properties SYSTEM_DEFAULT_PROPERTIES;

	static {
		CONTEXT = Exec.createProcessVariable("context", true);
		assert Context.CONTEXT != null;

		SYSTEM_DEFAULT_PROPERTIES = System.getProperties();
		assert Context.SYSTEM_DEFAULT_PROPERTIES != null;

		/**
		 * Now we'll replace default properties with smart ones
		 */
		System.setProperties(new ContextSupportProperties());
		/**
		 *
		 */
		{
			final ExecProcess ctx = Exec.getRootProcess();
			ROOT_SERVER = new DefaultServer(ctx);
			assert Context.ROOT_SERVER != null;
			assert Context.CONTEXT.baseValue(ctx) != null;
		}
		{
			final ExecProcess ctx = Context.ROOT_SERVER.getRootContext();
			assert Context.CONTEXT.baseValue(ctx) != null;
			Context.CONTEXT.baseValue(ctx).server = Context.ROOT_SERVER;
		}

		/**
		 *
		 */
		Produce.registerFactory(new FactoryDispatcher());
	}

	/**
	 * Need to use system properties as prototype - otherwise java stream
	 * handlers won't work
	 *
	 * @return
	 */
	public static Properties defaultProperties() {
		
		
		return new Properties(Context.SYSTEM_DEFAULT_PROPERTIES);
	}

	/**
	 * @param process
	 * @return
	 */
	private final static Context deriveContext(final ExecProcess process) {
		
		
		final Context context = Context.CONTEXT.baseValue(process);
		if (context == null) {
			final Context created = new Context();
			Context.CONTEXT.execSet(process, created);
			assert Context.CONTEXT.baseValue(process) != null : "Shouldn't be NULL";
			assert Context.CONTEXT.baseValue(process) == created : "Should be equal!";
			return created;
		}
		assert process != Exec.getRootProcess() : "Not in ROOT process, please!";
		if (process.getParentProcess() != null) {
			final Context created = new Context();
			created.flags = new BaseNativeObject();
			created.language = null;
			created.query = context.query;
			created.server = context.server;
			created.session = null;
			created.sessionId = context.sessionId;
			created.sessionStateDefault = context.sessionStateDefault;
			created.user = null;
			created.userId = context.userId;
			Context.CONTEXT.execSet(process, created);
			assert Context.CONTEXT.baseValue(process) != null : "Shouldn't be NULL";
			assert Context.CONTEXT.baseValue(process) == created : "Should be equal!";
			return created;
		}
		return context;
	}

	/**
	 * @param process
	 * @return
	 */
	public final static Context getContext(final ExecProcess process) {
		
		
		final Context context = Context.CONTEXT.baseValue(process);
		if (context == null) {
			final Context created = new Context();
			created.flags = new BaseNativeObject();
			Context.CONTEXT.execSet(process, created);
			assert Context.CONTEXT.baseValue(process) != null : "Shouldn't be NULL";
			assert Context.CONTEXT.baseValue(process) == created : "Should be equal!";
			return created;
		}
		return context;
	}

	/**
	 * @param process
	 * @return map
	 */
	public static final BaseObject getFlags(final ExecProcess process) {
		
		
		final Context context = Context.getContext(process);
		assert context.flags != null : "Should be created with context!";
		return context.flags;
	}

	/**
	 * @param process
	 * @return language
	 */
	public static final Language getLanguage(final ExecProcess process) {
		
		
		return Context.getContext(process).getLanguage();
	}

	/**
	 * @param process
	 * @return query
	 */
	public static ServeRequest getRequest(final ExecProcess process) {
		
		
		return Context.getContext(process).getRequest();
	}

	/**
	 * @param process
	 * @return server
	 */
	public static Server getServer(final ExecProcess process) {
		
		
		return Context.getContext(process).getServer();
	}

	/**
	 * @param process
	 * @return session data
	 */
	public static final BaseMap getSessionData(final ExecProcess process) {
		
		
		return Context.getContext(process).getSessionData();
	}

	/**
	 * @param process
	 * @return sessionId
	 */
	public static final String getSessionId(final ExecProcess process) {
		
		
		return Context.getContext(process).getSessionId();
	}

	/**
	 * @param process
	 * @return session
	 */
	public static final int getSessionState(final ExecProcess process) {
		
		
		return Context.getContext(process).getSessionState();
	}

	/**
	 * @param process
	 * @return user
	 */
	public static final AccessUser<?> getUser(final ExecProcess process) {
		
		
		assert process != null : "Process is NULL";
		return Context.getContext(process).getUser();
	}

	/**
	 * @param process
	 * @return userid
	 */
	public static final String getUserId(final ExecProcess process) {
		
		
		return Context.getContext(process).getUserId();
	}

	/**
	 * @param process
	 * @return
	 */
	public static final boolean hasSessionId(final ExecProcess process) {
		
		
		final Context context = Context.getContext(process);
		return context.sessionId != null;
	}

	/**
	 * @param process
	 */
	public static final void invalidateAuth(final ExecProcess process) {
		
		
		assert process != Exec.getRootProcess() : "Not in ROOT process, please!";
		Context.getContext(process).invalidateAuth();
	}

	/**
	 * @param process
	 */
	public static final void invalidateUser(final ExecProcess process) {
		
		
		assert process != Exec.getRootProcess() : "Not in ROOT process, please!";
		Context.getContext(process).invalidateUser();
	}

	/**
	 * @param process
	 * @param query
	 */
	public static void replaceQuery(final ExecProcess process, final ServeRequest query) {
		
		
		assert process != Exec.getRootProcess() : "Not in ROOT process, please!";
		final Context context = Context.deriveContext(process);
		if (query == null) {
			context.query = Request.ROOT_REQUEST;
			context.sessionId = null;
			context.userId = null;
		} else {
			context.query = query;
			context.sessionId = query.getSessionID();
			context.userId = query.getUserID();
		}
	}

	/**
	 * @param process
	 * @param server
	 */
	public static final void replaceServer(final ExecProcess process, final Server server) {
		
		
		assert process != Exec.getRootProcess() : "Not in ROOT process, please!";
		final Context context = Context.deriveContext(process);
		context.server = server == null
			? Context.ROOT_SERVER
			: server;
	}

	/**
	 * @param process
	 * @return state
	 */
	public static final ExecStateCode sourceRender(final ExecProcess process) {
		
		
		final BaseFunction sourceBlock = Context.getContext(process).sourceBlock;
		if (sourceBlock == null) {
			return null;
		}

		final BaseObject ra0RB = process.ra0RB;
		final ExecStateCode code = sourceBlock.execCallPrepare(process, process.rb4CT, ResultHandler.FU_BNN_NXT, true);
		if (code == null) {
			process.ra0RB = ra0RB;
		}
		return code;
	}

	/**
	 * @param process
	 * @param source
	 * @return instruction
	 */
	public static final BaseFunction sourceReplace(final ExecProcess process, final BaseFunction source) {
		
		
		final Context context = Context.getContext(process);
		try {
			return context.sourceBlock;
		} finally {
			context.sourceBlock = source;
		}
	}

	/**
	 * @param process
	 * @param flags
	 */
	public static final void useFlags(final ExecProcess process, final BaseNativeObject flags) {
		
		
		assert process != Exec.getRootProcess() : "Not in ROOT process, please!";
		final Context context = Context.getContext(process);
		context.flags = flags == null
			? new BaseNativeObject()
			: flags;
	}

	/**
	 * @param process
	 * @param flags
	 */
	public static final void useFlags(final ExecProcess process, final BaseObject flags) {
		
		
		assert process != Exec.getRootProcess() : "Not in ROOT process, please!";
		final Context context = Context.getContext(process);
		context.flags = flags == null
			? new BaseNativeObject()
			: flags;
	}

	private BaseObject flags;

	private Language language;

	private ServeRequest query;

	private Server server;

	private BaseMap session;

	private String sessionId;

	private int sessionStateDefault;

	private AccessUser<?> user;

	private String userId;

	private BaseFunction sourceBlock;

	private Context() {
		
		//
	}

	/**
	 * @param login
	 * @param password
	 * @param level
	 * @return
	 */
	private final boolean authorizeLogin(final String login, final String password, final int level) {
		
		
		final PasswordType passwordType = level < AuthLevels.AL_AUTHORIZED_HIGH
			? PasswordType.NORMAL
			: PasswordType.HIGHER;
		final AccessUser<?> user = Access.getUserByLoginCheckPassword(this.server.getAccessManager(), login, password, passwordType);
		if (user == null) {
			return false;
		}
		final String uid = user.getKey();
		this.replaceUserId(uid);
		/**
		 * So level is not higher than HIGH
		 */
		this.setSessionState(Math.min(level, AuthLevels.AL_AUTHORIZED_HIGH));
		if (Report.MODE_DEBUG) {
			Report.debug("AE1/AUTH", "login/password auth passed: request=" + this.query + ", cur_user=" + this.getUserId() + ", cur_session=" + this.getSessionId());
		}
		return true;
	}

	@Override
	public BaseObject basePrototype() {
		
		
		return null;
	}

	/**
	 * Authorization from request's user in session.
	 *
	 * Authentication of user associated with request.
	 *
	 *
	 *
	 * @param level
	 * @return false when access is explicitly prohibited
	 */
	public final boolean checkAuthorization(final int level) {
		
		
		{
			final int sessionState = this.getSessionState();
			if (level < AuthLevels.AL_AUTHORIZED_HIGHER && sessionState >= level) {
				return true;
			}
		}
		if (level == AuthLevels.AL_AUTHORIZED_AUTOMATICALLY && this.userId != null) {
			this.setSessionState(AuthLevels.AL_AUTHORIZED_AUTOMATICALLY);
			return true;
		}
		final ServeRequest query = this.getRequest();
		boolean accessGranted = false;
		if (Report.MODE_DEBUG) {
			Report.debug("AE1/AUTH", "Checking auth for: request=" + query + ", cur_user=" + this.getUserId() + ", cur_session=" + this.getSessionId());
		}
		try {
			final String authType = Base.getString(query.getParameters(), "__auth_type", "").toLowerCase();
			final BaseObject credentials = authType.length() == 0
				? AuthUtils.squeezeCredentials(query)
				: null;
			if (credentials != null) {
				final String login = Base.getString(credentials, "login", "").toLowerCase();
				assert login.length() > 0 : "login is empty!";
				final String password = Base.getString(credentials, "password", "");
				assert password.length() > 0 : "password is empty!";
				final boolean success = this.authorizeLogin(login, password, level);
				if (!success) {
					throw new InvalidCredentials();
				}
				accessGranted = true;
			} else //
			if (level < AuthLevels.AL_AUTHORIZED_HIGH) {
				if (authType.length() > 0) {
					accessGranted = this.checkExtAuthorization(query, authType, level);
				}
			}

		} catch (final AbstractReplyException e) {
			if (e.getCode() != Reply.CD_DENIED && e.getCode() != Reply.CD_UNAUTHORIZED) {
				/**
				 * any non login related response must be forwarded.
				 */
				throw e;
			}
		}
		if (!accessGranted) {
			if (level == AuthLevels.AL_AUTHORIZED_AUTOMATICALLY && this.getUserId() != null) {
				this.setSessionState(AuthLevels.AL_AUTHORIZED_AUTOMATICALLY);
				return true;
			}

			if (Report.MODE_DEBUG) {
				Report.debug("AE1/AUTH", "Sending auth to: request=" + query + ", cur_user=" + this.getUserId() + ", cur_session=" + this.getSessionId());
			}
			this.setSessionState(AuthLevels.AL_UNAUTHORIZED);
			throw Reply.exception(Reply.stringUnauthorized("SRV_AUTH", query, "auth") //
					.setSessionID(this.getSessionId())) //
			;
		}
		{
			final int sessionState = this.getSessionState();
			if (level < AuthLevels.AL_AUTHORIZED_HIGHER && sessionState >= level) {
				return true;
			}
			if (level >= AuthLevels.AL_AUTHORIZED_HIGHER && sessionState >= AuthLevels.AL_AUTHORIZED_HIGH) {
				return true;
			}
		}
		if (level == AuthLevels.AL_AUTHORIZED_AUTOMATICALLY && this.getUserId() != null) {
			this.setSessionState(AuthLevels.AL_AUTHORIZED_AUTOMATICALLY);
			return true;
		}
		return false;
	}

	private final boolean checkExtAuthorization(final ServeRequest query, final String type, final int level) {
		
		
		final BaseObject parameters = query.getParameters();
		assert parameters != null : "NULL java value";
		if (parameters.baseIsPrimitive()) {
			return false;
		}
		// final String type = Base.getString( parameters, "__auth_type", ""
		// ).toLowerCase();
		if (type.length() == 0) {
			return false;
		}
		final LoginCheckContextBean checkContext = new LoginCheckContextBean();
		final ExecProcess ctx = Exec.createProcess(null, "context authentication: " + type);
		ctx.contextCreateMutableBinding("parameters", parameters, false);
		ctx.contextCreateMutableBinding("authTypeName", Base.forString(type), false);
		ctx.contextCreateMutableBinding("checkContext", Base.forUnknown(checkContext), false);
		final boolean success = Evaluate.evaluateBoolean("require('ru.acmcms.internal/access.auth/auth-type-' + authTypeName).checkAuth(checkContext, parameters)", ctx, null);
		{
			final String userId = checkContext.getUserId();
			if (!success || userId == null) {
				final String errorText = checkContext.getErrorText();
				if (errorText != null) {
					throw new Error("AUTH fail: " + errorText);
				}
				return false;
			}
			this.replaceUserId(userId);
		}
		{
			final String sessionId = checkContext.getSessionId();
			if (sessionId != null) {
				this.replaceSessionId(sessionId);
			} else {
				this.setSessionState(Math.min(level, AuthLevels.AL_AUTHORIZED_NORMAL));
			}
		}
		if (Report.MODE_DEBUG) {
			Report.debug("AE1/AUTH", "ext auth passed: request=" + this.query + ", cur_user=" + this.getUserId() + ", cur_session=" + this.getSessionId());
		}
		return true;
	}

	/**
	 *
	 * Authorization from request's user in session.
	 *
	 * Authentication of user associated with request.
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
	 *
	 * Throws 'Access Forbidden' if login unsuccessful
	 *
	 * @param level
	 * @return
	 */
	public final AccessUser<?>
			ensureAuthorization(final int level/*
												 * , final BaseObject parameters
												 */) {
		
		
		if (!this.checkAuthorization(level)) {
			throw Reply.exception(Reply.stringForbidden(
					"SERVER-CONTEXT", //
					this.query,
					"Access denied"));
		}
		return this.getUser();
	}

	/**
	 * @return
	 */
	public final BaseObject getFlags() {
		
		
		return this.flags;
	}

	/**
	 * @return language
	 */
	public final Language getLanguage() {
		
		
		final Language language = this.language;
		if (language != null) {
			return language;
		}
		final Server server = this.server;
		if (server == null) {
			return Language.en;
		}
		final ServeRequest query = this.query;
		final String name = server.getLanguage(query == null
			? null
			: query.getLanguage());
		final Language found = Language.getLanguage(name);
		this.language = found;
		return found;
	}

	/**
	 * @return
	 */
	public ServeRequest getRequest() {
		
		
		final ServeRequest query = this.query;
		return query == null
			? Request.ROOT_REQUEST
			: query;
	}

	/**
	 * @return
	 */
	public Server getServer() {
		
		
		final Server known = this.server;
		return known == null
			? this.server = Context.ROOT_SERVER
			: known;
	}

	/**
	 * TODO: fix possible race with multiple threads running same process
	 * context
	 *
	 * @return session data
	 */
	public final BaseMap getSessionData() {
		
		
		final BaseMap session = this.session;
		if (session != null) {
			return session;
		}
		final String sessionId = this.getSessionId();
		final BaseMap created = SessionManager.session(sessionId);
		this.session = created;
		return created;
	}

	/**
	 * @return sessionId
	 */
	public final String getSessionId() {
		
		
		final String sessionId = this.sessionId;
		return sessionId != null
			? sessionId
			: (this.sessionId = Engine.createGuid());
	}

	/**
	 * @return
	 */
	public int getSessionState() {
		
		
		return this.sessionId != null
			? this.userId == null
				? this.sessionStateDefault
				: (int) Base.getInt(this.getSessionData(), this.userId, this.sessionStateDefault < AuthLevels.AL_AUTHORIZED_AUTOMATICALLY
					? AuthLevels.AL_AUTHORIZED_AUTOMATICALLY
					: this.sessionStateDefault)
			: this.userId == null
				? this.sessionStateDefault
				: this.sessionStateDefault < AuthLevels.AL_AUTHORIZED_AUTOMATICALLY
					? AuthLevels.AL_AUTHORIZED_AUTOMATICALLY
					: this.sessionStateDefault;
	}

	/**
	 * @return user
	 */
	public final AccessUser<?> getUser() {
		
		
		final AccessUser<?> user = this.user;
		if (user != null) {
			return user;
		}
		final Server server = this.server;
		if (server == null) {
			throw new NullPointerException("No server!");
		}
		final AccessManager manager = server.getAccessManager();
		if (manager == null) {
			throw new NullPointerException("No access manager!");
		}
		final String userId = this.userId;
		if (userId == null) {
			final AccessUser<?> created = manager.createUser();
			assert created != null : "User manager cannot create users!";
			this.user = created;
			this.userId = created.getKey();
			return created;
		}
		final AccessUser<?> found = manager.getUser(userId, true);
		assert found != null : "User manager cannot create users!";
		this.user = found;
		this.userId = found.getKey();
		return found;
	}

	/**
	 * @return
	 */
	public String getUserId() {
		
		
		final String userId = this.userId;
		if (userId == null) {
			final AccessUser<?> user = this.user;
			if (user == null) {
				final String created = Engine.createGuid();
				this.userId = created;
				return created;
			}
			final AccessUser<?> created = this.getUser();
			return this.userId = created.getKey();
		}
		return userId;
	}

	/**
	 * @return
	 */
	public ZoneServer getZoneServer() {
		
		
		final Server known = this.getServer();
		return known instanceof ZoneServer
			? (ZoneServer) known
			: Context.ROOT_SERVER;
	}

	/**
	 *
	 */
	public void invalidateAuth() {
		
		
		this.userId = null;
		this.user = null;
		this.session = null;
	}

	/**
	 *
	 */
	public final void invalidateUser() {
		
		
		this.userId = null;
		this.user = null;
		this.sessionId = Engine.createGuid();
		this.session = null;
	}

	/**
	 *
	 * @param sid
	 */
	public final void replaceSessionId(final String sid) {
		
		
		if (sid == null || this.sessionId == null || !sid.equals(this.sessionId)) {
			this.sessionId = sid;
			this.session = null;
		}
	}

	/**
	 * @param uid
	 */
	public final void replaceUserId(final String uid) {
		
		
		if (uid == null || this.userId == null || !uid.equals(this.userId)) {
			this.userId = uid;
			this.user = null;
		}
	}

	/**
	 * @param state
	 */
	public final void setSessionState(final int state) {
		
		
		final String userId = this.getUserId();
		final BaseMap session = this.getSessionData();
		session.baseDefine("", userId);
		session.baseDefine(userId, state);
	}

	/**
	 * @param state
	 */
	public final void setSessionStateDefault(final int state) {
		
		
		this.sessionStateDefault = state;
	}

	@Override
	public String toString() {
		
		
		return "[object " + this.baseClass() + "(" + "uid=" + this.userId + ", sid=" + this.sessionId + ")]";
	}

}
