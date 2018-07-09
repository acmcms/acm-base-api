/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.messaging;

import ru.myx.ae3.base.BaseObject;

final class DefaultManager implements MessagingManager {
	@Override
	public MessageBlank createBlankMessage(final String messageFactoryId, final BaseObject messageFactoryParameters) {
		return null;
	}
	
	@Override
	public MessageBlank createBlankMessageText(final String subject, final String body) {
		return null;
	}
	
	@Override
	public void deleteInbox(final int key) {
		// ignore
	}
	
	@Override
	public void deleteInbox(final int[] keys) {
		// ignore
	}
	
	@Override
	public void deleteSent(final int key) {
		// ignore
	}
	
	@Override
	public void deleteSent(final int[] keys) {
		// ignore
	}
	
	@Override
	public Message[] getInbox() {
		return null;
	}
	
	@Override
	public Message getInbox(final int key) {
		return null;
	}
	
	@Override
	public Message[] getSent() {
		return null;
	}
	
	@Override
	public Message getSent(final int key) {
		return null;
	}
	
	@Override
	public boolean hasInbox() {
		return false;
	}
	
	@Override
	public boolean hasSent() {
		return false;
	}
}
