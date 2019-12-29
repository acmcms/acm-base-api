/**
 *
 */
package ru.myx.sapi.create_sapi;



import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveNumber;
import ru.myx.ae3.base.reflected.BaseReflectedObject;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.reflect.ImplementReflect;
import ru.myx.util.Counter;

/** @author myx */
public final class Function_counter extends BaseFunctionAbstract implements ExecCallableBoth.NativeEX {
	
	static final BaseObject COUNTER_PROTOTYPE = ImplementReflect.basePrototypeForJavaClass(Counter.class, false);
	
	@Override
	public final BaseReflectedObject<Counter> callNEX(final ExecProcess context, final BaseObject instance, final BaseArray arguments) {
		
		return new BaseReflectedObject<>(arguments.length() == 0
			? new Counter()
			: new Counter(arguments.baseGetFirst(BasePrimitiveNumber.ZERO).baseToNumber().doubleValue()), Function_counter.COUNTER_PROTOTYPE);
	}
	
	@Override
	public final int execArgumentsAcceptable() {
		
		return 1;
	}
	
	@Override
	public final int execArgumentsDeclared() {
		
		return 1;
	}
	
	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return Counter.class;
	}
	
}
