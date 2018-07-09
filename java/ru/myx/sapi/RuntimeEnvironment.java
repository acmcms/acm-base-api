package ru.myx.sapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.myx.ae1.know.Server;
import ru.myx.ae3.base.BaseHostEmpty;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.control.ControlActor;
import ru.myx.ae3.email.EmailSender;
import ru.myx.ae3.report.Report;

/**
 * Title: Base Implementations Description: Copyright: Copyright (c) 2001
 *
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
public class RuntimeEnvironment extends BaseHostEmpty {
	
	
	private static final String OWNER = "RT3/RTE";

	private final Set<ControlActor<?>> personalActors = new HashSet<>();

	private EmailSender sender = EmailSender.NUL_SENDER;

	/**
	 * @param server
	 */
	public RuntimeEnvironment(final Server server) {
		/**
		 * register API for scripting context
		 */
		server.getRootContext().ri10GV.baseDefine("Runtime", new RuntimeSAPI(server, this), BaseProperty.ATTRS_MASK_NNN);
	}

	@Override
	public BaseObject basePrototype() {
		
		
		return null;
	}

	/**
	 * @return sender
	 */
	public final EmailSender getEmailSender() {
		
		
		return this.sender;
	}

	/**
	 * @return actors
	 */
	public final ControlActor<?>[] getPersonalActors() {
		
		
		final List<ControlActor<?>> result = new ArrayList<>();
		for (final ControlActor<?> actor : this.personalActors) {
			if (actor != null) {
				result.add(actor);
			}
		}
		return result.toArray(new ControlActor<?>[result.size()]);
	}

	/**
	 * @param Sender
	 */
	public final void registerEmailSender(final EmailSender Sender) {
		
		
		Report.debug(RuntimeEnvironment.OWNER, "Registering email sender: class=" + Sender.getClass().getName());
		this.sender = Sender;
	}

	/**
	 * @param actor
	 */
	public final void registerPersonalActor(final ControlActor<?> actor) {
		
		
		this.personalActors.add(actor);
	}
}
