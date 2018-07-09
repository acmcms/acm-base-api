/*
 * Created on 20.12.2005
 */
package ru.myx.ae1.handle;

import java.util.function.Function;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.serve.ServeRequest;

final class StarterQuery implements Function<ServeRequest, Void> {
	
	static StarterQuery STARTER_QUERY = new StarterQuery();

	private StarterQuery() {
		// prevent
	}

	@Override
	public final Void apply(final ServeRequest query) {
		
		Context.getServer(Exec.currentProcess()).absorb(query);
		return null;
	}
}
