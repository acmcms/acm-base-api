/*
 * Created on 31.08.2004 Window - Preferences - Java - Code Style - Code
 * Templates
 */
package ru.myx.ae2.indexing;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.status.StatusFiller;
import ru.myx.ae3.status.StatusInfo;
import ru.myx.ae3.vfs.EntryContainer;

/**
 * @author myx
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public final class IndexingDictionaryVfs extends IndexingDictionaryAbstract implements StatusFiller {
	private final EntryContainer		containerExact;
	
	private final EntryContainer		containerInexact;
	
	private final IndexingDictionary	parent;
	
	private final Object				lockExact;
	
	private final Object				lockInexact;
	
	private int							stsGetWordCode			= 0;
	
	private int							stsGetWordCodeHits		= 0;
	
	private int							stsStoreWordCode		= 0;
	
	private int							stsStoreWordCodeHits	= 0;
	
	
	/**
	 * @param containerExact
	 * @param containerInexact
	 * @param parent
	 */
	public IndexingDictionaryVfs(final EntryContainer containerExact,
			final EntryContainer containerInexact,
			final IndexingDictionary parent) {
	
		this.containerExact = containerExact;
		this.containerInexact = containerInexact;
		this.parent = parent;
		this.lockExact = new Object();
		this.lockInexact = new Object();
	}
	
	
	@Override
	public final boolean areCodesUnique() {
	
		return true;
	}
	
	
	@Override
	public final int[] getPatternCodes(
			final String pattern,
			final boolean exact,
			final boolean required) {
	
		return this.parent.getPatternCodes( pattern, exact, required );
	}
	
	
	@Override
	public final int getWordCode(
			final String word,
			final boolean exact,
			final boolean required) {
	
		this.stsGetWordCode++;
		if (word == null || word.length() == 0) {
			return 0;
		}
		final EntryContainer container = exact
				? this.containerExact
				: this.containerInexact;
		{
			final int integer = Convert.Any.toInt( container.getContentPrimitive( word, null ), -1 );
			if (integer != -1) {
				this.stsGetWordCodeHits++;
				return integer;
			}
		}
		synchronized (exact
				? this.lockExact
				: this.lockInexact) {
			{
				final int integer = Convert.Any.toInt( container.getContentPrimitive( word, null ), -1 );
				if (integer != -1) {
					this.stsGetWordCodeHits++;
					return integer;
				}
			}
			final int result = this.parent.getWordCode( word, exact, required );
			if (result <= 0) {
				return result;
			}
			{
				container.setContentCachedPrimitive( word, Base.forInteger( result ) ).baseValue();
			}
			return result;
		}
	}
	
	
	@Override
	public void statusFill(
			final StatusInfo status) {
	
		status.put( "DICT-VFS, getWordCode", Format.Compact.toDecimal( this.stsGetWordCode ) );
		status.put( "DICT-VFS, getWordCode hits", Format.Compact.toDecimal( this.stsGetWordCodeHits ) );
		status.put( "DICT-VFS, storeWordCode", Format.Compact.toDecimal( this.stsStoreWordCode ) );
		status.put( "DICT-VFS, storeWordCode hits", Format.Compact.toDecimal( this.stsStoreWordCodeHits ) );
	}
	
	
	@Override
	public final int storeWordCode(
			final String word,
			final boolean exact,
			final Object attachment) {
	
		this.stsStoreWordCode++;
		if (word == null || word.length() == 0) {
			return 0;
		}
		final EntryContainer container = exact
				? this.containerExact
				: this.containerInexact;
		{
			final int integer = Convert.Any.toInt( container.getContentPrimitive( word, null ), -1 );
			if (integer != -1) {
				this.stsStoreWordCodeHits++;
				return integer;
			}
		}
		synchronized (exact
				? this.lockExact
				: this.lockInexact) {
			{
				final int integer = Convert.Any.toInt( container.getContentPrimitive( word, null ), -1 );
				if (integer != -1) {
					this.stsStoreWordCodeHits++;
					return integer;
				}
			}
			final int result = this.parent.storeWordCode( word, exact, attachment );
			if (result <= 0) {
				return 0;
			}
			{
				container.setContentCachedPrimitive( word, Base.forInteger( result ) ).baseValue();
			}
			return result;
		}
	}
}
