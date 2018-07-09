/*
 * Created on 31.03.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.ae2.indexing;

import java.util.Collection;

/**
 * @author myx
 * 
 */
public interface IndexingStemmer {
	/**
	 * @author myx
	 * 
	 */
	static final class DummyStemmer implements IndexingStemmer {
		DummyStemmer() {
			// empty
		}
		
		@Override
		public void fillForms(final String word, final Collection<String> target) {
			// empty
		}
	}
	
	/**
	 * @author myx
	 * 
	 */
	static final class SimpleStemmer implements IndexingStemmer {
		SimpleStemmer() {
			// empty
		}
		
		@Override
		public void fillForms(final String original, final Collection<String> target) {
			if (original == null) {
				return;
			}
			target.add( StemmerSimple.transform( original ) );
		}
	}
	
	/**
     * 
     */
	public static final IndexingStemmer	SIMPLE_STEMMER	= new SimpleStemmer();
	
	/**
     * 
     */
	public static final IndexingStemmer	DUMMY_STEMMER	= new DummyStemmer();
	
	/**
	 * @param word
	 * @param target
	 */
	public void fillForms(final String word, final Collection<String> target);
}
