/*
 * Created on 25.04.2006
 */
package ru.myx.ae1.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.i3.Handler;

/**
 * @author myx
 * 		
 */
public abstract class AbstractNode extends AbstractControlEntry<AbstractNode> implements ControlNode<AbstractNode> {
	
	private List<ControlNode<?>> bindings;
	
	private Map<String, ControlNode<?>> bindingMap;
	
	@Override
	public final void bind(final ControlNode<?> node) {
		
		if (this.bindings == null) {
			synchronized (this) {
				if (this.bindings == null) {
					this.bindings = new ArrayList<>();
				}
			}
		}
		this.bindings.add(node);
	}
	
	@Override
	public final ControlNode<?> getChildByName(final String name) {
		
		if (this.bindings != null && !this.bindings.isEmpty()) {
			if (this.bindingMap == null) {
				synchronized (this) {
					if (this.bindingMap == null) {
						this.bindingMap = new TreeMap<>();
						for (final ControlNode<?> current : this.bindings) {
							this.bindingMap.put(current.getKey(), current);
						}
					}
				}
			}
			final Object result = this.bindingMap.get(name);
			if (result != null) {
				return (ControlNode<?>) result;
			}
		}
		return this.internGetChildByName(name);
	}
	
	@Override
	public final ControlNode<?>[] getChildren() {
		
		if (this.bindings != null && !this.bindings.isEmpty()) {
			final ControlNode<?>[] children = this.internGetChildren();
			if (children == null || children.length == 0) {
				return this.bindings.toArray(new ControlNode<?>[this.bindings.size()]);
			}
			final int bindingsLength = this.bindings.size();
			final ControlNode<?>[] result = new ControlNode<?>[children.length + bindingsLength];
			for (int i = bindingsLength - 1; i >= 0; --i) {
				result[i] = this.bindings.get(i);
			}
			System.arraycopy(children, 0, result, bindingsLength, children.length);
			return result;
		}
		return this.internGetChildren();
	}
	
	@Override
	public final ControlNode<?>[] getChildrenExternal() {
		
		if (this.bindings != null && !this.bindings.isEmpty()) {
			final ControlNode<?>[] children = this.internGetChildrenExternal();
			if (children == null || children.length == 0) {
				return this.bindings.toArray(new ControlNode<?>[this.bindings.size()]);
			}
			final int bindingsLength = this.bindings.size();
			final ControlNode<?>[] result = new ControlNode<?>[children.length + bindingsLength];
			for (int i = bindingsLength - 1; i >= 0; --i) {
				result[i] = this.bindings.get(i);
			}
			System.arraycopy(children, 0, result, bindingsLength, children.length);
			return result;
		}
		return this.internGetChildrenExternal();
	}
	
	@Override
	public ControlCommandset getContentCommands(final String key) {
		
		return null;
	}
	
	@Override
	public ControlEntry<?> getContentEntry(final String key) {
		
		return null;
	}
	
	@Override
	public ControlFieldset<?> getContentFieldset() {
		
		return null;
	}
	
	@Override
	public ControlCommandset getContentMultipleCommands(final BaseArray keys) {
		
		return null;
	}
	
	@Override
	public List<ControlBasic<?>> getContents() {
		
		return null;
	}
	
	@Override
	public ControlEntry<?> getEntry() {
		
		return this;
	}
	
	@Override
	public String getIcon() {
		
		return "container";
	}
	
	@Override
	public String getLocationControl() {
		
		return null;
	}
	
	@Override
	public final boolean hasChildren() {
		
		if (this.bindings != null && !this.bindings.isEmpty()) {
			return true;
		}
		return this.internHasChildren();
	}
	
	/**
	 * @param name
	 * @return
	 */
	protected ControlNode<?> internGetChildByName(final String name) {
		
		if (name == null) {
			throw new NullPointerException();
		}
		final ControlNode<?>[] children = this.internGetChildren();
		if (children == null || children.length == 0) {
			return null;
		}
		for (final ControlNode<?> node : children) {
			if (name.equals(node.getKey())) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings("static-method")
	protected ControlNode<?>[] internGetChildren() {
		
		return null;
	}
	
	/**
	 * @return
	 */
	protected ControlNode<?>[] internGetChildrenExternal() {
		
		return this.internGetChildren();
	}
	
	/**
	 * @return
	 */
	protected boolean internHasChildren() {
		
		final ControlNode<?>[] children = this.internGetChildren();
		return children != null && children.length > 0;
	}
	
	@Override
	public Handler substituteHandler() {
		
		return this;
	}
}
