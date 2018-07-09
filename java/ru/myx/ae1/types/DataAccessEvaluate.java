/*
 * Created on 04.05.2006
 */
package ru.myx.ae1.types;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHost;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.base.BasePrimitiveString;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecStateCode;
import ru.myx.ae3.exec.ResultHandler;

/**
 * @author myx
 * 
 * 
 */
final class DataAccessEvaluate implements BaseHost, BaseProperty {
	
	private static final BaseObject SYNCHRONIZE = new BaseNativeObject(null);
	
	private static final BaseObject NULL_FIELD_VALUE = BaseObject.NULL;
	
	private final ControlFieldset<?> fieldset;
	
	/**
	 * only OWN properties
	 */
	private final BaseObject values;
	
	private final BaseObject data;
	
	DataAccessEvaluate(final ControlFieldset<?> fieldset, final BaseObject data) {
		this.fieldset = fieldset;
		this.values = new BaseNativeObject(null);
		this.data = data;
	}
	
	@Override
	public void baseClear() {
		
		this.data.baseClear();
		this.values.baseClear();
	}
	
	@Override
	public boolean baseDefine(final BasePrimitiveString name, final BaseObject value, final short attributes) {
		
		this.values.baseDefine(name, value, attributes);
		return this.data.baseDefine(name, value, attributes);
	}
	
	@Override
	public boolean baseDefine(final String name, final BaseObject value, final short attributes) {
		
		this.values.baseDefine(name, value, attributes);
		return this.data.baseDefine(name, value, attributes);
	}
	
	@Override
	public boolean baseDelete(final String key) {
		
		this.values.baseDelete(key);
		return this.data.baseDelete(key);
	}
	
	@Override
	public final BaseProperty baseGetOwnProperty(final BasePrimitiveString key) {
		
		return Base.hasProperty(this.values, key) || Base.hasProperty(this.data, key) || this.fieldset.innerFields().contains(key.toString())
			? this
			: null;
	}
	
	@Override
	public final BaseProperty baseGetOwnProperty(final String key) {
		
		return Base.hasProperty(this.values, key) || Base.hasProperty(this.data, key) || this.fieldset.innerFields().contains(key)
			? this
			: null;
	}
	
	@Override
	public boolean baseHasKeysOwn() {
		
		return !this.fieldset.innerFields().isEmpty() || Base.hasKeys(this.data);
	}
	
	@Override
	public boolean baseIsExtensible() {
		
		return true;
	}
	
	@Override
	public Iterator<String> baseKeysOwn() {
		
		final Set<String> result = new TreeSet<>();
		for (final Iterator<String> iterator = this.data.baseKeysOwn(); iterator.hasNext();) {
			result.add(iterator.next());
		}
		for (final Iterator<String> iterator = this.values.baseKeysOwn(); iterator.hasNext();) {
			result.add(iterator.next());
		}
		return new Iterator<String>() {
			
			private final Iterator<String> e = result.iterator();
			
			private String key;
			
			@Override
			public boolean hasNext() {
				
				return this.e.hasNext();
			}
			
			@Override
			public String next() {
				
				return this.key = this.e.next();
			}
			
			@Override
			public void remove() {
				
				DataAccessEvaluate.this.baseDelete(this.key);
			}
		};
	}
	
	@Override
	public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
		
		final Set<BasePrimitive<?>> result = new TreeSet<>();
		for (final Iterator<? extends BasePrimitive<?>> iterator = this.data.baseKeysOwnPrimitive(); iterator.hasNext();) {
			result.add(iterator.next());
		}
		for (final Iterator<? extends BasePrimitive<?>> iterator = this.values.baseKeysOwnPrimitive(); iterator.hasNext();) {
			result.add(iterator.next());
		}
		return new Iterator<BasePrimitive<?>>() {
			
			private final Iterator<BasePrimitive<?>> e = result.iterator();
			
			private BasePrimitive<?> key;
			
			@Override
			public boolean hasNext() {
				
				return this.e.hasNext();
			}
			
			@Override
			public BasePrimitive<?> next() {
				
				return this.key = this.e.next();
			}
			
			@Override
			public void remove() {
				
				DataAccessEvaluate.this.baseDelete(this.key.baseToJavaString());
			}
		};
	}
	
	@Override
	public short propertyAttributes(final CharSequence name) {
		
		return BaseProperty.ATTRS_MASK_WED_IPK;
	}
	
	@Override
	public BaseObject propertyGet(final BaseObject instance, final BasePrimitiveString key) {
		
		final BaseObject result = this.values.baseGet(key, BaseObject.UNDEFINED);
		assert result != null : "NULL java value";
		if (result != BaseObject.UNDEFINED && result != DataAccessEvaluate.SYNCHRONIZE) {
			return result;
		}
		synchronized (this) {
			if (result == DataAccessEvaluate.SYNCHRONIZE) {
				final BaseObject value = this.values.baseGet(key, BaseObject.UNDEFINED);
				assert value != null : "NULL java value";
				if (value == DataAccessEvaluate.SYNCHRONIZE) {
					return this.data.baseGet(key, BaseObject.UNDEFINED);
				}
				if (value != BaseObject.UNDEFINED) {
					return value;
				}
			}
			final String keyString = key.toString();
			final ControlField field;
			if ("$type".equals(keyString)) {
				field = null;
			} else {
				final ControlFieldset<?> load = this.fieldset;
				field = load == null
					? null
					: load.getField(keyString);
			}
			if (field != null) {
				this.values.baseDefine(key, DataAccessEvaluate.SYNCHRONIZE);
				this.values.baseDefine(key, field.dataRetrieve(this.baseGet(key, BaseObject.UNDEFINED), this));
				final BaseObject value = this.values.baseGet(key, BaseObject.UNDEFINED);
				assert value != null : "NULL java value";
				if (value == DataAccessEvaluate.SYNCHRONIZE) {
					this.values.baseDefine(key, DataAccessEvaluate.NULL_FIELD_VALUE);
					return BaseObject.NULL;
				}
				return this.values.baseGet(key, BaseObject.UNDEFINED);
			}
			final BaseObject value = this.data.baseGet(key, BaseObject.UNDEFINED);
			assert value != null : "NULL java value";
			this.values.baseDefine(key, value == BaseObject.UNDEFINED
				? DataAccessEvaluate.NULL_FIELD_VALUE
				: value);
			return value;
		}
	}
	
	@Override
	public BaseObject propertyGet(final BaseObject instance, final String key) {
		
		final BaseObject result = this.values.baseGet(key, BaseObject.UNDEFINED);
		assert result != null : "NULL java value";
		if (result != BaseObject.UNDEFINED && result != DataAccessEvaluate.SYNCHRONIZE) {
			return result;
		}
		synchronized (this) {
			if (result == DataAccessEvaluate.SYNCHRONIZE) {
				final BaseObject value = this.values.baseGet(key, BaseObject.UNDEFINED);
				assert value != null : "NULL java value";
				if (value == DataAccessEvaluate.SYNCHRONIZE) {
					return this.data.baseGet(key, BaseObject.UNDEFINED);
				}
				if (value != BaseObject.UNDEFINED) {
					return value;
				}
			}
			final String keyString = key.toString();
			final ControlField field;
			if ("$type".equals(keyString)) {
				field = null;
			} else {
				final ControlFieldset<?> load = this.fieldset;
				field = load == null
					? null
					: load.getField(keyString);
			}
			if (field != null) {
				this.values.baseDefine(key, DataAccessEvaluate.SYNCHRONIZE);
				this.values.baseDefine(key, field.dataRetrieve(this.baseGet(key, BaseObject.UNDEFINED), this));
				final BaseObject value = this.values.baseGet(key, BaseObject.UNDEFINED);
				assert value != null : "NULL java value";
				if (value == DataAccessEvaluate.SYNCHRONIZE) {
					this.values.baseDefine(key, DataAccessEvaluate.NULL_FIELD_VALUE);
					return BaseObject.NULL;
				}
				return this.values.baseGet(key, BaseObject.UNDEFINED);
			}
			final BaseObject value = this.data.baseGet(key, BaseObject.UNDEFINED);
			assert value != null : "NULL java value";
			this.values.baseDefine(key, value == BaseObject.UNDEFINED
				? DataAccessEvaluate.NULL_FIELD_VALUE
				: value);
			return value;
		}
	}
	
	@Override
	public BaseObject propertyGetAndSet(final BaseObject instance, final String name, final BaseObject value) {
		
		try {
			return this.propertyGet(instance, name);
		} finally {
			this.propertySet(instance, name, value, BaseProperty.ATTRS_MASK_WED);
		}
	}
	
	@Override
	public ExecStateCode propertyGetCtxResult(final ExecProcess ctx, final BaseObject instance, final BasePrimitive<?> key, final ResultHandler store) {
		
		return store.execReturn(ctx, key instanceof BasePrimitiveString
			? this.propertyGet(instance, (BasePrimitiveString) key)
			: this.propertyGet(instance, key.toString()));
	}
	
	@Override
	public boolean propertySet(final BaseObject instance, final CharSequence name, final BaseObject value, final short attributes) {
		
		return this.baseDefine(name, value, attributes);
	}
	
	@Override
	public String toString() {
		
		return "[DataAccessEvaluate]";
	}
}
