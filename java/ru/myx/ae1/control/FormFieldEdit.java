package ru.myx.ae1.control;

import java.util.Collections;

import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/*
 * Created on 26.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

final class FormFieldEdit extends AbstractForm<FormFieldEdit> {
	
	private final ControlFieldset<?> fieldset;
	
	private final ControlFieldset<?> list;
	
	private final int index;
	
	private final String fieldClass;
	
	private static final ControlCommand<?> CMD_SAVE = Control.createCommand("save", " OK ").setCommandIcon("command-save");
	
	FormFieldEdit(final ControlFieldset<?> list, final int index, final ControlField field) {
		this.fieldClass = field.getFieldClass();
		final BaseObject fieldType = field.getAttributes().baseGet("type", BaseObject.UNDEFINED);
		final BaseObject fieldVariant = field.getAttributes().baseGet("variant", BaseObject.UNDEFINED);
		this.fieldset = ControlFieldset.createFieldset()
				.addField(
						ControlFieldFactory
								.createFieldString("class", MultivariantString.getString("Field class", Collections.singletonMap("ru", "Класс поля")), field.getFieldClassTitle())
								.setConstant())
				.addField(
						ControlFieldFactory.createFieldString("type", MultivariantString.getString("Field type", Collections.singletonMap("ru", "Тип поля")), fieldType)
								.setConstant());
		LookupFieldTypes.makeVariantFieldForType(fieldType, fieldVariant, this.fieldset);
		final String fieldTypeString = fieldType.baseToJavaString();
		final ControlFieldset<?> forType = LookupFieldTypes.getTypeFieldset(fieldTypeString);
		if (forType != null) {
			this.fieldset.addFields(forType);
		}
		this.fieldset.addField(ControlFieldFactory.createFieldString("id", MultivariantString.getString("Identifier", Collections.singletonMap("ru", "Идентификатор")), "", 1, 64))
				.addField(ControlFieldFactory.createFieldString("title", MultivariantString.getString("Title", Collections.singletonMap("ru", "Заголовок")), ""))
				.addFields(field.getFieldAttributesFieldset());
		this.list = list;
		this.index = index;
		
		this.setData(field.cloneField().getAttributes());
		
		this.setAttributeIntern("id", "fieldset_editor");
		this.setAttributeIntern("title", MultivariantString.getString("Create a field", Collections.singletonMap("ru", "Создание поля")));
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if (command == FormFieldEdit.CMD_SAVE) {
			final BaseObject attributes = new BaseNativeObject(this.getData());
			attributes.baseDefine("class", this.fieldClass);
			final ControlField created = ControlFieldFactory.createField(this.fieldClass, attributes);
			if (this.index == -1) {
				this.list.addField(created);
			} else {
				this.list.set(this.index, created);
			}
			return null;
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		return Control.createOptionsSingleton(FormFieldEdit.CMD_SAVE);
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		
		return this.fieldset;
	}
}
