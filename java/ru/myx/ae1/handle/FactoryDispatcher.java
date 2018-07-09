package ru.myx.ae1.handle;

/*
 * Created on 02.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
import java.util.function.Function;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.flow.ObjectTarget;
import ru.myx.ae3.produce.ObjectFactory;
import ru.myx.ae3.serve.ServeRequest;

/**
 * @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class FactoryDispatcher implements ObjectFactory<Object, ObjectTarget<ServeRequest>> {

	private static ObjectTarget<ServeRequest> DISPATCHER = null;

	private static final Class<?>[] TARGETS = {
			ObjectTarget.class, Function.class
	};

	private static final Class<?>[] SOURCES = null;

	private static final String[] VARIETY = {
			"DISPATCH", "DISPATCHER"
	};

	@Override
	public final boolean accepts(final String variant, final BaseObject attributes, final Class<?> source) {
		
		return true;
	}

	@Override
	public final ObjectTarget<ServeRequest> produce(final String variant, final BaseObject attributes, final Object source) {
		
		if (FactoryDispatcher.DISPATCHER == null) {
			synchronized (this) {
				if (FactoryDispatcher.DISPATCHER == null) {
					FactoryDispatcher.DISPATCHER = new DispatcherTarget();
				}
			}
		}
		return FactoryDispatcher.DISPATCHER;
	}

	@Override
	public final Class<?>[] sources() {
		
		return FactoryDispatcher.SOURCES;
	}

	@Override
	public final Class<?>[] targets() {
		
		return FactoryDispatcher.TARGETS;
	}

	@Override
	public final String[] variety() {
		
		return FactoryDispatcher.VARIETY;
	}
}
