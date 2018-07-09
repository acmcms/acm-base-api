/**
 * Created on 25.10.2002
 *
 * myx - barachta */
package ru.myx.ae1.control.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.provide.FormStatusFiller;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.status.StatusProvider;

/** @author myx
 * 
 *         myx - barachta */
public final class NodeStatusProvider extends AbstractNode {

	private static final ControlCommand<?> CMD_SHOW_STATUS = Control
			.createCommand("show", MultivariantString.getString("Show status...", Collections.singletonMap("ru", "Показать статус..."))).setCommandPermission("show")
			.setCommandIcon("command-open");
	
	private final StatusProvider provider;

	/** @param provider
	 */
	public NodeStatusProvider(final StatusProvider provider) {
		
		this.setAttributeIntern("id", provider.statusName());
		this.setAttributeIntern("title", provider.statusDescription());
		this.recalculate();
		this.provider = provider;
	}

	@Override
	public AccessPermissions getCommandPermissions() {

		return Access.createPermissionsLocal().addPermission("show", MultivariantString.getString("Watch status", Collections.singletonMap("ru", "Просматривать статус")))
				.addPermission("invalidate", MultivariantString.getString("Invalidate tree", Collections.singletonMap("ru", "Перестраивать дерево")));
	}

	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject parameters) {

		if (command == NodeStatusProvider.CMD_SHOW_STATUS) {
			return FormStatusFiller.createFormStatusFiller(Base.forString(this.getTitle()), this.provider);
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}

	@Override
	public ControlCommandset getCommands() {

		final ControlCommandset result = Control.createOptions();
		result.add(NodeStatusProvider.CMD_SHOW_STATUS);
		return result;
	}

	@Override
	public String getIcon() {

		return "container-information";
	}

	@Override
	public String getTitle() {

		return this.provider.statusDescription();
	}

	@Override
	protected ControlNode<?>[] internGetChildren() {

		final StatusProvider[] children = this.provider.childProviders();
		if (children != null && children.length > 0) {
			final List<ControlNode<?>> list = new ArrayList<>(children.length);
			for (final StatusProvider element : children) {
				if (element != null) {
					list.add(new NodeStatusProvider(element));
				}
			}
			return list.toArray(new ControlNode<?>[list.size()]);
		}
		return null;
	}

	@Override
	public String toString() {

		return "[object " + this.baseClass() + "(" + "key=" + this.getKey() + ", path=" + this.getLocationControl() + ")]";
	}
}
