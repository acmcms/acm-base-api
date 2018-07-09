package ru.myx.ae2.indexing;

/**
 * @author myx
 * 
 */
public abstract class IndexingDictionaryAbstract implements IndexingDictionary {
	@Override
	public abstract int getWordCode(final String word, boolean exact, boolean required);
	
	@Override
	public abstract int storeWordCode(final String word, final boolean exact, final Object attachment);
}
