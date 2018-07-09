package ru.myx.ae2.indexing;

import java.util.Comparator;
import java.util.Map;

/**
 * @author myx
 * 
 */
public final class ComparatorComparableAscending implements Comparator<Map.Entry<String, Comparable<Object>>> {
	@Override
	public final int compare(
			final Map.Entry<String, Comparable<Object>> o1,
			final Map.Entry<String, Comparable<Object>> o2) {
		try {
			return o1.getValue().compareTo( o2.getValue() );
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException( "Not Comparable: " + o1.getValue().getClass().getName() );
		}
	}
}
