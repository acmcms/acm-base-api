package ru.myx.ae1;

import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecProcessVariable;
import ru.myx.sapi.RuntimeEnvironment;

/**
 * Title: ae1 Base definitions Description: Copyright: Copyright (c) 2001
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
public final class BaseRT3 {
	private static final ExecProcessVariable<RuntimeEnvironment>	RUNTIME	= Exec.createProcessVariable( "rt3", true );
	
	/**
	 * @return runtime
	 */
	public static final RuntimeEnvironment runtime() {
		return BaseRT3.RUNTIME.baseValue();
	}
	
	/**
	 * @param context
	 * @return runtime
	 */
	public static final RuntimeEnvironment runtime(final ExecProcess context) {
		return BaseRT3.RUNTIME.baseValue( context );
	}
	
	/**
	 * @param context
	 * @param runtime
	 */
	public static final void setRuntime(final ExecProcess context, final RuntimeEnvironment runtime) {
		BaseRT3.RUNTIME.execSet( context, runtime );
	}
	
	// //////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////
	private BaseRT3() {
		// prevent
	}
	
}
