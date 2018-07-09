package ru.myx.ae2.indexing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import ru.myx.ae3.report.Report;
import ru.myx.jdbc.queueing.RequestAttachment;
import ru.myx.jdbc.queueing.RunnerDatabaseRequestor;

/**
 * @author myx
 *
 */
public class ExecSearchInstruction extends RequestAttachment<Void, RunnerDatabaseRequestor> {

	private final String query;

	private final SearchOperation operation;

	private final Map<String, Object> result;

	private final long dateStart;

	private final long dateEnd;

	private final int sortMode;

	ExecSearchInstruction(final Map<String, Object> result, final long dateStart, final long dateEnd, final int sortMode, final String query, final SearchOperation operation) {
		this.result = result;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.sortMode = sortMode;
		this.query = query;
		this.operation = operation;
	}

	/**
	 * @param connection
	 * @throws SQLException
	 */
	public void execute(final Connection connection) throws SQLException {

		if (this.operation.needNonEmptyResult() && this.result.isEmpty()) {
			return;
		}
		if (Report.MODE_DEBUG) {
			Report.debug(IndexingFinder.OWNER, "Search (" + this.operation + "): " + this.query);
		}
		try (final PreparedStatement ps = connection.prepareStatement(this.query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			int index = 1;
			if (this.dateStart > 0L) {
				ps.setTimestamp(index++, new Timestamp(this.dateStart));
			}
			if (this.dateEnd > 0L) {
				ps.setTimestamp(index++, new Timestamp(this.dateEnd));
			}
			try (final ResultSet rs = ps.executeQuery()) {
				this.operation.handle(this.sortMode, rs, this.result);
			}
		}
	}

	@Override
	public Void apply(final RunnerDatabaseRequestor ctx) {

		try {
			this.execute(ctx.ctxGetConnection());
		} catch (final SQLException e) {
			throw new RuntimeException(this.getClass().getSimpleName(), e);
		}
		this.setResult(null);
		return null;
	}

	@Override
	public String getKey() {

		return null;
	}
}
