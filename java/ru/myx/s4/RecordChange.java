package ru.myx.s4;

import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.vfs.TreeLinkType;

/**
 * @author myx
 * 
 */
public interface RecordChange extends Record {
	
	/**
	 * 
	 */
	void cancel();
	
	/**
	 * 
	 */
	void commit();
	
	/**
	 * @return original record
	 */
	Record getRecord();
	
	/**
	 * @param copier
	 */
	void setBinaryData(final TransferCopier copier);
	
	/**
	 * @param linkageKey
	 * @param linkageType
	 * @param record
	 */
	void setContainer(final String linkageKey, final TreeLinkType linkageType, final Record record);
	
	/**
	 * @param linkageKey
	 * @param linkageType
	 * @param record
	 */
	void setContent(final String linkageKey, final TreeLinkType linkageType, final Record record);
}
