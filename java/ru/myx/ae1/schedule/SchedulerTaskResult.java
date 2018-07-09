/*
 * Created on 11.04.2006
 */
package ru.myx.ae1.schedule;

import ru.myx.ae3.help.Format;

final class SchedulerTaskResult {
	final String	scheduleid;
	
	final long		schedule;
	
	final int		result;
	
	final String	resultData;
	
	final long		reschedule;
	
	SchedulerTaskResult(final String scheduleid,
			final long schedule,
			final int result,
			final String resultData,
			final long reschedule) {
		this.scheduleid = scheduleid;
		this.schedule = schedule;
		this.result = result;
		this.resultData = resultData;
		this.reschedule = reschedule;
	}
	
	@Override
	public String toString() {
		return "TASKRESULT{scheduleid="
				+ this.scheduleid
				+ ", schedule="
				+ Format.Compact.date( this.schedule )
				+ ", result="
				+ this.result
				+ ", reschedule="
				+ this.reschedule
				+ "}";
	}
}
