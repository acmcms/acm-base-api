/*
 * Created on 20.08.2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.ae1.storage;

/** @author myx
 *
 *         Window - Preferences - Java - Code Style - Code Templates */
public interface BaseRecycled {

	/** @return boolean */
	public boolean canClean();

	/** @return boolean */
	public boolean canMove();

	/** @return boolean */
	public boolean canRestore();

	/**
	 *
	 */
	public void doClean();

	/** @param parentGuid
	 */
	public void doMove(final String parentGuid);

	/**
	 *
	 */
	public void doRestore();

	/** @return date */
	public long getDate();

	/** @return string */
	public String getFolder();

	/** @return string */
	public String getGuid();

	/** @return string */
	public String getOwner();

	/** @return string */
	public String getTitle();
}
