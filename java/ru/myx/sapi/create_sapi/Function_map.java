/**
 *
 */
package ru.myx.sapi.create_sapi;

import java.util.Iterator;



import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;

/** @author myx */
public final class Function_map extends BaseFunctionAbstract implements ExecCallableBoth.NativeEX {
	
	@Override
	public final BaseMap callNEX(final ExecProcess context, final BaseObject instance, final BaseArray arguments) {
		
		final BaseMap result = BaseObject.createObject();
		if (arguments.baseHasKeysOwn()) {
			for (final Iterator<String> iterator = arguments.baseKeysOwn(); iterator.hasNext();) {
				final String key = iterator.next();
				result.baseDefine(key, arguments.baseGet(key, BaseObject.UNDEFINED));
			}
		} else {
			if (arguments.length() > 0) {
				final BaseObject object = arguments.baseGetFirst(BaseObject.UNDEFINED);
				if (!object.baseIsPrimitive()) {
					result.baseDefineImportAllEnumerable(object);
				}
			}
		}
		return result;
	}
	
	@Override
	public final int execArgumentsAcceptable() {
		
		return Integer.MAX_VALUE;
	}
	
	@Override
	public final int execArgumentsDeclared() {
		
		return 0;
	}
	
	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return BaseMap.class;
	}
	
}
