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
public interface AccessUser<T extends AccessUser<?>> extends AccessPrincipal<T> {
	
	
	/**
	 *
	 */
	public static final BaseObject PROTOTYPE = new BaseNativeObject(Reflect.classToBasePrototype(AccessUser.class));
	
	@Override
	default BaseObject basePrototype() {
		
		
		return AccessUser.PROTOTYPE;
	}
	
	/**
	 * @param password
	 * @return boolean
	 */
	public boolean checkPassword(String password);
	
	/**
	 * @param password
	 * @param passwordType
	 * @return boolean
	 */
	public boolean checkPassword(String password, PasswordType passwordType);
	
	/**
	 *
	 */
	public void commit();
	
	@Override
	default int compareTo(final T o) {
		
		
		return this.getKey().compareTo(o.getKey());
	}
	
	@Override
	default boolean isGroup() {
		
		
		return false;
	}
	
	@Override
	default boolean isPerson() {
		
		
		return true;
	}
	
	/**
	 * @return date
	 */
	public long getChanged();
	
	/**
	 * @return date
	 */
	public long getCreated();
	
	/**
	 * @return description
	 */
	public String getDescription();
	
	/**
	 * @return email
	 */
	public String getEmail();
	
	/**
	 * @return groups
	 */
	public AccessGroup<?>[] getGroups();
	
	/**
	 * @return language
	 */
	public String getLanguage();
	
	/**
	 * @return login
	 */
	public String getLogin();
	
	/**
	 * @return registration profile
	 */
	public BaseObject getProfile();
	
	/**
	 * @param key
	 * @param create
	 * @return profile
	 */
	public BaseObject getProfile(final String key, final boolean create);
	
	/**
	 * @return type
	 */
	public int getType();
	
	/**
	 * @param group
	 */
	public void groupAdd(final AccessGroup<?> group);
	
	/**
	 * @param group
	 */
	public void groupRemove(final AccessGroup<?> group);
	
	/**
	 * @return boolean
	 */
	public boolean isActive();
	
	/**
	 * no personal data are exist for this user
	 *
	 * @return boolean
	 */
	public boolean isAnonymous();
	
	/**
	 * @param group
	 * @return boolean
	 */
	public boolean isInGroup(final AccessGroup<?> group);
	
	/**
	 * @param groupId
	 * @return boolean
	 */
	public boolean isInGroup(final String groupId);
	
	/**
	 * @return boolean
	 */
	default boolean isSystem() {
		
		return false;
	}
	
	/**
	 * @param authType
	 */
	public void removeAuth(final String authType);
	
	/**
	 *
	 */
	public void setActive();
	
	/**
	 * TODO:
	 *
	 * @param authType
	 * @param extUID
	 * @param extData
	 * @param extDataExpire
	 */
	public void setAuth(final String authType, final String extUID, final String extData, final long extDataExpire);
	
	/**
	 * @param description
	 */
	public void setDescription(final String description);
	
	/**
	 * @param email
	 */
	public void setEmail(final String email);
	
	/**
	 * @param language
	 */
	public void setLanguage(final String language);
	
	/**
	 * @param login
	 */
	public void setLogin(final String login);
	
	/**
	 * Sets both HIGH and NORMAL passwords
	 *
	 * @param password
	 */
	public void setPassword(final String password);
	
	/**
	 * Sets HIGH password
	 *
	 * @param password
	 */
	public void setPasswordHigh(final String password);
	
	/**
	 * Sets NORMAL password
	 *
	 * @param password
	 */
	public void setPasswordNormal(final String password);
	
	/**
	 * @param profile
	 */
	public void setProfile(final BaseObject profile);
	
	/**
	 * @param key
	 * @param profile
	 */
	public void setProfile(final String key, final BaseObject profile);
	
	/**
	 *
	 */
	public void setRegistered();
	
	/**
	 *
	 */
	public void setSystem();
	
	/**
	 * @param type
	 */
	public void setType(final int type);
}
