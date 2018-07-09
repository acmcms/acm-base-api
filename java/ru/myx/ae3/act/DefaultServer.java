/*
 * Created on 05.05.2006
 */
package ru.myx.ae3.act;

import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.know.AbstractZoneServer;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;

/**
 * @author myx
 *
 */
final class DefaultServer extends AbstractZoneServer {
	
	DefaultServer(final ExecProcess context) {
		super("DEFAULT", "local", context);
	}

	@Override
	public boolean absorb(final ServeRequest request) {
		
		if (Report.MODE_DEBUG) {
			Report.exception("DEFAULT/SRV", "Query is here: query=" + request, new Error("Queries should not get there!"));
		} else {
			Report.warning("DEFAULT/SRV", "Query is here: " + request);
		}
		request.getResponseTarget()
				.apply(Reply.string(
						"DEF-CONTEXT", //
						request,
						"Nothing here! target=" + request.getTarget())//
						.setCode(Reply.CD_UNKNOWN) //
		);
		return true;
	}

	@Override
	public void close() {
		
		// ignore
	}

	@Override
	public Share<?>[] filterAllowedShares(final Share<?>[] sharings) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ControlNode<?> getControlNodeForShare(final String share) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLanguage(final String language) {
		
		return "en";
	}

	@Override
	public Share<?>[] getSharings() {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isRootServer() {
		
		return true;
	}
}
