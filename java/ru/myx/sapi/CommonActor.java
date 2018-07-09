/**
 * 
 */
package ru.myx.sapi;

import ru.myx.ae1.control.Control;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractActor;
import ru.myx.ae3.control.ControlActor;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.exec.Exec;

final class CommonActor extends AbstractActor<CommonActor> {
	private final String	path;
	
	CommonActor(final String path) {
		this.path = path;
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {
		final ControlActor<?> container = (ControlActor<?>) command.getAttributes().baseGet( "cmn_actor",
				BaseObject.UNDEFINED );
		final ControlCommand<?> commonCommand = (ControlCommand<?>) command.getAttributes().baseGet( "cmn_cmnd",
				BaseObject.UNDEFINED );
		if (container != null && commonCommand != null) {
			return container.getCommandResult( commonCommand, arguments );
		}
		throw new IllegalArgumentException( "Unknown command: "
				+ command.getAttributes().baseGet( "cmn_cmnd", BaseObject.UNDEFINED ) );
	}
	
	@Override
	public ControlCommandset getCommands() {
		final ControlCommandset result = Control.createOptions();
		final ControlActor<?>[] commons = Context.getServer( Exec.currentProcess() ).getCommonActors( this.path );
		if (commons == null || commons.length == 0) {
			return null;
		}
		for (int i = 0; i < commons.length; ++i) {
			final ControlActor<?> actor = commons[i];
			if (actor != null) {
				final ControlCommandset options = actor.getCommands();
				if (options != null) {
					for (final ControlCommand<?> command : options) {
						result.add( Control.createCommand( "", "" ).setAttributes( command.getAttributes() )
								.setAttribute( "cmn_actor", actor ).setAttribute( "cmn_cmnd", command )
								.setAttribute( "id", i + "_" + command.getKey() ) );
					}
				}
			}
		}
		return result;
	}
}
