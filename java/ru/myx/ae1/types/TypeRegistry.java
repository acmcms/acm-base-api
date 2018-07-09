/*
 * Created on 16.01.2005
 */
package ru.myx.ae1.types;

/**
 * @author myx
 */
public interface TypeRegistry {
	
	/**
	 * Should never return null. Default type should exist always.
	 * 
	 * @param typeName
	 * @return type
	 */
	public Type<?> getType(final String typeName);
	
	/**
	 * @return default type name
	 */
	public String getTypeNameDefault();
	
	/**
	 * @return string array
	 */
	public String[] getTypeNames();
	
	/**
	 * 
	 */
	public void start();
	
	/**
	 * 
	 */
	public void stop();
}
