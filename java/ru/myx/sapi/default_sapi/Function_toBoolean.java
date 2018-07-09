/**
 *
 */
package ru.myx.sapi.default_sapi;

import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveBoolean;
import ru.myx.ae3.base.BaseString;
import ru.myx.ae3.exec.ExecCallableBoth;

/** @author myx */
public final class Function_toBoolean extends BaseFunctionAbstract implements ExecCallableBoth.NativeJ1 {
	
	@Override
	public final BasePrimitiveBoolean callNJ1(final BaseObject instance, final BaseObject argument) {
		
		return BaseString.STR_FALSE == argument
			? BaseObject.FALSE
			: argument.baseToBoolean();
	}
	
	@Override
	public final boolean execIsConstant() {
		
		return true;
	}
	
	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return Boolean.class;
	}
}
