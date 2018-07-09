/**
 * 
 */
package ru.myx.ae1.know;

import java.util.Arrays;
import java.util.Iterator;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.know.Language;

final class LookupSystemLanguages extends BaseHostLookup {
	
	
	@Override
	public BaseObject baseGetLookupValue(final BaseObject key) {
		
		
		final Language language = Language.getLanguage(String.valueOf(key));
		return language.baseGet("nativeName", BaseObject.UNDEFINED);
	}
	
	@Override
	public Iterator<String> baseKeysOwn() {
		
		
		return Arrays.asList(Context.getServer(Exec.currentProcess()).getLanguages()).iterator();
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
		
		
		return "[Lookup: System Languages]";
	}
}
