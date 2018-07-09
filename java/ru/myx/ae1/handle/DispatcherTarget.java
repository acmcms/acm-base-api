package ru.myx.ae1.handle;

import ru.myx.ae1.know.Server;
import ru.myx.ae3.act.Act;
import java.util.function.Function;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.flow.ObjectTarget;
import ru.myx.ae3.i3.web.WebInterface;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;

/*
 * Created on 30.10.2003
 */
final class DispatcherTarget implements ObjectTarget<ServeRequest>, Function<ServeRequest, Boolean> {
	
	@Override
	public final boolean absorb(final ServeRequest query) {
		
		final String request = query.getResourceIdentifier();
		final String target = query.getTarget();
		assert Report.devel("WSM-QD", "Request: rq=" + query + ", tg=" + target + ", id=" + request);
		final Server server = Handle.getServer(target);
		if (server == null) {
			return WebInterface.dispatch(query);
		}
		final ExecProcess rootContext = server.getRootContext();
		assert rootContext != null : "Server context is NULL, server=" + server;
		assert rootContext != Exec.getRootProcess() : "Server context is same as system root context, server=" + server;
		final ExecProcess ctx = Exec.createProcess(rootContext, query + " " + request);
		ctx.vmScopeDeriveContext(rootContext.ri10GV);
		Context.replaceQuery(ctx, query);
		Context.replaceServer(ctx, server);
		try {
			Act.run(ctx, StarterQuery.STARTER_QUERY, query);
		} catch (final Throwable e) {
			Report.exception("WSM-QD", "Exception while enqueueing request processing job", e);
		}
		return true;
	}

	@Override
	public final Class<? extends ServeRequest> accepts() {
		
		return ServeRequest.class;
	}

	@Override
	public final Boolean apply(final ServeRequest object) {
		
		return this.absorb(object)
			? Boolean.TRUE
			: Boolean.FALSE;
	}

	@Override
	public final String toString() {
		
		return "AE2 Request Dispatcher";
	}
}
