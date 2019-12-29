package ru.myx.ae1.know;

import ru.myx.ae1.control.Control;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractContainer;
import ru.myx.ae3.control.ControlContainer;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.produce.ObjectFactory;

/*
 * Created on 14.11.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
/**
 * @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
final class CommandCreateFactory implements ObjectFactory<Object, ControlContainer<?>> {

	private static final String UID_CC = "ACM_ROOT_COMMAND_CLEAR_CACHE";

	private static final ControlContainer<?> CONTAINER_CC = new AbstractContainer<>() {

		@Override
		public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {

			return NodeControlRoot.getStaticCommandResult(command);
		}

		@Override
		public ControlCommandset getCommands() {

			return Control.createOptionsSingleton(new SignalClearCache());
		}
	};

	private static final Class<?>[] TARGETS = {
			ControlContainer.class
	};

	private static final String[] VARIETY = {
			CommandCreateFactory.UID_CC
	};

	@Override
	public final boolean accepts(final String variant, final BaseObject attributes, final Class<?> source) {

		return true;
	}

	@Override
	public final ControlContainer<?> produce(final String variant, final BaseObject attributes, final Object source) {

		if (CommandCreateFactory.UID_CC.equals(variant)) {
			return CommandCreateFactory.CONTAINER_CC;
		}
		return null;
	}

	@Override
	public final Class<?>[] sources() {

		return null;
	}

	@Override
	public final Class<?>[] targets() {

		return CommandCreateFactory.TARGETS;
	}

	@Override
	public final String[] variety() {

		return CommandCreateFactory.VARIETY;
	}
}
