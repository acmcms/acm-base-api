/**
 * Created on 23.12.2002
 * 
 * myx - barachta */
package ru.myx.ae3.help;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.Engine;

/**
 * @author myx
 * 
 * myx - barachta 
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
final class CompactDateFormatterIntl {
	static final class Formatter {
		private static final Object		UNDEFINED	= MultivariantString.getString( "n/a",
															Collections.singletonMap( "ru", "нет данных" ) );
		
		private static final Object		TODAY		= MultivariantString.getString( "today",
															Collections.singletonMap( "ru", "сегодня" ) );
		
		private static final Object		YESTERDAY	= MultivariantString.getString( "yesterday",
															Collections.singletonMap( "ru", "вчера" ) );
		
		private final SimpleDateFormat	formatFull;
		
		private final SimpleDateFormat	formatDate;
		
		private final SimpleDateFormat	formatTime;
		
		private final Calendar			calendar;
		
		private final Date				date;
		
		Formatter() {
			this.formatFull = new SimpleDateFormat( "yyyy.MM.dd HH:mm", Locale.ENGLISH );
			this.formatDate = new SimpleDateFormat( "yyyy.MM.dd", Locale.ENGLISH );
			this.formatTime = new SimpleDateFormat( "HH:mm", Locale.ENGLISH );
			this.calendar = Calendar.getInstance();
			this.date = new Date( 0 );
		}
		
		final String format(final Date date) {
			synchronized (this) {
				return this.formatFull.format( date );
			}
		}
		
		final String format(final long time) {
			synchronized (this) {
				this.date.setTime( time );
				return this.formatFull.format( this.date );
			}
		}
		
		final String formatRelative(final Date date) {
			if (date == null) {
				return Formatter.UNDEFINED.toString();
			}
			return this.formatRelative( date.getTime() );
		}
		
		final String formatRelative(final long date) {
			if (date == -1L) {
				return Formatter.UNDEFINED.toString();
			}
			final long current = Engine.fastTime();
			if (current > date) {
				final long time = current - date;
				if (time < 1000L * 60L) {
					return MultivariantString.getString( time / 1000L + " sec.",
							Collections.singletonMap( "ru", time / 1000L + " сек." ) ).toString();
				}
				if (time < 1000L * 60L * 60L / 2) {
					return MultivariantString.getString( time / (1000L * 60L) + " min.",
							Collections.singletonMap( "ru", time / (1000L * 60L) + " мин." ) ).toString();
				}
				synchronized (this) {
					this.calendar.setTimeInMillis( current );
					this.calendar.set( Calendar.HOUR_OF_DAY, 0 );
					this.calendar.set( Calendar.MINUTE, 0 );
					this.calendar.set( Calendar.SECOND, 0 );
					this.calendar.set( Calendar.MILLISECOND, 0 );
					final long dayStart = this.calendar.getTime().getTime();
					if (date >= dayStart) {
						this.date.setTime( date );
						return Formatter.TODAY.toString() + ", " + this.formatTime.format( this.date );
					}
					if (date >= dayStart - 1000L * 60L * 60L * 24L) {
						this.date.setTime( date );
						return Formatter.YESTERDAY.toString() + ", " + this.formatTime.format( this.date );
					}
					this.date.setTime( date );
					return this.formatDate.format( this.date );
				}
			}
			synchronized (this) {
				this.date.setTime( date );
				return this.formatFull.format( this.date );
			}
		}
	}
	
	private final int			capacity	= 32;
	
	private final int			mask		= this.capacity - 1;
	
	private int					counter		= 0;
	
	private final Formatter[]	formatters	= new Formatter[this.capacity];
	
	CompactDateFormatterIntl() {
		for (int i = this.mask; i >= 0; --i) {
			this.formatters[i] = new Formatter();
		}
	}
	
	final String format(final Date date) {
		final Formatter format = this.formatters[--this.counter & this.mask];
		return format.format( date );
	}
	
	final String format(final long date) {
		final Formatter format = this.formatters[--this.counter & this.mask];
		return format.format( date );
	}
	
	final String formatRelative(final Date date) {
		final Formatter format = this.formatters[--this.counter & this.mask];
		return format.formatRelative( date );
	}
	
	final String formatRelative(final long date) {
		final Formatter format = this.formatters[--this.counter & this.mask];
		return format.formatRelative( date );
	}
	
	@Override
	public String toString() {
		return "ae2core Compact Date formatter, capacity=" + this.capacity;
	}
}
