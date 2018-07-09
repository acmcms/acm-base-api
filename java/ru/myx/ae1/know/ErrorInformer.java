/**
 * 
 */
package ru.myx.ae1.know;

import ru.myx.ae1.messaging.MessageBlank;
import ru.myx.ae1.messaging.MessagingManager;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Text;
import ru.myx.ae3.report.Event;
import ru.myx.ae3.report.LogReceiver;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;

final class ErrorInformer extends LogReceiver {
	final Server	server;
	
	ErrorInformer(final Server server) {
		this.server = server;
	}
	
	@Override
	protected String[] eventClasses() {
		return null;
	}
	
	@Override
	protected String[] eventTypes() {
		return new String[] { "EXCEPTION" };
	}
	
	@Override
	protected void onEvent(final Event event) {
		final ServeRequest request = Context.getRequest( Exec.currentProcess() );
		final String responsible1 = this.server.getProperty( "responsible", Report.MAILTO ).trim();
		final String responsible2 = request != null
				? Base.getString( request.getSettings(), "responsible", "" ).trim()
				: "";
		if (responsible1.length() > 0 || responsible2.length() > 0) {
			final MessagingManager manager = this.server.getMessagingManager();
			if (manager != null) {
				final MessageBlank message = manager.createBlankMessageText( "EXCEPTION @ "
						+ this.server.getDomainId()
						+ " @ "
						+ Engine.HOST_NAME
						+ ", "
						+ Text.limitString( event.getTitle(), 256, "..." ), event.getSubject() );
				if (message != null) {
					if (responsible1.length() > 0) {
						message.addRecipientEmail( responsible1 );
					}
					if (responsible2.length() > 0) {
						message.addRecipientEmail( responsible2 );
					}
					message.commit();
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + this.server + ")";
	}
}
