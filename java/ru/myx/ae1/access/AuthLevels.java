/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.access;

/**
 * @author myx
 * 
 */
public final class AuthLevels {
	/**
	 * 
	 */
	public static final int	AL_UNAUTHORIZED					= 1;
	
	/**
	 * 
	 */
	public static final int	AL_AUTHORIZED_AUTOMATICALLY		= 2;
	
	/**
	 * 
	 */
	public static final int	AL_AUTHORIZED_3RDPARTY			= 5;
	
	/**
	 * 
	 */
	public static final int	AL_AUTHORIZED_NORMAL			= 7;
	
	/**
	 * 
	 */
	public static final int	AL_AUTHORIZED_NORMAL_LOCAL		= 9;
	
	/**
	 * 
	 */
	public static final int	AL_AUTHORIZED_HIGH				= 15;
	
	/**
	 * 
	 */
	public static final int	AL_AUTHORIZED_HIGHER			= 20;
	
	/**
	 * 
	 */
	public static final int	AL_AUTHORIZED_EXCLUSIVE			= 25;
	
	/**
	 * 
	 */
	public static final int	AL_AUTHORIZED_SYSTEM_EXCLUSIVE	= 35;
	
	private AuthLevels() {
		// ignore
	}
}
