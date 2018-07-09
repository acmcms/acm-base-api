package ru.myx.ae1.session;

import java.util.Collections;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.status.StatusProviderFiller;
import ru.myx.ae3.status.StatusRegistry;

/** @author myx */
public class SessionManager {
	
	/**
	 *
	 */
	public static SessionManagerImpl provideSession;
	
	static {
		
		SessionManager.provideSession = new SessionManagerDefault();
		
		StatusRegistry.ROOT_REGISTRY.register( //
				new StatusProviderFiller(//
						"sessionManager",
						MultivariantString.getString("Session manager", Collections.singletonMap("ru", "Менеджер сессий")),
						SessionManager.provideSession //
				)//
		);
		
	}
	
	/** @param sid
	 * @return data */
	public static final BaseMap session(final String sid) {
		
		return SessionManager.provideSession.session(sid);
	}
	
	/** @param sid
	 * @return data */
	public static final BaseMap sessionIfExists(final String sid) {
		
		return SessionManager.provideSession.sessionIfExists(sid);
	}
	
}
