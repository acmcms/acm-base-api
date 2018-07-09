/**
 * 
 */
package ru.myx.ae2.indexing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ru.myx.ae3.help.Create;
import ru.myx.ae3.reflect.Reflect;

enum SearchOperation {
	/**
	 * 
	 */
	FILL {
		@Override
		protected final void handle(final int sortMode, final ResultSet rs, final Map<String, Object> found)
				throws SQLException {
			if (sortMode == IndexingFinder.SM_NUMERIC) {
				while (rs.next()) {
					final String key = rs.getString( 1 );
					final int weight = rs.getInt( 2 );
					final Object counter = found.get( key );
					if (counter != null) {
						((AtomicInteger) counter).addAndGet( weight );
					} else {
						found.put( key, new AtomicInteger( weight ) );
					}
				}
			} else {
				while (rs.next()) {
					final String key = rs.getString( 1 );
					final Object value = rs.getObject( 2 );
					found.put( key, value );
				}
			}
		}
		
		@Override
		protected final void handleCalendar(
				final Calendar calendar,
				final ResultSet rs,
				final Map<Integer, Map<Integer, Map<String, Number>>> map) throws SQLException {
			final String paramName = "created";
			while (rs.next()) {
				rs.getString( 1 );
				calendar.setTime( rs.getTimestamp( 2 ) );
				final Integer yearDate = Reflect.getInteger( calendar.get( Calendar.YEAR ) );
				final Integer monthDate = Reflect.getInteger( calendar.get( Calendar.MONTH ) );
				final String dayDate = "created" + calendar.get( Calendar.DAY_OF_MONTH );
				Map<Integer, Map<String, Number>> yearMap = map.get( yearDate );
				if (yearMap == null) {
					map.put( yearDate, yearMap = Create.tempMap() );
				}
				Map<String, Number> monthMap = yearMap.get( monthDate );
				if (monthMap == null) {
					yearMap.put( monthDate, monthMap = Create.tempMap() );
				}
				{
					final Object value = monthMap.get( paramName );
					if (value == null) {
						monthMap.put( paramName, new AtomicInteger( 1 ) );
					} else {
						((AtomicInteger) value).incrementAndGet();
					}
				}
				{
					final Number value = monthMap.get( dayDate );
					if (value == null) {
						monthMap.put( dayDate, new AtomicInteger( 1 ) );
					} else {
						((AtomicInteger) value).incrementAndGet();
					}
				}
			}
		}
		
		@Override
		protected boolean needNonEmptyResult() {
			return false;
		}
		
		@Override
		protected boolean needOrderBy() {
			return true;
		}
	},
	/**
	 * 
	 */
	CLEAN {
		@Override
		protected final void handle(final int sortMode, final ResultSet rs, final Map<String, Object> found)
				throws SQLException {
			while (rs.next()) {
				final String key = rs.getString( 1 );
				found.remove( key );
			}
		}
		
		@Override
		protected final void handleCalendar(
				final Calendar calendar,
				final ResultSet rs,
				final Map<Integer, Map<Integer, Map<String, Number>>> map) throws SQLException {
			final String paramName = "created";
			while (rs.next()) {
				rs.getString( 1 );
				calendar.setTime( rs.getTimestamp( 2 ) );
				final Integer yearDate = Reflect.getInteger( calendar.get( Calendar.YEAR ) );
				final Integer monthDate = Reflect.getInteger( calendar.get( Calendar.MONTH ) );
				final String dayDate = "created" + calendar.get( Calendar.DAY_OF_MONTH );
				final Map<Integer, Map<String, Number>> yearMap = map.get( yearDate );
				if (yearMap == null) {
					continue;
				}
				final Map<String, Number> monthMap = yearMap.get( monthDate );
				if (monthMap == null) {
					continue;
				}
				{
					final Object value = monthMap.get( paramName );
					if (value == null) {
						continue;
					}
					if (((AtomicInteger) value).decrementAndGet() == 0) {
						monthMap.remove( paramName );
					}
				}
				{
					final Number value = monthMap.get( dayDate );
					if (value == null) {
						continue;
					}
					if (((AtomicInteger) value).decrementAndGet() == 0) {
						monthMap.remove( dayDate );
					}
				}
			}
		}
		
		@Override
		protected boolean needNonEmptyResult() {
			return true;
		}
		
		@Override
		protected boolean needOrderBy() {
			return false;
		}
	},
	;
	
	protected abstract void handle(final int sortMode, final ResultSet rs, final Map<String, Object> found)
			throws SQLException;
	
	protected abstract void handleCalendar(
			final Calendar calendar,
			final ResultSet rs,
			final Map<Integer, Map<Integer, Map<String, Number>>> found) throws SQLException;
	
	protected abstract boolean needNonEmptyResult();
	
	protected abstract boolean needOrderBy();
}
