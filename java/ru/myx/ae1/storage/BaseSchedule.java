package ru.myx.ae1.storage;

import ru.myx.ae3.base.BaseObject;

/** @author myx
 *
 *         Window - Preferences - Java - Code Style - Code Templates */
public interface BaseSchedule {

	/**
	 *
	 */
	public void clear();

	/**
	 *
	 */
	public void commit();

	/** @return boolean */
	public boolean isEmpty();

	/** @param name
	 * @param replace
	 * @param date
	 * @param command
	 * @param parameters
	 */
	public void schedule(final String name, final boolean replace, final long date, final String command, final BaseObject parameters);

	/** @param name
	 */
	public void scheduleCancel(final String name);

	/** @param key
	 */
	public void scheduleCancelGuid(final String key);

	/** @param schedule
	 * @param replace
	 */
	public void scheduleFill(final BaseSchedule schedule, final boolean replace);

	/** @param key
	 * @param replace
	 * @param date
	 * @param command
	 * @param parameters
	 */
	public void scheduleGuid(final String key, final boolean replace, final long date, final String command, final BaseObject parameters);
}
