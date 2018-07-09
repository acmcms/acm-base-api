/**
 * 
 */
package ru.myx.ae1.know;

import java.util.Iterator;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.exec.Exec;

final class LookupAllConnectionPools extends BaseHostLookup {
	
	
	@Override
	public final BaseObject baseGetLookupValue(final BaseObject key) {
		
		
		return key;
	}
	
	@Override
	public boolean baseHasKeysOwn() {
		
		
		return !Context.getServer(Exec.currentProcess()).getConnections().isEmpty();
	}
	
	@Override
	public final Iterator<String> baseKeysOwn() {
		
		
		return Context.getServer(Exec.currentProcess()).getConnections().keySet().iterator();
	}
	
	@Override
	public Iterator<? extends CharSequence> baseKeysOwnAll() {
		
		
		return this.baseKeysOwn();
	}
	
	@Override
	public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
		
		
		return Base.iteratorPrimitiveSafe(this.baseKeysOwn());
	}
	
	@Override
	public String toString() {
		
		
		return "[Lookup: All Connection Pools]";
	}
}
