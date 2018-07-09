package ru.myx.ae1.control;

import java.util.Collections;
import java.util.Map;

import org.xml.sax.SAXException;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.xml.Xml;

/**
 * Title: System DocumentLevel for every level of 6 level model Description:
 * Copyright: Copyright (c) 2001 Company: -= MyX =-
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */

final class FieldBaseMap extends AbstractField<FieldBaseMap, Object /*
																	 * Map<String
																	 * , Object>
																	 */, BaseObject> {
	private boolean				constant;
	
	private BaseObject		defaultValue;
	
	private ControlFieldset<?>	fieldset;
	
	private String				id;
	
	FieldBaseMap() {
		this.setAttributeIntern( "type", "map" );
		this.recalculate();
	}
	
	@Override
	public FieldBaseMap cloneField() {
		return new FieldBaseMap().setAttributes( this.getAttributes() );
	}
	
	@Override
	public BaseObject dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		if (argument instanceof Map<?, ?>) {
			if (this.fieldset == null) {
				return argument;
			}
			try {
				final BaseObject map = argument;
				this.fieldset.dataRetrieve( map, map );
			} catch (final UnsupportedOperationException e) {
				Report.exception( "FLD_MAP", "Cannot apply fieldset's retrieve to a map", e );
			}
			return argument;
		}
		if (argument.baseIsPrimitive()) {
			if (argument.baseIsPrimitiveString()) {
				if (this.fieldset == null) {
					return Xml.toMap( "mapRetrieve: " + this.id,
							argument.toString(),
							null,
							new BaseNativeObject( this.defaultValue.baseIsPrimitive()
									? null
									: this.defaultValue ),
							null,
							null );
				}
				final BaseMap map = new BaseNativeObject( this.defaultValue.baseIsPrimitive()
						? null
						: this.defaultValue );
				this.fieldset.dataRetrieve( Xml.toMap( "mapRetrieve: " + this.id,
						argument.toString(),
						null,
						new BaseNativeObject(),
						null,
						null ), map );
				return map;
			}
		}
		if (Base.hasKeys( argument )) {
			if (this.fieldset == null) {
				return argument;
			}
			final BaseObject map = new BaseNativeObject( this.defaultValue.baseIsPrimitive()
					? null
					: this.defaultValue );
			this.fieldset.dataRetrieve( argument, map );
			return map;
		}
		if (this.fieldset != null) {
			final BaseObject map = new BaseNativeObject();
			this.fieldset.dataRetrieve( this.defaultValue, map );
			return map;
		}
		return this.defaultValue;
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		assert argument != null : "NULL java object";
		if (argument instanceof Map<?, ?>) {
			if (this.fieldset != null) {
				try {
					this.fieldset.dataRetrieve( argument, argument );
				} catch (final UnsupportedOperationException e) {
					Report.exception( "FLD_MAP", "Cannot apply fieldset's retrieve to a map", e );
				}
			}
			return argument;
		}
		if (argument.baseIsPrimitive()) {
			if (argument.baseIsPrimitiveString()) {
				final BaseNativeObject result = new BaseNativeObject( this.defaultValue.baseIsPrimitive()
						? null
						: this.defaultValue );
				final BaseNativeObject source = this.fieldset == null
						? result
						: new BaseNativeObject();
				try {
					Xml.toMap( "mapRetrieve: " + this.id, //
							argument.toString(),
							null,
							source,
							null,
							null );
				} catch (final RuntimeException e) {
					final Throwable cause = e.getCause();
					if (cause instanceof SAXException) {
						// ignore - malformed XML likely
						Report.warning( "FIELD-MAP",
								"Ignored exception on heuristic XML parse",
								Format.Throwable.toText( e ) );
					} else {
						throw e;
					}
				}
				if (this.fieldset != null) {
					this.fieldset.dataRetrieve( source, result );
				}
				return result;
			}
		}
		if (Base.hasKeys( argument )) {
			if (this.fieldset == null) {
				return argument;
			}
			final BaseObject result = new BaseNativeObject( this.defaultValue.baseIsPrimitive()
					? null
					: this.defaultValue );
			this.fieldset.dataRetrieve( argument, result );
			return result;
		}
		return BaseObject.UNDEFINED;
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		return ControlFieldset
				.createFieldset()
				.addField( ControlFieldFactory.createFieldBoolean( "constant",
						MultivariantString.getString( "Constant", Collections.singletonMap( "ru", "Константа" ) ),
						false ) ).addField( ControlFieldFactory.createFieldMap( "default", "Default", null ) );
	}
	
	@Override
	public String getFieldClass() {
		return "map";
	}
	
	@Override
	public String getFieldClassTitle() {
		return "Map value";
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return this.fieldset;
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		return "map".equals( type );
	}
	
	@Override
	public boolean isConstant() {
		return this.constant;
	}
	
	@Override
	public void recalculate() {
		this.id = this.getKey();
		/**
		 * no the same as getBoolean - string value 'false', '0' etc should be
		 * treated as false!
		 */
		this.constant = Convert.MapEntry.toBoolean( this.getAttributes(), "constant", false );
		{
			final BaseObject object = this.getAttributes().baseGet( "fieldset", BaseObject.UNDEFINED );
			this.fieldset = object instanceof ControlFieldset<?>
					? (ControlFieldset<?>) object
					: null;
		}
		final BaseObject defaultObject = this.getAttributes().baseGet( "default", BaseObject.UNDEFINED );
		assert defaultObject != null : "NULL java value";
		if (defaultObject.baseIsPrimitiveString()) {
			this.defaultValue = defaultObject.toString().trim().startsWith( "<" )
					? Xml.toBase( "mapRecalculate", defaultObject.toString(), null, null, null )
					: new BaseNativeObject();
		} else {
			this.defaultValue = defaultObject;
		}
		assert this.defaultValue != null : "Should not be null!";
	}
	
}
