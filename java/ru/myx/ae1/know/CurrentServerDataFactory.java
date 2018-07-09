/*
 * Created on 05.05.2006
 */
package ru.myx.ae1.know;

import java.util.Properties;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.produce.ObjectFactory;

final class CurrentServerDataFactory implements ObjectFactory<Object, Properties> {
	
	private static final Class<?>[] TARGETS = {
			Properties.class
	};

	private static final String[] VARIETY = {
			"CURRENT_SERVER_DATA_MAP_FACTORY"
	};

	@Override
	public final boolean accepts(final String variant, final BaseObject attributes, final Class<?> source) {
		
		return true;
	}

	@Override
	public final Properties produce(final String variant, final BaseObject attributes, final Object source) {
		
		return Context.getServer(Exec.currentProcess()).getProperties();
	}

	@Override
	public final Class<?>[] sources() {
		
		return null;
	}

	@Override
	public final Class<?>[] targets() {
		
		return CurrentServerDataFactory.TARGETS;
	}

	@Override
	public final String[] variety() {
		
		return CurrentServerDataFactory.VARIETY;
	}
}
