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

/** Title: System DocumentLevel for every level of 6 level model Description: Copyright: Copyright
 * (c) 2001 Company: -= MyX =-
 *
 * @author Alexander I. Kharitchev
 * @version 1.0 */

class FieldBaseDouble extends AbstractField<FieldBaseDouble, Number /* Double */, BasePrimitiveNumber> {

	private boolean constant;

	private ValueSource<BasePrimitiveNumber> defaultValue;

	private String id;

	double max = Double.MAX_VALUE;

	double min = Double.MIN_VALUE;

	FieldBaseDouble() {
		this.setAttributeIntern("type", "number");
		this.setAttributeIntern("variant", "floating");
		this.recalculate();
	}

	@Override
	public FieldBaseDouble cloneField() {

		return new FieldBaseDouble().setAttributes(this.getAttributes());
	}

	@Override
	public BasePrimitiveNumber dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {

		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			final double l1 = Convert.Any.toDouble(argument, 0L);
			final double l2 = Convert.Any.toDouble(argument, 1L);
			if (l1 == l2) {
				return Base.forDouble(l1);
			}
		}
		return this.defaultValue.getObject(null);
	}

	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {

		assert argument != null : "NULL java object";
		if (argument != BaseObject.UNDEFINED && argument != BaseObject.NULL) {
			final double l1 = Convert.Any.toDouble(argument, 0L);
			final double l2 = Convert.Any.toDouble(argument, 1L);
			if (l1 == l2) {
				return Base.forDouble(l1);
			}
		}
		return BaseObject.UNDEFINED;
	}

	@Override
	public String dataValidate(final BaseObject source) {

		if (this.constant) {
			return null;
		}

		final Object val = Base.getJava(source, this.id, null);
		final double value = Convert.Any.toDouble(//
				val == null
					? this.defaultValue.getObject(Collections.EMPTY_MAP)
					: val,
				0.0//
		);
		if (value < this.min) {
			return "Minimum value is " + this.min;
		}
		if (value > this.max) {
			return "Maximum value is " + this.max;
		}
		return null;
	}

	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {

		return ControlFieldset.createFieldset()
				.addField(ControlFieldFactory.createFieldBoolean("constant", MultivariantString.getString("Constant", Collections.singletonMap("ru", "Константа")), false))
				.addField(ControlFieldFactory.createFieldFloating("default", "Default", 0.0))
				.addField(ControlFieldFactory.createFieldFloating("min", "Min value", Double.NEGATIVE_INFINITY))
				.addField(ControlFieldFactory.createFieldFloating("max", "Max value", Double.POSITIVE_INFINITY));
	}

	@Override
	public String getFieldClass() {

		return "floating";
	}

	@Override
	public String getFieldClassTitle() {

		return "Floating variable";
	}

	@Override
	public boolean getFieldTypeAvailability(final String type) {

		return "number".equals(type) || "select".equals(type);
	}

	@Override
	public boolean isConstant() {

		return this.constant;
	}

	@Override
	protected void recalculate() {

		this.id = this.getKey();
		this.defaultValue = Control.getAttributeFloatingEx(Base.getJava(this.getAttributes(), "default", ""));
		/** no the same as getBoolean - string value 'false', '0' etc should be treated as false! */
		this.constant = Convert.MapEntry.toBoolean(this.getAttributes(), "constant", false);

		/** Not same as getInteger, human input allowed, values like 30m are valid */
		this.min = Convert.MapEntry.toDouble(this.getAttributes(), "min", Double.MIN_VALUE);
		/** Not same as getInteger, human input allowed, values like 30m are valid */
		this.max = Convert.MapEntry.toDouble(this.getAttributes(), "max", Double.MAX_VALUE);
	}

}
