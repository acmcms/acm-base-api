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
import ru.myx.ae3.reflect.ControlType;
import ru.myx.ae3.reflect.Reflect;

/**
 * Title: System DocumentLevel for every level of 6 level model Description:
 * Copyright: Copyright (c) 2001 Company: -= MyX =-
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */

class FieldBaseLong extends AbstractField<FieldBaseLong, Number /* Long */, BasePrimitiveNumber> {
	private boolean								constant;
	
	private ValueSource<BasePrimitiveNumber>	defaultValue;
	
	private String								id;
	
	long										max	= Long.MAX_VALUE;
	
	long										min	= Long.MIN_VALUE;
	
	FieldBaseLong() {
		this.setAttributeIntern( "type", "number" );
		this.setAttributeIntern( "variant", "integer" );
		this.recalculate();
	}
	
	@Override
	public FieldBaseLong cloneField() {
		return new FieldBaseLong().setAttributes( this.getAttributes() );
	}
	
	@Override
	public BasePrimitiveNumber dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			final long l1 = Convert.Any.toLong( argument, 0L );
			final long l2 = Convert.Any.toLong( argument, 1L );
			if (l1 == l2) {
				return Base.forLong( l1 );
			}
		}
		return this.defaultValue.getObject( argument ).baseToInteger();
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			final long l1 = Convert.Any.toLong( argument, 0L );
			final long l2 = Convert.Any.toLong( argument, 1L );
			if (l1 == l2) {
				return Base.forLong( l1 );
			}
		}
		return BaseObject.UNDEFINED;
	}
	
	@Override
	public String dataValidate(final BaseObject source) {
		try {
			this.dataValidate( Base.getJava( source, this.id, null ) );
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
						false ) ).addField( ControlFieldFactory.createFieldLong( "default", "Default", 0 ) )
				.addField( ControlFieldFactory.createFieldLong( "min", "Min value", Long.MIN_VALUE ) )
				.addField( ControlFieldFactory.createFieldLong( "max", "Max value", Long.MAX_VALUE ) );
	}
	
	@Override
	public String getFieldClass() {
		return "integer";
	}
	
	@Override
	public String getFieldClassTitle() {
		return "Integer value";
	}
	
	@Override
	public final ControlType<?, ?> getFieldDataType() {
		return Reflect.CONTROL_TYPE_LONG;
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
		this.id = this.getKey();
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
		this.min = Convert.MapEntry.toLong( this.getAttributes(), "min", Long.MIN_VALUE );
		/**
		 * Not same as getInteger, human input allowed, values like 30m are
		 * valid
		 */
		this.max = Convert.MapEntry.toLong( this.getAttributes(), "max", Long.MAX_VALUE );
	}
	
}
