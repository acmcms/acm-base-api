/*
 * Created on 22.06.2004
 */
package ru.myx.ae1.storage;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import ru.myx.ae1.control.ControlEntry;
import ru.myx.ae1.types.Type;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.reflect.Reflect;

/** @author myx
 * @param <T>
 */
public interface BaseEntry<T extends BaseEntry<?>> extends ControlEntry<T>, Comparable<Object> {

	/**
	 *
	 */
	static BaseObject PROTOTYPE = new BaseNativeObject(Reflect.classToBasePrototype(BaseEntry.class));

	@Override
	default BaseObject basePrototype() {

		return BaseEntry.PROTOTYPE;
	}

	/** Should return this.getGuid().compareTo(((BaseEntry<?>)o).getGuid()). */
	@Override
	int compareTo(Object o);

	/** @return change */
	BaseChange createChange();

	/** @return change */
	BaseChange createChild();

	/** Should return (o instanceof BaseEntry) &&
	 * this.getGuid().equals(((BaseEntry<?>)o).getGuid()). */
	@Override
	boolean equals(Object o);

	/** @return string array */
	String[] getAliases();

	/** @param name
	 * @return entry */
	BaseEntry<?> getChildByName(String name);

	/** @param path
	 * @return entry */
	default BaseEntry<?> getChildByPath(final String path) {

		if (path == null) {
			return this;
		}
		BaseEntry<?> current = this;
		for (final StringTokenizer st = new StringTokenizer(path, "/"); st.hasMoreTokens() && current != null;) {
			final String token = st.nextToken().trim();
			if (token.length() > 0) {
				current = current.getChildByName(token);
			}
		}
		return current;
	}

	/** @return list */
	List<ControlBasic<?>> getChildren();

	/** @param limit
	 * @param sort
	 * @return list */
	List<ControlBasic<?>> getChildren(int limit, String sort);

	/** @param limit
	 * @param sort
	 * @return list */
	List<ControlBasic<?>> getChildrenListable(int limit, String sort);

	/** @return date */
	long getCreated();

	/** @return string */
	String getEntryBehaviorListingSort();

	/** @param limit
	 * @param sort
	 * @return list */
	List<ControlBasic<?>> getFiles(int limit, String sort);

	/** @param limit
	 * @param sort
	 * @return list */
	List<ControlBasic<?>> getFilesListable(int limit, String sort);

	/** @return list */
	List<ControlBasic<?>> getFolders();

	/** @return list */
	List<ControlBasic<?>> getFoldersListable();

	/** @return string */
	String getGuid();

	/** @return history array */
	BaseHistory[] getHistory();

	/** @param historyId
	 * @return entry */
	BaseEntry<?> getHistorySnapshot(String historyId);

	/** @return string */
	String getLinkedIdentity();

	/** @return date */
	long getModified();

	/** @return owner */
	String getOwner();

	/** @return entry */
	BaseEntry<?> getParent();

	/** @return string */
	String getParentGuid();

	/** @return schedule */
	BaseSchedule getSchedule();

	/** @return int */
	int getState();

	/** @return storage */
	StorageImpl getStorageImpl();

	/** @return sync */
	BaseSync getSynchronization();

	/** @return type */
	Type<?> getType();

	/** @return string */
	String getTypeName();

	/** @param versionId
	 * @return entry */
	BaseEntry<?> getVersion(String versionId);

	/** @return version array */
	BaseVersion[] getVersions();

	/** @param parentProcess
	 * @param content
	 * @return object */
	default BaseObject handleResponseFilter(final ExecProcess parentProcess, final BaseObject content) {

		final Type<?> type = this.getType();
		return type == null
			? content
			: type.getResponse(parentProcess, this, content);
	}

	/** @return boolean */
	boolean isFolder();

	/** @param limit
	 * @param all
	 * @param timeout
	 * @param sort
	 * @param startDate
	 * @param endDate
	 * @param filter
	 * @return list */
	List<ControlBasic<?>> search(int limit, boolean all, long timeout, String sort, long startDate, long endDate, String filter);

	/** @param all
	 * @param timeout
	 * @param startDate
	 * @param endDate
	 * @param filter
	 * @return map */
	Map<Integer, Map<Integer, Map<String, Number>>> searchCalendar(boolean all, long timeout, long startDate, long endDate, String filter);

	/** @param limit
	 * @param all
	 * @param sort
	 * @param startDate
	 * @param endDate
	 * @param filter
	 * @return list */
	List<ControlBasic<?>> searchLocal(int limit, boolean all, String sort, long startDate, long endDate, String filter);

	/** @param all
	 * @param alphabetConversion
	 * @param defaultLetter
	 * @param filter
	 * @return map */
	Map<String, Number> searchLocalAlphabet(boolean all, Map<String, String> alphabetConversion, String defaultLetter, String filter);

	/** @param limit
	 * @param all
	 * @param sort
	 * @param alphabetConversion
	 * @param defaultLetter
	 * @param filterLetter
	 * @param filter
	 * @return list */
	List<ControlBasic<?>>
			searchLocalAlphabet(int limit, boolean all, String sort, Map<String, String> alphabetConversion, String defaultLetter, String filterLetter, String filter);

	/** @param all
	 * @param startDate
	 * @param endDate
	 * @param filter
	 * @return map */
	Map<Integer, Map<Integer, Map<String, Number>>> searchLocalCalendar(boolean all, long startDate, long endDate, String filter);
}
