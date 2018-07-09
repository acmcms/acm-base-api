package ru.myx.ae1.control;

import java.util.Collections;

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
final class FieldBaseObject extends AbstractField<FieldBaseObject, Object, BaseObject> {
	private boolean			constant;
	
	private BaseObject	defaultValue;
	
	FieldBaseObject() {
		this.setAttributeIntern( "type", "object" );
		this.recalculate();
	}
	
	@Override
	public FieldBaseObject cloneField() {
		return new FieldBaseObject().setAttributes( this.getAttributes() );
	}
	
	@Override
	public BaseObject dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		return argument == BaseObject.UNDEFINED
				? this.defaultValue
				: argument;
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		return argument == BaseObject.NULL
				? BaseObject.UNDEFINED
				: argument;
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		return ControlFieldset
				.createFieldset()
				.addField( ControlFieldFactory.createFieldBoolean( "constant",
						MultivariantString.getString( "Constant", Collections.singletonMap( "ru", "Константа" ) ),
						false ) ).addField( ControlFieldFactory.createFieldObject( "default", "Default", null ) );
	}
	
	@Override
	public String getFieldClass() {
		return "object";
	}
	
	@Override
	public String getFieldClassTitle() {
		return "Object value";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		return "object".equals( type );
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
		this.constant = Convert.MapEntry.toBoolean( this.getAttributes(), "constant", false );
		this.defaultValue = this.getAttributes().baseGet( "default", BaseObject.UNDEFINED );
	}
}
