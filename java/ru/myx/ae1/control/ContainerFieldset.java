package ru.myx.ae1.control;

import java.util.Collections;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractContainer;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Text;

/*
 * Created on 26.04.2004
 */
final class ContainerFieldset extends AbstractContainer<ContainerFieldset> {
	
	private static final ControlCommand<?> CMD_ADD = Control
			.createCommand(
					"add", //
					MultivariantString.getString(
							"Add", //
							Collections.singletonMap(
									"ru", //
									"Добавить"))) //
			.setCommandIcon("command-add") //
			;
			
	private static final ControlCommand<?> CMD_PASTE = Control
			.createCommand(
					"paste", //
					MultivariantString.getString(
							"Paste", //
							Collections.singletonMap(
									"ru", //
									"Вставить код"))) //
			.setCommandIcon("command-paste") //
			;
			
	private static final ControlCommand<?> CMD_CLEAR = Control
			.createCommand(
					"clear", //
					MultivariantString.getString(
							"Clear", //
							Collections.singletonMap(
									"ru", //
									"Очистить"))) //
			.setCommandIcon("command-dispose") //
			;
			
	private final ControlFieldset<?> fieldset;
	
	ContainerFieldset(final ControlFieldset<?> fieldset) {
		this.fieldset = fieldset;
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if (command == ContainerFieldset.CMD_ADD) {
			return new FormChooseType(this.fieldset);
		}
		if (command == ContainerFieldset.CMD_PASTE) {
			return new FormPasteFieldset(this.fieldset);
		}
		if (command == ContainerFieldset.CMD_CLEAR) {
			this.fieldset.baseClear();
			return null;
		}
		{
			final String commandKey = command.getKey();
			final String key = Base.getString(command.getAttributes(), "key", "");
			final int index = this.getIndex(key);
			if (index == -1) {
				throw new IllegalArgumentException("Unknown key '" + key + "' for command '" + command.getKey() + "', fieldsetSize=" + this.fieldset.size());
			}
			if ("edit".equals(commandKey)) {
				final ControlField field = this.fieldset.get(index);
				return new FormFieldEdit(this.fieldset, index, field);
			}
			if ("change".equals(commandKey)) {
				final ControlField field = this.fieldset.get(index);
				final String type = Base.getString(field.getAttributes(), "type", "string");
				final String variant = Base.getString(field.getAttributes(), "variant", null);
				return new FormChooseClass(this.fieldset, index, type, variant);
			}
			if ("delete".equals(commandKey)) {
				this.fieldset.remove(index);
				return null;
			}
			if ("up".equals(command.getKey())) {
				final ControlField prev = this.fieldset.get(index - 1);
				this.fieldset.set(index - 1, this.fieldset.get(index));
				this.fieldset.set(index, prev);
				return null;
			}
			if ("down".equals(commandKey)) {
				final ControlField prev = this.fieldset.get(index + 1);
				this.fieldset.set(index + 1, this.fieldset.get(index));
				this.fieldset.set(index, prev);
				return null;
			}
			{
				throw new IllegalArgumentException("Unknown command: " + command.getKey());
			}
		}
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		final ControlCommandset result = Control.createOptions();
		result.add(ContainerFieldset.CMD_ADD);
		result.add(ContainerFieldset.CMD_PASTE);
		result.add(ContainerFieldset.CMD_CLEAR);
		return result;
	}
	
	@Override
	public ControlCommandset getContentCommands(final String key) {
		
		final int index = this.getIndex(key);
		assert index != -1 : "Invalid key for content commands: key=" + key;
		final ControlCommandset result = Control.createOptions();
		if (index > 0) {
			result.add(Control.createCommand(
					"up", //
					MultivariantString.getString(
							"Up", //
							Collections.singletonMap(
									"ru", //
									"Вверх"))) //
					.setCommandIcon("command-up") //
					.setAttribute("key", key)) //
					;
		}
		if (index < this.fieldset.size() - 1) {
			result.add(Control.createCommand(
					"down", //
					MultivariantString.getString(
							"Down", //
							Collections.singletonMap("ru", "Вниз"))) //
					.setCommandIcon("command-down") //
					.setAttribute("key", key)) //
					;
		}
		result.add(Control.createCommand(
				"edit", //
				MultivariantString.getString(
						"Edit", //
						Collections.singletonMap("ru", "Изменить"))) //
				.setCommandIcon("command-edit") //
				.setAttribute("key", key)) //
				;
		result.add(Control.createCommand(
				"change", //
				MultivariantString.getString(
						"Change class", //
						Collections.singletonMap("ru", "Сменить класс"))) //
				.setCommandIcon("command-edit-change") //
				.setAttribute("key", key)) //
				;
		result.add(Control.createCommand(
				"delete", //
				MultivariantString.getString(
						"Remove", //
						Collections.singletonMap("ru", "Удалить"))) //
				.setCommandIcon("command-delete-remove") //
				.setAttribute("key", key)) //
				;
		return result;
	}
	
	private final int getIndex(final String key) {
		
		final int intIndex = Convert.Any.toInt(key, -1);
		if (intIndex != -1) {
			return intIndex;
		}
		for (int i = this.fieldset.size() - 1; i >= 0; --i) {
			final ControlField field = this.fieldset.get(i);
			if (key.equals(field.getKey())) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public String toString() {
		
		return "[object " + this.baseClass() + "(" + this.toStringDetails() + ")]";
	}
	
	@Override
	protected String toStringDetails() {
		
		return this.fieldset == null
			? "fieldset == null"
			: "innerFields: " + Text.join(this.fieldset.innerFields(), ", ");
	}
}
