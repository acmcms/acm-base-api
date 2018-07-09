/*
 * Created on 14.10.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.sapi;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.Engine;
import ru.myx.ae3.base.BaseDate;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.help.Convert;

/**
 * @author myx
 */
public class CalendarSAPI {
	
	/**
	 * 
	 */
	public static final BaseDate	NOW					= BaseDate.NOW;
	
	/**
	 * 
	 */
	public static final BaseDate	UNDEFINED			= BaseDate.UNKNOWN;
	
	/**
     * 
     */
	public static final Object[]	ARRAY_MONTHNAMES3	= new Object[] {
			MultivariantString.getString( "jan", Collections.singletonMap( "ru", "янв" ) ),
			MultivariantString.getString( "feb", Collections.singletonMap( "ru", "фев" ) ),
			MultivariantString.getString( "mar", Collections.singletonMap( "ru", "мар" ) ),
			MultivariantString.getString( "apr", Collections.singletonMap( "ru", "апр" ) ),
			MultivariantString.getString( "may", Collections.singletonMap( "ru", "май" ) ),
			MultivariantString.getString( "jun", Collections.singletonMap( "ru", "июн" ) ),
			MultivariantString.getString( "jul", Collections.singletonMap( "ru", "июл" ) ),
			MultivariantString.getString( "aug", Collections.singletonMap( "ru", "авг" ) ),
			MultivariantString.getString( "sep", Collections.singletonMap( "ru", "сен" ) ),
			MultivariantString.getString( "oct", Collections.singletonMap( "ru", "окт" ) ),
			MultivariantString.getString( "nov", Collections.singletonMap( "ru", "ноя" ) ),
			MultivariantString.getString( "dec", Collections.singletonMap( "ru", "дек" ) ), };
	
	/**
     * 
     */
	public static final Object[]	ARRAY_WEEKDAYS2		= new Object[] {
			MultivariantString.getString( "mo", Collections.singletonMap( "ru", "пн" ) ),
			MultivariantString.getString( "tu", Collections.singletonMap( "ru", "вт" ) ),
			MultivariantString.getString( "we", Collections.singletonMap( "ru", "ср" ) ),
			MultivariantString.getString( "th", Collections.singletonMap( "ru", "чт" ) ),
			MultivariantString.getString( "fr", Collections.singletonMap( "ru", "пт" ) ),
			MultivariantString.getString( "sa", Collections.singletonMap( "ru", "сб" ) ),
			MultivariantString.getString( "su", Collections.singletonMap( "ru", "вс" ) ), };
	
	/**
	 * @param params
	 * @return date
	 */
	public static final long build(final Map<?, ?> params) {
		if (params == null || params.isEmpty()) {
			return Engine.fastTime();
		}
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( 0L );
		for (final Map.Entry<?, ?> current : params.entrySet()) {
			final String key = current.getKey().toString();
			final int value = Convert.Any.toInt( current.getValue(), 0 );
			if (key.equals( "WEEK_OF_YEAR" )) {
				cal.set( Calendar.WEEK_OF_YEAR, value );
			} else if (key.equals( "WEEK_OF_MONTH" )) {
				cal.set( Calendar.WEEK_OF_MONTH, value );
			} else if (key.equals( "DAY_OF_YEAR" )) {
				cal.set( Calendar.DAY_OF_YEAR, value );
			} else if (key.equals( "DAY_OF_WEEK" )) {
				cal.set( Calendar.DAY_OF_WEEK, value );
			} else if (key.equals( "DAY_OF_MONTH" )) {
				cal.set( Calendar.DAY_OF_MONTH, value );
			} else if (key.equals( "YEAR" )) {
				cal.set( Calendar.YEAR, value );
			} else if (key.equals( "MONTH" )) {
				cal.set( Calendar.MONTH, value );
			} else if (key.equals( "HOUR" )) {
				cal.set( Calendar.HOUR, value );
			} else if (key.equals( "PM" )) {
				cal.set( Calendar.AM_PM, value );
			} else if (key.equals( "HOUR_OF_DAY" )) {
				cal.set( Calendar.HOUR_OF_DAY, value );
			} else if (key.equals( "MINUTE" )) {
				cal.set( Calendar.MINUTE, value );
			} else if (key.equals( "SECOND" )) {
				cal.set( Calendar.SECOND, value );
			} else {
				continue;
			}
		}
		return cal.getTimeInMillis();
	}
	
	/**
	 * @return map
	 */
	public static final Map<String, Object> get() {
		return CalendarSAPI.get( Engine.fastTime() );
	}
	
	/**
	 * @param date
	 * @return map
	 */
	public static final Map<String, Object> get(final Date date) {
		return CalendarSAPI.get( date.getTime() );
	}
	
	/**
	 * @param time
	 * @return map
	 */
	public static final BaseMap get(final long time) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( time );
		return new BaseNativeObject()//
				.putAppend( "WEEK_OF_YEAR", cal.get( Calendar.WEEK_OF_YEAR ) )//
				.putAppend( "WEEK_OF_MONTH", cal.get( Calendar.WEEK_OF_MONTH ) )//
				.putAppend( "DAY_OF_YEAR", cal.get( Calendar.DAY_OF_YEAR ) )//
				.putAppend( "DAY_OF_WEEK", cal.get( Calendar.DAY_OF_WEEK ) )//
				.putAppend( "DAY_OF_MONTH", cal.get( Calendar.DAY_OF_MONTH ) )//
				.putAppend( "YEAR", cal.get( Calendar.YEAR ) )//
				.putAppend( "MONTH", cal.get( Calendar.MONTH ) )//
				.putAppend( "HOUR", cal.get( Calendar.HOUR ) )//
				.putAppend( "PM", cal.get( Calendar.AM_PM ) )//
				.putAppend( "HOUR_OF_DAY", cal.get( Calendar.HOUR_OF_DAY ) )//
				.putAppend( "MINUTE", cal.get( Calendar.MINUTE ) )//
				.putAppend( "SECOND", cal.get( Calendar.SECOND ) );
	}
	
	/**
	 * @param date
	 * @return map
	 */
	public static final Map<String, Object> get(final Number date) {
		return CalendarSAPI.get( date.longValue() );
	}
	
	/**
	 * @return array
	 */
	public static final Object[] getArrayMonthNames3() {
		return CalendarSAPI.ARRAY_MONTHNAMES3;
	}
	
	/**
	 * @return array
	 */
	public static final Object[] getArrayWeekdays2() {
		return CalendarSAPI.ARRAY_WEEKDAYS2;
	}
	
	/**
	 * @return int
	 */
	public static final int getDayOfMonth() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.DAY_OF_MONTH );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getDayOfMonth(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.DAY_OF_MONTH );
	}
	
	/**
	 * @return int
	 */
	public static final int getDayOfWeek() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.DAY_OF_WEEK );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getDayOfWeek(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.DAY_OF_WEEK );
	}
	
	/**
	 * @return int
	 */
	public static final int getDayOfYear() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.DAY_OF_YEAR );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getDayOfYear(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.DAY_OF_YEAR );
	}
	
	/**
	 * @return int
	 */
	public static final int getHour() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.HOUR );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getHour(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.HOUR );
	}
	
	/**
	 * @return int
	 */
	public static final int getHourOfDay() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.HOUR_OF_DAY );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getHourOfDay(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.HOUR_OF_DAY );
	}
	
	/**
	 * @return int
	 */
	public static final int getMinute() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.MINUTE );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getMinute(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.MINUTE );
	}
	
	/**
	 * @return int
	 */
	public static final int getMonth() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.MONTH );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getMonth(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.MONTH );
	}
	
	/**
	 * @return int
	 */
	public static final int getPm() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.AM_PM );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getPm(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.AM_PM );
	}
	
	/**
	 * @return int
	 */
	public static final int getSecond() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.SECOND );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getSecond(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.SECOND );
	}
	
	/**
	 * @return int
	 */
	public static final int getWeekOfMonth() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.WEEK_OF_MONTH );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getWeekOfMonth(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.WEEK_OF_MONTH );
	}
	
	/**
	 * @return int
	 */
	public static final int getWeekOfYear() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.WEEK_OF_YEAR );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getWeekOfYear(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.WEEK_OF_YEAR );
	}
	
	/**
	 * @return int
	 */
	public static final int getYear() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( Engine.fastTime() );
		return cal.get( Calendar.YEAR );
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public static final int getYear(final Object o) {
		final Calendar cal = Calendar.getInstance();
		if (o == null) {
			cal.setTimeInMillis( Engine.fastTime() );
		} else if (o instanceof Date) {
			cal.setTime( (Date) o );
		} else if (o instanceof Number) {
			cal.setTimeInMillis( ((Number) o).longValue() );
		} else {
			throw new IllegalArgumentException( "Invalid date type!" + o.getClass().getName() );
		}
		return cal.get( Calendar.YEAR );
	}
	
	/**
	 * @param date
	 * @param round
	 * @return date
	 */
	public static final long roundTime(final Date date, final long round) {
		return date.getTime() / round * round;
	}
	
	/**
	 * @param time
	 * @param round
	 * @return date
	 */
	public static final long roundTime(final long time, final long round) {
		return time / round * round;
	}
}
