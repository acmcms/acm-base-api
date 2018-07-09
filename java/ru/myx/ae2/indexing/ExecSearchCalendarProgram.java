package ru.myx.ae2.indexing;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.Map;

import ru.myx.ae3.help.Format;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 * 
 */
public class ExecSearchCalendarProgram {
	private final Map<Integer, Map<Integer, Map<String, Number>>>	result;
	
	private final LinkedList<ExecSearchCalendarInstruction>			instructions;
	
	private final long												searchStart;
	
	ExecSearchCalendarProgram(final Map<Integer, Map<Integer, Map<String, Number>>> result,
			final LinkedList<ExecSearchCalendarInstruction> instructions) {
		this.result = result;
		this.instructions = instructions;
		this.searchStart = System.currentTimeMillis();
	}
	
	/**
	 * @return search results
	 */
	public Map<Integer, Map<Integer, Map<String, Number>>> baseValue() {
		if (this.result.isEmpty()) {
			final long current = System.currentTimeMillis();
			Report.debug( IndexingFinder.OWNER, "Total time="
					+ Format.Compact.toPeriod( current - this.searchStart )
					+ ", empty result" );
			return null;
		}
		if (Report.MODE_DEBUG) {
			final long current = System.currentTimeMillis();
			Report.debug( IndexingFinder.OWNER, "Total time=" + Format.Compact.toPeriod( current - this.searchStart ) );
		}
		return this.result;
	}
	
	/**
	 * @param timeout
	 * @param connection
	 * @return true if has next to execute
	 * @throws Throwable
	 */
	public Map<Integer, Map<Integer, Map<String, Number>>> executeAll(final long timeout, final Connection connection)
			throws Throwable {
		for (final long timeoutDate = timeout <= 0L
				? Long.MAX_VALUE
				: System.currentTimeMillis() + timeout; timeoutDate > System.currentTimeMillis();) {
			final ExecSearchCalendarInstruction instruction = this.nextItem();
			if (instruction == null) {
				break;
			}
			instruction.execute( connection );
		}
		return this.baseValue();
	}
	
	/**
	 * @return
	 */
	public ExecSearchCalendarInstruction nextItem() {
		return this.instructions.isEmpty()
				? null
				: this.instructions.removeFirst();
	}
}
