/**
 *
 */
package ru.myx.sapi.create_sapi;

import java.net.MalformedURLException;
import java.net.URL;

import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseString;
import ru.myx.ae3.exec.BaseFunctionExecFullJ;
import ru.myx.ae3.exec.ExecProcess;

/**
 * @author myx
 *
 *         REDIRECT:
 *         Create.sessionTransferUrl("http://forum.myx.ru/topics/5133245/")
 *
 *
 */
public final class Function_sessionTransferUrl extends BaseFunctionExecFullJ<URL> {
	
	
	private final static class Record {
		
		
		final long expiration;

		final String ticket;

		Record(final long expiration, final String ticket) {
			this.expiration = expiration;
			this.ticket = ticket;
		}
	}

	@Override
	public final int execArgumentsAcceptable() {
		
		
		return 1;
	}

	@Override
	public final int execArgumentsDeclared() {
		
		
		return 1;
	}

	@Override
	public final int execArgumentsMinimal() {
		
		
		return 1;
	}

	@Override
	public Class<? extends URL> execResultClassJava() {
		
		
		return URL.class;
	}

	@Override
	public final URL getValue(final ExecProcess ctx) throws MalformedURLException {
		
		
		final String argument = ctx.baseGetFirst(BaseString.EMPTY).baseToJavaString();
		final URL url = new URL(argument);
		if (!Context.hasSessionId(ctx) || Context.getSessionState(ctx) < AuthLevels.AL_AUTHORIZED_AUTOMATICALLY) {
			return url;
		}
		final String ticket;
		{
			final BaseObject data = Context.getSessionData(ctx);
			final String host = url.getHost();
			final Record found = (Record) data.baseGet("tk:" + host, BaseObject.UNDEFINED).baseValue();
			if (found != null && found.expiration > Engine.fastTime()) {
				ticket = found.ticket;
			} else {
				ticket = Engine.createGuid();
				final BaseObject action = new BaseNativeObject()//
						.putAppend("actionType", "defaultAR")//
						.putAppend("action", "sessionTransfer")//
						.putAppend("uid", Context.getUserId(ctx))//
						.putAppend("sid", Context.getSessionId(ctx))//
						.putAppend("state", Math.min(Context.getSessionState(ctx), AuthLevels.AL_AUTHORIZED_NORMAL)) //
				;
				final long expiration = Engine.fastTime() + 1000L * 60L * 60L * 24L * 1L;
				Context.getServer(ctx).getStorage().saveTemporary(ticket, action, expiration);
			}
		}
		return new URL(url, "/_sys/ticket/" + ticket + url.getPath() + (url.getQuery() == null
			? ""
			: "?" + url.getQuery()));
	}
}
