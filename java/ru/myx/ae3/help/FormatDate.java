package ru.myx.ae3.help;

import java.util.Date;

/**
 * @author myx
 * 
 */
public class FormatDate {
	/**
	 * Inexact, Human readable
	 */
	public static final class Compact {
		
		private static final CompactDateFormatterIntl	DATE	= new CompactDateFormatterIntl();
		
		/**
		 * @param date
		 * @return string
		 */
		public static final String date(final Date date) {
			return Compact.DATE.format( date );
		}
		
		/**
		 * @param time
		 * @return string
		 */
		public static final String date(final long time) {
			return Compact.DATE.format( time );
		}
		
		/**
		 * @param date
		 * @return string
		 */
		public static final String dateRelative(final Date date) {
			return Compact.DATE.formatRelative( date );
		}
		
		/**
		 * @param time
		 * @return string
		 */
		public static final String dateRelative(final long time) {
			return Compact.DATE.formatRelative( time );
		}
		
		private Compact() {
			// empty
		}
		
	}
}
