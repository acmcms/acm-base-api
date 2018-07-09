package ru.myx.ae1.control;

import java.util.Collections;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/*
 * Created on 26.04.2004
 */

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class FormPasteFieldset extends AbstractForm<FormPasteFieldset> {
	private static final ControlFieldset<?>	FIELDSET	= ControlFieldset
																.createFieldset()
																.addField( ControlFieldFactory
																		.createFieldString( "definition",
																				"Definition",
																				"",
																				0,
																				32768 )
																		.setFieldHint( "Paste fieldset definition here." )
																		.setFieldType( "text" )
																		.setFieldVariant( "bigtext" ) );
	
	private final ControlFieldset<?>		fieldset;
	
	private static final ControlCommand<?>	CMD_ADD		= Control.createCommand( "add",
																MultivariantString.getString( "Add",
																		Collections.singletonMap( "ru", "Добавить" ) ) )
																.setCommandIcon( "command-add" );
	
	private static final ControlCommand<?>	CMD_REPLACE	= Control.createCommand( "replace",
																MultivariantString.getString( "Replace",
																		Collections.singletonMap( "ru", "Заменить" ) ) )
																.setCommandIcon( "command-replace" );
	
	FormPasteFieldset(final ControlFieldset<?> fieldset) {
		this.fieldset = fieldset;
		
		this.setAttributeIntern( "id", "paste_fieldset" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Paste fieldset definition",
						Collections.singletonMap( "ru", "Вставка описания схемы полей" ) ) );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormPasteFieldset.CMD_ADD) {
			final ControlFieldset<?> fieldset = ControlFieldset.materializeFieldset( Base.getString( this
					.getData(), "definition", "" ) );
			for (int i = 0; i < fieldset.size(); ++i) {
				this.fieldset.add( fieldset.get( i ) );
			}
			return null;
		} else if (command == FormPasteFieldset.CMD_REPLACE) {
			final ControlFieldset<?> fieldset = ControlFieldset.materializeFieldset( Base.getString( this
					.getData(), "definition", "" ) );
			this.fieldset.baseClear();
			for (int i = 0; i < fieldset.size(); ++i) {
				this.fieldset.add( fieldset.get( i ) );
			}
			return null;
		} else {
			throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
		}
	}
	
	@Override
	public ControlCommandset getCommands() {
		final ControlCommandset result = Control.createOptions();
		result.add( FormPasteFieldset.CMD_ADD );
		result.add( FormPasteFieldset.CMD_REPLACE );
		return result;
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return FormPasteFieldset.FIELDSET;
	}
}
