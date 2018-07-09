/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.access;

import ru.myx.ae3.produce.Reproducible;
import ru.myx.ae3.reflect.ReflectionIgnore;

/**
 * @author myx
 * @param <T>
 * 
 */
@ReflectionIgnore
public abstract class AbstractAccessGroup<T extends AbstractAccessGroup<?>> extends AbstractAccessPrincipal<T> implements AccessGroup<T>, Reproducible {
	
	
	@Override
	public String restoreFactoryIdentity() {
		
		
		return "UM-GROUP";
	}

	@Override
	public String restoreFactoryParameter() {
		
		
		return "AE1:" + this.getKey();
	}

}
