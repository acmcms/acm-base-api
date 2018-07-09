/**
 *
 */
package ru.myx.sapi.runtime_sapi;

import java.util.Iterator;
import java.util.Map;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseString;
import ru.myx.ae3.exec.ExecCallableFull;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecStateCode;

/**
 *
 * @deprecated
 *
 * @author myx
 *
 */
@Deprecated
public final class Function_listLookup extends BaseFunctionAbstract implements ExecCallableFull {
	
	
	@Override
	public final int execArgumentsAcceptable() {
		
		
		return 3;
	}
	
	@Override
	public int execArgumentsDeclared() {
		
		
		return 1;
	}
	
	@Override
	public final int execArgumentsMinimal() {
		
		
		return 1;
	}
	
	@Override
	public ExecStateCode execCallImpl(final ExecProcess ctx) {
		
		
		final BaseObject lookup = ctx.baseGetFirst(null);
		if (lookup == null || lookup.baseIsPrimitive()) {
			return null;
		}
		final String keyName = Base.getString(ctx, 1, "Key");
		final String valueName = Base.getString(ctx, 2, "Title");
		final Iterator<String> iterable = lookup.baseKeysOwn();
		if (iterable == null) {
			return ctx.vmSetCallResult(BaseString.EMPTY);
		}
		ctx.vmScopeDeriveLocals();
		final Map<String, Object> acceptor = new MapRenderSource(ctx, keyName, valueName);
		while (iterable.hasNext()) {
			final String key = iterable.next();
			acceptor.put(key, lookup.baseGet(key, BaseObject.UNDEFINED));
		}
		return ctx.vmSetCallResultString(acceptor.toString());
	}
	
	@Override
	public final boolean execIsConstant() {
		
		
		return true;
	}
	
	@Override
	public Class<? extends Object> execResultClassJava() {
		
		
		return String.class;
	}
	
	@Override
	public BaseObject execScope() {
		
		
		/**
		 * executes in real current scope
		 */
		return ExecProcess.GLOBAL;
	}
	
}
