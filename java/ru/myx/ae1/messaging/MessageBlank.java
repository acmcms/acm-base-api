/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.messaging;

/**
 * @author myx
 * 
 */
public interface MessageBlank {
	/**
	 * @param path
	 * @param permission
	 */
	public void addRecipientAccess(final String path, final String permission);
	
	/**
	 * @param email
	 */
	public void addRecipientEmail(final String email);
	
	/**
	 * @param groupId
	 */
	public void addRecipientGroupId(final String groupId);
	
	/**
	 * @param userId
	 */
	public void addRecipientUserId(final String userId);
	
	/**
     * 
     */
	public void commit();
	
	/**
	 * @param interactive
	 */
	public void setSendInteractive(final boolean interactive);
}
