/**
 *
 */
package ru.myx.ae1.storage;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/** @author myx */
public abstract class AbstractSync implements BaseSync {

	private final Set<String> exportListOriginal;

	private final Set<String> importListOriginal;

	private final Set<String> exportListCurrent;

	private final Set<String> importListCurrent;

	/**
	 *
	 */
	protected AbstractSync() {

		this.exportListOriginal = new TreeSet<>();
		this.importListOriginal = new TreeSet<>();
		this.exportListCurrent = new TreeSet<>(this.exportListOriginal);
		this.importListCurrent = new TreeSet<>(this.importListOriginal);
	}

	/** @param original
	 */
	protected AbstractSync(final BaseSync original) {

		this.exportListOriginal = new TreeSet<>(Arrays.asList(original.getExportSynchronizations()));
		this.importListOriginal = new TreeSet<>(Arrays.asList(original.getImportSynchronizations()));
		this.exportListCurrent = new TreeSet<>(this.exportListOriginal);
		this.importListCurrent = new TreeSet<>(this.importListOriginal);
	}

	@Override
	public void clear() {

		this.exportListCurrent.clear();
		this.importListCurrent.clear();
	}

	@Override
	public abstract void commit();

	@Override
	public String[] getExportSynchronizations() {

		return this.exportListCurrent.toArray(new String[this.exportListCurrent.size()]);
	}

	@Override
	public String[] getImportSynchronizations() {

		return this.importListCurrent.toArray(new String[this.importListCurrent.size()]);
	}

	@Override
	public boolean isEmpty() {

		return this.exportListCurrent.isEmpty() && this.importListCurrent.isEmpty();
	}

	@Override
	public void synchronizeExport(final String guid) {

		this.exportListCurrent.add(guid);
	}

	@Override
	public void synchronizeExportCancel(final String guid) {

		this.exportListCurrent.remove(guid);
	}

	@Override
	public void synchronizeFill(final BaseSync synchronization) {

		for (final String key : this.exportListOriginal) {
			if (!this.exportListCurrent.contains(key)) {
				synchronization.synchronizeExportCancel(key);
			}
		}
		for (final String key : this.importListOriginal) {
			if (!this.importListCurrent.contains(key)) {
				synchronization.synchronizeImportCancel(key);
			}
		}
		for (final String key : this.exportListCurrent) {
			if (!this.exportListOriginal.contains(key)) {
				synchronization.synchronizeExport(key);
			}
		}
		for (final String key : this.importListCurrent) {
			if (!this.importListOriginal.contains(key)) {
				synchronization.synchronizeImport(key);
			}
		}
	}

	@Override
	public void synchronizeImport(final String guid) {

		this.importListCurrent.add(guid);
	}

	@Override
	public void synchronizeImportCancel(final String guid) {

		this.importListCurrent.remove(guid);
	}
}
