package ru.myx.ae1.control;

import java.util.Collections;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveString;
import ru.myx.ae3.base.BaseString;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.control.value.ValueSource;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.reflect.ControlType;
import ru.myx.ae3.reflect.Reflect;

/** Title: System DocumentLevel for every level of 6 level model Description: Copyright: Copyright
 * (c) 2001 Company: -= MyX =-
 *
 * @author Alexander I. Kharitchev
 * @version 1.0 */

public class FieldBaseString extends AbstractField<FieldBaseString, String, BasePrimitiveString> {

	private static final Object STR_MAX_LENGTH = MultivariantString.getString("Maximal length is ", Collections.singletonMap("ru", "максимальная длинна значения "));

	private static final Object STR_MIN_LENGTH = MultivariantString.getString("Minimal length is ", Collections.singletonMap("ru", "минимальная длинна значения "));

	private static final Object STR_REQUIRED = MultivariantString.getString("Field is required", Collections.singletonMap("ru", "поле обязательно для заполнения"));

	private boolean constant;

	private ValueSource<BasePrimitiveString> defaultValue;

	private int max;

	private int min;

	/**
	 *
	 */
	protected FieldBaseString() {
		
		this.setAttributeIntern("type", "string");
		this.recalculate();
	}

	@Override
	public FieldBaseString cloneField() {

		return new FieldBaseString().setAttributes(this.getAttributes());
	}

	@Override
	public BasePrimitiveString dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {

		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			final BasePrimitiveString string = argument.baseToString();
			final int length = string.length();
			if (length >= this.min && length <= this.max) {
				return string;
			}
		}
		return this.defaultValue.getObject(null);
	}

	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {

		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			final BasePrimitiveString string = argument.baseToString();
			final int length = string.length();
			if (length >= this.min && length <= this.max) {
				return string;
			}
		}
		return BaseObject.UNDEFINED;
	}

	@Override
	public String dataValidate(final BaseObject source) {

		if (this.constant) {
			return null;
		}
		assert source != null : "NULL java value";
		try {
			BaseObject valueObject = source.baseGet(this.getKey(), BaseObject.UNDEFINED);
			assert valueObject != null : "NULL java value";
			if (valueObject == BaseObject.NULL || valueObject == BaseObject.UNDEFINED) {
				valueObject = this.defaultValue == null
					? BaseString.EMPTY
					: this.defaultValue.getStringValue(Collections.EMPTY_MAP);
			}
			final String value = valueObject.baseToJavaString();
			final int length = value.length();
			if (length < this.min) {
				throw new IllegalArgumentException(
						this.min == 1
							? FieldBaseString.STR_REQUIRED.toString()
							: FieldBaseString.STR_MIN_LENGTH.toString() + this.min //
				);
			}
			if (length > this.max) {
				throw new IllegalArgumentException(FieldBaseString.STR_MAX_LENGTH.toString() + this.max);
			}
			return null;
		} catch (final Throwable t) {
			return t.getMessage();
		}
	}

	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {

		return ControlFieldset.createFieldset()
				.addField(ControlFieldFactory.createFieldBoolean("constant", MultivariantString.getString("Constant", Collections.singletonMap("ru", "Константа")), false))
				.addField(ControlFieldFactory.createFieldString("default", "Default", ""))
				.addField(ControlFieldFactory.createFieldInteger("min", "Min length", 0, 0, Integer.MAX_VALUE))
				.addField(ControlFieldFactory.createFieldInteger("max", "Max length", 1024, 1, Integer.MAX_VALUE));
	}

	@Override
	public String getFieldClass() {

		return "string";
	}

	@Override
	public String getFieldClassTitle() {

		return "String value";
	}

	@Override
	public final ControlType<?, ?> getFieldDataType() {

		return Reflect.CONTROL_TYPE_STRING;
	}

	@Override
	public boolean getFieldTypeAvailability(final String type) {

		return "string".equals(type) || "text".equals(type) || "select".equals(type);
	}

	@Override
	public boolean isConstant() {

		return this.constant;
	}

	@Override
	public void recalculate() {

		this.defaultValue = Control.getAttributeStringEx(Base.getJava(this.getAttributes(), "default", ""));
		/** no the same as getBoolean - string value 'false', '0' etc should be treated as false! */
		this.constant = Convert.MapEntry.toBoolean(this.getAttributes(), "constant", false);

		/** Human readable (kinda 64k) and so on */
		this.min = Convert.MapEntry.toInt(this.getAttributes(), "min", 0);
		this.max = Convert.MapEntry.toInt(this.getAttributes(), "max", Integer.MAX_VALUE);
	}

}
