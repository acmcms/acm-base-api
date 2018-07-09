package ru.myx.sapi.default_sapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.TransferBuffer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.exec.BaseFunctionExecFullJ;
import ru.myx.ae3.exec.ExecProcess;

/**
 * @author myx
 *
 */
public class Function_binaryDigest extends BaseFunctionExecFullJ<String> {
	
	
	private static final MessageDigest binaryMd5(final Object x) throws UnsupportedEncodingException {
		
		
		if (x == null) {
			return Engine.getMessageDigestInstance();
		}
		if (x instanceof byte[]) {
			final MessageDigest md = Engine.getMessageDigestInstance();
			md.update((byte[]) x);
			return md;
		}
		if (x instanceof TransferBuffer) {
			return ((TransferBuffer) x).getMessageDigest();
		}
		if (x instanceof TransferCopier) {
			return ((TransferCopier) x).getMessageDigest();
		}
		if (x instanceof Value<?>) {
			final Object baseValue = ((Value<?>) x).baseValue();
			if (baseValue != x) {
				return Function_binaryDigest.binaryMd5(baseValue);
			}
		}
		if (x instanceof BaseMessage) {
			return ((BaseMessage) x).toBinary().getBinaryMessageDigest();
		}
		final MessageDigest md = Engine.getMessageDigestInstance();
		md.update(x.toString().getBytes(Engine.CHARSET_UTF8));
		return md;
	}
	
	private static final String toHex(final byte hash[]) {
		
		
		final StringBuilder buf = new StringBuilder(hash.length * 2);
		int i;
		for (i = 0; i < hash.length; ++i) {
			if ((hash[i] & 0xff) < 0x10) {
				buf.append('0');
			}
			buf.append(Long.toString(hash[i] & 0xff, 16));
		}
		return buf.toString();
	}
	
	@Override
	public final int execArgumentsAcceptable() {
		
		
		return 1;
	}
	
	@Override
	public int execArgumentsDeclared() {
		
		
		return 1;
	}
	
	@Override
	public final int execArgumentsMinimal() {
		
		
		return 1;
	}
	
	@Override
	public final boolean execIsConstant() {
		
		
		return true;
	}
	
	@Override
	public Class<? extends String> execResultClassJava() {
		
		
		return String.class;
	}
	
	@Deprecated
	@Override
	public final String getValue(final ExecProcess context) throws IOException {
		
		
		final BaseObject x = context.baseGetFirst(null);
		if (x == null || x == BaseObject.UNDEFINED || x == BaseObject.NULL) {
			return null;
		}
		return Function_binaryDigest.toHex(Function_binaryDigest.binaryMd5(x).digest());
	}
	
}
