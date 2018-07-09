/*
 * Created on 26.05.2004
 */
package ru.myx.ae1.schedule;

import ru.myx.ae1.provide.TaskRunner;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class ScheduleRunnerRunner implements Runnable {
	
	private final ExecProcess	ctx;
	
	private final SchedulerTask	task;
	
	private final String		scheduleid;
	
	private final long			schedule;
	
	private final TaskRunner	actor;
	
	private final BaseObject	data;
	
	ScheduleRunnerRunner(final ExecProcess ctx,
			final SchedulerTask task,
			final String scheduleid,
			final long schedule,
			final TaskRunner actor,
			final BaseObject data) {
		this.ctx = ctx;
		this.task = task;
		this.scheduleid = scheduleid;
		this.schedule = schedule;
		this.actor = actor;
		this.data = data;
	}
	
	@Override
	public void run() {
		try {
			final Object result = this.actor.run( this.ctx, this.data );
			this.task.enqueueResult( this, this.scheduleid, this.schedule, Base.forUnknown( result ) );
		} catch (final Throwable e) {
			Report.exception( "SCHEDULER",
					("Exception while running a task, task=" + this.scheduleid + ", actor=" + this.actor),
					e );
			this.task.enqueueResult( this, this.scheduleid, this.schedule, Base.forThrowable( e ) );
		}
	}
}
