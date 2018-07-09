/*
 * Created on 17.05.2006
 */
package ru.myx.ae1.sharing;

import java.util.Collections;

import ru.myx.ae1.control.MultivariantString;

/**
 * @author myx
 * 
 */
public enum SecureType {
	/**
     * 
     */
	NONE("Disabled", "Отключена"),
	/**
     * 
     */
	ANY("Any mode", "Любая"),
	/**
     * 
     */
	ONDEMAND("On demand", "По необходимости"),
	/**
     * 
     */
	REQUIRED("Require", "Требуется");
	private final Object	name;
	
	SecureType(final Object name) {
		this.name = name;
	}
	
	SecureType(final String english, final String russian) {
		this( MultivariantString.getString( english, Collections.singletonMap( "ru", russian ) ) );
	}
	
	@Override
	public String toString() {
		return this.name.toString();
	}
}
