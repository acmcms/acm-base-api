/**
 *
 */
package ru.myx.sapi.create_sapi;

import java.net.MalformedURLException;
import java.net.URL;

import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;

/** @author myx
 *
 *         REDIRECT: Create.sessionTransferUrl("http://forum.myx.co.nz/topics/5133245/") */
public final class Function_sessionTransferUrl extends BaseFunctionAbstract implements ExecCallableBoth.NativeE1 {
	
	private final static class Record {
		
		final long expiration;
		
		final String ticket;
		
		@SuppressWarnings("unused")
		Record(final long expiration, final String ticket) {
			
			this.expiration = expiration;
			this.ticket = ticket;
		}
	}
	
	private final static URL makeUrl(final ExecProcess ctx, final String argument) throws MalformedURLException {

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
		return new URL(//
				url, //
				"/_sys/ticket/" + ticket + url.getPath() //
						+ (url.getQuery() == null
							? ""
							: "?" + url.getQuery())//
		);
		
	}
	
	@Override
	public final BaseObject callNE1(final ExecProcess ctx, final BaseObject instance, final BaseObject argumentObject) {
		
		if (argumentObject == BaseObject.UNDEFINED) {
			return BaseObject.UNDEFINED;
		}
		try {
			return Base.forUnknown(Function_sessionTransferUrl.makeUrl(ctx, argumentObject.baseToJavaString()));
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Class<? extends URL> execResultClassJava() {
		
		return URL.class;
	}
}
