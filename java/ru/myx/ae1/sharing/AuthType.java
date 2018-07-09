/*
 * Created on 17.05.2006
 */
package ru.myx.ae1.sharing;

import java.util.Collections;

import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.control.MultivariantString;

/**
 * @author myx
 * 
 */
public enum AuthType {
	/**
     * 
     */
	SITEFORM("Site login form", "Форма входа на сайте", AuthLevels.AL_AUTHORIZED_NORMAL),
	
	/**
     * 
     */
	SYSTEM("System authorization", "Системная авторизация", AuthLevels.AL_AUTHORIZED_HIGH),
	
	/**
	 * 
	 */
	;
	private final Object	string;
	
	private final int		level;
	
	AuthType(final String english, final String russian, final int level) {
		this.string = MultivariantString.getString( english, Collections.singletonMap( "ru", russian ) );
		this.level = level;
	}
	
	/**
	 * @return
	 */
	public int getLevel() {
		return this.level;
	}
	
	@Override
	public String toString() {
		return this.string.toString();
	}
}
