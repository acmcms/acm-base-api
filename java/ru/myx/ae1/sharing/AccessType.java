/*
 * Created on 17.05.2006
 */
package ru.myx.ae1.sharing;

import java.util.Collections;

import ru.myx.ae1.control.MultivariantString;

/**
 * @author myx
 * 
 */
public enum AccessType {
	/**
	 * 
	 */
	PUBLIC("Public access / front-door", "Публичный доступ / афишируемый"),
	/**
	 * 
	 */
	TESTING("Testing / experimental", "Тестовый / неафишируемый"),
	/**
	 * 
	 */
	CLOSED("Closed: require authorization", "Закрытый: требуется авторизация");
	private final Object	string;
	
	AccessType(final String english, final String russian) {
		this.string = MultivariantString.getString( english, Collections.singletonMap( "ru", russian ) );
	}
	
	/**
	 * @return
	 */
	public String getIcon() {
		return this.name().toLowerCase();
	}
	
	@Override
	public String toString() {
		return this.string.toString();
	}
}
