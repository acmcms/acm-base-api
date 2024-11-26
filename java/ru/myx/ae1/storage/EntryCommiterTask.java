/*
 * Created on 19.04.2006
 */
package ru.myx.ae1.storage;

import java.util.IdentityHashMap;
import java.util.function.Function;

import ru.myx.ae3.act.Act;

/** @author myx */
public final class EntryCommiterTask implements Function<BaseEntry<?>, Object> {
	
	private static final Function<BaseEntry<?>, Object> ENTRY_COMMITER_TASK = new EntryCommiterTask();
	
	private final static IdentityHashMap<BaseEntry<?>, Boolean> check = new IdentityHashMap<>();
	
	/** Commits and touches an entry within 5 seconds and check if same action is already enqueued
	 *
	 * @param entry */
	public final static void touch(final BaseEntry<?> entry) {
		
		synchronized (EntryCommiterTask.check) {
			if (EntryCommiterTask.check.put(entry, Boolean.TRUE) != null) {
				return;
			}
		}
		Act.later(null, EntryCommiterTask.ENTRY_COMMITER_TASK, entry, 5_000L);
	}
	
	private EntryCommiterTask() {
		
		// prevent
	}
	
	@Override
	public final Object apply(final BaseEntry<?> entry) {
		
		synchronized (EntryCommiterTask.check) {
			if (EntryCommiterTask.check.remove(entry) == null) {
				return null;
			}
		}
		final BaseChange change = entry.createChange();
		change.touch();
		change.commit();
		return null;
	}
}
