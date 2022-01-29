/*
 * Created on 30.07.2003
 */
package ru.myx.ae1.schedule;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.provide.ProvideRunner;
import ru.myx.ae1.provide.TaskRunner;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae1.storage.ModuleInterface;
import ru.myx.ae1.storage.StorageImpl;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Act;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.xml.Xml;

/** @author myx
 * 
 *         To change the template for this generated type comment go to Window>Preferences>Java>Code
 *         Generation>Code and Comments */
final class SchedulerTask implements Runnable {

	static final int RESULT_RESCHEDULE = 2;

	static final int RESULT_UNKNOWN = 1;

	static final int RESULT_DONE = 0;

	static final int RESULT_EXCEPTION = -2;

	static final int RESULT_TIMEOUT = -3;

	static final int RESULT_ENTRY_DRAFT = -4;

	static final int RESULT_ENTRY_DEAD = -5;

	static final int SCHEDULE_STATE_WAITING = 0;

	static final int SCHEDULE_STATE_LAUNCHING = 1;

	static final int SCHEDULE_STATE_RUNNING = 2;

	private static final String OWNER = "SCHEDULER";

	/** >= 1 */
	private static final int ACTIVE_SCHEDULER_MAX = Math.min(Engine.PARALLELISM, 4);

	private static volatile int activeSchedulerCount = 0;

	private final StorageImpl parent;

	private long lastCleanup = Engine.fastTime();

	private final LinkedList<SchedulerTaskResult> finished = new LinkedList<>();

	private final String tnQueue;

	private final String tnLog;

	private final boolean singleMode;

	private boolean destroyed = false;

	/** @param parent
	 * @param singleMode
	 * @param tnQueue
	 * @param tnLog */
	SchedulerTask(final StorageImpl parent, final boolean singleMode, final String tnQueue, final String tnLog) {
		this.parent = parent;
		this.singleMode = singleMode;
		this.tnQueue = tnQueue;
		this.tnLog = tnLog;
	}

	/** @param runner
	 * @param scheduleid
	 * @param schedule
	 * @param result */
	void enqueueResult(final Runnable runner, final String scheduleid, final long schedule, final BaseObject result) {

		final SchedulerTaskResult info;
		if (result == null) {
			info = new SchedulerTaskResult(scheduleid, schedule, SchedulerTask.RESULT_DONE, Xml.toXmlString("data", new BaseNativeObject(), false), 0L);
		} else if (result.baseIsPrimitiveInteger()) {
			final long reschedule = result.baseToJavaLong();
			info = new SchedulerTaskResult(scheduleid, schedule, SchedulerTask.RESULT_RESCHEDULE, Xml.toXmlString("data", new BaseNativeObject("date", result), false), reschedule);
		} else if (result instanceof Date) {
			final long reschedule = ((Date) result).getTime();
			info = new SchedulerTaskResult(scheduleid, schedule, SchedulerTask.RESULT_RESCHEDULE, Xml.toXmlString("data", new BaseNativeObject("date", result), false), reschedule);
		} else if (result instanceof Throwable) {
			final String text = Format.Throwable.toText((Throwable) result);
			info = new SchedulerTaskResult(scheduleid, schedule, SchedulerTask.RESULT_EXCEPTION, Xml.toXmlString("data", new BaseNativeObject("throwable", text.length() < 4000
				? text
				: text.substring(0, 3999)), false), 0L);
		} else {
			info = new SchedulerTaskResult(scheduleid, schedule, SchedulerTask.RESULT_UNKNOWN, Xml.toXmlString("data", new BaseNativeObject("result", result), false), 0L);
		}
		Report.event(SchedulerTask.OWNER, "LAUNCHING-ENQUEUE-RESULT", "runnable=" + runner + ", result=" + info);
		if (this.singleMode || this.destroyed) {
			this.finished.addLast(info);
			try (final Connection conn = this.parent.nextConnection()) {
				this.flushFinished(conn);
			} catch (final Error e) {
				throw e;
			} catch (final RuntimeException e) {
				throw e;
			} catch (final Throwable e) {
				throw new RuntimeException(e);
			}
		} else {
			synchronized (this.finished) {
				this.finished.addLast(info);
			}
		}
	}

	private void flushFinished(final Connection conn) throws Throwable {

		for (;;) {
			final SchedulerTaskResult info;
			synchronized (this.finished) {
				if (this.finished.isEmpty()) {
					break;
				}
				info = this.finished.removeFirst();
			}
			if (info == null) {
				continue;
			}
			Report.event(SchedulerTask.OWNER, "FINISHED", "scheduleid=" + info.scheduleid);
			try {
				conn.setAutoCommit(false);
				{
					final String query = "INSERT INTO " + this.tnLog + "(scheduleid,objectid,systemid,ownerid,schedule,name,command,parameters,result,resultData) "
							+ "SELECT scheduleid,objectid,systemid,ownerid,?,name,command,parameters,?,? FROM " + this.tnQueue + " WHERE scheduleid=?";
					try (final PreparedStatement ps = conn.prepareStatement(query)) {
						ps.setTimestamp(1, new Timestamp(info.schedule));
						ps.setInt(2, info.result);
						ps.setBytes(3, String.valueOf(info.resultData).getBytes(StandardCharsets.UTF_8));
						ps.setString(4, info.scheduleid);
						if (ps.executeUpdate() == 0) {
							continue;
						}
					}
				}
				if (info.reschedule == 0L) {
					final String query = "DELETE FROM " + this.tnQueue + " WHERE scheduleid=? AND schedule<?";
					try (final PreparedStatement ps = conn.prepareStatement(query)) {
						ps.setString(1, info.scheduleid);
						ps.setTimestamp(2, new Timestamp(info.schedule + 1000L));
						ps.executeUpdate();
					}
				} else {
					final String query = "UPDATE " + this.tnQueue + " SET scheduleid=?, state=" + SchedulerTask.SCHEDULE_STATE_WAITING
							+ ", schedule=? WHERE scheduleid=? AND schedule<?";
					try (final PreparedStatement ps = conn.prepareStatement(query)) {
						ps.setString(1, Engine.createGuid());
						ps.setTimestamp(2, new Timestamp(info.reschedule));
						ps.setString(3, info.scheduleid);
						ps.setTimestamp(4, new Timestamp(info.schedule + 1000L));
						ps.executeUpdate();
					}
				}
				conn.commit();
			} catch (final Throwable t) {
				synchronized (this.finished) {
					this.finished.addLast(info);
				}
				try {
					conn.rollback();
				} catch (final Throwable tt) {
					// ignore
				}
				throw t;
			} finally {
				try {
					conn.setAutoCommit(true);
				} catch (final Throwable t) {
					// ignore
				}
			}
		}
	}

	@Override
	public void run() {

		synchronized (SchedulerTask.class) {
			if (SchedulerTask.activeSchedulerCount >= SchedulerTask.ACTIVE_SCHEDULER_MAX) {
				if (!this.destroyed) {
					Report.warning(SchedulerTask.OWNER, "Too many active schedulers, limit=" + SchedulerTask.ACTIVE_SCHEDULER_MAX + ", will retry in 10 seconds");
					Act.later(null, this, 10000L);
				}
				return;
			}
			SchedulerTask.activeSchedulerCount++;
		}
		long nextIterationDelay = 15000L;
		try {
			final BaseObject settings = this.parent.getSettingsProtected();
			try (final Connection conn = this.parent.nextConnection()) {
				try {
					this.flushFinished(conn);
				} catch (final SQLException e) {
					throw new RuntimeException(e);
				}
				// clear log if needed
				{
					final long nextClean = Convert.MapEntry.toLong(settings, "scheduleLogCleanupDate", 0L);
					if (nextClean < Engine.fastTime()) {
						try {
							try (final PreparedStatement ps = conn.prepareStatement("DELETE FROM " + this.tnLog + " WHERE schedule<?")) {
								ps.setTimestamp(1, new Timestamp(Engine.fastTime() - 1000L * 60L * 60L * 24L * 7L));
								ps.execute();
							}
							settings.baseDefine("scheduleLogCleanupDate", Base.forDateMillis(Engine.fastTime() + 1000L * 60L * 60L * 24L));
							this.parent.commitProtectedSettings();
						} catch (final SQLException e) {
							throw new RuntimeException(e);
						}
					}
				}
				if (this.lastCleanup + 30L * 60L * 1000L < Engine.fastTime()) {
					this.lastCleanup = Engine.fastTime();
					List<String> timedOutIds = null;
					List<Boolean> timedOutStates = null;
					try (final PreparedStatement ps = conn.prepareStatement(
							"SELECT scheduleid,state FROM " + this.tnQueue + " WHERE schedule<? AND state IN (" + SchedulerTask.SCHEDULE_STATE_LAUNCHING + ","
									+ SchedulerTask.SCHEDULE_STATE_RUNNING + ")",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY)) {
						ps.setTimestamp(1, new Timestamp(this.lastCleanup - 6L * 60L * 60L * 1000L));
						try (final ResultSet rs = ps.executeQuery()) {
							if (rs.next()) {
								timedOutIds = new ArrayList<>();
								timedOutStates = new ArrayList<>();
								do {
									timedOutIds.add(rs.getString(1));
									timedOutStates.add(rs.getInt(2) == SchedulerTask.SCHEDULE_STATE_LAUNCHING
										? Boolean.TRUE
										: Boolean.FALSE);
								} while (rs.next());
							}
						}
					}
					if (timedOutIds != null && timedOutStates != null) {
						try {
							conn.setAutoCommit(false);
							for (int i = timedOutIds.size() - 1; i >= 0; --i) {
								final String scheduleid = timedOutIds.get(i);
								final boolean launching = timedOutStates.get(i) == Boolean.TRUE;
								Report.event(SchedulerTask.OWNER, "TIMED_OUT", "scheduleid=" + scheduleid);
								try (final PreparedStatement ps = conn.prepareStatement(
										"INSERT INTO " + this.tnLog + "(scheduleid,objectid,systemid,ownerid,schedule,name,command,parameters,result,resultData) "
												+ "SELECT scheduleid,objectid,systemid,ownerid,schedule,name,command,parameters,?,? FROM " + this.tnQueue
												+ " WHERE scheduleid=?")) {
									ps.setInt(1, SchedulerTask.RESULT_TIMEOUT);
									ps.setBytes(2, Xml.toXmlString("data", new BaseNativeObject("launching", launching
										? BaseObject.TRUE
										: BaseObject.FALSE), false).getBytes(StandardCharsets.UTF_8));
									ps.setString(3, scheduleid);
									ps.executeUpdate();
								}
								try (final PreparedStatement ps = conn.prepareStatement(
										"UPDATE " + this.tnQueue + " SET scheduleid=?, state=" + SchedulerTask.SCHEDULE_STATE_WAITING + ", schedule=? WHERE scheduleid=?")) {
									ps.setString(1, Engine.createGuid());
									ps.setTimestamp(2, new Timestamp(Engine.fastTime()));
									ps.setString(3, scheduleid);
									ps.executeUpdate();
								}
								conn.commit();
							}
						} catch (final Exception e) {
							try {
								conn.rollback();
							} catch (final Throwable t) {
								// ignore
							}
							throw e;
						} finally {
							try {
								conn.setAutoCommit(true);
							} catch (final Throwable t) {
								// ignore
							}
						}
					}
				}
				// do queue
				{
					final List<String> pending = new ArrayList<>();
					// get pending
					try (final PreparedStatement ps = conn.prepareStatement(
							"SELECT scheduleid FROM " + this.tnQueue + " WHERE schedule<? AND state=" + SchedulerTask.SCHEDULE_STATE_WAITING + " ORDER BY schedule ASC",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY)) {
						ps.setTimestamp(1, new Timestamp(Engine.fastTime() + 30000L));
						ps.setMaxRows(50);
						try (final ResultSet rs = ps.executeQuery()) {
							while (rs.next()) {
								pending.add(rs.getString(1));
							}
						}
					} catch (final Throwable t) {
						Report.exception(SchedulerTask.OWNER, "Error while analyzing schedule queue for " + this.parent.getMnemonicName() + " storage.", t);
					}
					if (!pending.isEmpty()) {
						nextIterationDelay = 3000L;
						Report.event(SchedulerTask.OWNER, "PENDING", pending.toString());
						for (final String scheduleid : pending) {
							Report.event(SchedulerTask.OWNER, "LAUNCHING", scheduleid);
							try {
								final String systemid = Engine.createGuid();
								try (final PreparedStatement ps = conn.prepareStatement(
										"UPDATE " + this.tnQueue + " SET systemid=?, state=" + SchedulerTask.SCHEDULE_STATE_LAUNCHING + " WHERE scheduleid=? AND state="
												+ SchedulerTask.SCHEDULE_STATE_WAITING)) {
									ps.setString(1, systemid);
									ps.setString(2, scheduleid);
									if (ps.executeUpdate() == 0) {
										continue;
									}
								}
								final String objectid;
								final String ownerid;
								final String name;
								final String command;
								final BaseObject parameters;
								final long schedule;
								{
									try (final PreparedStatement ps = conn
											.prepareStatement(
													"SELECT objectid,ownerid,name,command,parameters,schedule FROM " + this.tnQueue
															+ " WHERE scheduleid=? AND systemid=? AND state=" + SchedulerTask.SCHEDULE_STATE_LAUNCHING,
													ResultSet.TYPE_FORWARD_ONLY,
													ResultSet.CONCUR_READ_ONLY)) {
										ps.setString(1, scheduleid);
										ps.setString(2, systemid);
										try (final ResultSet rs = ps.executeQuery()) {
											if (!rs.next()) {
												objectid = rs.getString(1);
												ownerid = rs.getString(2);
												name = rs.getString(3);
												command = rs.getString(4);
												parameters = Scheduling.getBlobAsMap(rs.getBytes(5));
												schedule = rs.getTimestamp(6).getTime();
											} else {
												continue;
											}
										}
									}
								}
								this.start(conn, scheduleid, schedule, objectid, ownerid, name, command, parameters);
							} catch (final Throwable t) {
								Report.exception(SchedulerTask.OWNER, "Error while scheduling: " + scheduleid, t);
							}
							// //////////////////////////////////////////////////////////////////////////////////
						}
					}
				}
			}
		} catch (final SQLException t) {
			nextIterationDelay = 15000L;
			Report.exception(SchedulerTask.OWNER, "Error while requesting database connection, will resume in 15 seconds", t);
		} catch (final Throwable t) {
			nextIterationDelay = 30000L;
			Report.exception(SchedulerTask.OWNER, "Unknown error, will resume in 30 seconds", t);
		} finally {
			synchronized (SchedulerTask.class) {
				SchedulerTask.activeSchedulerCount--;
			}
			if (!this.destroyed) {
				Act.later(null, this, nextIterationDelay);
			}
		}
	}

	void start(final Connection conn,
			final String scheduleid,
			final long schedule,
			final String objectid,
			final String ownerid,
			final String name,
			final String command,
			final BaseObject parameters) throws Exception {
		
		final BaseEntry<?> entry = this.parent.getStorage().getByGuid(objectid);
		if (entry == null) {
			Report.event(SchedulerTask.OWNER, "NO_ENTRY", "Entry (objectid=" + objectid + ") was not found!");
			try (final PreparedStatement ps = conn.prepareStatement(
					"INSERT INTO " + this.tnLog + "(scheduleid,objectid,systemid,ownerid,schedule,name,command,parameters,result,resultData) "
							+ "SELECT scheduleid,objectid,systemid,ownerid,schedule,name,command,parameters," + SchedulerTask.RESULT_ENTRY_DEAD + ",? FROM " + this.tnQueue
							+ " WHERE scheduleid=?")) {
				ps.setBytes(1, Xml.toXmlString("data", BaseObject.UNDEFINED, false).getBytes(StandardCharsets.UTF_8));
				ps.setString(2, scheduleid);
				ps.executeUpdate();
			}
			try (final PreparedStatement ps = conn.prepareStatement("DELETE FROM " + this.tnQueue + " WHERE scheduleid=?")) {
				ps.setString(1, scheduleid);
				ps.executeUpdate();
			}
			return;
		}
		if (entry.getState() == ModuleInterface.STATE_DRAFT && "*".equals(name)) {
			Report.event(SchedulerTask.OWNER, "ENTRY_DRAFT", "Entry (objectid=" + objectid + ") is draft - skipping!");
			try (final PreparedStatement ps = conn.prepareStatement(
					"INSERT INTO " + this.tnLog + "(scheduleid,objectid,systemid,ownerid,schedule,name,command,parameters,result,resultData) "
							+ "SELECT scheduleid,objectid,systemid,ownerid,schedule,name,command,parameters," + SchedulerTask.RESULT_ENTRY_DRAFT + ",? FROM " + this.tnQueue
							+ " WHERE scheduleid=?")) {
				ps.setBytes(1, Xml.toXmlString("data", BaseObject.UNDEFINED, false).getBytes(StandardCharsets.UTF_8));
				ps.setString(2, scheduleid);
				ps.executeUpdate();
			}
			try (final PreparedStatement ps = conn.prepareStatement("DELETE FROM " + this.tnQueue + " WHERE scheduleid=?")) {
				ps.setString(1, scheduleid);
				ps.executeUpdate();
				return;
			}
		}
		final ExecProcess process = Exec.createProcess(this.parent.getServer().getRootContext(), "schedule");
		Context.replaceQuery(process, null); // derive context
		final Context context = Context.getContext(process);
		context.replaceUserId(ownerid);
		context.replaceSessionId(Engine.createGuid());
		context.setSessionStateDefault(AuthLevels.AL_AUTHORIZED_HIGHER);
		process.rb4CT = entry;
		final Runnable runner;
		{
			final TaskRunner actor = ProvideRunner.forName(command);
			if (actor == null) {
				process.replaceInfo("Storage scheduler context: command=" + command + ", type=" + entry.getTypeName());
				runner = new ScheduleRunnerCommand(process, this, scheduleid, schedule, entry, command, parameters);
			} else {
				process.replaceInfo("Storage scheduler context: task=" + actor.getTitle());
				runner = new ScheduleRunnerRunner(process, this, scheduleid, schedule, actor, parameters);
			}
		}
		{
			try (final PreparedStatement ps = conn.prepareStatement("UPDATE " + this.tnQueue + " SET state=" + SchedulerTask.SCHEDULE_STATE_RUNNING + " WHERE scheduleid=?")) {
				ps.setString(1, scheduleid);
				ps.executeUpdate();
			}
		}
		Report.event(SchedulerTask.OWNER, "LAUNCHING-ENQUEUE-ACT", "runnable=" + runner);
		final long left = schedule - Engine.fastTime();
		if (left > 2000L) {
			Act.later(process, runner, left);

		} else {
			Act.launch(process, runner);
		}
	}

	void stop() {

		this.destroyed = true;
	}

	@Override
	public final String toString() {

		return SchedulerTask.OWNER + ", parent=" + this.parent;
	}
}
