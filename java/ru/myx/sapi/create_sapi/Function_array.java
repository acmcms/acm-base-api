/**
 *
 */
package ru.myx.sapi.create_sapi;



import ru.myx.ae3.base.BaseArrayDynamic;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseNativeArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;

/** @author myx */
public final class Function_array extends BaseFunctionAbstract implements ExecCallableBoth.NativeJ2 {
	
	@Override
	public final BaseArrayDynamic<Object> callNJ2(final BaseObject instance, final BaseObject argumentSize, final BaseObject argumentFill) {
		
		final int size = argumentSize.baseToJavaInteger();
		if (size == 0) {
			return BaseObject.createArray();
		}
		return new BaseNativeArray(size, argumentFill);
	}
	
	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return BaseArrayDynamic.class;
	}
	
}
