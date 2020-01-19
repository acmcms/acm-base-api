/**
 *
 */
package ru.myx.sapi.create_sapi;

import java.util.Collection;
import java.util.Set;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Create;

/** @author myx */
public final class Function_set extends BaseFunctionAbstract implements ExecCallableBoth.NativeJ1 {

	@Override
	public final BaseObject callNJ1(final BaseObject instance, final BaseObject argument) {

		final Set<Object> result = Create.tempSet();
		
		if (argument == BaseObject.UNDEFINED) {
			return Base.forUnknown(result);
		}

		if (argument instanceof Collection<?>) {
			result.addAll((Collection<?>) argument);
			return Base.forUnknown(result);
		}
		{
			final BaseArray array = argument.baseArray();
			/** string implements array */
			if (array != null && !argument.baseIsPrimitive()) {
				for (int i = array.length() - 1; i >= 0; --i) {
					result.add(array.get(i));
				}
				return Base.forUnknown(result);
			}
		}
		{
			final Object single = argument.baseValue();
			if (single != argument) {
				if (single instanceof Collection<?>) {
					result.addAll((Collection<?>) single);
					return Base.forUnknown(result);
				}
				if (single instanceof Object[]) {
					final Object[] initial = (Object[]) single;
					for (int i = initial.length - 1; i >= 0; --i) {
						result.add(initial[i]);
					}
					return Base.forUnknown(result);
				}
			}
			result.add(single);
			return Base.forUnknown(result);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public final int execArgumentsMinimal() {
		
		/** non default return value */
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
	
}
