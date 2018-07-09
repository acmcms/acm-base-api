/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.messaging;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlForm;

final class DefaultFactory implements MessageFactory {
	@Override
	public BaseObject createExternalMessage(final Message message) {
		return message.getMessageFactoryParams();
	}
	
	@Override
	public ControlForm<?> createMessageForm(final Message message) {
		return null;
	}
	
	@Override
	public String getMessageTitle(final Message message) {
		return Base.getString( message.getMessageFactoryParams(), "Subject", "no subject!" );
	}
	
	@Override
	public String getTitle() {
		return "Default simple message factory";
	}
	
	@Override
	public boolean isExternalSupported() {
		return true;
	}
	
	@Override
	public boolean isFormSupported() {
		return false;
	}
}
