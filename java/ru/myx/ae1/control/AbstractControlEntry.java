/*
 * Created on 25.04.2006
 */
package ru.myx.ae1.control;

import java.util.Collection;
import java.util.Collections;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractActor;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.reflect.ReflectionExplicit;
import ru.myx.ae3.reflect.ReflectionManual;
import ru.myx.ae3.serve.ServeRequest;

/**
 * @author myx
 * @param <T>
 * 
 */
@ReflectionManual
public abstract class AbstractControlEntry<T extends AbstractControlEntry<?>> extends AbstractActor<T> implements ControlEntry<T> {
	
	
	@Override
	public BaseObject getData() {
		
		
		return BaseObject.UNDEFINED;
	}

	@Override
	public ControlCommandset getForms() {
		
		
		return null;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("static-method")
	public String getGuid() {
		
		
		return null;
	}

	@Override
	public final String getLocation() {
		
		
		return this.getLocation(Exec.currentProcess());
	}

	/**
	 * @param process
	 * @return location
	 */
	@ReflectionExplicit
	public final String getLocation(final ExecProcess process) {
		
		
		final Context context = Context.getContext(process);
		final String guid = this.getGuid();
		if (guid == null) {
			return context.getServer().fixLocation(process, this.getLocationControl(), false);
		}
		final String id = ".loc-" + guid;
		final BaseObject flags = context.getFlags();
		String location = Base.getString(flags, id, null);
		if (location != null) {
			return location;
		}
		location = context.getServer().fixLocation(process, this.getLocationControl(), false);
		if (location == null) {
			return null;
		}
		flags.baseDefine(id, location);
		return location;
	}

	@Override
	public final String getLocationAbsolute() {
		
		
		return this.getLocationAbsolute(Exec.currentProcess());
	}

	/**
	 * @param process
	 * @return
	 */
	public final String getLocationAbsolute(final ExecProcess process) {
		
		
		final Context context = Context.getContext(process);
		final String guid = this.getGuid();
		if (guid == null) {
			return context.getServer().fixLocation(process, this.getLocationControl(), true);
		}
		final String id = ".abs-" + guid;
		final BaseObject flags = context.getFlags();
		String location = Base.getString(flags, id, null);
		if (location != null) {
			return location;
		}
		location = context.getServer().fixLocation(process, this.getLocationControl(), true);
		if (location == null) {
			return null;
		}
		flags.baseDefine(id, location);
		return location;
	}

	@Override
	public Collection<String> getLocationControlAll() {
		
		
		final String locationControl = this.getLocationControl();
		if (locationControl == null) {
			return null;
		}
		return Collections.singleton(locationControl);
	}

	@Override
	public ReplyAnswer onQuery(final ServeRequest request) {
		
		
		return Reply.empty("NONE", request);
	}

	@Override
	public String restoreFactoryIdentity() {
		
		
		return null;
	}

	@Override
	public String restoreFactoryParameter() {
		
		
		return null;
	}
}
