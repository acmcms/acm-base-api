/*
 * Created on 08.02.2006
 */
package ru.myx.ae2.indexing;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ru.myx.ae3.help.Create;

/**
 * @author myx
 * 
 */
class WeighterFrequencyStemmed {
	/**
	 * @param result
	 * @param content
	 * @return map
	 */
	public static final Map<String, AtomicInteger> analyzeContent(
			final Map<String, AtomicInteger> result,
			final List<String> content) {
		final Map<String, AtomicInteger> stemmed = Create.treeMap();
		for (final String word : content) {
			final String signature = StemmerSimple.transform( word );
			final AtomicInteger counter = stemmed.get( signature );
			if (counter == null) {
				final AtomicInteger created = new AtomicInteger( 1 );
				stemmed.put( signature, created );
				result.put( word, created );
			} else {
				counter.incrementAndGet();
				result.put( word, counter );
			}
		}
		return result;
	}
}
