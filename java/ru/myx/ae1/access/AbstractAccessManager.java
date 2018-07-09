/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.access;

import java.util.function.Function;
import ru.myx.ae3.control.ControlContainer;
import ru.myx.ae3.control.ControlForm;

/**
 * @author myx
 * 
 */
public abstract class AbstractAccessManager implements AccessManager {
	@Override
	public ControlForm<?> createFormGroupCreation(final String path) {
		return null;
	}
	
	@Override
	public ControlForm<?> createFormGroupProperties(final String path, final String key) {
		return null;
	}
	
	@Override
	public ControlForm<?> createFormSecuritySetup(final String path) {
		return null;
	}
	
	@Override
	public ControlForm<?> createFormUserSearch(final AccessGroup<?> group, final ControlContainer<?> container) {
		return null;
	}
	
	@Override
	public ControlForm<?> createFormUserSelection(final AccessGroup<?> group, final AccessUser<?> user) {
		return null;
	}
	
	@Override
	public ControlForm<?> createFormUsersSelection(
			final AccessGroup<?> group,
			final AccessUser<?>[] users,
			final Function<AccessUser<?>[], Object> resultFilter) {
		return null;
	}
	
	@Override
	public boolean isInGroup(final AccessUser<?> user, final AccessGroup<?> group) {
		return this.isInGroup( user.getKey(), group.getKey() );
	}
	
	@Override
	public String toString() {
		return "[object " + this.getClass().getName() + "]";
	}
}
