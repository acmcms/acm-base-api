/**
 *
 */
package ru.myx.sapi.default_sapi;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseString;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecStateCode;
import ru.myx.ae3.exec.ResultHandler;
import ru.myx.ae3.help.Convert;

/** @author myx */
public final class Function_intl extends BaseFunctionAbstract implements ExecCallableBoth.NativeEX {
	
	@Override
	public final BaseString<?> callNEX(final ExecProcess context, final BaseObject instance, final BaseArray arguments) {
		
		if (!arguments.baseHasKeysOwn()) {
			throw new IllegalArgumentException("Named arguments required!");
		}
		return MultivariantString.getString(Convert.MapEntry.toString(arguments, "en", "?"), arguments);
	}
	
	@Override
	public int execArgumentsAcceptable() {
		
		return Integer.MAX_VALUE;
	}
	
	@Override
	public int execArgumentsDeclared() {
		
		return 0;
	}
	
	@Override
	public int execArgumentsMinimal() {
		
		return 1;
	}
	
	@Override
	public ExecStateCode execCallPrepare(final ExecProcess ctx, final BaseObject instance, final ResultHandler store, final boolean inline, final BaseArray arguments) {
		
		if (!arguments.baseHasKeysOwn()) {
			return ctx.vmRaise("Named arguments required!");
		}
		return store.execReturn(ctx, MultivariantString.getString(Convert.MapEntry.toString(arguments, "en", "?"), arguments));
		
	}
	
	@Override
	public boolean execHasNamedArguments() {
		
		return true;
	}
	
	/** TODO: really?????!!!!! */
	@Override
	public final boolean execIsConstant() {
		
		return true;
	}
	
	@Override
	public Class<? extends CharSequence> execResultClassJava() {
		
		return CharSequence.class;
	}
	
}
