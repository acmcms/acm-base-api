package ru.myx.ae1.know;

import java.util.Collections;
import java.util.Map;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.access.AccessPermissions;
import java.util.function.Function;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Convert.MapEntry;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 */
public class NodeControlRoot extends AbstractNode {
	
	private static final String[] USER_KEYS = {
			"Name", "name"
	};
	
	private static final BaseObject STR_CONTROL_ROOT = MultivariantString.getString("Control root: ", Collections.singletonMap("ru", "Корень системы управления: "));
	
	/**
	 * @param command
	 * @return return
	 */
	public static final Object getStaticCommandResult(final ControlCommand<?> command) {
		
		assert command != null : "NULL command";
		final Server server = Context.getServer(Exec.currentProcess());
		assert server != null : "NULL server";
		final Function<Void, Object> signal = server.registrySignals().get(command.getKey());
		if (signal == null) {
			throw new IllegalArgumentException("Unknown command: " + command.getKey());
		}
		final BaseObject form = command.getAttributes().baseGet("form", BaseObject.UNDEFINED);
		assert form != null : "NULL value";
		if (form == BaseObject.UNDEFINED) {
			try {
				return signal.apply(null);
			} catch (final Exception e) {
				Report.exception("ROOT_NODE_COMMAND", "exception", e);
			}
		}
		return form;
	}
	
	/**
	 * @param server
	 */
	public NodeControlRoot(final AbstractServer server) {
		//
	}
	
	@Override
	public AccessPermissions getCommandPermissions() {
		
		final AccessPermissions result = Access.createPermissionsLocal();
		final Server server = Context.getServer(Exec.currentProcess());
		final Map<String, Function<Void, Object>> registrySignals = server.registrySignals();
		for (final Map.Entry<String, Function<Void, Object>> entry : registrySignals.entrySet()) {
			if (entry.getValue() instanceof ControlCommand<?>) {
				final ControlCommand<?> command = (ControlCommand<?>) entry.getValue();
				if (command.commandPermission() != null) {
					result.addPermission(command.commandPermission(), Base.forString(command.getTitle()));
				}
			}
		}
		return result;
	}
	
	@Override
	public final Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		return NodeControlRoot.getStaticCommandResult(command);
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		final ControlCommandset result = Control.createOptions();
		final Server server = Context.getServer(Exec.currentProcess());
		final Map<String, Function<Void, Object>> registrySignals = server.registrySignals();
		for (final Map.Entry<String, Function<Void, Object>> entry : registrySignals.entrySet()) {
			if (entry.getValue() instanceof ControlCommand<?>) {
				result.add((ControlCommand<?>) entry.getValue());
			}
		}
		return result.isEmpty()
			? null
			: result;
	}
	
	@Override
	public String getIcon() {
		
		return "container-home";
	}
	
	@Override
	public String getKey() {
		
		return "root";
	}
	
	@Override
	public final String getLocationControl() {
		
		return "/";
	}
	
	@Override
	public String getTitle() {
		
		final AccessUser<?> user = Context.getUser(Exec.currentProcess());
		return NodeControlRoot.STR_CONTROL_ROOT + MapEntry.anyToString(user.getProfile(), NodeControlRoot.USER_KEYS, user.getLogin());
	}
	
	@Override
	protected ControlNode<?>[] internGetChildren() {
		
		return null;
	}
	
	@Override
	public String toString() {
		
		return "[object " + this.baseClass() + "(" + this.getLocationControl() + ")]";
	}
}
