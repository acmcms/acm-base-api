/**
 *
 */
package ru.myx.sapi.default_sapi;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecStateCode;
import ru.myx.ae3.exec.ResultHandler;

/** /** For(From, Till[, Step]).
 * <p>
 * Step: default = 1
 * <p>
 * Sets CurrentIndex on every iteration.
 *
 * @deprecated
 *
 * @author myx */
@Deprecated
public final class Function_For extends BaseFunctionAbstract implements ExecCallableBoth.ExecStoreX {
	
	@Override
	public final int execArgumentsAcceptable() {
		
		return 3;
	}

	@Override
	public final int execArgumentsDeclared() {
		
		return 3;
	}

	@Override
	public final int execArgumentsMinimal() {
		
		return 2;
	}

	@Override
	public ExecStateCode execCallPrepare(final ExecProcess ctx, final BaseObject instance, final ResultHandler store, final boolean inline, final BaseArray arguments) {
		
		if (arguments == null) {
			return store.execReturnUndefined(ctx);
		}

		final int argumentCount = arguments.length();
		if (argumentCount < 2) {
			return store.execReturnUndefined(ctx);
		}
		
		final double from = arguments.baseGet(0, null).baseToNumber().doubleValue();
		final double till = arguments.baseGet(1, null).baseToNumber().doubleValue();
		final double step = argumentCount > 2
			? arguments.baseGet(2, null).baseToNumber().doubleValue()
			: 1;

		/** <code>
		System.err.println(">>>>>> For: " + from + " / " + till + " / " + step);
		 * </code> */

		if (step == 0) {
			return store.execReturnUndefined(ctx);
		}
		
		ctx.vmFrameEntryExFull();
		final int stackBase = ctx.ri0ASP;
		
		ctx.vmScopeDeriveLocals();
		ctx.contextCreateMutableBinding("First", BaseObject.TRUE, false);
		if (step > 0) {
			for (double i = from; i < till; i += step) {
				ctx.contextCreateMutableBinding("CurrentIndex", Base.forDouble(i), false);
				ctx.contextCreateMutableBinding("Last", i + step < till
					? BaseObject.FALSE
					: BaseObject.TRUE, false);

				final ExecStateCode code = ctx.vmStateFinalizeFrames(Context.sourceRender(ctx), stackBase, true);
				if (code != null) {
					/** <code>
					System.err.println(">>>>>> For: Non default code: " + code);
					 * </code> */
					return code;
				}
				ctx.contextCreateMutableBinding("First", BaseObject.FALSE, false);
			}
		} else {
			for (double i = from; i > till; i += step) {
				ctx.contextCreateMutableBinding("CurrentIndex", Base.forDouble(i), false);
				ctx.contextCreateMutableBinding("Last", i + step > till
					? BaseObject.FALSE
					: BaseObject.TRUE, false);

				final ExecStateCode code = ctx.vmStateFinalizeFrames(Context.sourceRender(ctx), stackBase, true);
				if (code != null) {
					/** <code>
					System.err.println(">>>>>> For: Non default code: " + code);
					 * </code> */
					return code;
				}
				ctx.contextCreateMutableBinding("First", BaseObject.FALSE, false);
			}
		}
		return ctx.vmStateFinalizeFrames(store.execReturnUndefined(ctx), stackBase - 1, inline);
	}

	@Override
	public final boolean execIsConstant() {
		
		return true;
	}

	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return Void.class;
	}

	@Override
	public BaseObject execScope() {
		
		/** executes in real current scope */
		return ExecProcess.GLOBAL;
	}

}
