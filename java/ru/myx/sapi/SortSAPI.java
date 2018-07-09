/*
 * Created on 14.10.2003 To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.sapi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.ecma.compare.ComparatorEcma;

/** @author myx
 *
 *         To change the template for this generated type comment go to Window>Preferences>Java>Code
 *         Generation>Code and Comments */
public class SortSAPI {

	static final class ComparatorDescending implements Comparator<Object> {
		
		@Override
		public final int compare(final Object o1, final Object o2) {
			
			return -SortSAPI.ASCENDING.compare(o1, o2);
		}
	}

	static final class ComparatorEntryCreated implements Comparator<BaseEntry<?>> {
		
		@Override
		public final int compare(final BaseEntry<?> o1, final BaseEntry<?> o2) {
			
			final long v1 = o1.getCreated();
			final long v2 = o2.getCreated();
			return v1 > v2
				? 1
				: v1 < v2
					? -1
					: 0;
		}
	}

	static final class ComparatorEntryCreatedDescending implements Comparator<BaseEntry<?>> {
		
		@Override
		public final int compare(final BaseEntry<?> o1, final BaseEntry<?> o2) {
			
			final long v1 = o1.getCreated();
			final long v2 = o2.getCreated();
			return v1 > v2
				? -1
				: v1 < v2
					? 1
					: 0;
		}
	}

	static final class ComparatorEntryTitle implements Comparator<BaseEntry<?>> {
		
		@Override
		public final int compare(final BaseEntry<?> o1, final BaseEntry<?> o2) {
			
			return o1.getTitle().compareTo(o2.getTitle());
		}
	}

	static final class ComparatorEntryTitleDescending implements Comparator<BaseEntry<?>> {
		
		@Override
		public final int compare(final BaseEntry<?> o1, final BaseEntry<?> o2) {
			
			return o2.getTitle().compareTo(o1.getTitle());
		}
	}

	static final class ComparatorMapKey implements Comparator<Map.Entry<?, ?>> {
		
		@Override
		public final int compare(final Map.Entry<?, ?> o1, final Map.Entry<?, ?> o2) {
			
			return SortSAPI.ASCENDING.compare(
					o1 == null
						? null
						: o1.getKey(),
					o2 == null
						? null
						: o2.getKey());
		}
	}

	static final class ComparatorMapKeyDescending implements Comparator<Map.Entry<?, ?>> {
		
		@Override
		public final int compare(final Map.Entry<?, ?> o1, final Map.Entry<?, ?> o2) {
			
			return -SortSAPI.ASCENDING.compare(
					o1 == null
						? null
						: o1.getKey(),
					o2 == null
						? null
						: o2.getKey());
		}
	}

	static final class ComparatorMapKeyNumeric implements Comparator<Map.Entry<? extends Number, ?>> {
		
		@Override
		public final int compare(final Map.Entry<? extends Number, ?> o1, final Map.Entry<? extends Number, ?> o2) {
			
			final double v1 = o1.getKey().doubleValue();
			final double v2 = o2.getKey().doubleValue();
			return v1 > v2
				? 1
				: v1 < v2
					? -1
					: 0;
		}
	}

	static final class ComparatorMapKeyNumericDescending implements Comparator<Map.Entry<? extends Number, ?>> {
		
		@Override
		public final int compare(final Map.Entry<? extends Number, ?> o1, final Map.Entry<? extends Number, ?> o2) {
			
			final double v1 = o1.getKey().doubleValue();
			final double v2 = o2.getKey().doubleValue();
			return v1 > v2
				? -1
				: v1 < v2
					? 1
					: 0;
		}
	}

	static final class ComparatorMapKeyText implements Comparator<Map.Entry<?, ?>> {
		
		@Override
		public final int compare(final Map.Entry<?, ?> o1, final Map.Entry<?, ?> o2) {
			
			final Object v1 = o1.getKey();
			final Object v2 = o2.getKey();
			final String s1 = v1 == null
				? ""
				: v1.toString();
			final String s2 = v2 == null
				? ""
				: v2.toString();
			return s1.compareTo(s2);
		}
	}

	static final class ComparatorMapKeyTextDescending implements Comparator<Map.Entry<?, ?>> {
		
		@Override
		public final int compare(final Map.Entry<?, ?> o1, final Map.Entry<?, ?> o2) {
			
			final Object v1 = o1.getKey();
			final Object v2 = o2.getKey();
			final String s1 = v1 == null
				? ""
				: v1.toString();
			final String s2 = v2 == null
				? ""
				: v2.toString();
			return s2.compareTo(s1);
		}
	}

	static final class ComparatorMapValue implements Comparator<Map.Entry<?, ?>> {
		
		@Override
		public final int compare(final Map.Entry<?, ?> o1, final Map.Entry<?, ?> o2) {
			
			return SortSAPI.ASCENDING.compare(
					o1 == null
						? null
						: o1.getValue(),
					o2 == null
						? null
						: o2.getValue());
		}
	}

	static final class ComparatorMapValueDescending implements Comparator<Map.Entry<?, ?>> {
		
		@Override
		public final int compare(final Map.Entry<?, ?> o1, final Map.Entry<?, ?> o2) {
			
			return -SortSAPI.ASCENDING.compare(
					o1 == null
						? null
						: o1.getValue(),
					o2 == null
						? null
						: o2.getValue());
		}
	}

	static final class ComparatorMapValueNumeric implements Comparator<Map.Entry<?, ? extends Number>> {
		
		@Override
		public final int compare(final Map.Entry<?, ? extends Number> o1, final Map.Entry<?, ? extends Number> o2) {
			
			final double v1 = o1.getValue().doubleValue();
			final double v2 = o2.getValue().doubleValue();
			return v1 > v2
				? 1
				: v1 < v2
					? -1
					: 0;
		}
	}

	static final class ComparatorMapValueNumericDescending implements Comparator<Map.Entry<?, ? extends Number>> {
		
		@Override
		public final int compare(final Map.Entry<?, ? extends Number> o1, final Map.Entry<?, ? extends Number> o2) {
			
			final double v1 = o1.getValue().doubleValue();
			final double v2 = o2.getValue().doubleValue();
			return v1 > v2
				? -1
				: v1 < v2
					? 1
					: 0;
		}
	}

	static final class ComparatorMapValueText implements Comparator<Map.Entry<?, ?>> {
		
		@Override
		public final int compare(final Map.Entry<?, ?> o1, final Map.Entry<?, ?> o2) {
			
			final Object v1 = o1.getValue();
			final Object v2 = o2.getValue();
			final String s1 = v1 == null
				? ""
				: v1.toString();
			final String s2 = v2 == null
				? ""
				: v2.toString();
			return s1.compareTo(s2);
		}
	}

	static final class ComparatorMapValueTextDescending implements Comparator<Map.Entry<?, ?>> {
		
		@Override
		public final int compare(final Map.Entry<?, ?> o1, final Map.Entry<?, ?> o2) {
			
			final Object v1 = o1.getValue();
			final Object v2 = o2.getValue();
			final String s1 = v1 == null
				? ""
				: v1.toString();
			final String s2 = v2 == null
				? ""
				: v2.toString();
			return s2.compareTo(s1);
		}
	}

	static final class ComparatorNumeric implements Comparator<Number> {
		
		@Override
		public final int compare(final Number o1, final Number o2) {
			
			final double v1 = o1.doubleValue();
			final double v2 = o2.doubleValue();
			return v1 > v2
				? 1
				: v1 < v2
					? -1
					: 0;
		}
	}

	static final class ComparatorNumericDescending implements Comparator<Number> {
		
		@Override
		public final int compare(final Number o1, final Number o2) {
			
			final double v1 = o1.doubleValue();
			final double v2 = o2.doubleValue();
			return v1 > v2
				? -1
				: v1 < v2
					? 1
					: 0;
		}
	}

	static final class ComparatorText implements Comparator<Object> {
		
		@Override
		public final int compare(final Object o1, final Object o2) {
			
			final String s1 = o1 == null
				? ""
				: o1.toString();
			final String s2 = o2 == null
				? ""
				: o2.toString();
			return s1.compareTo(s2);
		}
	}

	static final class ComparatorTextDescending implements Comparator<Object> {
		
		@Override
		public final int compare(final Object o1, final Object o2) {
			
			final String s1 = o1 == null
				? ""
				: o1.toString();
			final String s2 = o2 == null
				? ""
				: o2.toString();
			return s2.compareTo(s1);
		}
	}

	/**
	 *
	 */
	public static final Comparator<Object> ASCENDING = ComparatorEcma.INSTANCE;

	/**
	 *
	 */
	public static final Comparator<Object> DESCENDING = new ComparatorDescending();

	/**
	 *
	 */
	public static final Comparator<? extends BaseEntry<?>> ENTRY_CREATED = new ComparatorEntryCreated();

	/**
	 *
	 */
	public static final Comparator<? extends BaseEntry<?>> ENTRY_CREATED_DESC = new ComparatorEntryCreatedDescending();

	/**
	 *
	 */
	public static final Comparator<? extends BaseEntry<?>> ENTRY_TITLE = new ComparatorEntryTitle();

	/**
	 *
	 */

	public static final Comparator<? extends BaseEntry<?>> ENTRY_TITLE_DESC = new ComparatorEntryTitleDescending();

	/**
	 *
	 */
	public static final Comparator<Map.Entry<?, ?>> MAP_KEY = new ComparatorMapKey();

	/**
	 *
	 */
	public static final Comparator<Map.Entry<?, ?>> MAP_KEY_DESC = new ComparatorMapKeyDescending();

	/**
	 *
	 */
	public static final Comparator<Map.Entry<? extends Number, ?>> MAP_KEY_NUMERIC = new ComparatorMapKeyNumeric();

	/**
	 *
	 */
	public static final Comparator<Map.Entry<? extends Number, ?>> MAP_KEY_NUMERIC_DESC = new ComparatorMapKeyNumericDescending();

	/**
	 *
	 */
	public static final Comparator<Map.Entry<?, ?>> MAP_KEY_TEXT = new ComparatorMapKeyText();

	/**
	 *
	 */
	public static final Comparator<Map.Entry<?, ?>> MAP_KEY_TEXT_DESC = new ComparatorMapKeyTextDescending();

	/**
																							 *
																							 */
	public static final Comparator<Map.Entry<?, ?>> MAP_VALUE = new ComparatorMapValue();

	/**
																							 *
																							 */
	public static final Comparator<Map.Entry<?, ?>> MAP_VALUE_DESC = new ComparatorMapValueDescending();

	/**
	 *
	 */
	public static final Comparator<Map.Entry<?, ? extends Number>> MAP_VALUE_NUMERIC = new ComparatorMapValueNumeric();

	/**
	 *
	 */
	public static final Comparator<Map.Entry<?, ? extends Number>> MAP_VALUE_NUMERIC_DESC = new ComparatorMapValueNumericDescending();

	/**
	 *
	 */
	public static final Comparator<Map.Entry<?, ?>> MAP_VALUE_TEXT = new ComparatorMapValueText();

	/**
	 *
	 */
	public static final Comparator<Map.Entry<?, ?>> MAP_VALUE_TEXT_DESC = new ComparatorMapValueTextDescending();

	/**
	 *
	 */
	public static final Comparator<?> NUMERIC = new ComparatorNumeric();

	/**
	 *
	 */
	public static final Comparator<?> NUMERIC_DESC = new ComparatorNumericDescending();

	/**
	 *
	 */
	public static final Comparator<?> TEXT = new ComparatorText();

	/**
	 *
	 */
	public static final Comparator<?> TEXT_DESC = new ComparatorTextDescending();
	
	/** @param collection
	 * @param comparator
	 * @return array */
	public final static Object[] array(final Collection<Object> collection, final Comparator<Object> comparator) {
		
		if (collection == null) {
			return null;
		}
		final int size = collection.size();
		final Object[] array = collection.toArray(new Object[size]);
		if (size > 1) {
			Arrays.sort(
					array,
					comparator == null
						? ComparatorEcma.INSTANCE
						: comparator);
		}
		return array;
	}
	
	/** @param array
	 * @param comparator
	 * @return array */
	public final static Object[] array(final Object[] array, final Comparator<Object> comparator) {
		
		if (array == null) {
			return null;
		}
		if (array.length > 1) {
			Arrays.sort(
					array,
					comparator == null
						? ComparatorEcma.INSTANCE
						: comparator);
		}
		return array;
	}
	
	/** @param collection
	 * @param comparator
	 * @return collection */
	public final static Collection<?> collection(final Collection<Object> collection, final Comparator<Object> comparator) {
		
		if (collection == null) {
			return null;
		}
		final int size = collection.size();
		final Object[] array = collection.toArray(new Object[size]);
		if (size > 1) {
			Arrays.sort(
					array,
					comparator == null
						? ComparatorEcma.INSTANCE
						: comparator);
		}
		return Arrays.asList(array);
	}
	
	/** @param collection
	 * @param comparator
	 * @return collection */
	public final static Collection<?> collection(final Object[] collection, final Comparator<Object> comparator) {
		
		if (collection == null) {
			return null;
		}
		final int size = collection.length;
		final Object[] array = new Object[size];
		if (size > 0) {
			System.arraycopy(collection, 0, array, 0, size);
			if (size > 1) {
				Arrays.sort(
						array,
						comparator == null
							? ComparatorEcma.INSTANCE
							: comparator);
			}
		}
		return Arrays.asList(array);
	}
	
	/** @param map
	 * @param comparator
	 * @return */
	public final static Map<String, Object> map(final Map<String, Object> map, final Comparator<Map.Entry<?, ?>> comparator) {
		
		if (map == null) {
			return null;
		}
		final Set<Map.Entry<String, Object>> set = new TreeSet<>(
				comparator == null
					? SortSAPI.MAP_VALUE
					: comparator);
		for (final Map.Entry<String, Object> entry : map.entrySet()) {
			final String key = entry.getKey();
			if ("$ORDER".equals(key)) {
				continue;
			}
			set.add(entry);
		}
		final Map<String, Object> result = new BaseNativeObject();
		final List<String> order = BaseObject.createArray(set.size());
		for (final Map.Entry<String, Object> entry : set) {
			final String key = entry.getKey();
			order.add(key);
			result.put(key, entry.getValue());
		}
		result.put("$ORDER", order);
		return result;
	}
}
