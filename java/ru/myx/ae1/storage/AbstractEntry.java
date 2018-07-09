package ru.myx.ae1.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ru.myx.ae1.control.AbstractControlEntry;
import ru.myx.ae1.types.Type;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.serve.ServeRequest;

/** @author myx
 * @param <T>
 */
public abstract class AbstractEntry<T extends AbstractEntry<?>> extends AbstractControlEntry<T> implements BaseEntry<T> {

	private String controlLocation = null;

	private BaseObject dataMap = null;

	@Override
	public final int compareTo(final Object o) {

		return this.getGuid().compareTo(((BaseEntry<?>) o).getGuid());
	}

	@Override
	public final boolean equals(final Object o) {

		return o instanceof BaseEntry<?> && this.getGuid().equals(((BaseEntry<?>) o).getGuid());
	}

	@Override
	public String[] getAliases() {

		return null;
	}

	@Override
	public final Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {

		return this.getType().getCommandAdditionalResult(this, command, arguments);
	}

	@Override
	public final ControlCommandset getCommands() {

		final Type<?> type = this.getType();
		if (type == null) {
			return null;
		}
		return type.getCommandsAdditional(this, null, null, null);
	}

	@Override
	public BaseObject getData() {

		if (this.dataMap == null) {
			synchronized (this) {
				if (this.dataMap == null) {
					final Type<?> type = this.getType();
					if (type == null) {
						final BaseMap map = new BaseNativeObject();
						final BaseObject data = this.internGetData();
						if (data != null) {
							map.baseDefineImportAllEnumerable(data);
						}
						this.dataMap = map;
					} else {
						this.dataMap = Type.getDataAccessMapDefaultImpl(this.getType(), this.internGetData());
					}
				}
			}
		}
		return this.dataMap;
	}

	@Override
	public String getEntryBehaviorListingSort() {

		final Type<?> type = this.getType();
		return type == null
			? null
			: type.getTypeBehaviorListingSort();
	}

	@Override
	public BaseHistory[] getHistory() {

		return null;
	}

	@Override
	public BaseEntry<?> getHistorySnapshot(final String historyId) {

		return this;
	}

	@Override
	public final String getIcon() {

		final Type<?> type = this.getType();
		final String typeIcon = type == null
			? "unknown"
			: type.getIcon();
		return this.isFolder()
			? "container-" + typeIcon
			: "object-" + typeIcon;
	}

	@Override
	public final String getLocationControl() {

		if (this.controlLocation != null) {
			return this.controlLocation;
		}
		final StorageImpl parent = this.getStorageImpl();
		if (this.getGuid().equals(parent.getStorage().getRootIdentifier())) {
			return this.controlLocation = parent.getLocationControl();
		}
		final BaseEntry<?> entry = parent.getStorage().getByGuid(this.getParentGuid());
		if (entry == null || entry == this) {
			return null;
		}
		final String parentPath = entry.getLocationControl();
		if (parentPath == null) {
			return null;
		}
		return this.controlLocation = parentPath.endsWith("/")
			? this.isFolder()
				? parentPath + this.getKey() + '/'
				: parentPath + this.getKey()
			: this.isFolder()
				? parentPath + '/' + this.getKey() + '/'
				: parentPath + '/' + this.getKey();
	}

	@Override
	public final Collection<String> getLocationControlAll() {

		final StorageImpl parent = this.getStorageImpl();
		if (!parent.areLinksSupported()) {
			return Collections.singleton(this.getLocationControl());
		}
		final Collection<ControlBasic<?>> same = parent.getInterface().searchForIdentity(this.getLinkedIdentity(), true);
		if (same == null) {
			return Collections.singleton(this.getLocationControl());
		}
		final List<String> result = new ArrayList<>();
		for (final ControlBasic<?> current : same) {
			if (current != null) {
				final BaseEntry<?> entry = (BaseEntry<?>) current;
				result.add(entry.getLocationControl());
			}
		}
		return result;
	}

	@Override
	public String getOwner() {

		return Base.getString(this.getData(), "$owner", null);
	}

	@Override
	public final BaseEntry<?> getParent() {

		return this.getStorageImpl().getStorage().getByGuid(this.getParentGuid());
	}

	@Override
	public BaseSchedule getSchedule() {

		final ModuleSchedule scheduling = this.getStorageImpl().getScheduling();
		return scheduling == null
			? null
			: scheduling.createChange(this.getGuid());
	}

	@Override
	public BaseSync getSynchronization() {

		final ModuleSynchronizer synchronizer = this.getStorageImpl().getSynchronizer();
		return synchronizer == null
			? null
			: synchronizer.createChange(this.getGuid());
	}

	@Override
	public BaseEntry<?> getVersion(final String versionId) {

		return this;
	}

	@Override
	public BaseVersion[] getVersions() {

		return null;
	}

	@Override
	public final int hashCode() {

		return this.getGuid().hashCode();
	}

	/** @return */
	protected abstract BaseObject internGetData();

	@Override
	public ReplyAnswer onQuery(final ServeRequest query) {

		final Type<?> type = this.getType();
		return type == null
			? Reply.string("BASE_ENTRY", query, "NULL TYPE: " + this.getTypeName())
			: type.getResponse(query, this);
	}

	@Override
	public final String restoreFactoryIdentity() {

		return "storage";
	}

	@Override
	public String restoreFactoryParameter() {

		return this.getStorageImpl().getMnemonicName() + ',' + this.getGuid();
	}

	@Override
	public List<ControlBasic<?>> searchLocal(final int limit, final boolean all, final String sort, final long startDate, final long endDate, final String filter) {

		throw new UnsupportedOperationException("Local filter are not supported!");
	}

	@Override
	public String toString() {

		return "[object " + this.baseClass() + "(" + "id=" + this.getGuid() + ", title=" + this.getTitle() + ")]";
	}
}
