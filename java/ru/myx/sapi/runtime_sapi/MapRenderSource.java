/*
 * Created on 05.05.2006
 */
package ru.myx.sapi.runtime_sapi;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseFunction;
import ru.myx.ae3.exec.ExecOutputBuilder;
import ru.myx.ae3.exec.ExecProcess;

/**
 * @author myx
 *
 */
final class MapRenderSource implements Map<String, Object> {
	
	
	private static final Set<Object> EMPTY_SET_OBJECT = Collections.emptySet();

	private final ExecProcess ctx;

	private final String keyName;

	private final String valueName;

	private final StringBuilder result = new StringBuilder();

	private final BaseFunction output;

	/**
	 * @param ctx
	 * @param keyName
	 * @param valueName
	 */
	public MapRenderSource(final ExecProcess ctx, final String keyName, final String valueName) {
		this.ctx = ctx;
		this.keyName = keyName;
		this.valueName = valueName;
		this.output = new ExecOutputBuilder(this.result);
	}

	@Override
	public void clear() {
		
		
		this.result.setLength(0);
	}

	@Override
	public boolean containsKey(final Object key) {
		
		
		return false;
	}

	@Override
	public boolean containsValue(final Object value) {
		
		
		return false;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		
		
		return Collections.emptySet();
	}

	@Override
	public Object get(final Object key) {
		
		
		return null;
	}

	@Override
	public boolean isEmpty() {
		
		
		return this.result.length() > 0;
	}

	@Override
	public Set<String> keySet() {
		
		
		return Collections.emptySet();
	}

	@Override
	public Object put(final String key, final Object value) {
		
		
		synchronized (this) {
			final BaseFunction previousOutput = this.ctx.execOutputReplace(this.output);
			try {
				this.ctx.contextCreateMutableBinding(this.keyName, Base.forString(key), false);
				this.ctx.contextCreateMutableBinding(this.valueName, Base.forUnknown(value), false);
				this.ctx.vmStateToErrorOrSilence(Context.sourceRender(this.ctx));
				return null;
			} finally {
				this.ctx.execOutputReplace(previousOutput);
			}
		}
	}

	@Override
	public void putAll(final Map<? extends String, ? extends Object> t) {
		
		
		for (final Map.Entry<? extends String, ? extends Object> entry : t.entrySet()) {
			this.put(String.valueOf(entry.getKey()), entry.getValue());
		}
	}

	@Override
	public Object remove(final Object key) {
		
		
		return null;
	}

	@Override
	public int size() {
		
		
		return 0;
	}

	/**
	 * Returns the result of DoSource execution
	 */
	@Override
	public String toString() {
		
		
		return this.result.toString();
	}

	@Override
	public Collection<Object> values() {
		
		
		return MapRenderSource.EMPTY_SET_OBJECT;
	}
}
