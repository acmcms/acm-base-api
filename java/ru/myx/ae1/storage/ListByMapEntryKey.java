package ru.myx.ae1.storage;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Title: Xml Document Management for WSM3 Description: Copyright: Copyright (c) 2001
 *
 * FIXME: implement BaseArray in any way
 *
 * @author Alexander I. Kharitchev
 * @version 1.0
 * @param <R>
 *            list item class */
public final class ListByMapEntryKey<R> extends AbstractList<R> {

	private final Map.Entry<String, Object>[] ids;

	private final Function<String, ? extends R> storage;

	private final int start;

	private final int end;

	/** @param ids
	 * @param storage
	 */
	public ListByMapEntryKey(final Map.Entry<String, Object>[] ids, final Function<String, ? extends R> storage) {

		this.ids = ids;
		this.storage = storage;
		this.start = 0;
		this.end = ids.length;
	}

	private ListByMapEntryKey(final Map.Entry<String, Object>[] ids, final int start, final int end, final Function<String, ? extends R> storage) {

		this.ids = ids;
		this.storage = storage;
		this.start = start;
		this.end = end;
	}

	@Override
	public final boolean equals(final Object o) {

		if (o == this) {
			return true;
		}
		if (!(o instanceof ListByMapEntryKey<?>)) {
			return false;
		}
		final ListByMapEntryKey<?> list = (ListByMapEntryKey<?>) o;

		final int start1 = this.start;
		final int start2 = list.start;
		final int end1 = this.end;
		final int end2 = list.end;

		if (end1 - start1 != end2 - start2) {
			return false;
		}

		for (int i = end1 - start1 - 1; i >= 0; --i) {
			if (!this.ids[start1 + i].equals(list.ids[start2 + i])) {
				return false;
			}
		}

		return true;
	}

	@Override
	public final R get(final int index) {

		try {
			return this.storage.apply(this.ids[this.start + index].getKey());
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public final int hashCode() {

		final int start = this.start;
		final int end = this.end;
		int hashCode = 1;
		final Object[] ids = this.ids;
		for (int i = start; i < end; ++i) {
			final Object obj = ids[i];
			hashCode = 31 * hashCode + (obj == null
				? 0
				: obj.hashCode());
		}
		return hashCode;
	}

	@Override
	public final int size() {

		return this.end - this.start;
	}

	@Override
	public final List<R> subList(final int startPosition, final int endPosition) {

		return new ListByMapEntryKey<>(this.ids, this.start + startPosition, this.start + endPosition, this.storage);
	}

	@Override
	public final String toString() {

		final int start = this.start;
		final int end = this.end;
		final int size = end - start;
		final StringBuilder builder = new StringBuilder((size + 1) * 48);
		builder.append('[');
		if (size > 0) {
			final Map.Entry<String, Object>[] ids = this.ids;
			builder.append(ids[start].getKey());
			if (size > 1) {
				for (int i = start + 1; i < end; ++i) {
					builder.append(',');
					builder.append(ids[i].getKey());
				}
			}
		}
		builder.append(']');
		return builder.toString();
	}
}
