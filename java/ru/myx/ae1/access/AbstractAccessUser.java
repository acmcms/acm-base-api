/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.access;

import ru.myx.ae3.produce.Reproducible;

/**
 * @author myx
 * @param <T>
 * 			
 */
public abstract class AbstractAccessUser<T extends AbstractAccessUser<?>> extends AbstractAccessPrincipal<T> implements AccessUser<T>, Reproducible {
	
	@Override
	public void removeAuth(final String authType) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String restoreFactoryIdentity() {
		
		return "UM-USER";
	}
	
	@Override
	public String restoreFactoryParameter() {
		
		return "AE1:" + this.getKey();
	}
	
	@Override
	public void setAuth(final String authType, final String extUID, final String extData, final long extDataExpire) {
		// TODO Auto-generated method stub
		
	}
	
}
