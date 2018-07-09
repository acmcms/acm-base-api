/**
 *
 */
package ru.myx.sapi.default_sapi;

import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveNumber;
import ru.myx.ae3.exec.ExecCallableBoth;

/** @author myx */
public final class Function_toDouble extends BaseFunctionAbstract implements ExecCallableBoth.NativeJ1 {
	
	@Override
	public final BasePrimitiveNumber callNJ1(final BaseObject instance, final BaseObject argument) {
		
		return argument == BaseObject.UNDEFINED
			? BasePrimitiveNumber.ZERO
			: argument.baseToNumber();
	}
	
	@Override
	public final boolean execIsConstant() {
		
		return true;
	}
	
	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return Number.class;
	}
}
