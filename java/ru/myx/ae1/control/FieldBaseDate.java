package ru.myx.ae1.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseDate;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.base.BasePrimitiveString;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.base.BaseWrapDate;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;

/**
 * Title: System DocumentLevel for every level of 6 level model Description:
 * Copyright: Copyright (c) 2001
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
public class FieldBaseDate extends AbstractField<FieldBaseDate, Date, BaseDate> {
	
	
	final class FormattedDate extends BaseWrapDate {
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6539346197969304154L;
		
		FormattedDate(final Date date) {
			
			super(date);
		}
		
		FormattedDate(final long millis) {
			
			super(new Date(millis));
		}
		
		@Override
		public BasePrimitiveString baseToString() {
			
			
			return Base.forString(this.toString());
		}
		
		public ControlField getField() {
			
			
			return FieldBaseDate.this;
		}
		
		@Override
		public String toString() {
			
			
			return FieldBaseDate.this.dateFormatter.format(this.date);
		}
	}
	
	final class FormattedDateLookup extends BaseHostLookup {
		
		
		@Override
		public BaseObject baseGetLookupValue(final BaseObject date) {
			
			
			if (date instanceof Date) {
				if (date == BaseDate.NOW) {
					return Base.forString("now");
				}
				if (date == BaseDate.UNKNOWN) {
					return BaseObject.UNDEFINED.baseToString();
				}
				return Base.forString(FieldBaseDate.this.dateFormatter.format((Date) date));
			}
			final long millis = Convert.Any.toLong(date, 0L);
			return Base.forString(FieldBaseDate.this.dateFormatter.format(new Date(millis)));
		}
		
		@Override
		public boolean baseHasKeysOwn() {
			
			
			return false;
		}
		
		@Override
		public Iterator<String> baseKeysOwn() {
			
			
			return BaseObject.ITERATOR_EMPTY;
		}
		
		@Override
		public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
			
			
			return BaseObject.ITERATOR_EMPTY_PRIMITIVE;
		}
		
		@Override
		public String toString() {
			
			
			return "[Lookup: Date Formatter]";
		}
	}
	
	private static final String YYYY_MM_DD_HH_MM = "yyyy.MM.dd HH:mm";
	
	private boolean constant;
	
	SimpleDateFormat dateFormatter;
	
	private Date def;
	
	private String format;
	
	private String id;
	
	FieldBaseDate() {
		
		this.setAttributeIntern("type", "date");
		this.recalculate();
	}
	
	@Override
	public FieldBaseDate cloneField() {
		
		
		return new FieldBaseDate().setAttributes(this.getAttributes());
	}
	
	@Override
	public FormattedDate dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		
		
		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			if (argument instanceof FormattedDate) {
				final FormattedDate formatted = (FormattedDate) argument;
				return this == formatted.getField()
					? formatted
					: new FormattedDate(formatted.getTime());
			}
			/**
			 * including BaseWrapDate, BaseDate
			 */
			if (argument instanceof Date) {
				return new FormattedDate((Date) argument);
			}
			{
				final Object baseValue = argument.baseValue();
				if (baseValue != null && baseValue != argument) {
					if (baseValue instanceof Date) {
						return new FormattedDate((Date) baseValue);
					}
				}
				final Date value = this.getValue(baseValue);
				if (value != null) {
					return new FormattedDate(value);
				}
			}
			{
				final Date value = this.getValue(argument);
				if (value != null) {
					return new FormattedDate(value);
				}
			}
		}
		return new FormattedDate(this.def.getTime());
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		
		
		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			if (argument instanceof BaseDate) {
				return argument;
			}
			final Object baseValue = argument.baseValue();
			if (baseValue != null) {
				if (baseValue instanceof Date) {
					return Base.forDate((Date) baseValue);
				}
				final Date value = this.getValue(baseValue);
				if (value != null) {
					return Base.forDate(value);
				}
			}
		}
		return BaseObject.UNDEFINED;
	}
	
	@Override
	public String dataValidate(final BaseObject source) {
		
		
		try {
			this.dataValidate(Base.getJava(source, this.id, null));
			return null;
		} catch (final Throwable t) {
			return t.getMessage();
		}
	}
	
	/**
	 * @param value
	 */
	protected void dataValidate(final Object value) {
		
		
		if (this.constant) {
			return;
		}
		if (value == null) {
			/**
			 * default is valid yes?
			 */
			return;
		}
		if (this.getValue(value) == null) {
			throw new IllegalArgumentException("Input format is " + this.format + (value.getClass() == String.class
				? "(value='" + value + "')"
				: " (value class='" + value.getClass().getName() + "')"));
		}
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		
		
		return ControlFieldset.createFieldset()
				//
				.addField(ControlFieldFactory.createFieldBoolean(
						"constant", //
						MultivariantString.getString(
								"Constant", //
								Collections.singletonMap(
										"ru", //
										"Константа")),
						false))
				//
				.addField(ControlFieldFactory.createFieldString("format", "Format", FieldBaseDate.YYYY_MM_DD_HH_MM))//
				.addField(ControlFieldFactory.createFieldString("default", "Default", "NOW"))//
		;
	}
	
	@Override
	public String getFieldClass() {
		
		
		return "date";
	}
	
	@Override
	public String getFieldClassTitle() {
		
		
		return "Date value";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		
		
		return "date".equals(type);
	}
	
	private final Date getValue(final Object value) {
		
		
		if (value instanceof Number) {
			return new Date(((Number) value).longValue());
		}
		if (value instanceof Date) {
			return (Date) value;
		}
		final String valueString = value.toString().trim();
		if (valueString.length() == 0 || valueString.equalsIgnoreCase("NOW")) {
			return BaseDate.NOW;
		}
		if (valueString.equalsIgnoreCase("undefined")) {
			return BaseDate.UNKNOWN;
		}
		try {
			return this.dateFormatter.parse(valueString);
		} catch (final ParseException e) {
			return null;
		}
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
		this.constant = Convert.MapEntry.toBoolean(this.getAttributes(), "constant", false);
		this.format = Base.getString(this.getAttributes(), "format", FieldBaseDate.YYYY_MM_DD_HH_MM);
		this.dateFormatter = new SimpleDateFormat(this.format);
		/**
		 * default
		 */
		final Object dateObject = Base.getJava(this.getAttributes(), "default", "");
		if (dateObject instanceof Date) {
			this.def = new FormattedDate((Date) dateObject);
		} else {
			final String dateString = dateObject.toString();
			if (dateString.length() == 0 || dateString.equalsIgnoreCase("NOW")) {
				this.def = new FormattedDate(Engine.CURRENT_TIME);
			} else {
				try {
					this.def = new FormattedDate(this.dateFormatter.parse(dateString));
				} catch (final ParseException e) {
					this.def = new FormattedDate(Convert.Any.toLong(dateString, 0L));
				}
			}
		}
		/**
		 * lookup
		 */
		final BaseObject lookupChecker = this.getAttributes().baseGet("lookup", BaseObject.UNDEFINED);
		assert lookupChecker != null : "NULL java value";
		if (lookupChecker == BaseObject.UNDEFINED || lookupChecker instanceof FormattedDateLookup) {
			/**
			 * I doubt it is needed at all, cause dataRetrieve produces
			 * formatted object really. Kind of in CTRL interfaces we use
			 * lookups instead of dateRetrieve. Should it be changed?
			 */
			if (this.constant) {
				this.getAttributes().baseDelete("lookup");
			} else {
				this.getAttributes().baseDefine("lookup", new FormattedDateLookup(), BaseProperty.ATTRS_MASK_NND);
			}
		}
	}
}
