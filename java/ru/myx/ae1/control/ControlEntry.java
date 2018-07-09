/*
 * Created on 11.05.2006
 */
package ru.myx.ae1.control;

import java.util.Collection;

import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlActor;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.i3.Handler;
import ru.myx.ae3.produce.Reproducible;
import ru.myx.ae3.reflect.Reflect;

/**
 * @author myx
 * @param <T>
 * 			
 */
public interface ControlEntry<T extends ControlEntry<?>> extends ControlActor<T>, Handler, Reproducible {
	
	/**
	 * 
	 */
	public static final BaseObject PROTOTYPE = new BaseNativeObject(Reflect.classToBasePrototype(ControlEntry.class));
	
	@Override
	default BaseObject basePrototype() {
		
		return ControlEntry.PROTOTYPE;
	}
	
	/**
	 * @return commandset
	 */
	public ControlCommandset getForms();
	
	/**
	 * @return string
	 */
	public String getLocation();
	
	/**
	 * @return string
	 */
	public String getLocationAbsolute();
	
	/**
	 * @return string
	 */
	public String getLocationControl(); // ???? suspect!
	
	/**
	 * @return string array
	 */
	public Collection<String> getLocationControlAll(); // ???? suspect!
}
