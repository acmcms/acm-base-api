package ru.myx.ae3.cache;

import ru.myx.ae3.Engine;
import ru.myx.ae3.report.Report;

/** Title: ae1 Base definitions Description: Copyright: Copyright (c) 2001 Company:
 *
 * @author Alexander I. Kharitchev
 * @version 1.0
 * @param <V> */
public final class Cache3<V> implements CacheL3<V> {
	
	private static final String OWNER = "CACHE-L3";
	
	private static final Cache1Stats DEFAULT_CACHE_GROUP_STATS = new Cache1Stats();
	
	private static final int TABLE_SIZE = Engine.MODE_SIZE
		? 16
		: 32;
	
	private static final int CUTTER = Cache3.TABLE_SIZE - 1;
	
	private final Cache1Stats stats = Cache3.DEFAULT_CACHE_GROUP_STATS;
	
	private int stTest = 0;
	
	private int stHits = 0;
	
	private int stMiss = 0;
	
	private int stExpd = 0;
	
	private int stPuts = 0;
	
	private int stRepl = 0;
	
	private int stRttl = 0;
	
	private int stRmvs = 0;
	
	private int stRacc = 0;
	
	int lTest = 0;
	
	int lHits = 0;
	
	int lMiss = 0;
	
	int lExpd = 0;
	
	int lPuts = 0;
	
	int lRepl = 0;
	
	int lRttl = 0;
	
	int lRmvs = 0;
	
	int lRacc = 0;
	
	private final Cache3Leaf<V>[] data;
	
	private final int tableSize;
	
	private final int cutter;
	
	int size = 0;
	
	int leafCount = 0;
	
	int leafCapacity = 0;
	
	/**
	 */
	@SuppressWarnings("unchecked")
	Cache3() {
		
		this.tableSize = Cache3.TABLE_SIZE;
		this.cutter = Cache3.CUTTER;
		this.data = new Cache3Leaf[this.tableSize];
		for (int i = this.tableSize - 1; i >= 0; --i) {
			this.data[i] = new Cache3Leaf<>(this, Integer.toString(i, 36));
		}

		MaintainerRT3.registerManager(this);
	}
	
	@Override
	public final void clear() {
		
		final Cache3Leaf<V>[] data = this.data;
		for (int i = this.tableSize - 1; i >= 0; --i) {
			final Cache3Leaf<V> leaf = data[i];
			if (leaf != null) {
				if (Report.MODE_DEBUG) {
					Report.event("CACHE", "CLEAR", "Cleaning leaf (fixed), leaf=" + leaf + "...");
				}
				leaf.clear();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final CacheL2<V> getCacheL2(final Object group) {
		
		final String key = group.toString();
		return this.data[key.hashCode() & this.cutter].get(key, key);
	}
	
	final void maintenance() {
		
		if (Report.MODE_DEVEL) {
			Report.event(Cache3.OWNER, "MAINTENANCE", "CacheL3 maintenance started...");
		}
		synchronized (this.stats) {
			this.stats.stsHits += this.lHits;
			this.stHits += this.lHits;
			this.lHits = 0;
			this.stats.stsMiss += this.lMiss;
			this.stMiss += this.lMiss;
			this.lMiss = 0;
			this.stats.stsTest += this.lTest;
			this.stTest += this.lTest;
			this.lTest = 0;
			this.stats.stsExpd += this.lExpd;
			this.stExpd += this.lExpd;
			this.lExpd = 0;
			this.stats.stsPuts += this.lPuts;
			this.stPuts += this.lPuts;
			this.lPuts = 0;
			this.stats.stsRepl += this.lRepl;
			this.stRepl += this.lRepl;
			this.lRepl = 0;
			this.stats.stsRmvs += this.lRmvs;
			this.stRmvs += this.lRmvs;
			this.lRmvs = 0;
		}
		if (Report.MODE_DEVEL) {
			Report.event(Cache3.OWNER, "MAINTENANCE", "Cleaning leafs...");
		}
		final Cache3Leaf<V>[] data = this.data;
		for (int i = this.tableSize - 1; i >= 0; --i) {
			final Cache3Leaf<V> leaf = data[i];
			if (Report.MODE_DEVEL) {
				Report.event(Cache3.OWNER, "MAINTENANCE", "leaf maintenance (fixed), leaf=" + leaf + "...");
			}
			leaf.maintenance();
		}
		if (Report.MODE_DEVEL) {
			Report.event(Cache3.OWNER, "MAINTENANCE", "Updating leaf statistics...");
		}
		synchronized (this.stats) {
			this.stats.stsRttl += this.lRttl;
			this.stRttl += this.lRttl;
			this.lRttl = 0;
			this.stats.stsRacc += this.lRacc;
			this.stRacc += this.lRacc;
			this.lRacc = 0;
		}
		if (Report.MODE_DEVEL) {
			Report.event(Cache3.OWNER, "MAINTENANCE", "CacheL3 maintenance done.");
		}
	}
}
