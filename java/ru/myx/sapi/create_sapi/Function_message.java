/**
 *
 */
package ru.myx.sapi.create_sapi;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.TransferBuffer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.flow.BinaryMessage;

/** @author myx */
public class Function_message extends BaseFunctionAbstract implements ExecCallableBoth.NativeE2 {

	@Override
	public final ReplyAnswer callNE2(final ExecProcess ctx, final BaseObject instance, final BaseObject argument, final BaseObject argument2) {

		assert argument != null : "NULL java value";
		final BaseObject attributes;
		{
			assert argument2 != null : "NULL java value";
			if (argument2 == BaseObject.UNDEFINED) {
				attributes = BaseObject.UNDEFINED;
			} else//
			if (argument2.baseIsPrimitiveString()) {
				attributes = new BaseNativeObject();
				attributes.baseDefine("Content-Type", argument2);
			} else {
				attributes = argument2;
			}
		}
		if (argument == BaseObject.UNDEFINED || argument == BaseObject.NULL) {
			return Reply.empty(
					"CREATE-SAPI", //
					Context.getRequest(ctx),
					BaseObject.createObject(attributes))//
					.setFinal();
		}
		if (argument.baseIsPrimitiveString()) {
			return Reply.string(
					"CREATE-SAPI", //
					Context.getRequest(ctx),
					BaseObject.createObject(attributes),
					argument.baseToJavaString())//
					.setFinal();
		}
		if (argument instanceof BaseMessage) {
			final BinaryMessage<?> message = ((BaseMessage) argument).toBinary();
			final BaseObject messageAttributes = message.getAttributes();
			final ReplyAnswer result = Reply.binary(//
					"CREATE-SAPI", //
					Context.getRequest(ctx), //
					BaseObject.createObject(messageAttributes), //
					message.getBinary()//
			);
			if (attributes != BaseObject.UNDEFINED) {
				result.setAttributes(attributes);
			}
			return result.setFinal();
		}
		{
			final Object java = argument.baseValue();
			if (java instanceof TransferCopier) {
				return Reply.binary(
						"CREATE-SAPI", //
						Context.getRequest(ctx),
						BaseObject.createObject(attributes),
						((TransferCopier) java).nextCopy())//
						.setFinal();
			}
			if (java instanceof TransferBuffer) {
				return Reply.binary(
						"CREATE-SAPI", //
						Context.getRequest(ctx),
						BaseObject.createObject(attributes),
						(TransferBuffer) java)//
						.setFinal();
			}
		}
		return Reply.object(
				"CREATE-SAPI", //
				Context.getRequest(ctx),
				BaseObject.createObject(attributes),
				argument)//
				.setFinal();
	}

	@Override
	public Class<? extends ReplyAnswer> execResultClassJava() {

		return ReplyAnswer.class;
	}

}
