/**
 * 
 */
package ru.myx.ae1.access;

import java.util.Collection;
import java.util.Set;

import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.access.AccessPrincipal;

final class AnomymousAccessManager extends AbstractAccessManager {
	@Override
	public void commitGroup(final AccessGroup<?> group) {
		// empty
	}
	
	@Override
	public void commitUser(final AccessUser<?> user) {
		// empty
	}
	
	@Override
	public AccessGroup<?> createGroup() {
		return null;
	}
	
	@Override
	public AccessUser<?> createUser() {
		return new AccessUserDynamic();
	}
	
	@Override
	public void deleteGroup(final String key) {
		// empty
	}
	
	@Override
	public boolean deleteUser(final String key) {
		return false;
	}
	
	@Override
	public AccessGroup<?>[] getAllGroups() {
		return new AccessGroup<?>[] { AccessGroup.EVERYONE };
	}
	
	@Override
	public AccessUser<?>[] getAllUsers() {
		return null;
	}
	
	@Override
	public AccessGroup<?> getGroup(final String key, final boolean create) {
		return null;
	}
	
	@Override
	public AccessGroup<?>[] getGroups(final AccessUser<?> user) {
		return null;
	}
	
	@Override
	public AccessUser<?> getUser(final String key, final boolean create) {
		return null;
	}
	
	@Override
	public AccessUser<?> getUserByAuth(final String authType, final String uniqueId) {
		return null;
	}
	
	@Override
	public AccessUser<?> getUserByLogin(final String key, final boolean create) {
		return null;
	}
	
	@Override
	public AccessUser<?>[] getUsers(final AccessGroup<?> group) {
		return null;
	}
	
	@Override
	public boolean isInGroup(final String userId, final String groupId) {
		return "def.guest".equals( groupId );
	}
	
	@Override
	public AccessUser<?>[] search(
			final String login,
			final String email,
			final long logonStart,
			final long logonEnd,
			final SortMode sortMode) {
		return null;
	}
	
	@Override
	public AccessUser<?>[] searchByMembership(final Collection<String> groups, final SortMode sortMode) {
		return null;
	}
	
	@Override
	public AccessUser<?>[] searchByType(final int minType, final int maxType, final SortMode sortMode) {
		return null;
	}
	
	@Override
	public AccessPrincipal<?> securityCheck(final int forceLevel, final String path, final String command) {
		return AccessGroup.EVERYONE;
	}
	
	@Override
	public AccessPrincipal<?>[] securityGetAccessEffective(final String path, final String permission) {
		return new AccessPrincipal<?>[] { AccessGroup.SUPERVISOR };
	}
	
	@Override
	public Set<String> securityGetPermissionsEffective(final AccessPrincipal<?> principal, final String path) {
		return AccessPermissions.PERMISSIONS_ALL;
	}
	
	@Override
	public AccessGroup<?>[] setGroups(final AccessUser<?> user, final AccessGroup<?>[] groups) {
		return groups;
	}
	
	@Override
	public void setPassword(final AccessUser<?> user, final String password, final PasswordType passwordType) {
		// empty
	}
	
	@Override
	public void updateGroups(
			final AccessUser<?> user,
			final Set<AccessGroup<?>> removed,
			final Set<AccessGroup<?>> added) {
		// error
	}
}
