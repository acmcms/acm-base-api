package ru.myx.ae2.watch;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;

import ru.myx.ae3.act.Act;

/**
 * @author myx
 * 
 */
final class MonitorWatcher implements Runnable {
	private boolean							scheduled	= false;
	
	private final boolean					stopped		= false;
	
	private final LinkedList<Watch.Monitor>	newOnes		= new LinkedList<>();
	
	private final LinkedList<Watch.Monitor>	watches		= new LinkedList<>();
	
	public void register(final Watch.Monitor monitor) {
		synchronized (this) {
			this.newOnes.add( monitor );
			if (!this.scheduled) {
				Act.later( null, this, 1000 );
				this.scheduled = true;
			}
		}
	}
	
	@Override
	public void run() {
		if (this.newOnes.size() > 0) {
			synchronized (this) {
				for (final Watch.Monitor monitor : this.newOnes) {
					if (!this.watches.contains( monitor )) {
						this.watches.add( monitor );
					}
				}
				this.newOnes.clear();
			}
		}
		for (final Iterator<Watch.Monitor> i = this.watches.iterator(); i.hasNext();) {
			final Watch.Monitor monitor;
			try {
				monitor = i.next();
			} catch (final ConcurrentModificationException e) {
				Act.launch( null, this );
				return;
			}
			try {
				if (!monitor.check()) {
					i.remove();
				}
			} catch (final Throwable e) {
				e.printStackTrace();
				i.remove();
			}
		}
		synchronized (this) {
			if (!this.stopped && this.newOnes.size() + this.watches.size() > 0) {
				Act.later( null, this, 5000 );
			} else {
				this.scheduled = false;
			}
		}
	}
	
	@Override
	public String toString() {
		return "ae2 default monitor watcher";
	}
}
