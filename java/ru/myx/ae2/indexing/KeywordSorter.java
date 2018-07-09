/*
 * Created on 08.02.2006
 */
package ru.myx.ae2.indexing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ru.myx.ae3.help.Convert;

/**
 * @author myx
 */
public class KeywordSorter {
	/**
	 * 
	 */
	static final Comparator<Map.Entry<String, ? extends Number>>	COMPARATOR	= new Comparator<Map.Entry<String, ? extends Number>>() {
																					@Override
																					public final int compare(
																							final Map.Entry<String, ? extends Number> o1,
																							final Map.Entry<String, ? extends Number> o2) {
																						final double v1 = o1.getValue()
																								.doubleValue();
																						final double v2 = o2.getValue()
																								.doubleValue();
																						if (v1 > v2) {
																							return -1;
																						}
																						if (v1 < v2) {
																							return 1;
																						}
																						final int l1 = o1.getKey()
																								.length();
																						final int l2 = o2.getKey()
																								.length();
																						return l1 > l2
																								? -1
																								: l1 < l2
																										? 1
																										: 0;
																					}
																				};
	
	/**
	 * @param target
	 * @param source
	 * @return list
	 */
	public static final List<String> sortKeywords(
			final List<String> target,
			final Collection<Map.Entry<String, AtomicInteger>> source) {
		final Map.Entry<String, AtomicInteger>[] entries = Convert.Array.toAny( source.toArray( new Map.Entry[source
				.size()] ) );
		Arrays.sort( entries, KeywordSorter.COMPARATOR );
		for (final Map.Entry<String, AtomicInteger> entry : entries) {
			target.add( entry.getKey() );
		}
		return target;
	}
}
