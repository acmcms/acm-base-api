/*
 * Created on 22.06.2004
 */
package ru.myx.ae1.storage;

import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.reflect.Reflect;

/** @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments */
public interface BaseChange {

	/**
	 *
	 */
	static BaseObject PROTOTYPE = new BaseNativeObject(Reflect.classToBasePrototype(BaseChange.class));

	/**
	 *
	 */
	static int STATE_DRAFT = 0;

	/**
	 *
	 */
	static int STATE_READY = 1;

	/**
	 *
	 */
	static int STATE_PUBLISH = 2;

	/**
	 *
	 */
	static int STATE_PUBLISHED = 2;

	/**
	 *
	 */
	static int STATE_DEAD = 3;

	/**
	 *
	 */
	static int STATE_SYSTEM = 4;

	/**
	 *
	 */
	static int STATE_ARCHIVE = 5;

	/**
	 *
	 */
	static int STATE_ARCHIVED = 5;

	/**
	 *
	 */
	static int STATE_ARCHIEVE = 5;

	/**
	 *
	 */
	static int STATE_ARCHIEVED = 5;

	/** @param alias */
	void aliasAdd(String alias);

	/** @param alias */
	void aliasRemove(String alias);

	/**
	 *
	 */
	void commit();

	/** @param entry
	 * @return change */
	BaseChange createChange(BaseEntry<?> entry);

	/** @return change */
	BaseChange createChild();

	/**
	 *
	 */
	void delete();

	/** @param soft */
	void delete(boolean soft);

	/** @return date */
	long getCreated();

	/** @return map */
	BaseObject getData();

	/** @return string */
	String getGuid();

	/** @return history array */
	BaseHistory[] getHistory();

	/** @param historyId
	 * @return change */
	BaseChange getHistorySnapshot(String historyId);

	/** @return string */
	String getKey();

	/** @return string */
	String getLinkedIdentity();

	/** @param ctx
	 * @return url */
	String getLocation(ExecProcess ctx);

	/** @param ctx
	 * @return url */
	String getLocationAbsolute(ExecProcess ctx);

	/** @return url */
	String getLocationControl();

	/** @return map */
	BaseObject getParentalData();

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

	/** @return string */
	String getTitle();

	/** @return string */
	String getTypeName();

	/** @param versionId
	 * @return change */
	BaseChange getVersion(String versionId);

	/** @return map */
	BaseObject getVersionData();

	/** @return string */
	String getVersionId();

	/** @return boolean */
	boolean getVersioning();

	/** @return version array */
	BaseVersion[] getVersions();

	/** @return boolean */
	boolean isFolder();

	/** Backwards compatibility
	 *
	 * @return */
	boolean isMultivariant();

	/** @param entry */
	default void nestUnlink(final BaseEntry<?> entry) {
		
		this.nestUnlink(entry, false);
	}

	/** @param entry
	 * @param soft */
	void nestUnlink(BaseEntry<?> entry, final boolean soft);

	/**
	 *
	 */
	void resync();

	/**
	 *
	 */
	void segregate();

	/**
	 *
	 */
	void setCommitActive();

	/**
	 *
	 */
	void setCommitLogged();

	/** @param created */
	void setCreated(long created);

	/** @param folder */
	void setCreateLinkedIn(BaseEntry<?> folder);

	/** @param folder
	 * @param key */
	void setCreateLinkedIn(BaseEntry<?> folder, String key);

	/** @param entry */
	void setCreateLinkedWith(BaseEntry<?> entry);

	/** @param local */
	void setCreateLocal(boolean local);

	/** @param folder */
	void setFolder(boolean folder);

	/** @param key */
	void setKey(String key);

	/** @param parent */
	void setParent(BaseEntry<?> parent);

	/** @param parentGuid */
	void setParentGuid(String parentGuid);

	/** @param state */
	void setState(int state);

	/** @param title */
	void setTitle(String title);

	/** @param typeName */
	void setTypeName(String typeName);

	/** @param comment */
	void setVersionComment(String comment);

	/** @param data */
	void setVersionData(BaseObject data);

	/** @param versioning */
	void setVersioning(boolean versioning);

	/**
	 *
	 */
	void touch();

	/**
	 *
	 */
	default void unlink() {
		
		this.unlink(false);
	}

	/** @param soft */
	void unlink(boolean soft);
}
