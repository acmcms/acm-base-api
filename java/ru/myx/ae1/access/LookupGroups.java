/**
 * 
 */
package ru.myx.ae1.access;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.base.BasePrimitiveString;
import ru.myx.ae3.base.BaseString;
import ru.myx.ae3.exec.Exec;

class LookupGroups extends BaseHostLookup {
	
	
	private static BasePrimitiveString buildTitle(final AccessGroup<?> group) {
		
		
		if (group == null) {
			return Base.forString("-= error: null =-");
		}
		final String title = group.getTitle();
		final String description = group.getDescription();
		if (description != null && description.length() > 0) {
			return Base.forString(title + " (" + description + ')');
		}
		return Base.forString(title);
	}
	
	private final boolean any;
	
	LookupGroups(final boolean any) {
		
		this.any = any;
	}
	
	@Override
	public BaseObject baseGetLookupValue(final BaseObject key) {
		
		
		assert key != null : "NULL java object";
		final String k = key.baseToJavaString();
		if (k.length() == 0) {
			return Base.forString("-= Not specified =-");
		}
		return LookupGroups.buildTitle(Context.getServer(Exec.currentProcess()).getAccessManager().getGroup(k, true));
	}
	
	@Override
	public Iterator<String> baseKeysOwn() {
		
		
		final List<String> keys = new ArrayList<>();
		if (this.any) {
			keys.add("");
		}
		for (final AccessGroup<?> group : Context.getServer(Exec.currentProcess()).getAccessManager().getAllGroups()) {
			keys.add(group.getKey());
		}
		return keys.iterator();
	}
	
	@Override
	public Iterator<? extends CharSequence> baseKeysOwnAll() {
		
		
		return this.baseKeysOwn();
	}
	
	@Override
	public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
		
		
		final List<BasePrimitive<?>> keys = new ArrayList<>();
		if (this.any) {
			keys.add(BaseString.EMPTY);
		}
		for (final AccessGroup<?> group : Context.getServer(Exec.currentProcess()).getAccessManager().getAllGroups()) {
			keys.add(Base.forString(group.getKey()));
		}
		return keys.iterator();
	}
	
	@Override
	public String toString() {
		
		
		return "[Lookup: Access Groups]";
	}
}
