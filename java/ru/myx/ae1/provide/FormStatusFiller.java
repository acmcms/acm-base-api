package ru.myx.ae1.provide;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.ControlForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.status.StatusFiller;
import ru.myx.ae3.status.StatusInfo;

/**
 * 
 * @author myx
 * 
 *         Created on 21.05.2004
 */
public final class FormStatusFiller extends AbstractForm<FormStatusFiller> {
	private final StatusFiller				filler;
	
	private final ControlFieldset<?>		fieldset;
	
	private static final ControlCommand<?>	CMD_REFRESH	= Control.createCommand( "refresh",
																MultivariantString.getString( "Refresh",
																		Collections.singletonMap( "ru", "Обновить" ) ) )
																.setCommandIcon( "command-reload" );
	
	FormStatusFiller(final BaseObject title, final StatusFiller filler) {
		this.filler = filler;
		this.fieldset = ControlFieldset.createFieldset().addField( ControlFieldFactory.createFieldMap( "data",
				MultivariantString.getString( "Data", Collections.singletonMap( "ru", "Данные" ) ),
				null ).setConstant() );
		
		this.setAttributeIntern( "id", "status_filler" );
		this.setAttributeIntern( "title", title );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject parameters) {
		if (command == FormStatusFiller.CMD_REFRESH) {
			return this;
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormStatusFiller.CMD_REFRESH );
	}
	
	@Override
	public BaseObject getData() {
		final BaseMap data = new BaseNativeObject();
		final StatusInfo info = new StatusInfo( this.getTitle() );
		this.filler.statusFill( info );
		data.put( "data", info.getStatusAsMap() );
		return data;
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return this.fieldset;
	}

	/**
	 * @param title
	 * @param filler
	 * @return form
	 */
	public static final ControlForm<?> createFormStatusFiller(final BaseObject title, final StatusFiller filler) {
		return new FormStatusFiller( title, filler );
	}
	
}
