/*
 * Created on 08.02.2006
 */
package ru.myx.ae2.indexing;

import java.util.Comparator;

/**
 * @author myx
 * 
 */
public final class CollectionSizeComparator implements Comparator<Condition> {
	@Override
	public final int compare(final Condition o1, final Condition o2) {
		return o1.size() - o2.size();
	}
}
