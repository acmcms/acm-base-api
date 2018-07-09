/**
 *
 */
package ru.myx.sapi.create_sapi;

import java.io.IOException;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.TransferBuffer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.exec.BaseFunctionExecFullJ;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.flow.BinaryMessage;

/**
 * @author myx
 *
 */
public class Function_message extends BaseFunctionExecFullJ<ReplyAnswer> {
	
	
	@Override
	public final int execArgumentsAcceptable() {
		
		
		return 2;
	}

	@Override
	public final int execArgumentsDeclared() {
		
		
		return 2;
	}

	@Override
	public final int execArgumentsMinimal() {
		
		
		return 2;
	}

	@Override
	public Class<? extends ReplyAnswer> execResultClassJava() {
		
		
		return ReplyAnswer.class;
	}

	@Override
	public final ReplyAnswer getValue(final ExecProcess process) throws IOException {
		
		
		final BaseObject o = process.baseGetFirst(BaseObject.UNDEFINED);
		assert o != null : "NULL java value";
		final BaseObject attributes;
		{
			if (process.length() < 2) {
				attributes = BaseObject.UNDEFINED;
			} else {
				final BaseObject argument = process.baseGet(1, null);
				assert argument != null : "NULL java value";
				if (argument.baseIsPrimitiveString()) {
					attributes = new BaseNativeObject();
					attributes.baseDefine("Content-Type", argument);
				} else {
					attributes = argument;
				}
			}
		}
		if (o == BaseObject.UNDEFINED || o == BaseObject.NULL) {
			return Reply
					.empty(
							"CREATE-SAPI", //
							Context.getRequest(process),
							BaseObject.createObject(attributes))//
					.setFinal();
		}
		if (o.baseIsPrimitiveString()) {
			return Reply
					.string(
							"CREATE-SAPI", //
							Context.getRequest(process),
							BaseObject.createObject(attributes),
							o.baseToJavaString())//
					.setFinal();
		}
		if (o instanceof BaseMessage) {
			final BinaryMessage<?> message = ((BaseMessage) o).toBinary();
			final BaseObject messageAttributes = message.getAttributes();
			final ReplyAnswer result = Reply.binary("CREATE-SAPI", Context.getRequest(process), BaseObject.createObject(messageAttributes), message.getBinary());
			if (attributes != null) {
				result.setAttributes(attributes);
			}
			return result.setFinal();
		}
		{
			final Object java = o.baseValue();
			if (java instanceof TransferCopier) {
				return Reply
						.binary(
								"CREATE-SAPI", //
								Context.getRequest(process),
								BaseObject.createObject(attributes),
								((TransferCopier) java).nextCopy())//
						.setFinal();
			}
			if (java instanceof TransferBuffer) {
				return Reply
						.binary(
								"CREATE-SAPI", //
								Context.getRequest(process),
								BaseObject.createObject(attributes),
								(TransferBuffer) java)//
						.setFinal();
			}
		}
		return Reply
				.object(
						"CREATE-SAPI", //
						Context.getRequest(process),
						BaseObject.createObject(attributes),
						o)//
				.setFinal();
	}

}
