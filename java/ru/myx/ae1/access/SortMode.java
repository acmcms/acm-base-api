/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.access;

/**
 * @author myx
 * 
 */
public enum SortMode {
	/**
	 * Sort Mode 'by login'
	 */
	SM_LOGIN,

	/**
	 * Sort Mode 'by login' descending
	 */
	SM_LOGIN_DESC,

	/**
	 * Sort Mode 'by email'
	 */
	SM_EMAIL,

	/**
	 * Sort Mode 'by email' descending
	 */
	SM_EMAIL_DESC,

	/**
	 * Sort Mode 'by creation date'
	 */
	SM_CREATION,

	/**
	 * Sort Mode 'by creation date' descending
	 */
	SM_CREATION_DESC,

	/**
	 * Sort Mode 'by last change'
	 */
	SM_CHANGE,

	/**
	 * Sort Mode 'by last change' descending
	 */
	SM_CHANGE_DESC,

	/**
	 * Sort Mode 'by last login'
	 */
	SM_ACCESSED,

	/**
	 * Sort Mode 'by last login' descending
	 */
	SM_ACCESSED_DESC
}
