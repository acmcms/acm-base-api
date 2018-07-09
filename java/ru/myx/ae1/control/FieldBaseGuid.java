package ru.myx.ae1.control;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveString;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/**
 * Title: System DocumentLevel for every level of 6 level model Description:
 * Copyright: Copyright (c) 2001 Company: -= MyX =-
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */

public class FieldBaseGuid extends AbstractField<FieldBaseGuid, String, BasePrimitiveString> {
	
	FieldBaseGuid() {
		this.setAttributeIntern( "type", "string" );
		this.recalculate();
	}
	
	@Override
	public FieldBaseGuid cloneField() {
		return new FieldBaseGuid().setAttributes( this.getAttributes() );
	}
	
	@Override
	public BasePrimitiveString dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		return argument == BaseObject.UNDEFINED
				? Base.forString( Engine.createGuid() )
				: argument.baseToString();
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		return argument == BaseObject.UNDEFINED
				? Base.forString( Engine.createGuid() )
				: argument.baseToString();
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		return ControlFieldset.createFieldset();
	}
	
	@Override
	public String getFieldClass() {
		return "guid";
	}
	
	@Override
	public String getFieldClassTitle() {
		return "GUID value";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		return "string".equals( type ) || "transparent".equals( type );
	}
	
	@Override
	public boolean isConstant() {
		return false;
	}
	
}
