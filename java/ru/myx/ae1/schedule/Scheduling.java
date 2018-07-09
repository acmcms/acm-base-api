/*
 * Created on 27.05.2004
 */
package ru.myx.ae1.schedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.myx.ae1.storage.BaseSchedule;
import ru.myx.ae1.storage.ModuleSchedule;
import ru.myx.ae1.storage.StorageImpl;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Act;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Text;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.xml.Xml;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Scheduling implements ModuleSchedule {
	private static final String	OWNER	= "XDS-SCHEDULING";
	
	static final BaseObject getBlobAsMap(final byte[] bytes) throws Exception {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return Xml.toMap( "blobToMap(scheduling)",
				Transfer.wrapCopier( bytes ),
				Engine.CHARSET_UTF8,
				null,
				new BaseNativeObject(),
				null,
				null );
	}
	
	/**
	 * @param parent
	 * @param tnQueue
	 * @param tnLog
	 * @return scheduler
	 */
	public static final Scheduling getScheduling(final StorageImpl parent, final String tnQueue, final String tnLog) {
		if (parent == null || tnQueue == null || tnLog == null) {
			return null;
		}
		try (final Connection conn = parent.nextConnection()) {
			if (conn == null) {
				return null;
			}
			try (final PreparedStatement ps = conn.prepareStatement( "SELECT count(name) FROM " + tnQueue,
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY )) {
				try (final ResultSet rs = ps.executeQuery()) {
					return new Scheduling( parent, tnQueue, tnLog );
				}
			}
		} catch (final SQLException e) {
			return null;
		}
	}
	
	private final StorageImpl	parent;
	
	private final String		tnQueue;
	
	private final String		tnLog;
	
	private SchedulerTask		schedulerTask;
	
	private Scheduling(final StorageImpl parent, final String tnQueue, final String tnLog) {
		this.parent = parent;
		this.tnQueue = tnQueue;
		this.tnLog = tnLog;
	}
	
	void commitChange(final Change change) {
		final String objectId = change.getObjectId();
		final String[] deleted = change.getDeleted();
		final Action<?>[] created = change.getCreated();
		if ((deleted == null || deleted.length == 0) && (created == null || created.length == 0)) {
			return;
		}
		try (final Connection conn = this.parent.nextConnection()) {
			try {
				conn.setAutoCommit( false );
				if (deleted != null && deleted.length > 0) {
					try (final PreparedStatement ps = conn.prepareStatement( "DELETE FROM "
							+ this.tnQueue
							+ " WHERE objectid=? AND scheduleid in ('"
							+ Text.join( deleted, "','" )
							+ "')" )) {
						ps.setString( 1, objectId );
						ps.executeUpdate();
					}
				}
				if (created != null && created.length > 0) {
					for (final Action<?> action : created) {
						try (final PreparedStatement ps = conn
								.prepareStatement( "INSERT INTO "
										+ this.tnQueue
										+ "(scheduleid,objectid,systemid,state,ownerid,schedule,name,command,parameters) VALUES (?,?,?,?,?,?,?,?,?)" )) {
							ps.setString( 1, action.getKey() );
							ps.setString( 2, objectId );
							ps.setString( 3, "new" );
							ps.setInt( 4, SchedulerTask.SCHEDULE_STATE_WAITING );
							ps.setString( 5, action.getOwner() );
							ps.setTimestamp( 6, new Timestamp( action.getDate() ) );
							ps.setString( 7, action.getName() );
							ps.setString( 8, action.getActor() );
							final BaseObject actorData = action.getActorData();
							ps.setBytes( 9, Xml.toXmlString( "data", actorData == null
									? BaseObject.UNDEFINED
									: actorData, false ).getBytes( Engine.CHARSET_UTF8 ) );
							ps.executeUpdate();
						}
					}
				}
				conn.commit();
			} catch (final Throwable t) {
				try {
					conn.rollback();
				} catch (final Throwable tt) {
					// ignore
				}
				Report.exception( Scheduling.OWNER,
						"Error commiting a change: storageId="
								+ this.parent.getMnemonicName()
								+ ", objectid="
								+ objectId,
						t );
				throw new RuntimeException( t );
			}
		} catch (final SQLException t) {
			throw new RuntimeException( t );
		}
	}
	
	@Override
	public BaseSchedule createChange(final String objectId) {
		return new Change( this, objectId, this.getQueue( objectId ) );
	}
	
	Action<?>[] getQueue(final String objectId) {
		try {
			try (final Connection conn = this.parent.nextConnection()) {
				try (final PreparedStatement ps = conn
						.prepareStatement( "SELECT scheduleid,state,ownerid,schedule,name,command,parameters FROM "
								+ this.tnQueue
								+ " WHERE objectid=? ORDER BY schedule ASC" )) {
					ps.setString( 1, objectId );
					try (final ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							final List<Action<?>> result = new ArrayList<>();
							do {
								final String scheduleid = rs.getString( 1 );
								final int state = rs.getInt( 2 );
								final String ownerid = rs.getString( 3 );
								final Date schedule = rs.getTimestamp( 4 );
								final String name = rs.getString( 5 );
								final String command = rs.getString( 6 );
								final BaseObject data = Scheduling.getBlobAsMap( rs.getBytes( 7 ) );
								final Action<?> action = new Action<>( scheduleid,
										state,
										schedule.getTime(),
										ownerid,
										name,
										command,
										data );
								result.add( action );
							} while (rs.next());
							return result.toArray( new Action[result.size()] );
						}
					}
					return null;
				}
			}
		} catch (final Throwable t) {
			Report.exception( Scheduling.OWNER, "Error getting action list, objectid=" + objectId, t );
			throw new RuntimeException( t );
		}
	}
	
	@Override
	public final int getVersion() {
		return 2;
	}
	
	@Override
	public void start() {
		if (this.schedulerTask != null) {
			return;
		}
		synchronized (this) {
			if (this.schedulerTask == null) {
				final ExecProcess process = Exec.createProcess( this.parent.getServer().getRootContext(),
						"scheduler context" );
				Context.replaceQuery( process, null ); // derive context
				final Context context = Context.getContext( process );
				context.replaceUserId( "Scheduler" );
				Act.later( process, this.schedulerTask = new SchedulerTask( this.parent,
						false,
						this.tnQueue,
						this.tnLog ), 20000L );
			}
		}
	}
	
	@Override
	public void stop() {
		synchronized (this) {
			if (this.schedulerTask != null) {
				this.schedulerTask.stop();
				this.schedulerTask = null;
			}
		}
	}
}
