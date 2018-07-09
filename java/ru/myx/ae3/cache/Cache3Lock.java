/*
 * Created on 28.12.2005
 */
package ru.myx.ae3.cache;

import ru.myx.ae3.Engine;

final class Cache3Lock extends Cache3Entry {
	private boolean		done	= false;
	
	private Cache3Entry	result	= null;
	
	Cache3Lock(final String key) {
		super( key );
	}
	
	final Cache3Entry getValue() {
		synchronized (this) {
			if (this.done) {
				return this.result;
			}
			try {
				/**
				 * I AM NOT PARANOID.
				 * 
				 * 
				 * Taken from Javadoc for 'wait' method:
				 * 
				 * A thread can also wake up without being notified,
				 * interrupted, or timing out, a so-called spurious wakeup.
				 * While this will rarely occur in practice, applications must
				 * guard against it by testing for the condition that should
				 * have caused the thread to be awakened, and continuing to wait
				 * if the condition is not satisfied. In other words, waits
				 * should always occur in loops, like this one:
				 * 
				 * synchronized (obj) { while (<condition does not hold>)
				 * obj.wait(timeout); ... // Perform action appropriate to
				 * condition }
				 * 
				 * (For more information on this topic, see Section 3.2.3 in
				 * Doug Lea's "Concurrent Programming in Java (Second Edition)"
				 * (Addison-Wesley, 2000), or Item 50 in Joshua Bloch's
				 * "Effective Java Programming Language Guide" (Addison-Wesley,
				 * 2001).
				 * 
				 */
				for (//
				long left = 30000L, expires = Engine.fastTime() + left; //
				left > 0; //
				left = expires - Engine.fastTime()) {
					//
					this.wait( left );
					if (this.done) {
						// if (this.error != null) {
						// throw this.error;
						// }
						return this.result;
					}
					if (this.result != null) {
						break;
					}
				}
			} catch (final InterruptedException e) {
				return null;
			}
		}
		throw new CacheWaitTimeoutException( "Cache wait timeout!" );
	}
	
	final void setFinally() {
		if (this.done) {
			return;
		}
		synchronized (this) {
			if (this.done) {
				return;
			}
			this.done = true;
			this.notifyAll();
		}
	}
	
	final void setResult(final Cache3Entry result) {
		synchronized (this) {
			this.done = true;
			this.result = result;
			this.notifyAll();
		}
	}
}
