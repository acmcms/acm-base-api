/*
 * Created on 01.08.2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.ae1.storage;

/** @author myx
 *
 *         Window - Preferences - Java - Code Style - Code Templates */
public interface ModuleSchedule {

	/** @param objectId
	 * @return schedule */
	public BaseSchedule createChange(final String objectId);

	/** @return int */
	public int getVersion();

	/**
	 *
	 */
	public void start();

	/**
	 *
	 */
	public void stop();
}
