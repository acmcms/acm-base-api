package ru.myx.ae1;

import java.util.Properties;

import ru.myx.ae1.know.Server;

/**
 * @author myx
 * 
 */
public interface PluginInstance {
	/**
	 * 
	 */
	public void destroy();
	
	/**
	 * 
	 */
	public void register();
	
	/**
	 * @param server
	 * @param info
	 * @throws IllegalArgumentException
	 */
	public void setup(final Server server, final Properties info) throws IllegalArgumentException;
	
	/**
	 * 
	 */
	public void start();
	
	@Override
	public String toString();
}
