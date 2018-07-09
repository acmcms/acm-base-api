/**
 *
 */
package ru.myx.sapi.create_sapi;

import java.util.Collection;
import java.util.Set;

import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.BaseFunctionExecFullJ;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Create;

/**
 * @author myx
 *
 */
public final class Function_set extends BaseFunctionExecFullJ<Set<Object>> {
	
	
	@Override
	public final int execArgumentsAcceptable() {
		
		
		return 1;
	}

	@Override
	public final int execArgumentsDeclared() {
		
		
		return 1;
	}

	@Override
	public final int execArgumentsMinimal() {
		
		
		return 0;
	}

	@Override
	public final boolean execIsConstant() {
		
		
		return false;
	}

	@Override
	public Class<? extends Set<Object>> execResultClassJava() {
		
		
		return Convert.Any.toAny(Set.class);
	}

	@Override
	public final Set<Object> getValue(final ExecProcess ctx) throws Exception {
		
		
		final int argcount = ctx.length();
		final Set<Object> result = Create.tempSet();
		if (argcount == 0) {
			return result;
		}
		if (argcount == 1) {
			final BaseObject object = ctx.baseGetFirst(BaseObject.UNDEFINED);
			if (object == BaseObject.UNDEFINED) {
				return result;
			}
			if (object instanceof Collection<?>) {
				result.addAll((Collection<?>) object);
				return result;
			}
			{
				final BaseArray array = object.baseArray();
				/**
				 * string implements array
				 */
				if (array != null && !object.baseIsPrimitive()) {
					for (int i = array.length() - 1; i >= 0; --i) {
						result.add(array.get(i));
					}
					return result;
				}
			}
			{
				final Object single = object.baseValue();
				if (single != object) {
					if (single instanceof Collection<?>) {
						result.addAll((Collection<?>) single);
						return result;
					}
					if (single instanceof Object[]) {
						final Object[] initial = (Object[]) single;
						for (int i = initial.length - 1; i >= 0; --i) {
							result.add(initial[i]);
						}
						return result;
					}
				}
				result.add(single);
				return result;
			}
		}
		throw new IllegalArgumentException("Illegal usage!");
	}

}
