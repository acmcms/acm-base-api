package ru.myx.ae1.types;

import ru.myx.ae1.storage.BaseChange;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae1.storage.BaseHistory;
import ru.myx.ae1.storage.BaseSchedule;
import ru.myx.ae1.storage.BaseSync;
import ru.myx.ae1.storage.BaseVersion;
import ru.myx.ae1.storage.StorageImpl;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostFilterSubstitution;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecProcess;

class TypeChangeFilter extends BaseHostFilterSubstitution<BaseObject> implements BaseChange {
	
	private final BaseObject prototype;
	
	private final BaseChange change;
	
	TypeChangeFilter(final BaseChange change) {
		this.prototype = Base.forUnknown(change);
		this.change = change;
	}
	
	@Override
	public void aliasAdd(final String alias) {
		
		this.change.aliasAdd(alias);
	}
	
	@Override
	public void aliasRemove(final String alias) {
		
		this.change.aliasRemove(alias);
	}
	
	@Override
	public BaseObject baseGetSubstitution() {
		
		return this.change.getData();
	}
	
	@Override
	public BaseObject basePrototype() {
		
		return this.prototype;
	}
	
	@Override
	public void commit() {
		
		// ignore
	}
	
	@Override
	public BaseChange createChange(final BaseEntry<?> entry) {
		
		return this.change.createChange(entry);
	}
	
	@Override
	public BaseChange createChild() {
		
		return this.change.createChild();
	}
	
	@Override
	public void delete() {
		
		this.change.delete();
	}
	
	@Override
	public void delete(final boolean soft) {
		
		this.change.delete(soft);
	}
	
	@Override
	public long getCreated() {
		
		return this.change.getCreated();
	}
	
	@Override
	public BaseObject getData() {
		
		return this.change.getData();
	}
	
	@Override
	public String getGuid() {
		
		return this.change.getGuid();
	}
	
	@Override
	public BaseHistory[] getHistory() {
		
		return this.change.getHistory();
	}
	
	@Override
	public BaseChange getHistorySnapshot(final String historyId) {
		
		return this.change.getHistorySnapshot(historyId);
	}
	
	@Override
	public String getKey() {
		
		return this.change.getKey();
	}
	
	@Override
	public String getLinkedIdentity() {
		
		return this.change.getLinkedIdentity();
	}
	
	@Override
	public String getLocation(final ExecProcess ctx) {
		
		return this.change.getLocation(ctx);
	}
	
	@Override
	public String getLocationAbsolute(final ExecProcess ctx) {
		
		return this.change.getLocationAbsolute(ctx);
	}
	
	@Override
	public String getLocationControl() {
		
		return this.change.getLocationControl();
	}
	
	@Override
	public BaseObject getParentalData() {
		
		return this.change.getParentalData();
	}
	
	@Override
	public String getParentGuid() {
		
		return this.change.getParentGuid();
	}
	
	@Override
	public BaseSchedule getSchedule() {
		
		return this.change.getSchedule();
	}
	
	@Override
	public int getState() {
		
		return this.change.getState();
	}
	
	@Override
	public StorageImpl getStorageImpl() {
		
		return this.change.getStorageImpl();
	}
	
	@Override
	public BaseSync getSynchronization() {
		
		return this.change.getSynchronization();
	}
	
	@Override
	public String getTitle() {
		
		return this.change.getTitle();
	}
	
	@Override
	public String getTypeName() {
		
		return this.change.getTypeName();
	}
	
	@Override
	public BaseChange getVersion(final String versionId) {
		
		return this.change.getVersion(versionId);
	}
	
	@Override
	public BaseObject getVersionData() {
		
		return this.change.getVersionData();
	}
	
	@Override
	public String getVersionId() {
		
		return this.change.getVersionId();
	}
	
	@Override
	public boolean getVersioning() {
		
		return this.change.getVersioning();
	}
	
	@Override
	public BaseVersion[] getVersions() {
		
		return this.change.getVersions();
	}
	
	@Override
	public boolean isFolder() {
		
		return this.change.isFolder();
	}
	
	@Override
	public boolean isMultivariant() {
		
		return this.change.isMultivariant();
	}
	
	@Override
	public void resync() {
		
		this.change.resync();
	}
	
	@Override
	public void segregate() {
		
		this.change.segregate();
	}
	
	@Override
	public void setCommitActive() {
		
		this.change.setCommitActive();
	}
	
	@Override
	public void setCommitLogged() {
		
		this.change.setCommitLogged();
	}
	
	@Override
	public void setCreated(final long created) {
		
		this.change.setCreated(created);
	}
	
	@Override
	public void setCreateLinkedIn(final BaseEntry<?> folder) {
		
		this.change.setCreateLinkedIn(folder);
	}
	
	@Override
	public void setCreateLinkedIn(final BaseEntry<?> folder, final String key) {
		
		this.change.setCreateLinkedIn(folder, key);
	}
	
	@Override
	public void setCreateLinkedWith(final BaseEntry<?> entry) {
		
		this.change.setCreateLinkedWith(entry);
	}
	
	@Override
	public void setCreateLocal(final boolean local) {
		
		this.change.setCreateLocal(local);
	}
	
	@Override
	public void setFolder(final boolean folder) {
		
		this.change.setFolder(folder);
	}
	
	@Override
	public void setKey(final String key) {
		
		this.change.setKey(key);
	}
	
	@Override
	public void setParent(final BaseEntry<?> parent) {
		
		this.change.setParent(parent);
	}
	
	@Override
	public void setParentGuid(final String parentGuid) {
		
		this.change.setParentGuid(parentGuid);
	}
	
	@Override
	public void setState(final int state) {
		
		this.change.setState(state);
	}
	
	@Override
	public void setTitle(final String title) {
		
		this.change.setTitle(title);
	}
	
	@Override
	public void setTypeName(final String typeName) {
		
		this.change.setTypeName(typeName);
	}
	
	@Override
	public void setVersionComment(final String comment) {
		
		this.change.setVersionComment(comment);
	}
	
	@Override
	public void setVersionData(final BaseObject data) {
		
		this.change.setVersionData(data);
	}
	
	@Override
	public void setVersioning(final boolean versioning) {
		
		this.change.setVersioning(versioning);
	}
	
	@Override
	public void touch() {
		
		this.change.touch();
	}
	
	@Override
	public void unlink() {
		
		this.change.unlink();
	}
	
	@Override
	public void unlink(final boolean soft) {
		
		this.change.unlink(soft);
	}
}
