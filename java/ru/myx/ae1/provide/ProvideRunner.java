/*
 * Created on 22.03.2006
 */
package ru.myx.ae1.provide;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Create;

/**
 * @author myx
 * 
 */
public class ProvideRunner {
	
	
	private static Map<String, TaskRunner> runners = null;
	
	/**
	 * @param target
	 */
	public static final void fillNames(final Set<String> target) {
		
		
		if (ProvideRunner.runners != null) {
			for (final String key : ProvideRunner.runners.keySet()) {
				target.add(key);
			}
		}
	}
	
	/**
	 * @param target
	 */
	public static final void fillNamesPrimitive(final Set<BasePrimitive<?>> target) {
		
		
		if (ProvideRunner.runners != null) {
			for (final String key : ProvideRunner.runners.keySet()) {
				target.add(Base.forString(key));
			}
		}
	}
	
	/**
	 * @param name
	 * @return runner
	 */
	public static final TaskRunner forName(final String name) {
		
		
		return (TaskRunner) Convert.MapEntry.toObject(ProvideRunner.runners, name, null);
	}
	
	/**
	 * @param cls
	 * @param parentalLookup
	 * @return lookup
	 */
	public static final BaseHostLookup getSchedulerTaskRunners(final Class<?> cls, final BaseHostLookup parentalLookup) {
		
		
		return new BaseHostLookup() {
			
			
			@Override
			public BaseObject baseGetLookupValue(final BaseObject key) {
				
				
				final TaskRunner tr = ProvideRunner.forName(String.valueOf(key));
				return tr == null
					? parentalLookup == null
						? Base.forString("n/a")
						: parentalLookup.baseGet(key.baseToString(), BaseObject.UNDEFINED)
					: Base.forString(tr.getTitle());
			}
			
			@Override
			public Iterator<String> baseKeysOwn() {
				
				
				final Set<String> names = Create.tempSet();
				if (parentalLookup != null) {
					final Iterator<String> parental = parentalLookup.baseKeysOwn();
					if (parental != null) {
						for (; parental.hasNext();) {
							names.add(parental.next());
						}
					}
				}
				ProvideRunner.fillNames(names);
				return names.iterator();
			}
			
			@Override
			public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
				
				
				final Set<BasePrimitive<?>> names = Create.tempSet();
				if (parentalLookup != null) {
					final Iterator<? extends BasePrimitive<?>> parental = parentalLookup.baseKeysOwnPrimitive();
					if (parental != null) {
						for (; parental.hasNext();) {
							names.add(parental.next());
						}
					}
				}
				ProvideRunner.fillNamesPrimitive(names);
				return names.iterator();
			}
			
			@Override
			public String toString() {
				
				
				return "[Lookup: Scheduler Task Runners]";
			}
		};
	}
	
	/**
	 * @param name
	 * @param runner
	 */
	public static final void register(final String name, final TaskRunner runner) {
		
		
		synchronized (ProvideRunner.class) {
			if (ProvideRunner.runners == null) {
				ProvideRunner.runners = new HashMap<>();
			}
			ProvideRunner.runners.put(name, runner);
		}
	}
}
