/*
 * Created on 20.08.2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.ae1.storage;

/** @author myx
 *
 *         Window - Preferences - Java - Code Style - Code Templates */
public interface ModuleRecycler {

	/**
	 *
	 */
	public void clearAllRecycled();

	/** @return recycled array */
	public BaseRecycled[] getRecycled();

	/** @param guid
	 * @return recycled */
	public BaseRecycled getRecycledByGuid(final String guid);
}
