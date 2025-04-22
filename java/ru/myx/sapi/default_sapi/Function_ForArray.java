/**
 *
 */
package ru.myx.sapi.default_sapi;

import java.util.Collection;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecStateCode;
import ru.myx.ae3.exec.ResultHandler;

/** ForArray(Array[, start, limit, step]).
 * <p>
 * Cycles trough an array. Sets 'Current' - current array item and 'CurrentIndex' - current index.
 * Array: java.util.List or a comma-separated string
 *
 *
 * @deprecated
 *
 * @author myx
 *
 *         ForArray(final Object object[, final int start[, final int limit[, final int step]]]) */
@Deprecated
public final class Function_ForArray extends BaseFunctionAbstract implements ExecCallableBoth.ExecStoreX {
	
	@Override
	public final int execArgumentsAcceptable() {
		
		return 4;
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
	public ExecStateCode execCallPrepare(final ExecProcess ctx, final BaseObject instance, final ResultHandler store, final boolean inline, final BaseArray arguments) {
		
		if (arguments == null) {
			return store.execReturnUndefined(ctx);
		}
		
		final int argumentCount = arguments.length();
		if (argumentCount == 0) {
			return store.execReturnUndefined(ctx);
		}
		
		final BaseObject object = arguments.baseGetFirst(null);
		assert object != null : "NULL java value!";
		if (object.baseIsPrimitive()) {
			return store.execReturnUndefined(ctx);
		}
		final int start = argumentCount > 1
			? arguments.baseGet(1, null).baseToJavaInteger()
			: 0;
		final int limit = argumentCount > 2
			? arguments.baseGet(2, null).baseToJavaInteger()
			: Integer.MAX_VALUE;
		final int step = argumentCount > 3
			? arguments.baseGet(3, null).baseToJavaInteger()
			: 1;
		
		if (start == limit) {
			return store.execReturnUndefined(ctx);
		}
		if (step <= 0) {
			return store.execReturnUndefined(ctx);
		}
		
		final int stackBase = ctx.ri0ASP;
		ctx.vmFrameEntryExFull();
		
		ctx.vmScopeDeriveLocals();
		ctx.contextCreateMutableBinding("First", BaseObject.TRUE, false);
		done : {
			{
				final BaseArray array = object.baseArray();
				if (array != null) {
					final int length = array.length();
					final int till = length < limit
						? length
						: limit;
					for (int i = start; i < till;) {
						ctx.contextCreateMutableBinding("Current", array.baseGet(i, BaseObject.UNDEFINED), false);
						ctx.contextCreateMutableBinding("CurrentIndex", Base.forInteger(i), false);
						i += step;
						ctx.contextCreateMutableBinding(
								"Last", //
								i >= till
									? BaseObject.TRUE
									: BaseObject.FALSE,
								false);
						
						final ExecStateCode code = ctx.vmStateFinalizeFrames(Context.sourceRender(ctx), stackBase, true);
						if (code != null) {
							return code;
						}
						ctx.contextCreateMutableBinding("First", BaseObject.FALSE, false);
					}
					break done;
				}
			}
			if (object.baseValue() instanceof Collection<?>) {
				final Object[] V = ((Collection<?>) object.baseValue()).toArray();
				final int length = V.length;
				final int till = length < limit
					? length
					: limit;
				for (int i = start; i < till;) {
					ctx.contextCreateMutableBinding("Current", Base.forUnknown(V[i]), false);
					ctx.contextCreateMutableBinding("CurrentIndex", Base.forInteger(i), false);
					i += step;
					ctx.contextCreateMutableBinding(
							"Last",
							i >= till
								? BaseObject.TRUE
								: BaseObject.FALSE,
							false);
					
					final ExecStateCode code = ctx.vmStateFinalizeFrames(Context.sourceRender(ctx), stackBase, true);
					if (code != null) {
						return code;
					}
					ctx.contextCreateMutableBinding("First", BaseObject.FALSE, false);
				}
				break done;
			}
			{
				final String[] V = object.toString().split(",");
				final int length = V.length;
				final int till = length < limit
					? length
					: limit;
				for (int i = start; i < till;) {
					ctx.contextCreateMutableBinding("Current", Base.forString(V[i].trim()), false);
					ctx.contextCreateMutableBinding("CurrentIndex", Base.forInteger(i), false);
					i += step;
					ctx.contextCreateMutableBinding(
							"Last",
							i >= till
								? BaseObject.TRUE
								: BaseObject.FALSE,
							false);
					
					final ExecStateCode code = ctx.vmStateFinalizeFrames(Context.sourceRender(ctx), stackBase, true);
					if (code != null) {
						return code;
					}
					ctx.contextCreateMutableBinding("First", BaseObject.FALSE, false);
				}
				break done;
			}
		}
		
		return ctx.vmStateFinalizeFrames(store.execReturnUndefined(ctx), stackBase, inline);
	}
	
	@Override
	public final boolean execIsConstant() {
		
		return true;
	}
	
	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return String.class;
	}
	
}
