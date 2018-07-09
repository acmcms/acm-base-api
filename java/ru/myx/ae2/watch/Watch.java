/**
 * Created on 02.12.2002
 * 
 * myx - barachta */
package ru.myx.ae2.watch;

/**
 * @author myx
 * 
 * myx - barachta 
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public final class Watch {
	/**
	 * @author myx
	 * 
	 */
	public static interface Monitor {
		/**
		 * Return <b>false </b> or throw an Exception to indicate that this
		 * watch should be stopped.
		 * 
		 * @return boolean
		 * @throws Exception
		 */
		boolean check() throws Exception;
	}
	
	private static MonitorWatcher	monitorWatcher	= new MonitorWatcher();
	
	/**
	 * @param instance
	 */
	public static final void monitor(final Watch.Monitor instance) {
		Watch.monitorWatcher.register( instance );
	}
	
	private Watch() {
		// empty
	}
	
}
