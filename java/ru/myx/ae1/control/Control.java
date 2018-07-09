/**
 * Created on 28.11.2002
 *
 * myx - barachta */
package ru.myx.ae1.control;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;

import javax.script.Bindings;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitiveNumber;
import ru.myx.ae3.base.BasePrimitiveString;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.ControlForm;
import ru.myx.ae3.control.DefaultBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.command.SimpleCommand;
import ru.myx.ae3.control.command.SimpleCommandset;
import ru.myx.ae3.control.command.SplitterCommand;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.control.value.ValueSource;
import ru.myx.ae3.control.value.ValueSourceFloatingConst;
import ru.myx.ae3.control.value.ValueSourceFloatingEx;
import ru.myx.ae3.control.value.ValueSourceFloatingObject;
import ru.myx.ae3.control.value.ValueSourceLongConst;
import ru.myx.ae3.control.value.ValueSourceLongEx;
import ru.myx.ae3.control.value.ValueSourceLongObject;
import ru.myx.ae3.control.value.ValueSourceStringConst;
import ru.myx.ae3.control.value.ValueSourceStringEx;
import ru.myx.ae3.control.value.ValueSourceStringObject;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Create;
import ru.myx.ae3.reflect.ControlType;
import ru.myx.ae3.reflect.Reflect;

/** @author myx
 *
 *         myx - barachta */
public final class Control {

	/**
	 *
	 */
	public static final String FIELD_TYPE_TRANSPARENT = "transparent";

	/**
	 *
	 */
	public static final String FIELD_TYPE_BOOLEAN = "boolean";

	/**
	 *
	 */
	public static final String FIELD_TYPE_NUMBER = "number";

	/**
	 *
	 */
	public static final String FIELD_TYPE_STRING = "string";

	/**
	 *
	 */
	public static final String FIELD_TYPE_TEXT = "text";

	/**
	 *
	 */
	public static final String FIELD_TYPE_DATE = "date";

	/**
	 *
	 */
	public static final String FIELD_TYPE_BINARY = "binary";

	/**
	 *
	 */
	public static final String FIELD_TYPE_SELECT = "select";

	/**
	 *
	 */
	public static final String FIELD_TYPE_SET = "set";

	/**
	 *
	 */
	public static final String FIELD_TYPE_LIST = "list";

	/**
	 *
	 */
	public static final String FIELD_TYPE_MAP = "map";

	/**
	 *
	 */
	public static final Object INSTANCE = new Control();

	private static final Map<String, ControlType<?, ?>> TYPES = Control.createTypeMap();

	static {
		/** make sure that static field map is created */
		ControlFieldFactory.registeredFields();

		// !!! documentation: new constant field types
		ControlFieldFactory.registerFieldClass(
				"true", //
				new FieldBaseBoolean().setDefault(true).setConstant());
		ControlFieldFactory.registerFieldClass(
				"false", //
				new FieldBaseBoolean().setDefault(false).setConstant());
		
		ControlFieldFactory.registerFieldClass(
				"boolean", //
				new FieldBaseBoolean());
		ControlFieldFactory.registerFieldClass(
				"binary", //
				new FieldBaseBinary());
		ControlFieldFactory.registerFieldClass(
				"date", //
				new FieldBaseDate());
		ControlFieldFactory.registerFieldClass(
				"double", //
				new FieldBaseDouble());
		ControlFieldFactory.registerFieldClass(
				"evaluate", //
				new FieldBaseEvaluate());
		ControlFieldFactory.registerFieldClass(
				"floating", //
				new FieldBaseDouble());
		ControlFieldFactory.registerFieldClass(
				"guid", //
				new FieldBaseGuid());
		ControlFieldFactory.registerFieldClass(
				"integer", //
				new FieldBaseLong());
		ControlFieldFactory.registerFieldClass(
				"long", //
				new FieldBaseLong());
		ControlFieldFactory.registerFieldClass(
				"int", //
				new FieldBaseInt());
		ControlFieldFactory.registerFieldClass(
				"int32", //
				new FieldBaseInt());
		ControlFieldFactory.registerFieldClass(
				"list", //
				new FieldBaseList());
		ControlFieldFactory.registerFieldClass(
				"map", //
				new FieldBaseMap());
		ControlFieldFactory.registerFieldClass(
				"money", //
				new FieldBaseMoney());
		ControlFieldFactory.registerFieldClass(
				"object", //
				new FieldBaseObject());
		ControlFieldFactory.registerFieldClass(
				"owner", //
				new FieldBaseOwner());
		ControlFieldFactory.registerFieldClass(
				"set", //
				new FieldBaseSet());
		ControlFieldFactory.registerFieldClass(
				"string", //
				new FieldBaseString());
		ControlFieldFactory.registerFieldClass(
				"template", //
				new FieldBaseTemplate());
		ControlFieldFactory.registerFieldClass(
				"text", //
				new FieldBaseString()//
						.setFieldType("text")//
						.setFieldVariant("bigtext")//
						.setAttribute("max", 65536));
		{
			Evaluate.registerLanguage(new AcmFieldsetLanguageImpl());
			Evaluate.registerLanguage(new AcmRetrieveLanguageImpl());
		}
	}

	/** @param key
	 * @param title
	 * @param data
	 * @return basic */
	public static final ControlBasic<?> createBasic(final String key, final Object title, final BaseObject data) {

		return new DefaultBasic(key, title, data);
	}

	/** @param name
	 * @param title
	 * @return command */
	public static final ControlCommand<?> createCommand(final String name, final Object title) {

		return new SimpleCommand(name, title);
	}

	/** @return command */
	public static final ControlCommand<?> createCommandSplitter() {

		return new SplitterCommand();
	}

	/** @param type
	 * @param attributes
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createField(String,BaseObject)} instead */
	@Deprecated
	public static final ControlField createField(final String type, final BaseObject attributes) {

		return ControlFieldFactory.createField(type, attributes);
	}

	/** @param id
	 * @param title
	 * @param lengthLimit
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldBinary(String,Object,int)} instead */
	@Deprecated
	public static final ControlField createFieldBinary(final String id, final Object title, final int lengthLimit) {

		return ControlFieldFactory.createFieldBinary(id, title, lengthLimit);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldBoolean(String,Object,boolean)}
	 *             instead */
	@Deprecated
	public static final ControlField createFieldBoolean(final String id, final Object title, final boolean defaultValue) {

		return ControlFieldFactory.createFieldBoolean(id, title, defaultValue);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldDate(String,Object,Date)} instead */
	@Deprecated
	public static final ControlField createFieldDate(final String id, final Object title, final Date defaultValue) {

		return ControlFieldFactory.createFieldDate(id, title, defaultValue);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldDate(String,Object,long)} instead */
	@Deprecated
	public static final ControlField createFieldDate(final String id, final Object title, final long defaultValue) {

		return ControlFieldFactory.createFieldDate(id, title, defaultValue);
	}

	/** @param id
	 * @param expression
	 * @return field */
	public static final ControlField createFieldEvaluate(final String id, final String expression) {

		final BaseMap attributes = new BaseNativeObject();
		attributes.baseDefine("id", id);
		attributes.baseDefine("expression", expression);
		return ControlFieldFactory.createField("evaluate", attributes);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldFloating(String,Object,double)}
	 *             instead */
	@Deprecated
	public static final ControlField createFieldFloating(final String id, final Object title, final double defaultValue) {

		return ControlFieldFactory.createFieldFloating(id, title, defaultValue);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @param min
	 * @param max
	 * @return field
	 * @deprecated Use
	 *             {@link ControlFieldFactory#createFieldFloating(String,Object,double,double,double)}
	 *             instead */
	@Deprecated
	public static final ControlField createFieldFloating(final String id, final Object title, final double defaultValue, final double min, final double max) {

		return ControlFieldFactory.createFieldFloating(id, title, defaultValue, min, max);
	}

	/** @param id
	 * @param title
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldGuid(String,Object)} instead */
	@Deprecated
	public static final ControlField createFieldGuid(final String id, final Object title) {

		return ControlFieldFactory.createFieldGuid(id, title);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldInteger(String,Object,int)} instead */
	@Deprecated
	public static final ControlField createFieldInteger(final String id, final Object title, final int defaultValue) {

		return ControlFieldFactory.createFieldInteger(id, title, defaultValue);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @param min
	 * @param max
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldInteger(String,Object,int,int,int)}
	 *             instead */
	@Deprecated
	public static final ControlField createFieldInteger(final String id, final Object title, final int defaultValue, final int min, final int max) {

		return ControlFieldFactory.createFieldInteger(id, title, defaultValue, min, max);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field */
	public static final ControlField createFieldList(final String id, final Object title, final BaseObject defaultValue) {

		final BaseMap attributes = new BaseNativeObject();
		attributes.baseDefine("id", id);
		attributes.baseDefine("title", Base.forUnknown(title));
		if (defaultValue != null && defaultValue != BaseObject.UNDEFINED) {
			attributes.baseDefine("default", defaultValue);
		}
		return ControlFieldFactory.createField("list", attributes);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldLong(String,Object,long)} instead */
	@Deprecated
	public static final ControlField createFieldLong(final String id, final Object title, final long defaultValue) {

		return ControlFieldFactory.createFieldLong(id, title, defaultValue);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @param min
	 * @param max
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldLong(String,Object,long,long,long)}
	 *             instead */
	@Deprecated
	public static final ControlField createFieldLong(final String id, final Object title, final long defaultValue, final long min, final long max) {

		return ControlFieldFactory.createFieldLong(id, title, defaultValue, min, max);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldMap(String,Object,BaseObject)}
	 *             instead */
	@Deprecated
	public static final ControlField createFieldMap(final String id, final Object title, final BaseObject defaultValue) {

		return ControlFieldFactory.createFieldMap(id, title, defaultValue);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldObject(String,Object,Object)}
	 *             instead */
	@Deprecated
	public static final ControlField createFieldObject(final String id, final Object title, final Object defaultValue) {

		return ControlFieldFactory.createFieldObject(id, title, defaultValue);
	}

	/** @param id
	 * @param title
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldOwner(String,Object)} instead */
	@Deprecated
	public static final ControlField createFieldOwner(final String id, final Object title) {

		return ControlFieldFactory.createFieldOwner(id, title);
	}

	/** @return fieldset
	 * @deprecated Use {@link ControlFieldset#createFieldset()} instead */
	@Deprecated
	public static final ControlFieldset<?> createFieldset() {

		return ControlFieldset.createFieldset();
	}

	/** @param base
	 * @param tail
	 * @return fieldset
	 * @deprecated Use {@link ControlFieldset#createFieldset()} instead */
	@Deprecated
	public static final ControlFieldset<?> createFieldset(final ControlFieldset<?> base, final ControlFieldset<?> tail) {

		return ControlFieldset.createFieldset(base, tail);
	}

	/** @param id
	 * @return fieldset
	 * @deprecated Use {@link ControlFieldset#createFieldset()} instead */
	@Deprecated
	public static final ControlFieldset<?> createFieldset(final String id) {

		return ControlFieldset.createFieldset(id);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldSet(String,Object,Set)} instead */
	@Deprecated
	public static final ControlField createFieldSet(final String id, final Object title, final Set<?> defaultValue) {

		return ControlFieldFactory.createFieldSet(id, title, defaultValue);
	}

	/** @param fieldset
	 * @return fieldset
	 * @deprecated Use {@link ControlFieldset#createFieldsetConstant(ControlFieldset) )} instead */
	@Deprecated
	public static final ControlFieldset<?> createFieldsetConstant(final ControlFieldset<?> fieldset) {

		return ControlFieldset.createFieldsetConstant(fieldset);
	}

	/** @param title
	 * @param fieldset
	 * @param target
	 * @return form */
	public static final ControlForm<?> createFieldsetEditorForm(final Object title, final ControlFieldset<?> fieldset, final Function<ControlFieldset<?>, Object> target) {

		return new FormFieldsetEditor(title, fieldset, target);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldString(String,Object,Object)}
	 *             instead */
	@Deprecated
	public static final ControlField createFieldString(final String id, final Object title, final Object defaultValue) {

		return ControlFieldFactory.createFieldString(id, title, defaultValue);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @param min
	 * @param max
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldString(String,Object,Object,int,int)}
	 *             instead */
	@Deprecated
	public static final ControlField createFieldString(final String id, final Object title, final Object defaultValue, final int min, final int max) {

		return ControlFieldFactory.createFieldString(id, title, defaultValue, min, max);
	}

	/** @param id
	 * @param title
	 * @param defaultValue
	 * @return field
	 * @deprecated Use {@link ControlFieldFactory#createFieldTemplate(String,Object,String)}
	 *             instead */
	@Deprecated
	public static final ControlField createFieldTemplate(final String id, final Object title, final String defaultValue) {

		return ControlFieldFactory.createFieldTemplate(id, title, defaultValue);
	}

	/** @return commandset */
	public static final ControlCommandset createOptions() {

		return new SimpleCommandset();
	}

	/** @param command
	 * @return commandset */
	public static final ControlCommandset createOptionsSingleton(final ControlCommand<?> command) {

		final ControlCommandset result = new SimpleCommandset();
		result.add(command);
		return result;
	}

	/** @param title
	 * @param target
	 * @param fieldset
	 * @param result
	 * @return form */
	public static final ControlForm<?> createSimpleForm(final BaseObject title, final BaseObject target, final ControlFieldset<?> fieldset, final BaseObject result) {

		return new DefaultMapEditor(title, target, fieldset, result);
	}

	/** @param title
	 * @param target
	 * @param fieldset
	 * @param result
	 * @return form */
	public static final ControlForm<?> createSimpleForm(final Object title, final Bindings target, final ControlFieldset<?> fieldset, final Object result) {

		return new DefaultMapEditor(title, target, fieldset, result);
	}

	/** @param title
	 * @param target
	 * @param fieldset
	 * @param result
	 * @return form */
	public static final ControlForm<?> createSimpleForm(final String title, final BaseObject target, final ControlFieldset<?> fieldset, final BaseObject result) {

		return new DefaultMapEditor(Base.forString(title), target, fieldset, result);
	}

	private static final Map<String, ControlType<?, ?>> createTypeMap() {

		final Map<String, ControlType<?, ?>> result = Create.tempMap();
		result.put("Boolean", Reflect.CONTROL_TYPE_BOOLEAN);
		result.put("boolean", Reflect.CONTROL_TYPE_BOOLEAN);
		result.put("Byte", Reflect.CONTROL_TYPE_BYTE);
		result.put("byte", Reflect.CONTROL_TYPE_BYTE);
		result.put("Char", Reflect.CONTROL_TYPE_CHAR);
		result.put("char", Reflect.CONTROL_TYPE_CHAR);
		result.put("Character", Reflect.CONTROL_TYPE_CHAR);
		result.put("character", Reflect.CONTROL_TYPE_CHAR);
		result.put("Double", Reflect.CONTROL_TYPE_NUMBER);
		result.put("double", Reflect.CONTROL_TYPE_NUMBER);
		result.put("Float", Reflect.CONTROL_TYPE_FLOAT);
		result.put("Float32", Reflect.CONTROL_TYPE_FLOAT);
		result.put("Float64", Reflect.CONTROL_TYPE_NUMBER);
		result.put("Int8", Reflect.CONTROL_TYPE_BYTE);
		result.put("Int16", Reflect.CONTROL_TYPE_SHORT);
		result.put("Int32", Reflect.CONTROL_TYPE_INTEGER);
		result.put("Int", Reflect.CONTROL_TYPE_INTEGER);
		result.put("int", Reflect.CONTROL_TYPE_INTEGER);
		result.put("Integer", Reflect.CONTROL_TYPE_LONG);
		result.put("Long", Reflect.CONTROL_TYPE_LONG);
		result.put("long", Reflect.CONTROL_TYPE_LONG);
		result.put("Number", Reflect.CONTROL_TYPE_NUMBER);
		result.put("number", Reflect.CONTROL_TYPE_NUMBER);
		result.put("Object", Reflect.CONTROL_TYPE_EXACT_OBJECT);
		result.put("object", Reflect.CONTROL_TYPE_EXACT_OBJECT);
		result.put("Short", Reflect.CONTROL_TYPE_SHORT);
		result.put("short", Reflect.CONTROL_TYPE_SHORT);
		result.put("String", Reflect.CONTROL_TYPE_STRING);
		result.put("string", Reflect.CONTROL_TYPE_STRING);
		return result;
	}

	static final ValueSource<BasePrimitiveNumber> getAttributeFloatingEx(final Object defaultAttributeObject) {

		final String defaultAttribute = Convert.Any.toString(defaultAttributeObject, "");
		if (defaultAttribute.startsWith("=")) {
			return new ValueSourceFloatingEx(defaultAttribute);
		}
		if (defaultAttributeObject instanceof String) {
			return new ValueSourceFloatingConst(defaultAttributeObject);
		}
		return new ValueSourceFloatingObject(defaultAttributeObject);
	}

	static final ValueSource<BasePrimitiveNumber> getAttributeLongEx(final Object defaultAttributeObject) {

		final String defaultAttribute = Convert.Any.toString(defaultAttributeObject, "");
		if (defaultAttribute.startsWith("=")) {
			return new ValueSourceLongEx(defaultAttribute);
		}
		if (defaultAttributeObject instanceof String) {
			return new ValueSourceLongConst(defaultAttributeObject);
		}
		return new ValueSourceLongObject(defaultAttributeObject);
	}

	static final ValueSource<BasePrimitiveString> getAttributeStringEx(final Object defaultAttributeObject) {

		final String defaultAttribute = Convert.Any.toString(defaultAttributeObject, "");
		if (defaultAttribute.startsWith("=")) {
			return new ValueSourceStringEx(defaultAttribute);
		}
		if (defaultAttributeObject instanceof String) {
			final String value = Convert.Any.toString(defaultAttributeObject, "");
			return new ValueSourceStringConst(value);
		}
		return new ValueSourceStringObject(defaultAttributeObject);
	}

	/** @param name
	 * @return type */
	public static final ControlType<?, ?> getTypeByName(final String name) {

		return Convert.Any.toAny(Convert.MapEntry.toObject(Control.TYPES, name, Reflect.CONTROL_TYPE_EXACT_BASE_OBJECT));
	}

	/** @param node
	 * @param path
	 * @return node */
	public static final ControlNode<?> relativeNode(final ControlNode<?> node, final String path) {

		if (node == null || path == null || path.length() == 0) {
			return node;
		}
		ControlNode<?> current = node;
		for (final StringTokenizer st = new StringTokenizer(path, "/"); st.hasMoreTokens() && current != null;) {
			final String token = st.nextToken().trim();
			if (token.length() > 0) {
				current = current.getChildByName(token);
			}
		}
		return current;
	}

	private Control() {
		
		// empty
	}
}
