/**
 *
 */
package ru.myx.sapi.create_sapi;

import ru.myx.ae3.base.BaseJoined;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.BaseFunctionExecFullJ;
import ru.myx.ae3.exec.ExecProcess;

/**
 * @author myx
 *
 */
public final class Function_mapFilter extends BaseFunctionExecFullJ<BaseObject> {
	
	
	@Override
	public final int execArgumentsAcceptable() {
		
		
		return 2;
	}

	@Override
	public final int execArgumentsDeclared() {
		
		
		return 1;
	}

	@Override
	public final int execArgumentsMinimal() {
		
		
		return 1;
	}

	@Override
	public Class<? extends BaseObject> execResultClassJava() {
		
		
		return BaseObject.class;
	}

	@Override
	public final BaseObject getValue(final ExecProcess context) {
		
		
		final int argcount = context.length();
		if (argcount == 1) {
			final BaseObject prototype = context.baseGetFirst(BaseObject.UNDEFINED);
			assert !prototype.baseIsPrimitive() : "prototype must not be primitive";
			return BaseObject.createObject(prototype);
			// return Base.OBJECT_FACTORY.createObject(prototype);
		}
		if (argcount == 2) {
			final BaseObject primary = context.baseGetFirst(BaseObject.UNDEFINED);
			final BaseObject parent = context.baseGet(1, BaseObject.UNDEFINED);
			assert primary != null : "Java NULL object";
			assert !primary.baseIsPrimitive() : "primary must not be primitive";
			assert parent != null : "Java NULL object";
			assert !parent.baseIsPrimitive() : "parent must not be primitive";
			return new BaseJoined(primary, parent);
		}
		throw new IllegalArgumentException("Illegal usage!");
	}

}
