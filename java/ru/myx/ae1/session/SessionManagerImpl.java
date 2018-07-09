package ru.myx.ae1.session;

import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.status.StatusFiller;

/**
 * @author myx
 * 
 */
public interface SessionManagerImpl extends StatusFiller {
	/**
	 * @param sid
	 * @return map
	 */
	public BaseMap session(final String sid);
	
	/**
	 * @param sid
	 * @return map
	 */
	public BaseMap sessionIfExists(final String sid);
}
