/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.access;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.exec.Exec;

final class AccessGroupEveryone extends AbstractAccessGroup<AccessGroupEveryone> {
	
	@Override
	public boolean checkExclusions() {
		
		return true;
	}

	@Override
	public void commit() {
		
		throw new IllegalArgumentException("Default group is not modifiable");
	}

	@Override
	public int getAuthLevel() {
		
		return AuthLevels.AL_UNAUTHORIZED;
	}

	@Override
	public String getDescription() {
		
		return "default group for anyone";
	}

	@Override
	public String getKey() {
		
		return "def.guest";
	}

	@Override
	public String getTitle() {
		
		return "Anyone / Guest";
	}

	@Override
	public AccessUser<?>[] getUsers() {
		
		return Context.getServer(Exec.currentProcess()).getAccessManager().getUsers(this);
	}

	@Override
	public void setAuthLevel(final int level) {
		
		throw new IllegalArgumentException("Default group is not modifiable");
	}

	@Override
	public void setDescription(final String description) {
		
		throw new IllegalArgumentException("Default group is not modifiable");
	}

	@Override
	public void setTitle(final String title) {
		
		throw new IllegalArgumentException("Default group is not modifiable");
	}
}
