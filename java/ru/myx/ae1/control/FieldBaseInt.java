package ru.myx.ae1.control;

import java.util.Collections;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveNumber;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.control.value.ValueSource;
import ru.myx.ae3.help.Convert;

/**
 * Title: System DocumentLevel for every level of 6 level model Description:
 * Copyright: Copyright (c) 2001 Company: -= MyX =-
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */

class FieldBaseInt extends AbstractField<FieldBaseInt, Number, BasePrimitiveNumber> {
	private boolean								constant;
	
	private ValueSource<BasePrimitiveNumber>	defaultValue;
	
	int											max	= Integer.MAX_VALUE;
	
	int											min	= Integer.MIN_VALUE;
	
	FieldBaseInt() {
		this.setAttributeIntern( "type", "number" );
		this.setAttributeIntern( "variant", "integer" );
		this.recalculate();
	}
	
	@Override
	public FieldBaseInt cloneField() {
		return new FieldBaseInt().setAttributes( this.getAttributes() );
	}
	
	@Override
	public BasePrimitiveNumber dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED) {
			final long l1 = Convert.Any.toLong( argument, 0L );
			final long l2 = Convert.Any.toLong( argument, 1L );
			if (l1 == l2) {
				return Base.forLong( l1 ).baseToInt32();
			}
		}
		return this.defaultValue.getObject( null ).baseToInt32();
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED) {
			final long l1 = Convert.Any.toLong( argument, 0L );
			final long l2 = Convert.Any.toLong( argument, 1L );
			if (l1 == l2) {
				return Base.forLong( l1 ).baseToInt32();
			}
		}
		return BaseObject.UNDEFINED;
	}
	
	@Override
	public String dataValidate(final BaseObject source) {
		try {
			this.dataValidate( Base.getJava( source, this.getKey(), null ) );
			return null;
		} catch (final Throwable t) {
			return t.getMessage();
		}
	}
	
	protected void dataValidate(final Object val) {
		if (this.constant) {
			return;
		}
		long value;
		if (val == null) {
			value = this.defaultValue.getLongValue( Collections.EMPTY_MAP );
		} else {
			final long l1 = Convert.Any.toLong( val, 0L );
			final long l2 = Convert.Any.toLong( val, 1L );
			if (l1 == l2) {
				value = l1;
			} else {
				value = this.defaultValue.getLongValue( Collections.EMPTY_MAP );
			}
		}
		if (value < this.min) {
			throw new IllegalArgumentException( "Minimum value is " + this.min );
		}
		if (value > this.max) {
			throw new IllegalArgumentException( "Maximum value is " + this.max );
		}
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		return ControlFieldset
				.createFieldset()
				.addField( ControlFieldFactory.createFieldBoolean( "constant",
						MultivariantString.getString( "Constant", Collections.singletonMap( "ru", "Константа" ) ),
						false ) ).addField( ControlFieldFactory.createFieldInteger( "default", "Default", 0 ) )
				.addField( ControlFieldFactory.createFieldInteger( "min", "Min value", Integer.MIN_VALUE ) )
				.addField( ControlFieldFactory.createFieldInteger( "max", "Max value", Integer.MAX_VALUE ) );
	}
	
	@Override
	public String getFieldClass() {
		return "int";
	}
	
	@Override
	public String getFieldClassTitle() {
		return "Int32 value";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		return "number".equals( type ) || "select".equals( type );
	}
	
	@Override
	public boolean isConstant() {
		return this.constant;
	}
	
	@Override
	protected void recalculate() {
		this.defaultValue = Control.getAttributeLongEx( Base.getJava( this.getAttributes(), "default", "0" ) );
		
		/**
		 * no the same as getBoolean - string value 'false', '0' etc should be
		 * treated as false!
		 */
		this.constant = Convert.MapEntry.toBoolean( this.getAttributes(), "constant", false );
		
		/**
		 * Not same as getInteger, human input allowed, values like 30m are
		 * valid
		 */
		this.min = Convert.MapEntry.toInt( this.getAttributes(), "min", Integer.MIN_VALUE );
		/**
		 * Not same as getInteger, human input allowed, values like 30m are
		 * valid
		 */
		this.max = Convert.MapEntry.toInt( this.getAttributes(), "max", Integer.MAX_VALUE );
	}
	
}
