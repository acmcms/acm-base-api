/**
 *
 */
package ru.myx.sapi.default_sapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.exec.BaseFunctionExecFullJ;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;

/**
 * @author myx
 *
 */
public final class Function_mapPutRecursive extends BaseFunctionExecFullJ<Map<String, Object>> {
	
	
	private final Object clone(final Object value) {
		
		
		if (value instanceof Map<?, ?>) {
			final BaseMap result = BaseObject.createObject();
			final Map<String, Object> source = Convert.Any.toAny(value);
			this.copyDeep(source, result);
			return result;
		}
		if (value instanceof List<?>) {
			final List<Object> result = new ArrayList<>();
			final List<Object> source = Convert.Any.toAny(value);
			for (final Object entry : source) {
				result.add(this.clone(entry));
			}
			return result;
		}
		if (value instanceof Value<?>) {
			final Object baseValue = ((Value<?>) value).baseValue();
			return baseValue != value
				? this.clone(baseValue)
				: value;
		}
		return value;
	}

	private final void copyDeep(final Map<String, Object> source, final Map<String, Object> target) {
		
		
		for (final String key : source.keySet()) {
			final Object value = source.get(key);
			target.put(key, this.clone(value));
		}
	}

	@Override
	public final int execArgumentsAcceptable() {
		
		
		return 2;
	}

	@Override
	public int execArgumentsDeclared() {
		
		
		return 2;
	}

	@Override
	public final int execArgumentsMinimal() {
		
		
		return 2;
	}

	@Override
	public Class<? extends Map<String, Object>> execResultClassJava() {
		
		
		return Convert.Any.toAny(Map.class);
	}

	@Override
	@Deprecated
	public final Map<String, Object> getValue(final ExecProcess context) {
		
		
		final Object targetObject = context.get(0);
		if (targetObject instanceof Map<?, ?>) {
			final Map<String, Object> target = Convert.Any.toAny(targetObject);
			final Object sourceObject = context.get(1);
			if (sourceObject instanceof Map<?, ?>) {
				final Map<String, Object> source = Convert.Any.toAny(sourceObject);
				this.copyDeep(source, target);
			}
			return target;
		}
		return null;
	}
}
