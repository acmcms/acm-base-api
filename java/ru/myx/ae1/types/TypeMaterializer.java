/**
 *
 */
package ru.myx.ae1.types;

import java.util.function.Function;

import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.xml.Xml;

final class TypeMaterializer implements Function<BaseMessage, BaseNativeObject> {

	static final Function<BaseMessage, BaseNativeObject> MATERIALIZER = new TypeMaterializer();

	/** null instead of Charset.defaultCharset() - to allow charset to be read from input. */
	@Override
	public final BaseNativeObject apply(final BaseMessage message) {

		final String context = Exec.currentProcess().contextGetDebug().toString();
		return Xml.toMap(context, message.toBinary().getBinary(), null, null, new BaseNativeObject(), null, null);
	}
}
