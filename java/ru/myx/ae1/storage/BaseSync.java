/*
 * Created on 01.08.2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.ae1.storage;

/** @author myx
 *
 *         Window - Preferences - Java - Code Style - Code Templates */
public interface BaseSync {

	/**
	 *
	 */
	public void clear();

	/**
	 *
	 */
	public void commit();

	/** @return string array */
	public String[] getExportSynchronizations();

	/** @return string array */
	public String[] getImportSynchronizations();

	/** @return boolean */
	public boolean isEmpty();

	/** @param guid
	 */
	public void synchronizeExport(final String guid);

	/** @param guid
	 */
	public void synchronizeExportCancel(final String guid);

	/** @param syncronization
	 */
	public void synchronizeFill(final BaseSync syncronization);

	/** @param guid
	 */
	public void synchronizeImport(final String guid);

	/** @param guid
	 */
	public void synchronizeImportCancel(final String guid);
}
