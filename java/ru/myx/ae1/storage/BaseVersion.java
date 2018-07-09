/*
 * Created on 22.08.2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.ae1.storage;

/** @author myx
 *
 *         Window - Preferences - Java - Code Style - Code Templates */
public interface BaseVersion {

	/** @return string */
	public String getComment();

	/** @return date */
	public long getDate();

	/** @return string */
	public String getGuid();

	/** @return string */
	public String getOwner();

	/** @return string */
	public String getParentGuid();

	/** @return string */
	public String getTitle();

	/** @return string */
	public String getTypeName();
}
