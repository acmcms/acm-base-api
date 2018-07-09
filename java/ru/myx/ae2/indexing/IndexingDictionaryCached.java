/*
 * Created on 31.08.2004
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.ae2.indexing;

import ru.myx.ae3.cache.CacheL2;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.status.StatusFiller;
import ru.myx.ae3.status.StatusInfo;

/**
 * @author myx
 */
public final class IndexingDictionaryCached extends IndexingDictionaryAbstract implements StatusFiller {
	private static final int[]				NULL_OBJECT	= new int[] { 1, 1 };
	
	private final IndexingDictionary		parent;
	
	private final CacheL2<int[]>			cache;
	
	private final CreatorDictionaryPattern	creatorExactOptional;
	
	private final CreatorDictionaryPattern	creatorInexactOptional;
	
	private final CreatorDictionaryPattern	creatorExactRequired;
	
	private final CreatorDictionaryPattern	creatorInexactRequired;
	
	private int								stsGetPatternCodes;
	
	/**
	 * @param parent
	 * @param cache
	 */
	public IndexingDictionaryCached(final IndexingDictionary parent, final CacheL2<int[]> cache) {
		this.parent = parent;
		this.cache = cache;
		this.creatorExactOptional = new CreatorDictionaryPattern( parent,
				true,
				false,
				IndexingDictionaryCached.NULL_OBJECT );
		this.creatorInexactOptional = new CreatorDictionaryPattern( parent,
				false,
				false,
				IndexingDictionaryCached.NULL_OBJECT );
		this.creatorExactRequired = new CreatorDictionaryPattern( parent,
				true,
				true,
				IndexingDictionaryCached.NULL_OBJECT );
		this.creatorInexactRequired = new CreatorDictionaryPattern( parent,
				false,
				true,
				IndexingDictionaryCached.NULL_OBJECT );
	}
	
	@Override
	public boolean areCodesUnique() {
		return this.parent.areCodesUnique();
	}
	
	@Override
	public int[] getPatternCodes(final String pattern, final boolean exact, final boolean required) {
		this.stsGetPatternCodes++;
		final int[] result = exact
				? required
						? this.cache.get( pattern, "#E", null, pattern, this.creatorExactRequired )
						: this.cache.get( pattern, "$E", null, pattern, this.creatorExactOptional )
				: required
						? this.cache.get( pattern, "#I", null, pattern, this.creatorInexactRequired )
						: this.cache.get( pattern, "$I", null, pattern, this.creatorInexactOptional );
		return result == IndexingDictionaryCached.NULL_OBJECT
				? null
				: result;
	}
	
	@Override
	public int getWordCode(final String word, final boolean exact, final boolean required) {
		return this.parent.getWordCode( word, exact, required );
	}
	
	@Override
	public void statusFill(final StatusInfo status) {
		status.put( "DICT-CACHE, getPatternCodes", Format.Compact.toDecimal( this.stsGetPatternCodes ) );
		status.put( "DICT-CACHE, getPatternCodes hits",
				Format.Compact.toDecimal( this.stsGetPatternCodes
						- this.creatorExactOptional.stsCount
						- this.creatorExactRequired.stsCount
						- this.creatorInexactOptional.stsCount
						- this.creatorInexactRequired.stsCount ) );
		status.put( "DICT-CACHE, getPatternCodes, exactOpt",
				Format.Compact.toDecimal( this.creatorExactOptional.stsCount ) );
		status.put( "DICT-CACHE, getPatternCodes, exactReq",
				Format.Compact.toDecimal( this.creatorExactRequired.stsCount ) );
		status.put( "DICT-CACHE, getPatternCodes, inexactOpt",
				Format.Compact.toDecimal( this.creatorInexactOptional.stsCount ) );
		status.put( "DICT-CACHE, getPatternCodes, inexactReq",
				Format.Compact.toDecimal( this.creatorInexactRequired.stsCount ) );
		if (this.parent instanceof StatusFiller) {
			((StatusFiller) this.parent).statusFill( status );
		}
	}
	
	@Override
	public int storeWordCode(final String word, final boolean exact, final Object attachment) {
		return this.parent.storeWordCode( word, exact, attachment );
	}
}
