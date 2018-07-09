package ru.myx.ae1.types;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/*
 * Created on 17.01.2005
 */

final class TypeCommandForm extends AbstractForm<TypeCommandForm> {
	
	final Type<?> type;
	
	final BaseEntry<?> entry;
	
	final ControlCommand<?> command;
	
	final String commandKey;
	
	final ControlFieldset<?> fieldset;
	
	TypeCommandForm(final Type<?> type, final BaseEntry<?> entry, final ControlCommand<?> command, final String commandKey, final ControlFieldset<?> fieldset) {
		this.type = type;
		this.entry = entry;
		this.command = command;
		this.commandKey = commandKey;
		this.fieldset = fieldset;
		
		this.setAttributeIntern("id", type.getKey() + "__" + command.getKey());
		this.setAttributeIntern("title", Base.getString(fieldset.getAttributes(), "title", command.getTitle()));
		this.recalculate();
		
		final BaseObject data = new BaseNativeObject();
		type.scriptCommandFormPrepare(commandKey, entry, data);
		this.setData(data);
	}
	
	@Override
	public final Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {
		
		if (command == this.command) {
			this.type.scriptCommandFormSubmit(this.commandKey, this.entry, this.getData());
			return this.type.getCommandAdditionalResult(this.entry, command, this.getData());
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}
	
	@Override
	public final ControlCommandset getCommands() {
		
		return Control.createOptionsSingleton(this.command);
	}
	
	@Override
	public final ControlFieldset<?> getFieldset() {
		
		return this.fieldset;
	}
	
}
