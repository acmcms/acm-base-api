/**
 * Created on 25.11.2002
 */
package ru.myx.ae1.know;

import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.produce.Produce;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 * 
 * myx - barachta 
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class Know {
	/**
	 * 
	 */
	public static final BaseHostLookup	SYSTEM_LANGUAGES	= new LookupSystemLanguages();
	
	/**
	 * 
	 */
	public static final BaseHostLookup	ALL_POOLS			= new LookupAllConnectionPools();
	
	static {
		/**
		 * Act should be initialized before this!
		 */
		Exec.currentProcess();
		/**
		 * Report should be initialized before this!
		 */
		Report.currentReceiverLog();
	}
	
	static {
		Produce.registerFactory( new CurrentServerDataFactory() );
	}
	
	/**
	 * @return system build serial number
	 */
	public static final int systemBuild() {
		return 679;
	}
	
	/**
	 * @return system version
	 */
	public static final String systemVersion() {
		return "4.95";
	}
	
	private Know() {
		// empty
	}
}
