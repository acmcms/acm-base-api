package ru.myx.ae1.control;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseNativeArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;

/**
 * Title: System DocumentLevel for every level of 6 level model Description:
 * Copyright: Copyright (c) 2001 Company: -= MyX =-
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */

final class FieldBaseList
		extends
			AbstractField<FieldBaseList, Object /* List<?> */, BaseArray> {
			
	private boolean constant;
	
	private List<?> defaultValue;
	
	FieldBaseList() {
		
		this.setAttributeIntern("type", "list");
		this.recalculate();
	}
	
	@Override
	public FieldBaseList cloneField() {
		
		return new FieldBaseList().setAttributes(this.getAttributes());
	}
	
	@Override
	public BaseArray dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		
		assert argument != null : "NULL java object";
		/**
		 * Check for a string before checking for an array cause string is kind
		 * of array.
		 */
		if (argument.baseIsPrimitiveString()) {
			return Base.forArray(argument.baseToJavaString().split(","));
		}
		{
			final BaseArray array = argument.baseArray();
			if (array != null) {
				return array;
			}
		}
		if (argument != BaseObject.UNDEFINED) {
			final Object baseValue = argument.baseValue();
			if (baseValue instanceof Collection<?>) {
				return Base.forArray(((Collection<?>) baseValue).toArray());
			}
			return new BaseNativeArray(argument);
		}
		if (this.defaultValue != null) {
			return Base.forArray(this.defaultValue.toArray());
		}
		return BaseObject.createArray(0);
		// return Base.OBJECT_FACTORY.createArray(expectedLength);
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		
		assert argument != null : "NULL java object";
		/**
		 * Check for a string before checking for an array cause string is kind
		 * of array.
		 */
		if (argument.baseIsPrimitiveString()) {
			return Base.forArray(argument.baseToJavaString().split(","));
		}
		{
			final BaseArray array = argument.baseArray();
			if (array != null) {
				return array;
			}
		}
		if (argument != BaseObject.UNDEFINED) {
			final Object baseValue = argument.baseValue();
			if (baseValue instanceof Collection<?>) {
				return Base.forArray(((Collection<?>) baseValue).toArray());
			}
			return new BaseNativeArray(argument);
		}
		return BaseObject.UNDEFINED;
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		
		return ControlFieldset.createFieldset()
				.addField(ControlFieldFactory.createFieldBoolean("constant", MultivariantString.getString("Constant", Collections.singletonMap("ru", "Константа")), false))
				.addField(Control.createFieldList("default", "Default", null));
	}
	
	@Override
	public String getFieldClass() {
		
		return "list";
	}
	
	@Override
	public String getFieldClassTitle() {
		
		return "List value";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		
		return "list".equals(type);
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
			if (defaultObject instanceof List<?>) {
				this.defaultValue = (List<?>) defaultObject;
			} else if (defaultObject instanceof Collection<?>) {
				this.defaultValue = Arrays.asList(((Collection<?>) defaultObject).toArray());
			} else if (defaultObject instanceof Object[]) {
				this.defaultValue = Arrays.asList((Object[]) defaultObject);
			} else {
				this.defaultValue = Arrays.asList(defaultObject.toString().split(","));
			}
		}
	}
	
}
