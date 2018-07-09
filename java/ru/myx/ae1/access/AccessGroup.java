/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.access;

import ru.myx.ae3.access.AccessPrincipal;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.reflect.Reflect;

/**
 * @author myx
 * @param <T>
 *
 */
public interface AccessGroup<T extends AccessGroup<?>> extends AccessPrincipal<T> {
	
	
	/**
	 *
	 */
	public static final BaseObject PROTOTYPE = new BaseNativeObject(Reflect.classToBasePrototype(AccessGroup.class));

	@Override
	default BaseObject basePrototype() {
		
		
		return AccessGroup.PROTOTYPE;
	}

	/**
	 *
	 */
	public static final AccessGroup<?> EVERYONE = new AccessGroupEveryone();

	/**
	 *
	 */
	public static final AccessGroup<?> REGISTERED = new AccessGroupRegistered();

	/**
	 *
	 */
	public static final AccessGroup<?> SUPERVISOR = new AccessGroupSupervisor();

	/**
	 * @return boolean
	 */
	public boolean checkExclusions();

	/**
	 *
	 */
	public void commit();

	@Override
	public default int compareTo(final T o) {
		
		
		return this.getKey().compareTo(o.getKey());
	}
	
	@Override
	public default boolean isGroup() {
		
		
		return true;
	}
	
	@Override
	public default boolean isPerson() {
		
		
		return false;
	}
	
	/**
	 * @return level
	 */
	public int getAuthLevel();

	/**
	 * @return description
	 */
	public String getDescription();

	/**
	 * @return title
	 */
	public String getTitle();

	/**
	 * @return users
	 */
	public AccessUser<?>[] getUsers();

	/**
	 * @param level
	 */
	public void setAuthLevel(final int level);

	/**
	 * @param description
	 */
	public void setDescription(final String description);

	/**
	 * @param title
	 */
	public void setTitle(final String title);
}
