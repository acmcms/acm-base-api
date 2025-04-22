/**
 *
 */
package ru.myx.sapi.default_sapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecStateCode;
import ru.myx.ae3.exec.ResultHandler;
import ru.myx.util.EntrySimple;

/** ForHash(Hash,Comparator).
 * <p>
 * Cycles trough an array. Sets 'Current' - current array item and 'CurrentIndex' - current index.
 * Array: java.util.List or a comma-separated string
 *
 * @deprecated
 *
 * @author myx */
@Deprecated
public final class Function_ForHash extends BaseFunctionAbstract implements ExecCallableBoth.ExecStoreX {

	@Override
	public final int execArgumentsAcceptable() {

		return 2;
	}

	@Override
	public final int execArgumentsDeclared() {

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
		
		final BaseObject map = arguments.baseGetFirst(null);
		if (map == null || map.baseIsPrimitive()) {
			return store.execReturnUndefined(ctx);
		}
		
		ctx.vmFrameEntryExFull();
		final int stackBase = ctx.ri0ASP;
		
		ctx.vmScopeDeriveLocals();

		@SuppressWarnings("unchecked")
		final Comparator<Map.Entry<?, ?>> comparator = argumentCount > 1
			? (Comparator<Map.Entry<?, ?>>) arguments.baseGet(1, null).baseValue()
			: null;
		
		if (comparator == null) {
			/** Base.keysPrimitive( map ) - comparator absent - collective keys */
			final Iterator<? extends BasePrimitive<?>> iter = map.baseKeysOwnPrimitive();
			for (int counter = 0; iter.hasNext(); counter++) {
				final BasePrimitive<?> key = iter.next();
				ctx.contextCreateMutableBinding(
						"First", //
						counter == 0
							? BaseObject.TRUE
							: BaseObject.FALSE,
						false);
				ctx.contextCreateMutableBinding(
						"Current", //
						map.baseGet(key, BaseObject.UNDEFINED),
						false);
				ctx.contextCreateMutableBinding(
						"CurrentKey", //
						key,
						false);
				ctx.contextCreateMutableBinding(
						"CurrentIndex", //
						Base.forInteger(counter),
						false);
				ctx.contextCreateMutableBinding(
						"Last", //
						iter.hasNext()
							? BaseObject.FALSE
							: BaseObject.TRUE,
						false);
				
				final ExecStateCode code = ctx.vmStateFinalizeFrames(Context.sourceRender(ctx), stackBase, true);
				if (code != null) {
					return code;
				}
			}
		} else {
			final List<Map.Entry<BasePrimitive<?>, BaseObject>> temp = new ArrayList<>();
			/** map.baseKeysOwnPrimitive() - comparator present - only own keys */
			for (final Iterator<? extends BasePrimitive<?>> iter = map.baseKeysOwnPrimitive(); iter.hasNext();) {
				final BasePrimitive<?> key = iter.next();
				temp.add(new EntrySimple<BasePrimitive<?>, BaseObject>(key, map.baseGet(key, BaseObject.UNDEFINED)));
			}

			@SuppressWarnings("unchecked")
			final Map.Entry<BasePrimitive<?>, BaseObject>[] entries = temp.toArray(new Map.Entry[temp.size()]);

			Arrays.sort(entries, comparator);
			for (int i = 0; i < entries.length; ++i) {
				final Map.Entry<BasePrimitive<?>, BaseObject> current = entries[i];
				ctx.contextCreateMutableBinding(
						"First",
						i == 0
							? BaseObject.TRUE
							: BaseObject.FALSE,
						false);
				ctx.contextCreateMutableBinding("Current", current.getValue(), false);
				ctx.contextCreateMutableBinding("CurrentKey", current.getKey(), false);
				ctx.contextCreateMutableBinding("CurrentIndex", Base.forInteger(i), false);
				ctx.contextCreateMutableBinding(
						"Last",
						i + 1 < entries.length
							? BaseObject.FALSE
							: BaseObject.TRUE,
						false);
				
				final ExecStateCode code = ctx.vmStateFinalizeFrames(Context.sourceRender(ctx), stackBase, true);
				if (code != null) {
					return code;
				}
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

		return String.class;
	}

}
