/*
 * Created on 28.12.2005
 */
package ru.myx.ae3.cache;

import ru.myx.ae3.Engine;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.report.Report;
import ru.myx.util.QueueStackRecord;

final class Cache3Leaf<V> {
	
	private static final String OWNER = "CACHE3-LEAF";

	private static final int LEAF_INITIAL_CAPACITY = 4;

	private final Cache3<V> cache;

	private volatile Cache3Entry[] data;

	private int dataSize;

	private int dataLength;

	private int dataCapacity;

	private final String description;

	Cache3Leaf(final Cache3<V> cache, final String description) {
		
		this.cache = cache;
		this.data = new Cache3Entry[Cache3Leaf.LEAF_INITIAL_CAPACITY];
		this.dataSize = 0;
		this.dataLength = 0;
		this.dataCapacity = Cache3Leaf.LEAF_INITIAL_CAPACITY;
		this.description = description;
		this.cache.leafCount++;
		this.cache.leafCapacity += Cache3Leaf.LEAF_INITIAL_CAPACITY;
	}

	/**
	 *
	 */
	public final void clear() {
		
		synchronized (this) {
			if (Report.MODE_DEBUG) {
				Report.event(
						Cache3Leaf.OWNER,
						"CLEAR_GROUPS",
						"Starting group clear (cap=" + this.dataCapacity + ", dsize=" + this.dataSize + ", size=" + this.cache.size + ")...");
			}
			for (int i = this.dataCapacity - 1; i >= 0; --i) {
				this.data[i] = null;
			}
		}
	}

	@Override
	public final String toString() {
		
		return Cache3Leaf.OWNER + ": " + this.description;
	}

	/** @param key
	 * @param creationKey
	 * @param creator
	 * @return entry */
	final Cache2<V> get(final String key, final String creationKey) {
		
		if (this.dataSize == 0) {
			final Cache3Lock lock;
			synchronized (this) {
				if (this.dataSize == 0) {
					this.cache.lMiss++;
					lock = new Cache3Lock(key);
					this.put(key, lock);
				} else {
					lock = null;
				}
			}
			if (lock != null) {
				try {
					final Cache2<V> result = new Cache2<>(
							key.length() > 60
								? key.substring(0, 60)
								: key, //
							key,
							CacheType.NORMAL_JAVA_SOFT);
					this.put(key, result);
					lock.setResult(result);
					return result;
				} finally {
					lock.setFinally();
				}
			}
		}
		final int dataLength = this.dataLength;
		final Cache3Entry[] data = this.data;
		for (int i = 0; i < dataLength; ++i) {
			final Cache3Entry entry = data[i];
			if (entry != null && entry.getKey().equals(key)) {
				this.cache.lTest += i + 1;
				this.cache.lHits++;
				final Cache3Entry real;
				if (entry instanceof Cache3Lock) {
					real = ((Cache3Lock) entry).getValue();
					if (real == null) {
						return null;
					}
				} else {
					real = entry;
				}
				real.setAccessed();
				return Convert.Any.toAny(real);
			}
		}
		this.cache.lTest += dataLength;
		Cache3Entry found = null;
		Cache3Lock lock = null;
		synchronized (this) {
			final int dataLength2 = this.dataLength;
			final Cache3Entry[] data2 = this.data;
			if (dataLength2 != dataLength || data2 != data) {
				for (int i = 0; i < dataLength2; ++i) {
					final Cache3Entry entry = data[i];
					if (entry != null && entry.getKey().equals(key)) {
						this.cache.lTest += i + 1;
						this.cache.lHits++;
						found = entry;
						break;
					}
				}
			}
			if (found == null) {
				this.cache.lTest += dataLength2;
				this.cache.lMiss++;
				lock = new Cache3Lock(key);
				this.put(key, lock);
			}
		}
		if (lock == null) {
			final Cache3Entry real;
			if (found instanceof Cache3Lock) {
				real = ((Cache3Lock) found).getValue();
			} else {
				real = found;
			}
			if (real == null) {
				return null;
			}
			real.setAccessed();
			return Convert.Any.toAny(real);
		}
		try {
			final Cache2<V> result = new Cache2<>(
					key.length() > 60
						? key.substring(0, 60)
						: key, //
					key,
					CacheType.NORMAL_JAVA_SOFT);
			this.put(key, result);
			lock.setResult(result);
			return result;
		} finally {
			lock.setFinally();
		}
	}

	final void maintenance() {
		
		QueueStackRecord<Cache2<?>> removed = null;
		synchronized (this) {
			if (Report.MODE_DEVEL) {
				Report.event(
						Cache3Leaf.OWNER,
						"MAINTENANCE",
						"Starting maintenance (fixed) (cap=" + this.dataCapacity + ", dsize=" + this.dataSize + ", size=" + this.cache.size + ")...");
			}
			final long deadDate = Engine.fastTime() - 60_000L * 30L;
			for (int i = 0; i < this.dataCapacity; ++i) {
				final Cache3Entry current = this.data[i];
				if (current == null) {
					continue;
				}
				try {
					if (current.getAccessed() < deadDate) {
						if (Report.MODE_DEBUG) {
							Report.event(Cache3Leaf.OWNER, "MAINTENANCE", "discard: access time.");
						}
						if (current instanceof Cache2) {
							if (removed == null) {
								removed = new QueueStackRecord<>();
							}
							removed.enqueue((Cache2<?>) current);
						}
						this.data[i] = null;
						this.dataSize--;
						this.cache.size--;
						this.cache.lRacc++;
						if (this.dataSize == 0) {
							this.dataLength = 0;
							if (this.dataCapacity > Cache3Leaf.LEAF_INITIAL_CAPACITY) {
								this.data = new Cache3Entry[Cache3Leaf.LEAF_INITIAL_CAPACITY];
								this.dataCapacity = Cache3Leaf.LEAF_INITIAL_CAPACITY;
							}
							break;
						}
						if (i + 1 == this.dataLength) {
							for (; this.data[i] == null; --i) {
								this.dataLength--;
							}
						}
						continue;
					}
				} catch (final Throwable t) {
					Report.exception(Cache3Leaf.OWNER, "Error while doing maintenance", t);
				}
			}
		}
		if (removed != null) {
			for (;;) {
				final Cache2<?> next = removed.next();
				if (next == null) {
					break;
				}
				next.clear();
			}
		}
	}

	final void put(final String key, final Cache3Entry object) {
		
		if (object == null) {
			this.remove(key);
			return;
		}
		synchronized (this) {
			this.cache.lPuts++;
			if (this.dataSize > 0) {
				for (int i = this.dataCapacity - 1; i >= 0; --i) {
					final Cache3Entry entry = this.data[i];
					if (entry != null && entry.getKey().equals(key)) {
						this.data[i] = object;
						this.cache.lRepl++;
						return;
					}
				}
			}
			if (this.dataSize + 1 > this.dataCapacity) {
				final int step = this.dataCapacity;
				final Cache3Entry[] newData = new Cache3Entry[this.dataCapacity + step];
				System.arraycopy(this.data, 0, newData, 0, this.dataCapacity);
				this.data = newData;
				this.dataCapacity += step;
				this.cache.leafCapacity += step;
				this.cache.lExpd++;
			}
			for (int i = 0; i < this.dataCapacity; ++i) {
				if (this.data[i] == null) {
					this.data[i] = object;
					this.dataSize++;
					this.cache.size++;
					if (this.dataLength <= i) {
						this.dataLength = i + 1;
					}
					break;
				}
			}
		}
	}

	final void remove(final String key) {
		
		synchronized (this) {
			for (int i = 0; i < this.dataLength; ++i) {
				final Cache3Entry entry = this.data[i];
				if (entry != null && entry.getKey().equals(key)) {
					this.data[i] = null;
					this.dataSize--;
					this.cache.size--;
					this.cache.lRmvs++;
					if (this.dataSize == 0) {
						this.dataLength = 0;
					} else {
						if (i + 1 == this.dataLength) {
							for (; this.data[i] == null; --i) {
								this.dataLength--;
							}
						}
					}
					return;
				}
			}
		}
	}
}
