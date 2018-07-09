/*
 * Created on 20.08.2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.ae1.storage;

/** @author myx
 *
 *         Window - Preferences - Java - Code Style - Code Templates */
public interface BaseHistory {

	/** @return date */
	public long getDate();

	/** @return string */
	public String getGuid();

	/** @return string */
	public String getTitle();
}
