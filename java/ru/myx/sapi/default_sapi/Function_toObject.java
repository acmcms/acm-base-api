/**
 *
 */
package ru.myx.sapi.default_sapi;

import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseNativeString;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;

/** @author myx */
public final class Function_toObject extends BaseFunctionAbstract implements ExecCallableBoth.NativeJ1 {
	
	@Override
	public final BaseObject callNJ1(final BaseObject instance, final BaseObject x) {
		
		if (x == BaseObject.TRUE || x == BaseObject.FALSE) {
			return x;
		}
		if (!x.baseIsPrimitive()) {
			return x;
		}
		if (x == BaseObject.UNDEFINED) {
			throw new IllegalArgumentException("toObject(undefined)");
		}
		if (x instanceof CharSequence) {
			return new BaseNativeString(x.baseToJavaString());
		}
		return new BaseNativeString(x.baseToJavaString());
	}

	@Override
	public final boolean execIsConstant() {
		
		return false;
	}

	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return Object.class;
	}
}
