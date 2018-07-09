/**
 *
 */
package ru.myx.sapi.create_sapi;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;

/** @author myx */
public final class Function_guid extends BaseFunctionAbstract implements ExecCallableBoth.JavaStringJ0 {
	
	@Override
	public String callSE0(final ExecProcess context, final BaseObject instance) {
		
		return Engine.createGuid();
	}

	@Override
	public String callSJ0(final BaseObject instance) {
		
		return Engine.createGuid();
	}
	
	@Override
	public Class<? extends Object> execResultClassJava() {
		
		return String.class;
	}

}
