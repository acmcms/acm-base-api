/*
 * Created on 08.02.2006
 */
package ru.myx.ae2.indexing;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author myx
 * 
 */
class WeighterFrequency {
	/**
	 * @param result
	 * @param content
	 * @return map
	 */
	public static final Map<String, AtomicInteger> analyzeContent(
			final Map<String, AtomicInteger> result,
			final List<String> content) {
		for (final String word : content) {
			final AtomicInteger counter = result.get( word );
			if (counter == null) {
				result.put( word, new AtomicInteger( 1 ) );
			} else {
				counter.incrementAndGet();
			}
		}
		return result;
	}
}
