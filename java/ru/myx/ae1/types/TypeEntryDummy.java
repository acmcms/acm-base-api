package ru.myx.ae1.types;

import java.util.List;
import java.util.Map;

import ru.myx.ae1.storage.AbstractEntry;
import ru.myx.ae1.storage.BaseChange;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae1.storage.StorageImpl;
import ru.myx.ae3.Engine;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveString;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.control.ControlBasic;

/*
 * Created on 17.01.2005
 */

final class TypeEntryDummy extends AbstractEntry<TypeEntryDummy> {
	
	final Type<?> type;
	
	final BaseChange change;
	
	TypeEntryDummy(final Type<?> type, final BaseChange change) {
		this.type = type;
		this.change = change;
	}
	
	@Override
	public BaseProperty baseGetOwnProperty(final BasePrimitiveString name) {
		
		{
			final BaseObject substitution = this.getData();
			assert substitution != null : "Should not be NULL, class=" + this.getClass().getName();
			assert substitution != this : "Should not be THIS, class=" + this.getClass().getName();
			final BaseProperty property = substitution.baseFindProperty(name);
			if (property != null) {
				return property;
			}
		}
		{
			final Type<?> type = this.getType();
			return type == null
				? null
				: type.getTypePrototypeObject().baseFindProperty(name, BaseObject.PROTOTYPE);
		}
	}
	
	@Override
	public BaseProperty baseGetOwnProperty(final String name) {
		
		{
			final BaseObject substitution = this.getData();
			assert substitution != null : "Should not be NULL, class=" + this.getClass().getName();
			assert substitution != this : "Should not be THIS, class=" + this.getClass().getName();
			final BaseProperty property = substitution.baseFindProperty(name, BaseObject.PROTOTYPE);
			if (property != null) {
				return property;
			}
		}
		{
			final Type<?> type = this.getType();
			return type == null
				? null
				: type.getTypePrototypeObject().baseFindProperty(name, BaseObject.PROTOTYPE);
		}
	}
	
	@Override
	public final BaseObject baseGetSubstitution() {
		
		return this.getData();
	}
	
	@Override
	public boolean baseHasKeysOwn() {
		
		/**
		 * faster - no need to load
		 */
		{
			final Type<?> type = this.getType();
			if (type != null) {
				final BaseObject typePrototype = type.getTypePrototypeObject();
				if (typePrototype != null
						/**
						 * not only OWN
						 */
						&& Base.hasKeys(typePrototype)) {
					return true;
				}
			}
		}
		{
			final BaseObject substitution = this.getData();
			assert substitution != null : "Should not be NULL, class=" + this.getClass().getName();
			assert substitution != this : "Should not be THIS, class=" + this.getClass().getName();
			return Base.hasKeys(substitution);
		}
	}
	
	@Override
	public final BaseChange createChange() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.createChange();
		}
		throw new IllegalStateException("Not allowed within constructor invocation!");
	}
	
	@Override
	public final BaseChange createChild() {
		
		return this.change.createChild();
	}
	
	@Override
	public final BaseEntry<?> getChildByName(final String name) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getChildByName(name);
		}
		return null;
	}
	
	@Override
	public final List<ControlBasic<?>> getChildren() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getChildren();
		}
		return null;
	}
	
	@Override
	public final List<ControlBasic<?>> getChildren(final int count, final String sort) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getChildren(count, sort);
		}
		return null;
	}
	
	@Override
	public final List<ControlBasic<?>> getChildrenListable(final int count, final String sort) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getChildrenListable(count, sort);
		}
		return null;
	}
	
	@Override
	public final long getCreated() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getCreated();
		}
		return Engine.fastTime();
	}
	
	@Override
	public final List<ControlBasic<?>> getFiles(final int count, final String sort) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getFiles(count, sort);
		}
		return null;
	}
	
	@Override
	public final List<ControlBasic<?>> getFilesListable(final int count, final String sort) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getFilesListable(count, sort);
		}
		return null;
	}
	
	@Override
	public final List<ControlBasic<?>> getFolders() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getFolders();
		}
		return null;
	}
	
	@Override
	public final List<ControlBasic<?>> getFoldersListable() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getFoldersListable();
		}
		return null;
	}
	
	@Override
	public final String getGuid() {
		
		return this.change.getGuid();
	}
	
	@Override
	public final String getLinkedIdentity() {
		
		return this.change.getGuid();
	}
	
	@Override
	public final long getModified() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getModified();
		}
		return Engine.fastTime();
	}
	
	@Override
	public final String getParentGuid() {
		
		return this.change.getParentGuid();
	}
	
	@Override
	public final int getState() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getState();
		}
		return 0;
	}
	
	@Override
	public final StorageImpl getStorageImpl() {
		
		return this.change.getStorageImpl();
	}
	
	@Override
	public final Type<?> getType() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getType();
		}
		return this.type;
	}
	
	@Override
	public final String getTypeName() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getTypeName();
		}
		return this.type.getKey();
	}
	
	@Override
	public final BaseObject internGetData() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.getData();
		}
		return this.change.getData();
	}
	
	@Override
	public final boolean isFolder() {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.isFolder();
		}
		return false;
	}
	
	@Override
	public final List<ControlBasic<?>> search(final int limit,
			final boolean all,
			final long timeout,
			final String sort,
			final long startDate,
			final long endDate,
			final String query) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.search(limit, all, timeout, sort, startDate, endDate, query);
		}
		return null;
	}
	
	@Override
	public final Map<Integer, Map<Integer, Map<String, Number>>> searchCalendar(final boolean all,
			final long timeout,
			final long startDate,
			final long endDate,
			final String filter) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.searchCalendar(all, timeout, startDate, endDate, filter);
		}
		return null;
	}
	
	@Override
	public final List<ControlBasic<?>> searchLocal(final int limit, final boolean all, final String sort, final long startDate, final long endDate, final String query) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.searchLocal(limit, all, sort, startDate, endDate, query);
		}
		return null;
	}
	
	@Override
	public final Map<String, Number> searchLocalAlphabet(final boolean all, final Map<String, String> alphabetConversion, final String defaultLetter, final String filter) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.searchLocalAlphabet(all, alphabetConversion, defaultLetter, filter);
		}
		return null;
	}
	
	@Override
	public final List<ControlBasic<?>> searchLocalAlphabet(final int limit,
			final boolean all,
			final String sort,
			final Map<String, String> alphabetConversion,
			final String defaultLetter,
			final String filterLetter,
			final String filter) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.searchLocalAlphabet(limit, all, sort, alphabetConversion, defaultLetter, filterLetter, filter);
		}
		return null;
	}
	
	@Override
	public final Map<Integer, Map<Integer, Map<String, Number>>> searchLocalCalendar(final boolean all, final long startDate, final long endDate, final String filter) {
		
		final BaseEntry<?> entry = this.change.getStorageImpl().getInterface().getByGuid(this.change.getGuid());
		if (entry != null) {
			return entry.searchLocalCalendar(all, startDate, endDate, filter);
		}
		return null;
	}
	
}
