package ru.myx.ae2.indexing;

/**
 * @author myx
 * 
 */
public final class IndexingDictionaryHashCode extends IndexingDictionaryAbstract {
	@Override
	public boolean areCodesUnique() {
		return false;
	}
	
	@Override
	public int[] getPatternCodes(final String pattern, final boolean exact, final boolean required) {
		if (pattern == null) {
			return null;
		}
		final int pos1 = pattern.indexOf( '?' );
		final int pos2 = pattern.indexOf( '*' );
		if (pos1 <= 0 && pos2 <= 0) {
			return null;
		} else if (pos1 <= 0 || pos2 <= pos1 && pos2 > 0) {
			return new int[] { this.getWordCode( pattern.substring( 0, pos2 ), exact, required ) };
		} else {
			return new int[] { this.getWordCode( pattern.substring( 0, pos1 ), exact, required ) };
		}
	}
	
	@Override
	public int getWordCode(final String word, final boolean exact, final boolean required) {
		if (word == null) {
			return 0;
		}
		return word.hashCode();
	}
	
	@Override
	public int storeWordCode(final String word, final boolean exact, final Object attachment) {
		return this.getWordCode( word, exact, false );
	}
}
