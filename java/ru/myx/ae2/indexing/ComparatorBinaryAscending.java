package ru.myx.ae2.indexing;

import java.util.Comparator;
import java.util.Map;

/**
 * @author myx
 * 
 */
public final class ComparatorBinaryAscending implements Comparator<Map.Entry<String, byte[]>> {
	@Override
	public final int compare(final Map.Entry<String, byte[]> o1, final Map.Entry<String, byte[]> o2) {
		try {
			final byte[] v1 = o1.getValue();
			final byte[] v2 = o2.getValue();
			final int len1 = v1.length;
			final int len2 = v2.length;
			final int lim = Math.min( len1, len2 );
			for (int k = 0; k < lim; k++) {
				final byte c1 = v1[k];
				final byte c2 = v2[k];
				if (c1 != c2) {
					return c1 - c2;
				}
			}
			return len1 - len2;
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException( "Not Binary: " + o1.getValue().getClass().getName() );
		}
	}
}
