package ru.myx.ae2.indexing;

import java.util.Comparator;
import java.util.Map;

/**
 * @author myx
 * 
 */
public final class ComparatorStringInsencetiveDescending implements Comparator<Map.Entry<String, String>> {
	@Override
	public final int compare(final Map.Entry<String, String> o1, final Map.Entry<String, String> o2) {
		try {
			return o2.getValue().toLowerCase().compareTo( o1.getValue().toLowerCase() );
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException( "Not Comparable: " + o1.getValue().getClass().getName() );
		}
	}
}
