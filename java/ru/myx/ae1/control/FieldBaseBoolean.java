package ru.myx.ae1.control;

import java.util.Collections;

import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveBoolean;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.reflect.ControlType;
import ru.myx.ae3.reflect.Reflect;

/**
 * Title: System DocumentLevel for every level of 6 level model Description:
 * Copyright: Copyright (c) 2001 Company: -= MyX =-
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */

final class FieldBaseBoolean extends AbstractField<FieldBaseBoolean, Boolean, BasePrimitiveBoolean> {
	private boolean	constant;
	
	private boolean	def;
	
	FieldBaseBoolean() {
		this.setAttributeIntern( "type", "boolean" );
		this.setAttributeIntern( "default", false );
		this.setAttributeIntern( "constant", false );
		this.recalculate();
	}
	
	@Override
	public FieldBaseBoolean cloneField() {
		return new FieldBaseBoolean().setAttributes( this.getAttributes() );
	}
	
	@Override
	public BasePrimitiveBoolean dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java value!";
		return argument == BaseObject.UNDEFINED
				? this.def
						? BaseObject.TRUE
						: BaseObject.FALSE
				: argument.baseToBoolean();
	}
	
	@Override
	public BasePrimitiveBoolean dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java value!";
		return Convert.Any.toBoolean( argument, this.def )
				? BaseObject.TRUE
				: BaseObject.FALSE;
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		return ControlFieldset
				.createFieldset()
				.addField( ControlFieldFactory.createFieldBoolean( "constant",
						MultivariantString.getString( "Constant", Collections.singletonMap( "ru", "Константа" ) ),
						false ) ).addField( ControlFieldFactory.createFieldBoolean( "default", "Default value", false ) );
	}
	
	@Override
	public String getFieldClass() {
		return "boolean";
	}
	
	@Override
	public String getFieldClassTitle() {
		return "Boolean variable";
	}
	
	@Override
	public ControlType<?, ?> getFieldDataType() {
		return Reflect.CONTROL_TYPE_BOOLEAN;
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		return "boolean".equals( type );
	}
	
	@Override
	public boolean isConstant() {
		return this.constant;
	}
	
	@Override
	protected void recalculate() {
		/**
		 * no the same as getBoolean - string value 'false', '0' etc should be
		 * treated as false!
		 */
		this.def = Convert.MapEntry.toBoolean( this.getAttributes(), "default", false );
		/**
		 * no the same as getBoolean - string value 'false', '0' etc should be
		 * treated as false!
		 */
		this.constant = Convert.MapEntry.toBoolean( this.getAttributes(), "constant", false );
	}
	
	public FieldBaseBoolean setDefault(final boolean value) {
		if (this.def != value) {
			this.setAttribute( "default", (this.def = value) );
		}
		return this;
	}
}
