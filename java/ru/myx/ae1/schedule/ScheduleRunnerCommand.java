/*
 * Created on 26.05.2004
 */
package ru.myx.ae1.schedule;

import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseFunction;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.report.Report;

/** @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments */
final class ScheduleRunnerCommand implements Runnable {
	
	private final ExecProcess ctx;
	
	private final SchedulerTask task;
	
	private final String scheduleid;
	
	private final long schedule;
	
	private final BaseEntry<?> entry;
	
	private final String command;
	
	private final BaseObject parameters;
	
	ScheduleRunnerCommand(
			final ExecProcess ctx,
			final SchedulerTask task,
			final String scheduleid,
			final long schedule,
			final BaseEntry<?> entry,
			final String command,
			final BaseObject parameters) {
		this.ctx = ctx;
		this.task = task;
		this.scheduleid = scheduleid;
		this.schedule = schedule;
		this.entry = entry;
		this.command = command;
		this.parameters = parameters;
	}
	
	@Override
	public void run() {
		
		try {
			final BaseFunction method = this.entry.baseGet(this.command, BaseObject.UNDEFINED).baseCall();
			if (method == null) {
				throw new IllegalArgumentException("Command is not available!");
			}
			this.task.enqueueResult(//
					this,
					this.scheduleid,
					this.schedule,
					method.callNEX(this.ctx, this.entry, this.ctx.argumentsMap(this.parameters))//
			);
		} catch (final Throwable e) {
			Report.exception("SCHEDULER", "Exception while running a task, task=" + this.scheduleid + ", entry=" + this.entry + ", command=" + this.command, e);
			this.task.enqueueResult(this, this.scheduleid, this.schedule, Base.forThrowable(e));
		}
	}
	
	@Override
	public final String toString() {
		
		return "SCHEDULED-TASK, command=" + this.command + ", entry=" + this.entry;
	}
}
