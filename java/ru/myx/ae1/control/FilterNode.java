/*
 * Created on 25.04.2006
 */
package ru.myx.ae1.control;

import java.util.Collection;
import java.util.List;

import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseHostFilter;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.i3.Handler;
import ru.myx.ae3.serve.ServeRequest;

/**
 * @author myx
 * @param <T>
 */
public class FilterNode<T extends FilterNode<?>> extends BaseHostFilter<ControlNode<?>> implements ControlNode<T> {
	
	
	/**
	 * @param node
	 */
	protected FilterNode(final ControlNode<?> node) {
		super(node);
		this.parent = node;
	}
	
	@Override
	public String toString() {
		
		
		return "[Filter: " + this.parent + "]";
	}
	
	@Override
	public void bind(final ControlNode<?> node) {
		
		
		this.parent.bind(node);
	}
	
	@Override
	public BaseObject getAttributes() {
		
		
		return this.parent.getAttributes();
	}
	
	@Override
	public ControlNode<?> getChildByName(final String name) {
		
		
		return this.parent.getChildByName(name);
	}
	
	@Override
	public ControlNode<?>[] getChildren() {
		
		
		return this.parent.getChildren();
	}
	
	@Override
	public ControlNode<?>[] getChildrenExternal() {
		
		
		return this.parent.getChildrenExternal();
	}
	
	@Override
	public AccessPermissions getCommandPermissions() {
		
		
		return this.parent.getCommandPermissions();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {
		
		
		return this.parent.getCommandResult(command, arguments);
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		
		return this.parent.getCommands();
	}
	
	@Override
	public ControlCommandset getContentCommands(final String key) {
		
		
		return this.parent.getContentCommands(key);
	}
	
	@Override
	public ControlEntry<?> getContentEntry(final String key) {
		
		
		return this.parent.getContentEntry(key);
	}
	
	@Override
	public ControlFieldset<?> getContentFieldset() {
		
		
		return this.parent.getContentFieldset();
	}
	
	@Override
	public ControlCommandset getContentMultipleCommands(final BaseArray keys) {
		
		
		return this.parent.getContentMultipleCommands(keys);
	}
	
	@Override
	public List<ControlBasic<?>> getContents() {
		
		
		return this.parent.getContents();
	}
	
	@Override
	public BaseObject getData() {
		
		
		return this.parent.getData();
	}
	
	@Override
	public ControlEntry<?> getEntry() {
		
		
		return this.parent.getEntry();
	}
	
	@Override
	public ControlCommandset getForms() {
		
		
		return this.parent.getForms();
	}
	
	@Override
	public String getIcon() {
		
		
		return this.parent.getIcon();
	}
	
	@Override
	public String getKey() {
		
		
		return this.parent.getKey();
	}
	
	@Override
	public String getLocation() {
		
		
		return this.parent.getLocation();
	}
	
	@Override
	public String getLocationAbsolute() {
		
		
		return this.parent.getLocationAbsolute();
	}
	
	@Override
	public String getLocationControl() {
		
		
		return this.parent.getLocationControl();
	}
	
	@Override
	public Collection<String> getLocationControlAll() {
		
		
		return this.parent.getLocationControlAll();
	}
	
	@Override
	public String getTitle() {
		
		
		return this.parent.getTitle();
	}
	
	@Override
	public ReplyAnswer onQuery(final ServeRequest request) {
		
		
		return this.parent.onQuery(request);
	}
	
	@Override
	public boolean hasAttributes() {
		
		
		return this.parent.hasAttributes();
	}
	
	@Override
	public boolean hasChildren() {
		
		
		return this.parent.hasChildren();
	}
	
	@Override
	public String restoreFactoryIdentity() {
		
		
		return this.parent.restoreFactoryIdentity();
	}
	
	@Override
	public String restoreFactoryParameter() {
		
		
		return this.parent.restoreFactoryParameter();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T setAttribute(final String name, final BaseObject value) {
		
		
		this.parent = this.parent.setAttribute(name, value);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T setAttribute(final String name, final boolean value) {
		
		
		this.parent = this.parent.setAttribute(name, value);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T setAttribute(final String name, final double value) {
		
		
		this.parent = this.parent.setAttribute(name, value);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T setAttribute(final String name, final long value) {
		
		
		this.parent = this.parent.setAttribute(name, value);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T setAttribute(final String name, final String value) {
		
		
		this.parent = this.parent.setAttribute(name, value);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T setAttributes(final BaseObject map) {
		
		
		this.parent = this.parent.setAttributes(map);
		return (T) this;
	}
	
	@Override
	public Handler substituteHandler() {
		
		
		return this.parent.substituteHandler();
	}
}
