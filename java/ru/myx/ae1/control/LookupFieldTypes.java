package ru.myx.ae1.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Create;

/*
 * Created on 26.04.2004
 */
/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class LookupFieldTypes extends BaseHostLookup {
	
	
	private static final Object[][] types = {
			{
					Control.FIELD_TYPE_TRANSPARENT, MultivariantString.getString("Transparent", Collections.singletonMap("ru", "Невидимое \\ Нередактируемое"))
			}, {
					Control.FIELD_TYPE_BOOLEAN, MultivariantString.getString("Boolean (yes/no)", Collections.singletonMap("ru", "Логическое (Да \\ Нет)"))
			}, {
					Control.FIELD_TYPE_NUMBER, MultivariantString.getString("Numeric", Collections.singletonMap("ru", "Числовое"))
			}, {
					"", "+"
			}, {
					Control.FIELD_TYPE_NUMBER + "/" + "integer", MultivariantString.getString("Integer", Collections.singletonMap("ru", "Целое"))
			}, {
					Control.FIELD_TYPE_NUMBER + "/" + "floating", MultivariantString.getString("Floating", Collections.singletonMap("ru", "Реальное"))
			}, {
					Control.FIELD_TYPE_NUMBER + "/" + "money", MultivariantString.getString("Money", Collections.singletonMap("ru", "Деньги"))
			}, {
					"", "-"
			}, {
					Control.FIELD_TYPE_STRING, MultivariantString.getString("String", Collections.singletonMap("ru", "Строка"))
			}, {
					"", "+"
			}, {
					Control.FIELD_TYPE_STRING + "/" + "url", MultivariantString.getString("URL", Collections.singletonMap("ru", "URL"))
			}, {
					Control.FIELD_TYPE_STRING + "/" + "email", MultivariantString.getString("e-mail", Collections.singletonMap("ru", "e-mail"))
			}, {
					Control.FIELD_TYPE_STRING + "/" + "host", MultivariantString.getString("Host", Collections.singletonMap("ru", "Хост"))
			}, {
					Control.FIELD_TYPE_STRING + "/" + "password", MultivariantString.getString("Password", Collections.singletonMap("ru", "Пароль"))
			}, {
					"", "-"
			}, {
					Control.FIELD_TYPE_TEXT, MultivariantString.getString("Text", Collections.singletonMap("ru", "Текст"))
			}, {
					"", "+"
			}, {
					Control.FIELD_TYPE_TEXT + "/" + "bigtext", MultivariantString.getString("Big text", Collections.singletonMap("ru", "Большой текст"))
			}, {
					Control.FIELD_TYPE_TEXT + "/" + "html", MultivariantString.getString("HTML", Collections.singletonMap("ru", "HTML"))
			}, {
					Control.FIELD_TYPE_TEXT + "/" + "template", MultivariantString.getString("Template", Collections.singletonMap("ru", "Шаблон"))
			}, {
					"", "-"
			}, {
					Control.FIELD_TYPE_DATE, MultivariantString.getString("Date", Collections.singletonMap("ru", "Дата"))
			}, {
					Control.FIELD_TYPE_BINARY, MultivariantString.getString("Binary (file/image)", Collections.singletonMap("ru", "Бинарные данные (файл \\ картинка)"))
			}, {
					Control.FIELD_TYPE_SELECT, MultivariantString.getString("Select", Collections.singletonMap("ru", "Выбор значения из множества"))
			}, {
					"", "+"
			}, {
					Control.FIELD_TYPE_SELECT + "/" + "bigselect", MultivariantString.getString("Big select", Collections.singletonMap("ru", "Крупный список"))
			}, {
					"", "-"
			}, {
					Control.FIELD_TYPE_SET, MultivariantString.getString("Set", Collections.singletonMap("ru", "Множество"))
			}, {
					"", "+"
			}, {
					Control.FIELD_TYPE_SET + "/" + "select",
					MultivariantString.getString("Multi select from a set", Collections.singletonMap("ru", "Множественный выбор из множества"))
			}, {
					"", "-"
			}, {
					Control.FIELD_TYPE_LIST, MultivariantString.getString("List", Collections.singletonMap("ru", "Список"))
			}, {
					Control.FIELD_TYPE_MAP, MultivariantString.getString("Map", Collections.singletonMap("ru", "Словарь"))
			}, {
					"", "+"
			}, {
					Control.FIELD_TYPE_MAP + "/" + "select", MultivariantString.getString("Map keys for a set", Collections.singletonMap("ru", "Словарь элементов из множества"))
			}, {
					"", "-"
			},
	};
	
	private static final BaseObject typesMapOriginal = new BaseNativeObject();
	
	private static final BaseObject typesMap = LookupFieldTypes.fillIntern(LookupFieldTypes.typesMapOriginal);
	
	private static final Map<Object, ControlFieldset<?>> fieldsetByType = Create.tempMap();
	
	private static final BaseObject fillIntern(final BaseObject acceptor) {
		
		
		for (final Object[] element : LookupFieldTypes.types) {
			acceptor.baseDefine(String.valueOf(element[0]), Base.forUnknown(element[1]));
		}
		return acceptor;
	}
	
	/**
	 * @param type
	 * @return fieldset
	 */
	public static final ControlFieldset<?> getTypeFieldset(final Object type) {
		
		
		return LookupFieldTypes.fieldsetByType.get(type);
	}
	
	/**
	 * @param type
	 * @param variant
	 * @param fieldset
	 */
	public static final void makeVariantFieldForType(final BaseObject type, final BaseObject variant, final ControlFieldset<?> fieldset) {
		
		
		final List<Object> result = new ArrayList<>();
		for (int i = 0; i < LookupFieldTypes.types.length; ++i) {
			final Object[] current = LookupFieldTypes.types[i];
			if (current[0].equals(type)) {
				if (LookupFieldTypes.types.length <= i + 1) {
					break;
				}
				if (LookupFieldTypes.types[i + 1][1].equals("+")) {
					for (int j = i + 2; j < LookupFieldTypes.types.length; j++) {
						if (LookupFieldTypes.types[j][1].equals("-")) {
							break;
						}
						result.add(LookupFieldTypes.types[j]);
					}
					break;
				}
				break;
			}
		}
		if (result.size() == 0) {
			// empty
		} else {
			final BaseObject lookup = new BaseNativeObject();
			lookup.baseDefine("*", Base.forString("-= Default =-"));
			for (int i = 0; i < result.size(); ++i) {
				final Object[] object = (Object[]) result.get(i);
				final String full = object[0].toString();
				final String variantId = full.substring(full.indexOf('/') + 1);
				lookup.baseDefine(variantId, (BaseObject) object[1]);
			}
			fieldset.addField(ControlFieldFactory.createFieldString("variant", Base.forString("Field variant"), variant).setFieldType("select").setAttribute("lookup", lookup));
		}
	}
	
	@Override
	public final BaseObject baseGetLookupValue(final BaseObject key) {
		
		
		return LookupFieldTypes.typesMap.baseGet(key.baseToString(), BaseObject.UNDEFINED);
	}
	
	@Override
	public boolean baseHasKeysOwn() {
		
		
		return LookupFieldTypes.types.length > 0;
	}
	
	@Override
	public final Iterator<String> baseKeysOwn() {
		
		
		return Base.keys(LookupFieldTypes.typesMap);
	}
	
	@Override
	public Iterator<? extends CharSequence> baseKeysOwnAll() {
		
		
		return this.baseKeysOwn();
	}
	
	@Override
	public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
		
		
		return Base.keysPrimitive(LookupFieldTypes.typesMap);
	}
	
	@Override
	public String toString() {
		
		
		return "[Lookup: Field Types]";
	}
}
