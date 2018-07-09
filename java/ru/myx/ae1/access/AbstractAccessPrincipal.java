/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.access;

import ru.myx.ae3.access.AccessPrincipal;
import ru.myx.ae3.base.BaseHostEmpty;
import ru.myx.ae3.reflect.ReflectionIgnore;

/**
 * @author myx
 * @param <T>
 *
 */
@ReflectionIgnore
public abstract class AbstractAccessPrincipal<T extends AbstractAccessPrincipal<?>> extends BaseHostEmpty implements AccessPrincipal<T> {
	
	
	@Override
	public boolean equals(final Object arg) {
		
		
		if (arg == null) {
			return false;
		}
		if (arg instanceof AccessPrincipal<?>) {
			final AccessPrincipal<?> principal = (AccessPrincipal<?>) arg;
			return principal.getKey().equals(this.getKey()) && principal.isGroup() == this.isGroup() && principal.isPerson() == this.isPerson();
		}
		{
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		
		
		return this.getKey().hashCode();
	}
}
