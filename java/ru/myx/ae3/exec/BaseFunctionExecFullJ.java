/*
 * Created on 05.05.2006
 */
package ru.myx.ae3.exec;



import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.reflect.ReflectionHidden;

/** @author myx
 * @param <R> */
@Deprecated
public abstract class BaseFunctionExecFullJ<R> extends BaseFunctionAbstract implements ExecCallableFull {
	
	@Override
	@ReflectionHidden
	public ExecStateCode execCallImpl(final ExecProcess ctx) throws Exception {

		// assert ctx.ri0BSB == ctx.ri0ASP : "Stack disbalance before function
		// call";
		// try {
		return ctx.vmSetCallResult(Base.forUnknown(this.getValue(ctx)));
		// } finally {
		// assert ctx.ri0BSB == ctx.ri0ASP : "Stack disbalance after function
		// call";
		// }
	}
	
	@Override
	@ReflectionHidden
	public abstract Class<? extends R> execResultClassJava();

	@Override
	public BaseObject execScope() {

		/** executes in real current scope */
		return ExecProcess.GLOBAL;
	}

	/** @param process
	 * @return
	 * @throws Exception */
	@ReflectionHidden
	public abstract R getValue(ExecProcess process) throws Exception;
}
