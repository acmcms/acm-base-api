/*
 * Created on 09.07.2004
 */
package ru.myx.ae2.indexing;

import ru.myx.ae3.cache.CreationHandlerObject;

final class CreatorDictionaryPattern implements CreationHandlerObject<Void, int[]> {
	
	private static final long TTL = 15L * 60_000L;

	int stsCount = 0;

	private final IndexingDictionary parent;

	private final boolean exact;

	private final boolean required;

	private final int[] NULL_OBJECT;

	CreatorDictionaryPattern(final IndexingDictionary parent, final boolean exact, final boolean required, final int[] NULL_OBJECT) {
		
		this.parent = parent;
		this.exact = exact;
		this.required = required;
		this.NULL_OBJECT = NULL_OBJECT;
	}

	@Override
	public final int[] create(final Void attachment, final String key) {
		
		this.stsCount++;
		final int[] result;
		result = this.parent.getPatternCodes(key, this.exact, this.required);
		return result == null
			? this.NULL_OBJECT
			: result;
	}

	@Override
	public final long getTTL() {
		
		return CreatorDictionaryPattern.TTL;
	}
}
