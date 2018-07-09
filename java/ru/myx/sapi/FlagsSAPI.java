package ru.myx.sapi;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseHostFilterSubstitution;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.reflect.Reflect;

/**
 * Title: ae1 Base definitions Description: Copyright: Copyright (c) 2001
 *
 * @author Alexander I. Kharitchev
 * @version 1.0
 */

public final class FlagsSAPI extends BaseHostFilterSubstitution<BaseObject> {
	
	
	private static final BaseObject PROTOTYPE = Reflect.classToBasePrototype(FlagsSAPI.class);
	
	@Override
	public final BaseObject baseGetSubstitution() {
		
		
		return Context.getContext(Exec.currentProcess()).getFlags();
	}
	
	@Override
	public BaseObject basePrototype() {
		
		
		return FlagsSAPI.PROTOTYPE;
	}
	
	/**
	 * @param ctx
	 * @return map
	 */
	@SuppressWarnings("static-method")
	public final BaseObject getData(final ExecProcess ctx) {
		
		
		return Context.getFlags(ctx);
	}
}
