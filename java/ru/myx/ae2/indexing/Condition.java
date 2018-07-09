package ru.myx.ae2.indexing;

import java.util.Comparator;
import java.util.TreeSet;

import ru.myx.query.OneCondition;

/**
 * @author myx
 */
public final class Condition extends TreeSet<OneCondition> {
	static final class ComparatorConditions implements Comparator<Condition> {
		@Override
		public int compare(final Condition o1, final Condition o2) {
			if (o2 == null) {
				return o1 == null
						? 0
						: -1;
			} else //
			if (o1 == null) {
				/**
				 * o2 != null
				 */
				return 1;
			}
			return o2.inexact - o1.inexact;
		}
	}
	
	private static final long					serialVersionUID		= 3617572682846123828L;
	
	/**
	 * compares conditions by inexact keyword count
	 */
	public static final Comparator<Condition>	COMPARATOR_CONDITIONS	= new ComparatorConditions();
	
	/**
	 * 
	 */
	public short								inexact					= 0;
	
	@Override
	public boolean add(final OneCondition o) {
		if (super.add( o )) {
			if (!o.isExact()) {
				this.inexact++;
			}
			return true;
		}
		return false;
	}
}
