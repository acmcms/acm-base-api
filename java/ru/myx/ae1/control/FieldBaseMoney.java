package ru.myx.ae1.control;

import java.text.NumberFormat;
import java.util.Collections;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveString;
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

final class FieldBaseMoney extends AbstractField<FieldBaseMoney, String, BasePrimitiveString> {
	private static final NumberFormat	FORMATTER	= NumberFormat.getInstance();
	static {
		FieldBaseMoney.FORMATTER.setGroupingUsed( false );
		FieldBaseMoney.FORMATTER.setMaximumFractionDigits( 2 );
		FieldBaseMoney.FORMATTER.setMinimumFractionDigits( 2 );
	}
	
	private boolean						constant;
	
	private double						def;
	
	private String						id;
	
	FieldBaseMoney() {
		this.setAttributeIntern( "type", "number" );
		this.recalculate();
	}
	
	@Override
	public FieldBaseMoney cloneField() {
		return new FieldBaseMoney().setAttributes( this.getAttributes() );
	}
	
	@Override
	public BasePrimitiveString dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		synchronized (FieldBaseMoney.FORMATTER) {
			return Base.forString( FieldBaseMoney.FORMATTER.format( Convert.Any.toDouble( argument, this.def ) ) );
		}
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			final double value = Convert.Any.toDouble( argument, Double.NaN );
			if (value != Double.NaN) {
				return Base.forDouble( value );
			}
		}
		return BaseObject.UNDEFINED;
	}
	
	@Override
	public String dataValidate(final BaseObject source) {
		try {
			final Object object = Base.getJava( source, this.id, null );
			if (object instanceof Number) {
				((Number) object).doubleValue();
			} else {
				try {
					Double.parseDouble( object.toString() );
				} catch (final java.lang.NumberFormatException e) {
					return object + " number format is invalid!";
				}
			}
			return null;
		} catch (final Throwable t) {
			return t.getMessage();
		}
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		return ControlFieldset
				.createFieldset()
				.addField( ControlFieldFactory.createFieldBoolean( "constant",
						MultivariantString.getString( "Constant", Collections.singletonMap( "ru", "Константа" ) ),
						false ) ).addField( ControlFieldFactory.createFieldString( "default", "Default", "0.00" ) )
				.addField( ControlFieldFactory.createFieldFloating( "min", "Min value", Double.NEGATIVE_INFINITY ) )
				.addField( ControlFieldFactory.createFieldFloating( "max", "Max value", Double.POSITIVE_INFINITY ) );
	}
	
	@Override
	public String getFieldClass() {
		return "money";
	}
	
	@Override
	public String getFieldClassTitle() {
		return "Money (floating)";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		return "number".equals( type );
	}
	
	@Override
	public boolean isConstant() {
		return this.constant;
	}
	
	@Override
	protected void recalculate() {
		this.id = this.getKey();
		this.def = Base.getDouble( this.getAttributes(), "default", 0d );
		/**
		 * no the same as getBoolean - string value 'false', '0' etc should be
		 * treated as false!
		 */
		this.constant = Convert.MapEntry.toBoolean( this.getAttributes(), "constant", false );
	}
	
}
