/*
 * Created on 01.08.2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.ae1.storage;

/** @author myx
 *
 *         Window - Preferences - Java - Code Style - Code Templates */
public interface ModuleSynchronizer {

	/** @param guid
	 * @return sync */
	public BaseSync createChange(final String guid);
}
