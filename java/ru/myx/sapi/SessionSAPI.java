package ru.myx.sapi;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseHostDataSubstitution;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.reflect.Reflect;
import ru.myx.ae3.reflect.ReflectionExplicit;
import ru.myx.ae3.reflect.ReflectionManual;

/**
 * @author myx
 * 
 */
@ReflectionManual
public final class SessionSAPI implements BaseHostDataSubstitution<BaseObject> {
	
	
	/**
	 * The only instance of SessionSAPI class
	 */
	public static final SessionSAPI INSTANCE;

	private static final BaseObject PROTOTYPE;

	static {
		PROTOTYPE = Reflect.classToBasePrototype(SessionSAPI.class);
		INSTANCE = new SessionSAPI();
	}

	/**
	 * @param ctx
	 * @return map
	 */
	public static final BaseObject getData(final ExecProcess ctx) {
		
		
		return Context.getSessionData(ctx);
	}

	/**
	 * @param ctx
	 * @return map
	 */
	@ReflectionExplicit
	public static final BaseObject getParameters(final ExecProcess ctx) {
		
		
		return Context.getSessionData(ctx);
	}

	/**
	 * TODO: search for '.SID()' and replace with .ID
	 *
	 * @param ctx
	 * @return string
	 */
	@ReflectionExplicit
	public static final String SID(final ExecProcess ctx) {
		
		
		return Context.getSessionId(ctx);
	}

	private SessionSAPI() {
		// prevent
	}

	@Override
	public BaseMap baseGetSubstitution() {
		
		
		return Context.getSessionData(Exec.currentProcess());
	}

	@Override
	public BaseObject basePrototype() {
		
		
		return SessionSAPI.PROTOTYPE;
	}
}
