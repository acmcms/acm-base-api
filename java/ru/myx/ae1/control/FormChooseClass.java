package ru.myx.ae1.control;

import java.util.Collections;
import java.util.Map;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.ControlLookupStatic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/*
 * Created on 26.04.2004 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

final class FormChooseClass extends AbstractForm<FormChooseClass> {
	
	private final ControlFieldset<?> fieldset;
	
	private final ControlFieldset<?> list;
	
	private final String type;
	
	private final String variant;
	
	private final int index;
	
	private static final ControlCommand<?> CMD_NEXT = Control.createCommand("next", MultivariantString.getString("Next...", Collections.singletonMap("ru", "Далее...")))
			.setCommandIcon("command-next");
			
	FormChooseClass(final ControlFieldset<?> list, final int index, final String type, final String variant) {
		
		this.type = type;
		this.variant = variant;
		final ControlField field = index == -1
			? null
			: (ControlField) list.get(index);
		final String defaultValue = field == null
			? "string"
			: field.getFieldClass();
		final ControlLookupStatic lookup = new ControlLookupStatic();
		
		final Map<String, ControlField> registeredFields = ControlFieldFactory.registeredFields();
		for (final Map.Entry<String, ControlField> current : registeredFields.entrySet()) {
			final ControlField fld = current.getValue();
			if (fld.getFieldTypeAvailability(type)) {
				lookup.putAppend(current.getKey(), fld.getFieldClassTitle());
			}
		}
		
		this.fieldset = ControlFieldset.createFieldset().addField(
				ControlFieldFactory.createFieldString("class", "Field class", defaultValue).setFieldType("select").setFieldVariant("bigselect").setAttribute("lookup", lookup));
				
		this.list = list;
		this.index = index;
		this.setAttributeIntern("id", "fieldset_editor");
		this.setAttributeIntern("title", MultivariantString.getString("Choose field class", Collections.singletonMap("ru", "Выберите класс поля")));
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if (command == FormChooseClass.CMD_NEXT) {
			final String fieldClass = Base.getString(this.getData(), "class", "string");
			final BaseObject attributes = new BaseNativeObject();
			if (this.index != -1) {
				attributes.baseDefineImportAllEnumerable(this.list.get(this.index).getAttributes());
			}
			attributes.baseDefine("class", fieldClass);
			attributes.baseDefine("type", this.type);
			attributes.baseDefine("variant", this.variant);
			return new FormFieldEdit(this.list, this.index, ControlFieldFactory.createField(fieldClass, attributes));
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		return Control.createOptionsSingleton(FormChooseClass.CMD_NEXT);
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		
		return this.fieldset;
	}
}
