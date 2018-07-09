package ru.myx.ae2.indexing;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author myx
 * 
 */
public final class ComparatorNumericDescending implements Comparator<Map.Entry<String, AtomicInteger>> {
	@Override
	public final int compare(final Map.Entry<String, AtomicInteger> o1, final Map.Entry<String, AtomicInteger> o2) {
		return o2.getValue().intValue() - o1.getValue().intValue();
	}
}
