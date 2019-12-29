package ru.myx.ae3.cache;

import java.lang.ref.Cleaner;

/** Title: ae1 Base definitions Description: Copyright: Copyright (c) 2001 Company:
 *
 * @author Alexander I. Kharitchev
 * @version 1.0
 * @param <T> */

abstract class Holder<T> {

	static int ohCreated = 0;
	
	static int ohFiled = 0;
	
	static int ohtFiled = 0;
	
	static int ohCurrent = 0;
	
	static int ohFinalized = 0;
	
	private static final Cleaner CLEANER = Cleaner.create();
	
	private static void clean() {

		Holder.ohFinalized++;
		Holder.ohCurrent--;
	}
	
	boolean fileBased = false;
	
	Holder() {

		Holder.ohCreated++;
		Holder.ohCurrent++;
		
		Holder.CLEANER.register(this, Holder::clean);
	}
	
	/** @return object
	 * @throws Throwable */
	public abstract T getHolderValue() throws Throwable;
}
