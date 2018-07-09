/*
 * Created on 13.04.2006
 */
package ru.myx.ae1.sharing;

import java.util.Comparator;


/**
 * Ensures correct share sort as well as uniquiness for set comparators.
 * 
 * It gives result -1, 0 , 1
 * 
 * @author myx
 * 
 */
final class ComparatorShare implements Comparator<Share<?>> {
	@Override
	public final int compare(final Share<?> o1, final Share<?> o2) {
		return Math.max( -1,
				Math.min( 1, 0
						+ 0x0FFFFF
						* (o1.getAccessType().ordinal() - o2.getAccessType().ordinal())
						+ 0x003FFF
						* (o1.getSecureType().ordinal() - o2.getSecureType().ordinal())
						+ 0x000004
						* (o1.getPath().length() - o2.getPath().length())
						+ 0x000002
						* (o1.getKey().length() - o2.getKey().length())
						+ 0x000001
						* o1.getKey().compareTo( o2.getKey() )
						+ 0 ) );
	}
}
