package ru.myx.sapi.default_sapi;

import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveNumber;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.exec.ExecCallableBase;
import ru.myx.ae3.exec.ExecCallableJava;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecStateCode;
import ru.myx.ae3.exec.ResultHandler;

/** @author myx */
public class Function_binarySize extends BaseFunctionAbstract implements ExecCallableBase.ExecStore1, ExecCallableJava.JavaLongJ1 {
	
	@Override
	public final long callLJ1(//
			final BaseObject instance,
			final BaseObject x) {
		
		if (x == BaseObject.UNDEFINED) {
			return 0L;
		}
		if (x == BaseObject.NULL) {
			return 0L;
		}
		return Transfer.binarySize(x);
	}
	
	@Override
	public final ExecStateCode execCallPrepare(//
			final ExecProcess ctx, //
			final BaseObject instance,
			final ResultHandler store,
			final boolean inline,
			final BaseObject x) {
		
		if (x == BaseObject.UNDEFINED) {
			return store.execReturn(ctx, BasePrimitiveNumber.NAN);
		}
		if (x == BaseObject.NULL) {
			return store.execReturn(ctx, BasePrimitiveNumber.ZERO);
		}
		return store.execReturnNumeric(ctx, Transfer.binarySize(x));
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
