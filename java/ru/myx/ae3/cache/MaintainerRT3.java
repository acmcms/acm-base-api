/*
 * Created on 26.03.2006
 */
package ru.myx.ae3.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.myx.ae3.act.Act;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.status.StatusInfo;
import ru.myx.ae3.status.StatusProvider;
import ru.myx.ae3.status.StatusRegistry;

final class MaintainerRT3 implements Runnable {
	
	static final Runnable MAINTENANCE_JOB;
	
	static final ReferenceQueue<Object> MANAGER_QUEUE = new ReferenceQueue<>();
	static final Set<Reference<?>> MANAGER_SET = new HashSet<>();

	static {
		Act.later(null, MAINTENANCE_JOB = new MaintainerRT3(), 20000);
		StatusRegistry.ROOT_REGISTRY.register(new StatusProvider() {
			
			@Override
			public StatusProvider[] childProviders() {
				
				if (MaintainerRT3.MANAGER_SET.size() > 0) {
					final List<StatusProvider> result = new ArrayList<>();
					final WeakReference<?>[] references;
					synchronized (MaintainerRT3.MANAGER_SET) {
						references = MaintainerRT3.MANAGER_SET.toArray(new WeakReference[MaintainerRT3.MANAGER_SET.size()]);
					}
					for (final WeakReference<?> reference : references) {
						final Object o = reference.get();
						if (o == null) {
							synchronized (MaintainerRT3.MANAGER_SET) {
								MaintainerRT3.MANAGER_SET.remove(reference);
							}
							continue;
						}
						if (o instanceof Cache3) {
							// ((cacheGroup)o).clear();
							continue;
						}
					}
					if (result.size() > 0) {
						return result.toArray(new StatusProvider[result.size()]);
					}
				}
				return null;
			}
			
			@Override
			public String statusDescription() {
				
				return "CacheManager status";
			}
			
			@Override
			public void statusFill(final StatusInfo data) {
				
				data.put("total created", Holder.ohCreated);
				data.put("total finalized", Holder.ohFinalized);
				data.put("current count", Holder.ohCurrent);
				data.put("total file based failed", Holder.ohtFiled - Holder.ohFiled);
				data.put("total file based entries", Holder.ohFiled);
				data.put("Cache managers", MaintainerRT3.MANAGER_SET.size());
			}
			
			@Override
			public String statusName() {
				
				return "cache";
			}
		});
	}

	/** @param cache */
	protected static void registerManager(final Object cache) {
		
		if (cache == null) {
			throw new NullPointerException("cache object is null!");
		}
		final WeakReference<Object> reference = new WeakReference<>(cache, MaintainerRT3.MANAGER_QUEUE);
		synchronized (MaintainerRT3.MANAGER_SET) {
			MaintainerRT3.MANAGER_SET.add(reference);
		}
	}
	
	@Override
	public final void run() {
		
		try {
			for (;;) {
				final Reference<?> removed = MaintainerRT3.MANAGER_QUEUE.poll();
				if (removed == null) {
					break;
				}
				synchronized (MaintainerRT3.MANAGER_SET) {
					MaintainerRT3.MANAGER_SET.remove(removed);
				}
			}
			if (MaintainerRT3.MANAGER_SET.size() > 0) {
				final WeakReference<?>[] ms;
				synchronized (MaintainerRT3.MANAGER_SET) {
					ms = MaintainerRT3.MANAGER_SET.toArray(new WeakReference[MaintainerRT3.MANAGER_SET.size()]);
				}
				for (int i = ms.length - 1; i >= 0; --i) {
					final Object o = ms[i].get();
					if (o == null) {
						synchronized (MaintainerRT3.MANAGER_SET) {
							MaintainerRT3.MANAGER_SET.remove(ms[i]);
						}
						continue;
					}
					if (o instanceof Cache3) {
						try {
							((Cache3<?>) o).maintenance();
						} catch (final Throwable t) {
							Report.exception("CACHE_MAN", "While performing maintenance on Cache3", t);
						}
						continue;
					}
				}
			}
		} finally {
			Act.later(null, this, 15000L);
		}
	}
	
	@Override
	public final String toString() {
		
		return "CACHE-MAINTAINER, setSize=" + MaintainerRT3.MANAGER_SET.size();
	}
}
