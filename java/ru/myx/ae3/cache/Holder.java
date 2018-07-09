package ru.myx.ae3.cache;

/**
 * Title: ae1 Base definitions Description: Copyright: Copyright (c) 2001
 * Company:
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 * @param <T>
 */

abstract class Holder<T> {
	static int	ohCreated	= 0;
	
	static int	ohFiled		= 0;
	
	static int	ohtFiled	= 0;
	
	static int	ohCurrent	= 0;
	
	static int	ohFinalized	= 0;
	
	boolean		fileBased	= false;
	
	Holder() {
		Holder.ohCreated++;
		Holder.ohCurrent++;
	}
	
	@Override
	protected void finalize() throws java.lang.Throwable {
		Holder.ohFinalized++;
		Holder.ohCurrent--;
		super.finalize();
	}
	
	/**
	 * @return object
	 * @throws Throwable
	 */
	public abstract T getHolderValue() throws Throwable;
}
