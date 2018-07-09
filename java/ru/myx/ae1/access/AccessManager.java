/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.access;

import java.util.Collection;
import java.util.Set;

import ru.myx.ae3.access.AccessPrincipal;
import java.util.function.Function;
import ru.myx.ae3.control.ControlContainer;
import ru.myx.ae3.control.ControlForm;

/**
 * @author myx
 * 
 */
public interface AccessManager {
	/**
	 * normal login
	 */
	String	AUTH_TYPE_LOGIN_NORMAL	= "login_normal";
	
	/**
	 * login high
	 */
	String	AUTH_TYPE_LOGIN_HIGH	= "login_high";
	
	/**
	 * @param group
	 */
	void commitGroup(AccessGroup<?> group);
	
	/**
	 * @param user
	 */
	void commitUser(AccessUser<?> user);
	
	/**
	 * @param path
	 * @return form
	 */
	ControlForm<?> createFormGroupCreation(String path);
	
	/**
	 * @param path
	 * @param key
	 * @return form
	 */
	ControlForm<?> createFormGroupProperties(String path, String key);
	
	/**
	 * @param path
	 * @return form
	 */
	ControlForm<?> createFormSecuritySetup(String path);
	
	/**
	 * @param group
	 * @param container
	 * @return form
	 */
	ControlForm<?> createFormUserSearch(AccessGroup<?> group, ControlContainer<?> container);
	
	/**
	 * @param group
	 * @param user
	 * @return form
	 */
	ControlForm<?> createFormUserSelection(AccessGroup<?> group, AccessUser<?> user);
	
	/**
	 * @param group
	 * @param users
	 * @param resultFilter
	 * @return form
	 */
	ControlForm<?> createFormUsersSelection(
			AccessGroup<?> group,
			AccessUser<?>[] users,
			Function<AccessUser<?>[], Object> resultFilter);
	
	/**
	 * @return group
	 */
	AccessGroup<?> createGroup();
	
	/**
	 * @return user
	 */
	AccessUser<?> createUser();
	
	/**
	 * @param key
	 */
	void deleteGroup(String key);
	
	/**
	 * @param key
	 * @return
	 */
	boolean deleteUser(String key);
	
	/**
	 * @return groups
	 */
	AccessGroup<?>[] getAllGroups();
	
	/**
	 * @return users
	 */
	AccessUser<?>[] getAllUsers();
	
	/**
	 * @param key
	 * @param create
	 * @return group
	 */
	AccessGroup<?> getGroup(String key, boolean create);
	
	/**
	 * @param user
	 * @return groups
	 */
	AccessGroup<?>[] getGroups(AccessUser<?> user);
	
	/**
	 * @param key
	 * @param create
	 * @return user
	 */
	AccessUser<?> getUser(String key, boolean create);
	
	/**
	 * @param authType
	 * @param uniqueId
	 * @return user
	 * 
	 *         FIXME mmsource
	 * 
	 */
	AccessUser<?> getUserByAuth(String authType, String uniqueId);
	
	/**
	 * @param login
	 * @param create
	 * @return user
	 */
	AccessUser<?> getUserByLogin(String login, boolean create);
	
	/**
	 * @param group
	 * @return users
	 */
	AccessUser<?>[] getUsers(AccessGroup<?> group);
	
	/**
	 * @param user
	 * @param group
	 * @return boolean
	 */
	boolean isInGroup(AccessUser<?> user, AccessGroup<?> group);
	
	/**
	 * @param userId
	 * @param groupId
	 * @return boolean
	 */
	boolean isInGroup(String userId, String groupId);
	
	/**
	 * @param login
	 * @param email
	 * @param logonStart
	 * @param logonEnd
	 * @param sortMode
	 * @return users
	 */
	AccessUser<?>[] search(String login, String email, long logonStart, long logonEnd, SortMode sortMode);
	
	/**
	 * @param groups
	 * @param sortMode
	 * @return users
	 */
	AccessUser<?>[] searchByMembership(Collection<String> groups, SortMode sortMode);
	
	/**
	 * @param minType
	 * @param maxType
	 * @param sortMode
	 * @return users
	 */
	AccessUser<?>[] searchByType(int minType, int maxType, SortMode sortMode);
	
	/**
	 * @param forceLevel
	 * @param path
	 * @param command
	 * @return principal
	 */
	AccessPrincipal<?> securityCheck(int forceLevel, String path, String command);
	
	/**
	 * @param path
	 * @param permission
	 * @return principals
	 */
	AccessPrincipal<?>[] securityGetAccessEffective(String path, String permission);
	
	/**
	 * Set of permissions, Permissions.PERMISSIONS_ALL or
	 * Permissions.PERMISSIONS_NONE.
	 * 
	 * @param principal
	 * @param path
	 * @return permissions
	 */
	Set<String> securityGetPermissionsEffective(AccessPrincipal<?> principal, String path);
	
	/**
	 * @param user
	 * @param groups
	 * @return groups
	 */
	AccessGroup<?>[] setGroups(AccessUser<?> user, AccessGroup<?>[] groups);
	
	/**
	 * @param user
	 * @param password
	 * @param passwordType
	 *            - should change both passwords when this parameter is null
	 */
	void setPassword(AccessUser<?> user, String password, PasswordType passwordType);
	
	/**
	 * @param user
	 * @param removed
	 * @param added
	 */
	void updateGroups(AccessUser<?> user, Set<AccessGroup<?>> removed, Set<AccessGroup<?>> added);
}
