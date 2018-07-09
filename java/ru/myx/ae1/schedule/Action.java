/*
 * Created on 27.05.2004
 */
package ru.myx.ae1.schedule;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractBasic;
import ru.myx.ae3.exec.Exec;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * @param <T>
 */
public class Action<T extends Action<?>> extends AbstractBasic<T> {
	private final String		key;
	
	private final int			state;
	
	private final long			date;
	
	private final String		owner;
	
	private final String		name;
	
	private final String		actor;
	
	private final BaseObject	actorData;
	
	/**
	 * @param action
	 */
	protected Action(final Action<?> action) {
		this( action.getKey(), action.getState(), action.getDate(), action.getOwner(), action.getName(), action
				.getActor(), action.getActorData() );
	}
	
	Action(final String key,
			final int state,
			final long date,
			final String owner,
			final String name,
			final String actor,
			final BaseObject data) {
		this.key = key;
		this.state = state;
		this.date = date;
		this.owner = owner;
		this.name = name;
		this.actor = actor;
		this.actorData = data;
	}
	
	/**
	 * @param key
	 * @param date
	 * @param name
	 * @param actor
	 * @param data
	 */
	public Action(final String key, final long date, final String name, final String actor, final BaseObject data) {
		this.key = key;
		this.state = 0 /* SCHEDULE_STATE_WAITING */;
		this.date = date;
		this.owner = Context.getUserId( Exec.currentProcess() );
		this.name = name;
		this.actor = actor;
		this.actorData = data;
	}
	
	/**
	 * @return string
	 */
	public String getActor() {
		return this.actor;
	}
	
	/**
	 * @return map
	 */
	public BaseObject getActorData() {
		return this.actorData;
	}
	
	/**
	 * @return date
	 */
	public long getDate() {
		return this.date;
	}
	
	/**
	 * @return string
	 */
	@Override
	public String getKey() {
		return this.key;
	}
	
	/**
	 * @return string
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return string
	 */
	public String getOwner() {
		return this.owner;
	}
	
	/**
	 * @return int
	 */
	public int getState() {
		return this.state;
	}
}
