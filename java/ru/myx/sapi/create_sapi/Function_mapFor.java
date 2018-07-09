/**
 *
 */
package ru.myx.sapi.create_sapi;

import java.util.Iterator;

import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;

/** @author myx */
public final class Function_mapFor extends BaseFunctionAbstract implements ExecCallableBoth.NativeEX {
	
	@Override
	public BaseObject callNEX(final ExecProcess ctx, final BaseObject instance, final BaseArray arguments) {
		
		final BaseObject result = BaseObject.createObject();
		for (final Iterator<String> iterator = arguments.baseKeysOwn(); iterator.hasNext();) {
			final String key = iterator.next();
			result.baseDefine(key, arguments.baseGet(key, BaseObject.UNDEFINED), BaseProperty.ATTRS_MASK_WED);
		}
		return result;
	}
	
	@Override
	public final int execArgumentsAcceptable() {
		
		return Integer.MAX_VALUE;
	}
	
	@Override
	public final int execArgumentsDeclared() {
		
		return 1;
	}
	
	@Override
	public final boolean execHasNamedArguments() {
		
		return true;
	}

	@Override
	public Class<? extends BaseObject> execResultClassJava() {
		
		return BaseObject.class;
	}
}
