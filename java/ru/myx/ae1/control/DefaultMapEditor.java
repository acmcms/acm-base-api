/*
 * Created on 25.04.2006
 */
package ru.myx.ae1.control;

import java.util.Map;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseFunction;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;

final class DefaultMapEditor extends AbstractForm<DefaultMapEditor> {
	
	
	private static final ControlCommand<?> CMD_SAVE = Control.createCommand("ok", " OK ").setCommandIcon("command-save-ok");
	
	private static final ControlCommand<?> CMD_CLOSE = Control.createCommand("close", "Close (invisible)").setCommandIcon("command-close");
	
	final BaseObject target;
	
	final ControlFieldset<?> fieldset;
	
	final BaseObject result;
	
	DefaultMapEditor(final BaseObject title, final BaseObject target, final ControlFieldset<?> fieldset, final BaseObject result) {
		this.target = target;
		this.fieldset = fieldset;
		this.result = result;
		this.setData(target);
		this.setAttributeIntern("id", "default_editor");
		this.setAttributeIntern("title", title);
		this.setAttributeIntern("on_close", DefaultMapEditor.CMD_CLOSE);
		this.recalculate();
	}
	
	DefaultMapEditor(final Object title, final Map<String, Object> target, final ControlFieldset<?> fieldset, final Object result) {
		this.target = Base.forUnknown(target);
		this.fieldset = fieldset;
		this.result = Base.forUnknown(result);
		final BaseObject data = Base.fromMap(target);
		this.setData(data);
		this.setAttributeIntern("id", "default_editor");
		this.setAttributeIntern("title", Base.forUnknown(title));
		this.setAttributeIntern("on_close", DefaultMapEditor.CMD_CLOSE);
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {
		
		
		if (command == DefaultMapEditor.CMD_SAVE) {
			this.target.baseDefineImportAllEnumerable(this.getData());
		} else if (command == DefaultMapEditor.CMD_CLOSE) {
			//
		} else {
			throw new IllegalArgumentException("Unknown command: " + command.getKey());
		}
		final BaseObject result = this.result;
		if (result != null) {
			final BaseFunction function = this.result.baseCall();
			if (function != null) {
				return function.callNJ1(this, Base.forString(command.getKey()));
			}
		}
		return result;
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		
		final ControlCommandset result = Control.createOptions();
		result.add(DefaultMapEditor.CMD_SAVE);
		return result;
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		
		
		return this.fieldset;
	}
}
