/*
 * Created on 31.10.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.ae3.cache;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CacheFactory {
	
	/**
	 * @param <V>
	 * @return cache
	 */
	public static final <V> CacheL3<V> createL3() {
		return new Cache3<>();
	}
}
