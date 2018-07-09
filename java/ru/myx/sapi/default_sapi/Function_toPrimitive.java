/**
 *
 */
package ru.myx.sapi.default_sapi;

import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;

/** @author myx */
public final class Function_toPrimitive extends BaseFunctionAbstract implements ExecCallableBoth.NativeJ1 {
	
	@Override
	public final BaseObject callNJ1(final BaseObject instance, final BaseObject x) {
		
		return x.baseToPrimitive(null);
	}

	@Override
	public boolean execIsConstant() {
		
		return true;
	}
	
	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return Object.class;
	}
}
