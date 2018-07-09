package ru.myx.ae2;

import ru.myx.ae3.base.BaseObject;

/**
 * @author myx
 * 
 */
public interface TemporaryStorage {
	/**
	 * @param key
	 * @return map - must not ever return null, use BaseObject.UNDEFINED
	 */
	public BaseObject load(final String key);
	
	/**
	 * @param key
	 * @param map
	 */
	public void savePersistent(final String key, final BaseObject map);
	
	/**
	 * @param key
	 * @param map
	 * @param expirationDate
	 */
	public void saveTemporary(final String key, final BaseObject map, final long expirationDate);
}
