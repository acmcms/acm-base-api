/*
 * Created on 22.06.2004
 */
package ru.myx.ae1.storage;

import java.util.Collection;
import java.util.function.Function;

import ru.myx.ae3.control.ControlBasic;

/** @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments */
public interface ModuleInterface extends Function<String, BaseEntry<?>> {

	/**
	 *
	 */
	public static final int STATE_DRAFT = 0;

	/**
	 *
	 */
	public static final int STATE_READY = 1;

	/**
	 *
	 */
	public static final int STATE_PUBLISH = 2;

	/**
	 *
	 */
	public static final int STATE_PUBLISHED = 2;

	/**
	 *
	 */
	public static final int STATE_DEAD = 3;

	/**
	 *
	 */
	public static final int STATE_SYSTEM = 4;

	/**
	 *
	 */
	public static final int STATE_ARCHIVE = 5;

	/**
	 *
	 */
	public static final int STATE_ARCHIVED = 5;

	/**
	 *
	 */
	public static final int STATE_ARCHIEVE = 5;

	/**
	 *
	 */
	public static final int STATE_ARCHIEVED = 5;

	/**
	 *
	 */
	public static final int TYPE_OBJECT = 0;

	/**
	 *
	 */
	public static final int TYPE_RECORD = 0;

	/**
	 *
	 */
	public static final int TYPE_FOLDER = 1;

	/**
	 *
	 */
	public static final int TYPE_DIRECTORY = 1;

	/**
	 *
	 */
	public static final int TYPE_CONTAINER = 1;

	/**
	 *
	 */
	public static final int SCRIPT_TYPE_DYNAMIC = 0;

	/**
	 *
	 */
	public static final int SCRIPT_TYPE_STATIC = 1;

	/**
	 *
	 */
	public static final int SCRIPT_TYPE_PAGE = 2;

	/** @return boolean */
	public boolean areAliasesSupported();

	/** @return boolean */
	public boolean areHistoriesSupported();

	/** @return boolean */
	public boolean areLinksSupported();

	/** @return boolean */
	public boolean areSchedulesSupported();

	/** @return boolean */
	public boolean areSynchronizationsSupported();

	/** @return boolean */
	public boolean areVersionsSupported();

	/** @param alias
	 * @param all
	 * @return entry */
	public BaseEntry<?> getByAlias(final String alias, boolean all);

	/** @param key
	 * @return entry */
	public BaseEntry<?> getByGuid(final String key);

	/** @param key
	 * @param typeName
	 * @return entry */
	public BaseEntry<?> getByGuidClean(final String key, final String typeName);

	/** @param entry
	 * @return */
	default String getEntryGuid(final BaseEntry<?> entry) {

		return entry == null
			? null
			: entry.getGuid();
	}

	/** @param entry
	 * @return */
	default String getEntryKey(final BaseEntry<?> entry) {

		return entry == null
			? null
			: entry.getKey();
	}

	/** @return entry */
	public BaseEntry<?> getRoot();

	/** @return string */
	public String getRootIdentifier();

	/** @param guid
	 * @param all
	 * @return collection */
	public Collection<ControlBasic<?>> searchForIdentity(final String guid, final boolean all);
}
