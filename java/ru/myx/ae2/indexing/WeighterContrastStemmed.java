/*
 * Created on 08.02.2006
 */
package ru.myx.ae2.indexing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ru.myx.ae3.help.Create;

/**
 * @author myx
 * 
 */
public class WeighterContrastStemmed {
	private static final int	SEGMENTS1	= 3;
	
	private static final int	SEGMENTS2	= 7;
	
	/**
	 * @param result
	 * @param content
	 * @return map
	 */
	public static final Map<String, AtomicInteger> analyzeContent(
			final Map<String, AtomicInteger> result,
			final List<String> content) {
		final Map<String, List<Integer>> stemmed = Create.treeMap();
		final Map<String, List<Integer>> words = Create.treeMap();
		int wordIndex = 0;
		for (final String word : content) {
			final String signature = StemmerSimple.transform( word );
			final List<Integer> counter = stemmed.get( signature );
			if (counter == null) {
				final List<Integer> created = new ArrayList<>();
				created.add( Integer.valueOf( wordIndex ) );
				stemmed.put( signature, created );
				words.put( word, created );
			} else {
				counter.add( Integer.valueOf( wordIndex ) );
				words.put( word, counter );
			}
			wordIndex++;
		}
		final boolean[] pattern1 = new boolean[WeighterContrastStemmed.SEGMENTS1];
		final boolean[] pattern2 = new boolean[WeighterContrastStemmed.SEGMENTS2];
		for (final Map.Entry<String, List<Integer>> current : words.entrySet()) {
			for (final Integer position : current.getValue()) {
				final int segment1 = (int) (1.0 * WeighterContrastStemmed.SEGMENTS1 * position.intValue() / wordIndex);
				pattern1[segment1] = true;
				final int segment2 = (int) (1.0 * WeighterContrastStemmed.SEGMENTS2 * position.intValue() / wordIndex);
				pattern2[segment2] = true;
			}
			int weight1 = 0;
			for (int i = WeighterContrastStemmed.SEGMENTS1 - 1; i >= 0; --i) {
				if (pattern1[i]) {
					weight1++;
				}
				pattern1[i] = false;
			}
			int weight2 = 0;
			for (int i = WeighterContrastStemmed.SEGMENTS2 - 1; i >= 0; --i) {
				if (pattern2[i]) {
					weight2++;
				}
				pattern2[i] = false;
			}
			final int weight = 1000
					* weight1
					/ WeighterContrastStemmed.SEGMENTS1
					+ 1000
					* weight2
					/ WeighterContrastStemmed.SEGMENTS2
					+ 1000
					* current.getValue().size()
					/ wordIndex;
			result.put( current.getKey(), new AtomicInteger( weight ) );
		}
		return result;
	}
}
