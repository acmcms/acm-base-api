/*
 * Created on 22.03.2006
 */
package ru.myx.ae1.provide;

import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.ExecProcess;

/**
 * @author myx
 * 
 */
public interface TaskRunner {
	/**
	 * @param settings
	 * @return string
	 */
	public String describe(final BaseObject settings);
	
	/**
	 * @return fieldset
	 */
	public ControlFieldset<?> getFieldset();
	
	/**
	 * @return class
	 */
	public Class<?> getParameterClass();
	
	/**
	 * @return string
	 */
	public String getTitle();
	
	/**
	 * @param process
	 * @param settings
	 * @return object
	 * @throws Throwable
	 */
	public Object run(final ExecProcess process, final BaseObject settings) throws Throwable;
}
