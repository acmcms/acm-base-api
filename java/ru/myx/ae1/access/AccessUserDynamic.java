package ru.myx.ae1.access;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 *
 */
public final class AccessUserDynamic extends AbstractAccessUser<AccessUserDynamic> {
	
	/**
	 *
	 */
	private final String guid;

	/**
	 */
	public AccessUserDynamic() {
		this.guid = Engine.createGuid();
	}

	@Override
	public boolean checkPassword(final String password) {
		
		return false;
	}

	@Override
	public boolean checkPassword(final String password, final PasswordType passwordType) {
		
		return false;
	}

	@Override
	public void commit() {
		
		Report.warning("USER", "commit on non-writable user object");
	}

	@Override
	public long getChanged() {
		
		return Engine.fastTime();
	}

	@Override
	public long getCreated() {
		
		return Engine.fastTime();
	}

	@Override
	public String getDescription() {
		
		return null;
	}

	@Override
	public String getEmail() {
		
		return null;
	}

	@Override
	public AccessGroup<?>[] getGroups() {
		
		return null;
	}

	@Override
	public String getKey() {
		
		return this.guid;
	}

	@Override
	public String getLanguage() {
		
		return null;
	}

	@Override
	public String getLogin() {
		
		return null;
	}

	@Override
	public BaseObject getProfile() {
		
		return BaseObject.UNDEFINED;
	}

	@Override
	public BaseObject getProfile(final String key, final boolean create) {
		
		return create
			? new BaseNativeObject()
			: BaseObject.UNDEFINED;
	}

	@Override
	public int getType() {
		
		return 0;
	}

	@Override
	public void groupAdd(final AccessGroup<?> group) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void groupRemove(final AccessGroup<?> group) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public boolean isActive() {
		
		return false;
	}

	@Override
	public boolean isAnonymous() {
		
		return true;
	}

	@Override
	public boolean isInGroup(final AccessGroup<?> group) {
		
		return false;
	}

	@Override
	public boolean isInGroup(final String groupId) {
		
		return false;
	}

	@Override
	public boolean isPerson() {
		
		return false;
	}

	@Override
	public boolean isSystem() {
		
		return this.getType() >= UserTypes.UT_SYSTEM;
	}

	@Override
	public void removeAuth(final String authType) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setActive() {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setAuth(final String authType, final String extUID, final String extData, final long extDataExpire) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setDescription(final String description) {
		
		Report.warning("USER", "modification on non-writable user object");

	}

	@Override
	public void setEmail(final String email) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setLanguage(final String language) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setLogin(final String login) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setPassword(final String password) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setPasswordHigh(final String password) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setPasswordNormal(final String password) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setProfile(final BaseObject profile) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setProfile(final String key, final BaseObject profile) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setRegistered() {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setSystem() {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public void setType(final int type) {
		
		Report.warning("USER", "modification on non-writable user object");
	}

	@Override
	public String toString() {
		
		return "USR{id=" + this.getKey() + ", login=" + this.getLogin() + ", email=" + this.getEmail() + ", changed=" + this.getChanged() + ", created=" + this.getCreated() + "}";
	}
}
