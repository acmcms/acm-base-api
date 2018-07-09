/**
 *
 */
package ru.myx.ae1.storage;

import ru.myx.ae1.types.Type;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostDataSubstitution;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;

/** @author myx */
public abstract class AbstractChange implements BaseHostDataSubstitution<BaseObject>, BaseChange {

	private static final Object NAME_GENERATOR = new NameGenerator();

	@Override
	public void aliasAdd(final String alias) {

		// do nothing
	}

	@Override
	public void aliasRemove(final String alias) {

		// do nothing
	}

	@Override
	public BaseObject baseGetSubstitution() {

		return this.getData();
	}

	@Override
	public BaseObject basePrototype() {

		return BaseChange.PROTOTYPE;
	}

	/** @return */
	@SuppressWarnings("static-method")
	protected String createDefaultKey() {

		return AbstractChange.NAME_GENERATOR.toString();
	}

	@Override
	public final long getCreated() {

		return Convert.MapEntry.toLong(this.getData(), "$created", 0L);
	}

	@Override
	public BaseHistory[] getHistory() {

		return null;
	}

	@Override
	public BaseChange getHistorySnapshot(final String historyId) {

		return this;
	}

	@Override
	public final String getKey() {

		return Base.getString(this.getData(), "$key", null);
	}

	@Override
	public final String getLocation(final ExecProcess ctx) {

		return Context.getServer(ctx).fixLocation(ctx, this.getLocationControl(), false);
	}

	@Override
	public final String getLocationAbsolute(final ExecProcess ctx) {

		return Context.getServer(ctx).fixLocation(ctx, this.getLocationControl(), true);
	}

	@Override
	public final int getState() {

		return Convert.MapEntry.toInt(this.getData(), "$state", 0);
	}

	@Override
	public final String getTitle() {

		return Base.getString(this.getData(), "$title", null);
	}

	/** @param process
	 * @return */
	protected Type<?> getType(final ExecProcess process) {

		return Context.getServer(process).getTypes().getType(this.getTypeName());
	}

	@Override
	public final String getTypeName() {

		return Base.getString(this.getData(), "$type", null);
	}

	@Override
	public BaseChange getVersion(final String versionId) {

		return this;
	}

	@Override
	public BaseObject getVersionData() {

		return null;
	}

	@Override
	public String getVersionId() {

		return null;
	}

	@Override
	public boolean getVersioning() {

		return false;
	}

	@Override
	public BaseVersion[] getVersions() {

		return null;
	}

	@Override
	public final boolean isFolder() {

		return Convert.MapEntry.toBoolean(this.getData(), "$folder", false);
	}

	@Override
	public void resync() {

		// ignore
	}

	@Override
	public void setCommitActive() {

		// do nothing
	}

	@Override
	public void setCommitLogged() {

		// do nothing
	}

	@Override
	public final void setCreated(final long created) {

		this.getData().baseDefine("$created", Base.forDateMillis(created), BaseProperty.ATTRS_MASK_WED);
	}

	@Override
	public final void setFolder(final boolean folder) {

		this.getData().baseDefine(
				"$folder",
				folder
					? BaseObject.TRUE
					: BaseObject.FALSE,
				BaseProperty.ATTRS_MASK_WED);
	}

	@Override
	public final void setKey(final String key) {

		this.getData().baseDefine("$key", key, BaseProperty.ATTRS_MASK_WED);
	}

	@Override
	public void setParent(final BaseEntry<?> parent) {

		this.setParentGuid(parent.getGuid());
	}

	@Override
	public final void setState(final int state) {

		this.getData().baseDefine("$state", state, BaseProperty.ATTRS_MASK_WED);
	}

	@Override
	public final void setTitle(final String title) {

		this.getData().baseDefine("$title", title, BaseProperty.ATTRS_MASK_WED);
	}

	@Override
	public final void setTypeName(final String typeName) {

		this.getData().baseDefine("$type", typeName, BaseProperty.ATTRS_MASK_WED);
	}

	@Override
	public void setVersionComment(final String comment) {

		// do nothing
	}

	@Override
	public void setVersionData(final BaseObject data) {

		// do nothing
	}

	@Override
	public void setVersioning(final boolean versioning) {

		// do nothing
	}

	@Override
	public String toString() {

		return "[object " + this.baseClass() + "(" + this.getGuid() + ")]";
	}

	@Override
	public void touch() {

		// ignore
	}

}
