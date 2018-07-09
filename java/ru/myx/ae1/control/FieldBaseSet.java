package ru.myx.ae1.control;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Create;

/**
 * Title: System DocumentLevel for every level of 6 level model Description:
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */

final class FieldBaseSet extends AbstractField<FieldBaseSet, Set<?>, BaseObject> {
	
	
	private boolean constant;
	
	private Set<?> defaultValue;
	
	FieldBaseSet() {
		
		this.setAttributeIntern("type", "set");
		this.recalculate();
	}
	
	@Override
	public FieldBaseSet cloneField() {
		
		
		return new FieldBaseSet().setAttributes(this.getAttributes());
	}
	
	@Override
	public BaseObject dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		
		
		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			{
				final BaseArray array = argument.baseArray();
				if (array != null) {
					final Set<BaseObject> set;
					set = Convert.Any.toAny(Create.tempSet(array.baseIterator()));
					return Convert.Any.toAny(Base.forUnknown(set));
				}
			}
			{
				if (argument instanceof Set) {
					return argument;
				}
			}
			{
				final Object baseValue = argument.baseValue();
				/**
				 * even when it is equal to original 'argument'
				 */
				if (baseValue != null) {
					if (baseValue instanceof Set) {
						return argument;
					}
					final Set<?> set;
					if (baseValue instanceof Collection) {
						set = Create.tempSet((Collection<?>) baseValue);
					} else if (baseValue instanceof Object[]) {
						set = Create.tempSet(Arrays.asList((Object[]) baseValue));
					} else {
						set = Create.tempSet(Arrays.asList((Object[]) baseValue.toString().split(",")));
					}
					return Convert.Any.toAny(Base.forUnknown(set));
				}
			}
		}
		return Convert.Any.toAny(Base.forUnknown(this.defaultValue != null
			? this.defaultValue
			: Create.tempSet()));
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		
		
		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			final Object baseValue = argument.baseValue();
			if (baseValue != null) {
				if (baseValue instanceof Set) {
					return argument;
				}
				final Set<?> set;
				if (baseValue instanceof Collection) {
					set = Create.tempSet((Collection<?>) baseValue);
				} else if (baseValue instanceof Object[]) {
					set = Create.tempSet(Arrays.asList((Object[]) baseValue));
				} else {
					set = Create.tempSet(Arrays.asList((Object[]) baseValue.toString().split(",")));
				}
				return Base.forUnknown(set);
			}
		}
		return BaseObject.UNDEFINED;
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		
		
		return ControlFieldset
				.createFieldset() //
				.addField(ControlFieldFactory.createFieldBoolean(
						"constant", //
						MultivariantString.getString(
								"Constant", //
								Collections.singletonMap("ru", "Константа")),
						false))//
				.addField(ControlFieldFactory.createFieldSet(
						"default", //
						"Default",
						Collections.EMPTY_SET));
	}
	
	@Override
	public String getFieldClass() {
		
		
		return "set";
	}
	
	@Override
	public String getFieldClassTitle() {
		
		
		return "Set value";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		
		
		return "set".equals(type);
	}
	
	@Override
	public boolean isConstant() {
		
		
		return this.constant;
	}
	
	@Override
	public void recalculate() {
		
		
		/**
		 * no the same as getBoolean - string value 'false', '0' etc should be
		 * treated as false!
		 */
		this.constant = Convert.MapEntry.toBoolean(this.getAttributes(), "constant", false);
		final Object defaultObject = Base.getJava(this.getAttributes(), "default", null);
		if (defaultObject == null) {
			this.defaultValue = null;
		} else {
			if (defaultObject instanceof Set<?>) {
				this.defaultValue = (Set<?>) defaultObject;
			} else if (defaultObject instanceof Collection<?>) {
				this.defaultValue = Create.tempSet((Collection<?>) defaultObject);
			} else if (defaultObject instanceof Object[]) {
				this.defaultValue = Create.tempSet(Arrays.asList((Object[]) defaultObject));
			} else {
				final String defaultString = defaultObject.toString();
				if (defaultString.length() == 0) {
					this.defaultValue = Create.tempSet();
				} else {
					this.defaultValue = Create.tempSet(Arrays.asList((Object[]) defaultString.split(",")));
				}
			}
		}
	}
	
}
