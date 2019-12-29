/**
 *
 */
package ru.myx.sapi.create_sapi;

import java.util.Collection;



import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseArrayDynamic;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseNativeArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;

/** @author myx */
public final class Function_list extends BaseFunctionAbstract implements ExecCallableBoth.NativeEX {
	
	@Override
	public final BaseArrayDynamic<Object> callNEX(final ExecProcess context, final BaseObject instance, final BaseArray arguments) {
		
		final int argcount = arguments.length();
		if (argcount == 0) {
			return BaseObject.createArray();
		}
		if (argcount == 1) {
			final BaseObject o = arguments.baseGetFirst(BaseObject.UNDEFINED);
			if (o == BaseObject.UNDEFINED || o == BaseObject.NULL) {
				return BaseObject.createArray();
			}
			/** string implements array */
			if (!(o instanceof CharSequence)) {
				final BaseArray array = o.baseArray();
				if (array != null) {
					final int size = array.length();
					if (size < 0) {
						throw new IllegalArgumentException("size is less than zero!");
					}
					final BaseArrayDynamic<Object> result = new BaseNativeArray(size);
					for (int i = 0; i < size; ++i) {
						result.baseDefaultPush(array.baseGet(i, BaseObject.UNDEFINED));
					}
					return result;
				}
			}
			if (o instanceof Collection<?>) {
				final BaseArrayDynamic<Object> result = BaseObject.createArray();
				for (final Object object : (Collection<?>) o) {
					result.add(object);
				}
				return result;
			}
			return new BaseNativeArray(o);
		}
		if (argcount == 2) {
			final int size = arguments.baseGetFirst(BaseObject.UNDEFINED).baseToJavaInteger();
			if (size < 0) {
				throw new IllegalArgumentException("size is less than zero!");
			}
			final BaseObject fill = arguments.baseGet(1, BaseObject.UNDEFINED);
			return new BaseNativeArray(size, fill);
		}
		throw new IllegalArgumentException("Illegal usage!");
	}

	@Override
	public final int execArgumentsAcceptable() {
		
		return 2;
	}

	@Override
	public final int execArgumentsDeclared() {
		
		return 1;
	}

	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return BaseArrayDynamic.class;
	}

}
