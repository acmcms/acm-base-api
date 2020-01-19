/**
 *
 */
package ru.myx.sapi.create_sapi;

import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseJoined;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;

/** @author myx */
public final class Function_mapFilter extends BaseFunctionAbstract implements ExecCallableBoth.NativeEX {
	
	@Override
	public BaseObject callNEX(final ExecProcess ctx, final BaseObject instance, final BaseArray arguments) {

		final BaseObject primary = arguments.baseGetFirst(BaseObject.UNDEFINED);
		if (primary.baseIsPrimitive()) {
			if (primary == BaseObject.UNDEFINED) {
				if (arguments.baseGet(1, BaseObject.UNDEFINED) != BaseObject.UNDEFINED) {
					throw new IllegalArgumentException("prototype must not be primitive when parent is specified");
				}
				/** OK, we'll create empty object when no parameters passed. */
				return BaseObject.createObject();
			}
			throw new IllegalArgumentException("prototype must not be primitive");
		}
		final BaseObject parent = arguments.baseGet(1, BaseObject.UNDEFINED);
		if (parent == BaseObject.UNDEFINED) {
			return BaseObject.createObject(primary);
		}
		if (parent.baseIsPrimitive()) {
			throw new IllegalArgumentException("parent must not be primitive");
		}
		return new BaseJoined(primary, parent);
	}
	
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
}
