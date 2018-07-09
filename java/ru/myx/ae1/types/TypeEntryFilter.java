package ru.myx.ae1.types;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ru.myx.ae1.storage.BaseChange;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae1.storage.BaseHistory;
import ru.myx.ae1.storage.BaseSchedule;
import ru.myx.ae1.storage.BaseSync;
import ru.myx.ae1.storage.BaseVersion;
import ru.myx.ae1.storage.StorageImpl;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.BaseHostFilter;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.serve.ServeRequest;

final class TypeEntryFilter extends BaseHostFilter<BaseEntry<?>> implements BaseEntry<TypeEntryFilter> {
	
	private final BaseChange change;
	
	TypeEntryFilter(final BaseEntry<?> entry, final BaseChange change) {
		super(entry);
		this.change = change;
	}
	
	@Override
	public final int compareTo(final Object o) {
		
		return this.parent.compareTo(o);
	}
	
	@Override
	public final BaseChange createChange() {
		
		return this.change.createChange(this.parent);
	}
	
	@Override
	public final BaseChange createChild() {
		
		return this.change.createChild();
	}
	
	@Override
	public final boolean equals(final Object o) {
		
		return o instanceof BaseEntry<?> && this.getGuid().equals(((BaseEntry<?>) o).getGuid());
	}
	
	@Override
	public final String[] getAliases() {
		
		return this.parent.getAliases();
	}
	
	@Override
	public final BaseObject getAttributes() {
		
		return this.parent.getAttributes();
	}
	
	@Override
	public final BaseEntry<?> getChildByName(final String name) {
		
		return this.parent.getChildByName(name);
	}
	
	@Override
	public final List<ControlBasic<?>> getChildren() {
		
		return this.parent.getChildren();
	}
	
	@Override
	public final List<ControlBasic<?>> getChildren(final int limit, final String sort) {
		
		return this.parent.getChildren(limit, sort);
	}
	
	@Override
	public final List<ControlBasic<?>> getChildrenListable(final int limit, final String sort) {
		
		return this.parent.getChildrenListable(limit, sort);
	}
	
	@Override
	public final AccessPermissions getCommandPermissions() {
		
		return this.parent.getCommandPermissions();
	}
	
	@Override
	public final Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {
		
		return this.parent.getCommandResult(command, arguments);
	}
	
	@Override
	public final ControlCommandset getCommands() {
		
		return this.parent.getCommands();
	}
	
	@Override
	public final long getCreated() {
		
		return this.parent.getCreated();
	}
	
	@Override
	public final BaseObject getData() {
		
		return this.parent.getData();
	}
	
	@Override
	public final String getEntryBehaviorListingSort() {
		
		return this.parent.getEntryBehaviorListingSort();
	}
	
	@Override
	public final List<ControlBasic<?>> getFiles(final int limit, final String sort) {
		
		return this.parent.getFiles(limit, sort);
	}
	
	@Override
	public final List<ControlBasic<?>> getFilesListable(final int limit, final String sort) {
		
		return this.parent.getFilesListable(limit, sort);
	}
	
	@Override
	public final List<ControlBasic<?>> getFolders() {
		
		return this.parent.getFolders();
	}
	
	@Override
	public final List<ControlBasic<?>> getFoldersListable() {
		
		return this.parent.getFoldersListable();
	}
	
	@Override
	public final ControlCommandset getForms() {
		
		return this.parent.getForms();
	}
	
	@Override
	public final String getGuid() {
		
		return this.parent.getGuid();
	}
	
	@Override
	public final BaseHistory[] getHistory() {
		
		return this.parent.getHistory();
	}
	
	@Override
	public final BaseEntry<?> getHistorySnapshot(final String historyId) {
		
		return this.parent.getHistorySnapshot(historyId);
	}
	
	@Override
	public final String getIcon() {
		
		return this.parent.getIcon();
	}
	
	@Override
	public final String getKey() {
		
		return this.parent.getKey();
	}
	
	@Override
	public final String getLinkedIdentity() {
		
		return this.parent.getLinkedIdentity();
	}
	
	@Override
	public final String getLocation() {
		
		return this.parent.getLocation();
	}
	
	@Override
	public final String getLocationAbsolute() {
		
		return this.parent.getLocationAbsolute();
	}
	
	@Override
	public final String getLocationControl() {
		
		return this.parent.getLocationControl();
	}
	
	@Override
	public final Collection<String> getLocationControlAll() {
		
		return this.parent.getLocationControlAll();
	}
	
	@Override
	public final long getModified() {
		
		return this.parent.getModified();
	}
	
	@Override
	public final String getOwner() {
		
		return this.parent.getOwner();
	}
	
	@Override
	public final BaseEntry<?> getParent() {
		
		return this.parent.getParent();
	}
	
	@Override
	public final String getParentGuid() {
		
		return this.parent.getParentGuid();
	}
	
	@Override
	public final BaseSchedule getSchedule() {
		
		return this.parent.getSchedule();
	}
	
	@Override
	public final int getState() {
		
		return this.parent.getState();
	}
	
	@Override
	public final StorageImpl getStorageImpl() {
		
		return this.parent.getStorageImpl();
	}
	
	@Override
	public final BaseSync getSynchronization() {
		
		return this.parent.getSynchronization();
	}
	
	@Override
	public final String getTitle() {
		
		return this.parent.getTitle();
	}
	
	@Override
	public final Type<?> getType() {
		
		return this.parent.getType();
	}
	
	@Override
	public final String getTypeName() {
		
		return this.parent.getTypeName();
	}
	
	@Override
	public final BaseEntry<?> getVersion(final String versionId) {
		
		return this.parent.getVersion(versionId);
	}
	
	@Override
	public final BaseVersion[] getVersions() {
		
		return this.parent.getVersions();
	}
	
	@Override
	public final ReplyAnswer onQuery(final ServeRequest request) {
		
		return this.parent.onQuery(request);
	}
	
	@Override
	public final BaseObject handleResponseFilter(final ExecProcess parentProcess, final BaseObject content) {
		
		return this.parent.handleResponseFilter(parentProcess, content);
	}
	
	@Override
	public final boolean hasAttributes() {
		
		return this.parent.hasAttributes();
	}
	
	@Override
	public final int hashCode() {
		
		return this.getGuid().hashCode();
	}
	
	@Override
	public final boolean isFolder() {
		
		return this.parent.isFolder();
	}
	
	@Override
	public final String restoreFactoryIdentity() {
		
		return this.parent.restoreFactoryIdentity();
	}
	
	@Override
	public final String restoreFactoryParameter() {
		
		return this.parent.restoreFactoryParameter();
	}
	
	@Override
	public final List<ControlBasic<?>> search(final int limit,
			final boolean all,
			final long timeout,
			final String sort,
			final long startDate,
			final long endDate,
			final String filter) {
		
		return this.parent.search(limit, all, timeout, sort, startDate, endDate, filter);
	}
	
	@Override
	public final Map<Integer, Map<Integer, Map<String, Number>>> searchCalendar(final boolean all,
			final long timeout,
			final long startDate,
			final long endDate,
			final String filter) {
		
		return this.parent.searchCalendar(all, timeout, startDate, endDate, filter);
	}
	
	@Override
	public final List<ControlBasic<?>> searchLocal(final int limit, final boolean all, final String sort, final long startDate, final long endDate, final String filter) {
		
		return this.parent.searchLocal(limit, all, sort, startDate, endDate, filter);
	}
	
	@Override
	public final Map<String, Number> searchLocalAlphabet(final boolean all, final Map<String, String> alphabetConversion, final String defaultLetter, final String filter) {
		
		return this.parent.searchLocalAlphabet(all, alphabetConversion, defaultLetter, filter);
	}
	
	@Override
	public final List<ControlBasic<?>> searchLocalAlphabet(final int limit,
			final boolean all,
			final String sort,
			final Map<String, String> alphabetConversion,
			final String defaultLetter,
			final String filterLetter,
			final String filter) {
		
		return this.parent.searchLocalAlphabet(limit, all, sort, alphabetConversion, defaultLetter, filterLetter, filter);
	}
	
	@Override
	public final Map<Integer, Map<Integer, Map<String, Number>>> searchLocalCalendar(final boolean all, final long startDate, final long endDate, final String filter) {
		
		return this.parent.searchLocalCalendar(all, startDate, endDate, filter);
	}
	
	@Override
	public final TypeEntryFilter setAttribute(final String name, final BaseObject value) {
		
		this.parent.setAttribute(name, value);
		return this;
	}
	
	@Override
	public final TypeEntryFilter setAttribute(final String name, final boolean value) {
		
		this.parent.setAttribute(name, value);
		return this;
	}
	
	@Override
	public final TypeEntryFilter setAttribute(final String name, final double value) {
		
		this.parent.setAttribute(name, value);
		return this;
	}
	
	@Override
	public final TypeEntryFilter setAttribute(final String name, final long value) {
		
		this.parent.setAttribute(name, value);
		return this;
	}
	
	@Override
	public final TypeEntryFilter setAttribute(final String name, final String value) {
		
		this.parent.setAttribute(name, value);
		return this;
	}
	
	@Override
	public TypeEntryFilter setAttributes(final BaseObject map) {
		
		this.parent.setAttributes(map);
		return this;
	}
	
	@Override
	public String toString() {
		
		return "EntryFilter: type=" + this.getTypeName();
	}
}
