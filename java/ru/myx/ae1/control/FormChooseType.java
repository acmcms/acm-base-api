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
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

final class FormChooseType extends AbstractForm<FormChooseType> {
	private final ControlFieldset<?>		fieldset;
	
	private final ControlFieldset<?>		list;
	
	private static final ControlCommand<?>	CMD_NEXT	= Control.createCommand( "next",
																MultivariantString.getString( "Next...",
																		Collections.singletonMap( "ru", "Далее..." ) ) )
																.setCommandIcon( "command-next" );
	
	FormChooseType(final ControlFieldset<?> list) {
		this.fieldset = ControlFieldset.createFieldset().addField( ControlFieldFactory.createFieldString( "type", "Field type", "string" )
				.setFieldType( "select" ).setFieldVariant( "bigselect" )
				.setAttribute( "lookup", new LookupFieldTypes() ) );
		this.list = list;
		this.setAttributeIntern( "id", "fieldset_editor" );
		this.setAttributeIntern( "title", MultivariantString.getString( "Choose field type",
				Collections.singletonMap( "ru", "Выберите тип поля" ) ) );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormChooseType.CMD_NEXT) {
			final String parameter = Base.getString( this.getData(), "type", "string" );
			final String type;
			final String variant;
			final int pos = parameter.indexOf( '/' );
			if (pos == -1) {
				type = parameter;
				variant = "*";
			} else {
				type = parameter.substring( 0, pos );
				variant = parameter.substring( pos + 1 );
			}
			return new FormChooseClass( this.list, -1, type, variant );
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormChooseType.CMD_NEXT );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return this.fieldset;
	}
}
