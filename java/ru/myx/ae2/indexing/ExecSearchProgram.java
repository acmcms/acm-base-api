package ru.myx.ae2.indexing;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 * 
 */
public class ExecSearchProgram {
	private static final Comparator<Map.Entry<String, Object>>	ORDER_BY_BINARY_ASCENDING		= Convert.Any
																										.toAny( new ComparatorBinaryAscending() );
	
	private static final Comparator<Map.Entry<String, Object>>	ORDER_BY_BINARY_DESCENDING		= Convert.Any
																										.toAny( new ComparatorBinaryDescending() );
	
	private static final Comparator<Map.Entry<String, Object>>	ORDER_BY_COMPARABLE_ASCENDING	= Convert.Any
																										.toAny( new ComparatorComparableAscending() );
	
	private static final Comparator<Map.Entry<String, Object>>	ORDER_BY_COMPARABLE_DESCENDING	= Convert.Any
																										.toAny( new ComparatorComparableDescending() );
	
	private static final Comparator<Map.Entry<String, Object>>	ORDER_BY_NUMERIC_ASCENDING		= Convert.Any
																										.toAny( new ComparatorNumericAscending() );
	
	private static final Comparator<Map.Entry<String, Object>>	ORDER_BY_NUMERIC_DESCENDING		= Convert.Any
																										.toAny( new ComparatorNumericDescending() );
	
	private static final Comparator<Map.Entry<String, Object>>	ORDER_BY_STRINSENCE_ASCENDING	= Convert.Any
																										.toAny( new ComparatorStringInsencetiveAscending() );
	
	private static final Comparator<Map.Entry<String, Object>>	ORDER_BY_STRINSENCE_DESCENDING	= Convert.Any
																										.toAny( new ComparatorStringInsencetiveDescending() );
	
	private final Map<String, Object>							result;
	
	private final LinkedList<ExecSearchInstruction>				instructions;
	
	private final int											sortMode;
	
	private final boolean										sortDescending;
	
	private final long											searchStart;
	
	ExecSearchProgram(final Map<String, Object> result,
			final LinkedList<ExecSearchInstruction> instructions,
			final int sortMode,
			final boolean sortDescending) {
		this.result = result;
		this.instructions = instructions;
		this.sortMode = sortMode;
		this.sortDescending = sortDescending;
		this.searchStart = System.currentTimeMillis();
	}
	
	/**
	 * @return search results
	 */
	public Map.Entry<String, Object>[] baseValue() {
		if (this.result.isEmpty()) {
			if (Report.MODE_DEBUG) {
				final long current = System.currentTimeMillis();
				Report.debug( IndexingFinder.OWNER,
						"Total time=" + Format.Compact.toPeriod( current - this.searchStart ) + ", empty result." );
			}
			return null;
		}
		final Map.Entry<String, Object>[] results = Convert.Array.toAny( this.result.entrySet()
				.toArray( new Map.Entry[this.result.size()] ) );
		final long sortStart = System.currentTimeMillis();
		final boolean binarySort = results[0].getValue() instanceof byte[];
		final Comparator<Map.Entry<String, Object>> comparator = this.sortDescending
				? this.sortMode == IndexingFinder.SM_NUMERIC
						? ExecSearchProgram.ORDER_BY_NUMERIC_DESCENDING
						: binarySort
								? ExecSearchProgram.ORDER_BY_BINARY_DESCENDING
								: this.sortMode == IndexingFinder.SM_STRING
										? ExecSearchProgram.ORDER_BY_COMPARABLE_DESCENDING
										: ExecSearchProgram.ORDER_BY_STRINSENCE_DESCENDING
				: this.sortMode == IndexingFinder.SM_NUMERIC
						? ExecSearchProgram.ORDER_BY_NUMERIC_ASCENDING
						: binarySort
								? ExecSearchProgram.ORDER_BY_BINARY_ASCENDING
								: this.sortMode == IndexingFinder.SM_STRING
										? ExecSearchProgram.ORDER_BY_COMPARABLE_ASCENDING
										: ExecSearchProgram.ORDER_BY_STRINSENCE_ASCENDING;
		Arrays.sort( results, comparator );
		if (Report.MODE_DEBUG) {
			final long current = System.currentTimeMillis();
			Report.debug( IndexingFinder.OWNER, "Total time="
					+ Format.Compact.toPeriod( current - this.searchStart )
					+ ", Sort time="
					+ Format.Compact.toPeriod( current - sortStart ) );
		}
		return results;
	}
	
	/**
	 * @param timeout
	 * @param limit
	 * @param connection
	 * @return true if has next to execute
	 * @throws Throwable
	 */
	public Map.Entry<String, Object>[] executeAll(final long timeout, final int limit, final Connection connection)
			throws Throwable {
		for (final long timeoutDate = timeout <= 0L
				? Long.MAX_VALUE
				: System.currentTimeMillis() + timeout; timeoutDate > System.currentTimeMillis();) {
			final ExecSearchInstruction instruction = this.nextItem();
			if (instruction == null) {
				break;
			}
			instruction.execute( connection );
		}
		final Map.Entry<String, Object>[] result = this.baseValue();
		if (limit > 0 && result != null && result.length > limit) {
			final Map.Entry<String, Object>[] limited = Convert.Array.toAny( new Map.Entry[limit] );
			System.arraycopy( result, 0, limited, 0, limit );
			return limited;
		}
		return result;
	}
	
	/**
	 * @return
	 */
	public ExecSearchInstruction nextItem() {
		return this.instructions.isEmpty()
				? null
				: this.instructions.removeFirst();
	}
}
