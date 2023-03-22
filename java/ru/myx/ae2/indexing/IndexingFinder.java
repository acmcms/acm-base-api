/*
 * Created on 30.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.ae2.indexing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Create;
import ru.myx.ae3.report.Report;
import ru.myx.query.OneCondition;
import ru.myx.query.OneSort;
import ru.myx.query.SyntaxQuery;

/** @author myx */
public final class IndexingFinder {

	static final String OWNER = "XDS_FINDER";
	
	/**
	 *
	 */
	public static final int SM_NUMERIC = 1;
	
	/**
	 *
	 */
	public static final int SM_STR_INS = 2;
	
	/**
	 *
	 */
	public static final int SM_STRING = 0;
	
	private static final String[] SORT_MODES = {
			"text", "numeric", "text-ins"
	};
	
	private static final void addConditionCode(final StringBuilder buffer, final OneCondition value) {

		final String condition = value.getValue();
		buffer.append(Integer.parseInt(condition, 36));
	}
	
	private final String created_field;
	
	private final IndexingStemmer stemmer;
	
	private final IndexingDictionary dictionary;
	
	private final String join_condition;
	
	private final String join_condition_all;
	
	private final Map<?, ?> replacementFields;
	
	private final String selection_prefix;
	
	private final String tnIndices;
	
	/** @param stemmer
	 * @param dictionary
	 * @param tnIndices
	 * @param replacementFields
	 * @param selection_prefix
	 * @param created_field
	 * @param join_condition
	 * @param join_condition_all */
	public IndexingFinder(
			final IndexingStemmer stemmer,
			final IndexingDictionary dictionary,
			final String tnIndices,
			final Map<?, ?> replacementFields,
			final String selection_prefix,
			final String created_field,
			final String join_condition,
			final String join_condition_all) {

		this.stemmer = stemmer;
		this.dictionary = dictionary;
		this.replacementFields = replacementFields;
		this.tnIndices = tnIndices;
		this.selection_prefix = selection_prefix;
		this.created_field = created_field;
		this.join_condition = join_condition;
		this.join_condition_all = join_condition_all;
	}
	
	/** @param hierarchy
	 * @param all
	 * @param sort
	 * @param dateStart
	 * @param dateEnd
	 * @param filter
	 * @return map entry listing */
	public final ExecSearchProgram search(final String hierarchy, final boolean all, final String sort, final long dateStart, final long dateEnd, final String filter) {

		final String query;
		final int minimum;
		if (filter == null) {
			if (hierarchy == null || hierarchy.isBlank()) {
				return null;
			}
			if (all) {
				minimum = 1;
				query = "\"$hierarchy:" + hierarchy + "\"";
			} else {
				return null;
			}
		} else {
			if (hierarchy == null || hierarchy.isBlank()) {
				minimum = 1;
				query = filter;
			} else {
				minimum = 2;
				query = "\"$hierarchy:" + hierarchy + "\" & (" + filter + ")";
			}
		}
		final boolean descending;
		final int sortMode;
		final String sourceSingle;
		final String sourceMultiple;
		final String sourceGroupBy;
		if (sort != null && !sort.isBlank()) {
			final List<Object> order = SyntaxQuery.parseOrder(new ArrayList<>(), null, null, sort);
			if (order == null || order.isEmpty()) {
				descending = true;
				sortMode = IndexingFinder.SM_NUMERIC;
				sourceSingle = "ix.weight";
				sourceMultiple = "SUM(ix.weight)";
				sourceGroupBy = null;
			} else {
				final OneSort one = (OneSort) order.get(0);
				final String fieldName = Convert.MapEntry.toString(this.replacementFields, one.getField(), null);
				if (fieldName == null) {
					descending = true;
					sortMode = IndexingFinder.SM_NUMERIC;
					sourceSingle = "ix.weight";
					sourceMultiple = "SUM(ix.weight)";
					sourceGroupBy = null;
				} else {
					descending = one.isDescending();
					sortMode = one.isNumeric()
						? IndexingFinder.SM_NUMERIC
						: one.isTextual()
							? IndexingFinder.SM_STR_INS
							: IndexingFinder.SM_STRING;
					sourceSingle = sourceMultiple = sourceGroupBy = fieldName;
				}
			}
		} else {
			descending = true;
			sortMode = IndexingFinder.SM_NUMERIC;
			sourceSingle = "ix.weight";
			sourceMultiple = "SUM(ix.weight)";
			sourceGroupBy = null;
		}
		return this.searchImpl(all, descending, sortMode, sourceSingle, sourceMultiple, sourceGroupBy, query, dateStart, dateEnd, minimum);
	}
	
	/** @param hierarchy
	 * @param all
	 * @param dateStart
	 * @param dateEnd
	 * @param filter
	 * @return map */
	public ExecSearchCalendarProgram searchCalendar(final String hierarchy, final boolean all, final long dateStart, final long dateEnd, final String filter) {

		final String query;
		final int minimum;
		if (filter == null) {
			if (hierarchy == null || hierarchy.isBlank()) {
				return null;
			}
			if (all) {
				minimum = 1;
				query = "\"$hierarchy:" + hierarchy + "\"";
			} else {
				return null;
			}
		} else {
			if (hierarchy == null || hierarchy.isBlank()) {
				minimum = 1;
				query = filter;
			} else {
				minimum = 2;
				query = "\"$hierarchy:" + hierarchy + "\" & (" + filter + ")";
			}
		}
		return this.searchCalendarImpl(all, query, dateStart, dateEnd, minimum);
	}
	
	private final ExecSearchCalendarProgram searchCalendarImpl(final boolean all, final String text, final long dateStart, final long dateStop, final int minimal) {

		final List<Object> parsed = IndexingParser.parse(SyntaxQuery.parseFilter("$text", ":", null, null, true, text), minimal, this.stemmer, this.dictionary);
		if (parsed == null) {
			return null;
		}
		final Condition[][] normalized = IndexingParser.normalize(parsed, 0);
		final OneCondition[][] searchMain = IndexingParser.optimize(normalized[0]);
		final OneCondition[][] searchSub = IndexingParser.optimize(normalized[1]);
		if (Report.MODE_DEBUG) {
			Report.debug(IndexingFinder.OWNER, "SearchCalendar prepare:  phraseMain=" + searchMain + ", phraseSub=" + searchSub);
		}
		if (searchMain == null || searchMain.length == 0) {
			return null;
		}
		// tree map for comparator - order matters
		final Map<Integer, Map<Integer, Map<String, Number>>> resultMap = Create.tempMap();
		final LinkedList<ExecSearchCalendarInstruction> instructions = new LinkedList<>();
		final ExecSearchCalendarProgram result = new ExecSearchCalendarProgram(resultMap, instructions);
		final Calendar calendar = Calendar.getInstance();
		for (int i = searchMain.length - 1; i >= 0; --i) {
			final OneCondition[] condition = searchMain[i];
			if (condition.length == 0) {
				continue;
			}
			this.searchCalendarImplOneOfMultiple(calendar, resultMap, instructions, all, condition, dateStart, dateStop, SearchOperation.FILL);
		}
		if (searchSub != null && searchSub.length != 0) {
			for (int i = searchSub.length - 1; i >= 0; --i) {
				final OneCondition[] condition = searchSub[i];
				if (condition.length == 0) {
					continue;
				}
				this.searchCalendarImplOneOfMultiple(calendar, resultMap, instructions, all, condition, dateStart, dateStop, SearchOperation.CLEAN);
			}
		}
		return result;
	}
	
	private final void searchCalendarImplOneOfMultiple(final Calendar calendar,
			final Map<Integer, Map<Integer, Map<String, Number>>> resultMap,
			final List<ExecSearchCalendarInstruction> instructions,
			final boolean all,
			final OneCondition[] parsed,
			final long dateStart,
			final long dateStop,
			final SearchOperation operation) {

		final StringBuilder query = new StringBuilder(128);
		query.append("SELECT ").append(this.selection_prefix).append(',').append(this.created_field).append(" FROM ").append(this.tnIndices).append(" ix, ").append(
				all
					? this.join_condition_all
					: this.join_condition);
		if (parsed.length > 0) {
			IndexingFinder.addConditionCode(query.append("ix.code IN ("), parsed[0]);
			for (int i = parsed.length - 1; i > 0; --i) {
				IndexingFinder.addConditionCode(query.append(','), parsed[i]);
			}
			query.append(')');
		}
		query.append(')');
		if (dateStart > 0L) {
			query.append(" AND ").append(this.created_field).append(">=?");
		}
		if (dateStop > 0L) {
			query.append(" AND ").append(this.created_field).append("<?");
		}
		if (parsed.length > 1) {
			query.append(" GROUP BY ").append(this.selection_prefix).append(", ").append(this.created_field);
			query.append(" HAVING COUNT(*)=").append(parsed.length);
		}
		instructions.add(new ExecSearchCalendarInstruction(resultMap, calendar, dateStart, dateStop, query.toString(), operation));
	}
	
	private final ExecSearchProgram searchImpl(final boolean all,
			final boolean sortDescending,
			final int sortMode,
			final String sortSourceSingle,
			final String sortSourceMultiple,
			final String sortGroupBy,
			final String text,
			final long dateStart,
			final long dateStop,
			final int minimal) {

		final List<Object> filter = SyntaxQuery.parseFilter("$text", ":", null, null, true, text);
		if (filter == null || filter.isEmpty()) {
			return null;
		}
		final List<Object> parsed = IndexingParser.parse(filter, minimal, this.stemmer, this.dictionary);
		if (parsed == null || parsed.isEmpty()) {
			return null;
		}
		final Condition[][] normalized = IndexingParser.normalize(parsed, 0);
		final OneCondition[][] searchMain = IndexingParser.optimize(normalized[0]);
		final OneCondition[][] searchSub = IndexingParser.optimize(normalized[1]);
		if (Report.MODE_DEBUG) {
			Report.debug(
					IndexingFinder.OWNER,
					"Search prepare: sort_desc=" + sortDescending + ", sort_mode=" + IndexingFinder.SORT_MODES[sortMode] + ", sort_fld=" + sortSourceSingle + ", phraseMain="
							+ searchMain + ", phraseSub=" + searchSub);
		}
		if (searchMain == null || searchMain.length == 0) {
			return null;
		}
		// hashmap faster, order doesn't matter, no special comparator
		final Map<String, Object> resultMap = new HashMap<>();
		final LinkedList<ExecSearchInstruction> instructions = new LinkedList<>();
		final ExecSearchProgram result = new ExecSearchProgram(resultMap, instructions, sortMode, sortDescending);
		for (int i = searchMain.length - 1; i >= 0; --i) {
			final OneCondition[] condition = searchMain[i];
			if (condition.length == 0) {
				continue;
			}
			this.searchImplOneOfMultiple(
					resultMap,
					instructions,
					all,
					condition,
					sortSourceSingle,
					sortSourceMultiple,
					sortGroupBy,
					sortMode,
					sortDescending,
					dateStart,
					dateStop,
					SearchOperation.FILL);
		}
		if (searchSub != null && searchSub.length != 0) {
			for (int i = searchSub.length - 1; i >= 0; --i) {
				final OneCondition[] condition = searchSub[i];
				if (condition.length == 0) {
					continue;
				}
				this.searchImplOneOfMultiple(
						resultMap,
						instructions,
						all,
						condition,
						sortSourceSingle,
						sortSourceMultiple,
						sortGroupBy,
						sortMode,
						sortDescending,
						dateStart,
						dateStop,
						SearchOperation.CLEAN);
			}
		}
		return result;
	}
	
	private final void searchImplOneOfMultiple(final Map<String, Object> resultMap,
			final List<ExecSearchInstruction> instructions,
			final boolean all,
			final OneCondition[] parsed,
			final String sortSourceSingle,
			final String sortSourceMultiple,
			final String sortGroupBy,
			final int sortMode,
			final boolean sortDescending,
			final long dateStart,
			final long dateStop,
			final SearchOperation operation) {

		final StringBuilder query = new StringBuilder(128);
		query.append("SELECT ").append(this.selection_prefix).append(',').append(
				parsed.length > 1
					? sortSourceMultiple
					: sortSourceSingle)
				.append(" as sm FROM ").append(this.tnIndices).append(" ix, ").append(
						all
							? this.join_condition_all
							: this.join_condition);
		if (parsed.length > 0) {
			IndexingFinder.addConditionCode(query.append("ix.code IN ("), parsed[0]);
			for (int i = parsed.length - 1; i > 0; --i) {
				IndexingFinder.addConditionCode(query.append(','), parsed[i]);
			}
			query.append(')');
		}
		query.append(')');
		if (dateStart > 0L) {
			query.append(" AND ").append(this.created_field).append(">=?");
		}
		if (dateStop > 0L) {
			query.append(" AND ").append(this.created_field).append("<?");
		}
		if (parsed.length > 1) {
			query.append(" GROUP BY ").append(this.selection_prefix);
			if (sortGroupBy != null) {
				query.append(", ").append(sortGroupBy);
			}
			query.append(" HAVING COUNT(*)=").append(parsed.length);
		}
		if (operation.needOrderBy()) {
			query.append(
					sortDescending
						? " ORDER BY sm DESC"
						: " ORDER BY sm ASC");
		}
		instructions.add(new ExecSearchInstruction(resultMap, dateStart, dateStop, sortMode, query.toString(), operation));
	}
}
