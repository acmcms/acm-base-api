/*
 * Created on 31.03.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.ae2.indexing;

/**
 * @author myx
 * 
 */
public interface IndexingDictionary {
	/**
	 * 
	 */
	public static final IndexingDictionary	DICT_HASHCODE	= new IndexingDictionaryHashCode();
	
	/**
	 * @return boolean
	 */
	public boolean areCodesUnique();
	
	/**
	 * @param pattern
	 * @param exact
	 * @param required
	 * @return int array
	 */
	public int[] getPatternCodes(final String pattern, final boolean exact, final boolean required);
	
	/**
	 * returns 0 for a stop word!
	 * 
	 * @param word
	 * @param exact
	 * @param required
	 * @return int
	 */
	public int getWordCode(final String word, final boolean exact, boolean required);
	
	/**
	 * @param word
	 * @param exact
	 * @param attachment
	 * @return int
	 */
	public int storeWordCode(final String word, final boolean exact, final Object attachment);
}
