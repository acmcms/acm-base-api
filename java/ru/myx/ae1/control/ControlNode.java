/*
 * Created on 11.05.2006
 */
package ru.myx.ae1.control;

import java.util.List;

import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.ControlContainer;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.i3.RequestHandler;
import ru.myx.ae3.reflect.Reflect;

/**
 * @author myx
 * @param <T>
 * 			
 */
public interface ControlNode<T extends ControlNode<?>> extends ControlEntry<T>, ControlContainer<T> {
	
	/**
	 * 
	 */
	public static final BaseObject PROTOTYPE = new BaseNativeObject(Reflect.classToBasePrototype(ControlNode.class));
	
	@Override
	default BaseObject basePrototype() {
		
		return ControlNode.PROTOTYPE;
	}
	
	/**
	 * @param node
	 */
	public void bind(final ControlNode<?> node);
	
	/**
	 * @param name
	 * @return child node
	 */
	public ControlNode<?> getChildByName(final String name);
	
	/**
	 * @return children
	 */
	public ControlNode<?>[] getChildren();
	
	/**
	 * @return children
	 */
	public ControlNode<?>[] getChildrenExternal();
	
	/**
	 * @param key
	 * @return entry
	 */
	public ControlEntry<?> getContentEntry(final String key);
	
	/**
	 * @return listing fieldset
	 */
	public ControlFieldset<?> getContentFieldset();
	
	/**
	 * 
	 * @return contents
	 */
	public List<ControlBasic<?>> getContents();
	
	/**
	 * @return underlying entry (or this)
	 */
	public ControlEntry<?> getEntry();
	
	/**
	 * @return boolean
	 */
	public boolean hasChildren();
	
	/**
	 * @return handler
	 */
	public RequestHandler substituteHandler();
}
