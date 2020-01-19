/**
 *
 */
package ru.myx.sapi.create_sapi;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseArrayDynamic;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.produce.Reproducible;
import ru.myx.ae3.reflect.ReflectionHidden;
import ru.myx.util.PublicCloneable;

/** @author myx */
public final class Function_mapClone extends BaseFunctionAbstract implements ExecCallableBoth.NativeJ1 {
	
	private static final BaseObject cloneBase(final BaseObject value) {
		
		if (value.baseIsPrimitive()) {
			return value;
		}
		if (value instanceof Reproducible) {
			/** Do not clone reproducible, just check that it is legitimate */
			final String factory = ((Reproducible) value).restoreFactoryIdentity();
			if (factory != null) {
				return value;
			}
		}
		if (value instanceof PublicCloneable) {
			/** must return same class */
			return (BaseObject) ((PublicCloneable) value).clone();
		}
		if (value instanceof Map<?, ?>) {
			final BaseMap result = BaseObject.createObject(value.basePrototype());
			final BaseObject source = value;
			Function_mapClone.copyDeepBase(source, result);
			return result;
		}
		{
			final BaseArray array = value.baseArray();
			if (array != null) {
				final int length = array.length();
				final BaseArrayDynamic<Object> result = BaseObject.createArray(length);
				for (int i = 0; i < length; ++i) {
					final BaseObject element = array.baseGet(i, BaseObject.UNDEFINED);
					assert element != null : "NULL java value, arrayClass: " + array.getClass().getName();
					result.baseDefaultPush(Function_mapClone.cloneBase(element));
				}
				return result;
			}
		}
		if (value instanceof List<?>) {
			final BaseArrayDynamic<Object> result = BaseObject.createArray();
			final List<Object> source = Convert.Any.toAny(value);
			for (final Object entry : source) {
				result.baseDefaultPush(Function_mapClone.cloneJava(entry));
			}
			return result;
		}
		{
			final Object baseValue = value.baseValue();
			if (baseValue != value && baseValue != null) {
				Function_mapClone.cloneJava(baseValue);
			}
		}
		return value;
	}
	
	private static final BaseObject cloneJava(final Object value) {
		
		if (value instanceof BaseObject) {
			return Function_mapClone.cloneBase((BaseObject) value);
		}
		if (value instanceof Reproducible) {
			final String factory = ((Reproducible) value).restoreFactoryIdentity();
			if (factory != null) {
				return Base.forUnknown(value);
			}
		}
		if (value instanceof PublicCloneable) {
			return Base.forUnknown(((PublicCloneable) value).clone());
		}
		if (value instanceof Map<?, ?>) {
			final BaseMap result = BaseObject.createObject();
			final Map<String, Object> source = Convert.Any.toAny(value);
			Function_mapClone.copyDeepJava(source, result);
			return result;
		}
		if (value instanceof List<?>) {
			final List<Object> source = Convert.Any.toAny(value);
			if (value instanceof RandomAccess) {
				final int length = source.size();
				final BaseArrayDynamic<Object> result = BaseObject.createArray(length);
				for (int i = 0; i < length; ++i) {
					result.baseDefaultPush(Function_mapClone.cloneJava(source.get(i)));
				}
				return result;
			}
			final BaseArrayDynamic<Object> result = BaseObject.createArray();
			for (final Object entry : source) {
				result.baseDefaultPush(Function_mapClone.cloneJava(entry));
			}
			return result;
		}
		if (value instanceof Value<?>) {
			final Object baseValue = ((Value<?>) value).baseValue();
			if (baseValue != value && baseValue != null) {
				return Function_mapClone.cloneJava(baseValue);
			}
		}
		return Base.forUnknown(value);
	}
	
	private static final void copyDeepBase(final BaseObject source, final BaseMap target) {
		
		for (final Iterator<String> keys = source.baseKeysOwn(); keys.hasNext();) {
			final String key = keys.next();
			final BaseObject value = source.baseGet(key, BaseObject.UNDEFINED);
			assert value != null : "NULL java value";
			target.baseDefine(key, Function_mapClone.cloneBase(value));
		}
	}
	
	private static final void copyDeepJava(final Map<String, Object> source, final BaseMap target) {
		
		for (final String key : source.keySet()) {
			final Object value = source.get(key);
			target.baseDefine(key, Function_mapClone.cloneJava(value));
		}
	}
	
	@Override
	@ReflectionHidden
	public final BaseObject callNJ1(final BaseObject instance, final BaseObject argument) {
		
		if (argument.baseIsPrimitive()) {
			return BaseObject.createObject();
		}
		final BaseMap result = BaseObject.createObject(argument.basePrototype());
		if (argument instanceof BaseMap) {
			Function_mapClone.copyDeepBase(argument, result);
			return result;
		}
		if (argument instanceof Map<?, ?>) {
			final Map<String, Object> source = Convert.Any.toAny(argument);
			Function_mapClone.copyDeepJava(source, result);
		}
		{
			final Object base = argument.baseValue();
			if (base == null || base == argument) {
				return result;
			}
			if (base instanceof BaseMap) {
				Function_mapClone.copyDeepBase((BaseMap) base, result);
				return result;
			}
			if (base instanceof Map<?, ?>) {
				final Map<String, Object> source = Convert.Any.toAny(base);
				Function_mapClone.copyDeepJava(source, result);
			}
		}
		return result;
	}
	
	@Override
	public Class<? extends BaseMap> execResultClassJava() {
		
		return BaseMap.class;
	}
	
}
