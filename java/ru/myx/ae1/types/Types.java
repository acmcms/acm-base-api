/*
 * Created on 12.05.2006
 */
package ru.myx.ae1.types;

import ru.myx.ae1.know.Server;
import ru.myx.ae3.act.Act;
import java.util.function.Function;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 * 
 * 
 */
public final class Types {
	
	/**
	 * @param server
	 * @param control
	 * @param message
	 * @param modified
	 * @param typeName
	 * @param defaultType
	 * @param resources
	 * @return type
	 */
	public static final Type<?> materializeType(final Server server,
			final TypeRegistry control,
			final BaseMessage message,
			final long modified,
			final String typeName,
			final Type<?> defaultType,
			final Function<String, BaseMessage> resources) {
		
		assert typeName != null : "Type name is NULL";
		final ExecProcess ctx = Exec.createProcess(Context.getServer(Exec.currentProcess()).getRootContext(), ("Type Materialization Context: " + typeName));
		ctx.contextExecFDEBUG("type-materialization: " + typeName);
		final BaseMap type;
		try {
			type = Act.run(ctx, TypeMaterializer.MATERIALIZER, message);
		} catch (final Throwable e) {
			Report.exception("TYPES", "Error while trying to materiaize type (" + typeName + ")", e);
			return null;
		}
		final Type<?> parentType;
		{
			final String parentTypeName = Base.getString(type, "extends", "").trim();
			if (typeName.equals(parentTypeName)) {
				throw new IllegalArgumentException("Type [" + typeName + "] extends itself!");
			}
			parentType = parentTypeName.length() == 0
				? defaultType
				: control == null
					? defaultType
					: control.getType(parentTypeName);
			if (parentType != null && parentType.isFinal()) {
				throw new IllegalArgumentException("Type [" + typeName + "]: Forbidden to extend type: " + parentType.getKey());
			}
		}
		return new TypeImpl(server, parentType, defaultType, typeName, modified, type, resources);
	}
	
	private Types() {
		// ignore
	}
}
