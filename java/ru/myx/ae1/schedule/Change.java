/*
 * Created on 27.05.2004
 */
package ru.myx.ae1.schedule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import ru.myx.ae1.storage.BaseSchedule;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.help.Text;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Change implements BaseSchedule {
	private static final Comparator<Action<?>>	COMPARATOR_ACTIONS_ASCENDING	= new Comparator<>() {
																					@Override
																					public int compare(
																							final Action<?> o1,
																							final Action<?> o2) {
																						return (int) (o1.getDate() - o2
																								.getDate());
																					}
																				};
	
	private final Scheduling					scheduling;
	
	private final Map<String, Action<?>>		current;
	
	private final Set<String>					deleted;
	
	private final Map<String, Action<?>>		created;
	
	private final String						objectId;
	
	/**
	 * @param change
	 */
	protected Change(final Change change) {
		this.scheduling = change.scheduling;
		this.current = change.internCurrent();
		this.deleted = change.internDeleted();
		this.created = change.internCreated();
		this.objectId = change.getObjectId();
	}
	
	Change(final Scheduling scheduling, final String objectId, final Action<?>[] actions) {
		this.scheduling = scheduling;
		this.current = new TreeMap<>();
		this.deleted = new TreeSet<>();
		this.created = new TreeMap<>();
		this.objectId = objectId;
		if (actions != null) {
			for (int i = actions.length - 1; i >= 0; --i) {
				this.current.put( actions[i].getKey(), actions[i] );
			}
		}
	}
	
	@Override
	public void clear() {
		this.deleted.addAll( this.current.keySet() );
		this.current.clear();
	}
	
	@Override
	public void commit() {
		this.scheduling.commitChange( this );
	}
	
	/**
	 * @param key
	 * @return action
	 */
	public Action<?> get(final String key) {
		return this.current.get( key );
	}
	
	Action<?>[] getCreated() {
		final Collection<Action<?>> collection = this.created.values();
		final int size = collection.size();
		if (size == 0) {
			return null;
		}
		final Action<?>[] actions = collection.toArray( new Action[size] );
		Arrays.sort( actions, Change.COMPARATOR_ACTIONS_ASCENDING );
		return actions;
	}
	
	/**
	 * @return actions
	 */
	public Action<?>[] getCurrent() {
		final Collection<Action<?>> collection = this.current.values();
		final int size = collection.size();
		if (size == 0) {
			return null;
		}
		final Action<?>[] actions = collection.toArray( new Action[size] );
		Arrays.sort( actions, Change.COMPARATOR_ACTIONS_ASCENDING );
		return actions;
	}
	
	String[] getDeleted() {
		final Collection<String> collection = this.deleted;
		final int size = collection.size();
		if (size == 0) {
			return null;
		}
		final String[] actionIds = collection.toArray( new String[size] );
		return actionIds;
	}
	
	final String getObjectId() {
		return this.objectId;
	}
	
	private final Map<String, Action<?>> internCreated() {
		return this.created;
	}
	
	private final Map<String, Action<?>> internCurrent() {
		return this.current;
	}
	
	private final Set<String> internDeleted() {
		return this.deleted;
	}
	
	@Override
	public boolean isEmpty() {
		return this.current.isEmpty();
	}
	
	/**
	 * @param action
	 */
	public void schedule(final Action<?> action) {
		final String key = action.getKey();
		if (this.current.put( key, action ) != null) {
			this.deleted.add( key );
		}
		this.created.put( key, action );
	}
	
	@Override
	public void schedule(
			final String name,
			final boolean replace,
			final long date,
			final String command,
			final BaseObject parameters) {
		final String key = this.objectId + "-" + Text.limitString( name, 18 );
		final Action<?> action = new Action<>( key, date, name, command, parameters );
		if (replace) {
			if (this.current.put( key, action ) != null) {
				this.deleted.add( key );
			}
			this.created.put( key, action );
		} else {
			if (!this.current.containsKey( key )) {
				this.current.put( key, action );
				this.created.put( key, action );
			}
		}
	}
	
	@Override
	public void scheduleCancel(final String name) {
		final String key = Text.limitString( this.objectId + "-" + name, 40 );
		if (this.current.remove( key ) != null) {
			this.deleted.add( key );
		}
	}
	
	@Override
	public void scheduleCancelGuid(final String key) {
		if (this.current.remove( key ) != null) {
			this.deleted.add( key );
		}
	}
	
	@Override
	public void scheduleFill(final BaseSchedule schedule, final boolean replace) {
		if (schedule == null) {
			return;
		}
		final Action<?>[] actions = this.getCurrent();
		if (actions != null) {
			for (int i = actions.length - 1; i >= 0; --i) {
				final String name = actions[i].getName();
				if (name != null && !"*".equals( name )) {
					schedule.schedule( name,
							replace,
							actions[i].getDate(),
							actions[i].getActor(),
							actions[i].getActorData() );
				} else {
					schedule.schedule( actions[i].getKey(),
							replace,
							actions[i].getDate(),
							actions[i].getActor(),
							actions[i].getActorData() );
				}
			}
		}
	}
	
	@Override
	public void scheduleGuid(
			final String key,
			final boolean replace,
			final long date,
			final String command,
			final BaseObject parameters) {
		final Action<?> action = new Action<>( key, date, "*", command, parameters );
		if (replace) {
			if (this.current.put( key, action ) != null) {
				this.deleted.add( key );
			}
			this.created.put( key, action );
		} else {
			if (!this.current.containsKey( key )) {
				this.current.put( key, action );
				this.created.put( key, action );
			}
		}
	}
}
