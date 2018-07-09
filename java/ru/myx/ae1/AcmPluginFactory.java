package ru.myx.ae1;

import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.produce.ObjectFactory;
import ru.myx.ae3.reflect.ReflectionHidden;

/**
 *
 * @author myx
 *
 */
public interface AcmPluginFactory extends ObjectFactory<Object, PluginInstance> {

	/**
	 * returned by default implementation of 'targets'
	 */
	@ReflectionHidden
	static final Class<?>[] TARGETS = {
			PluginInstance.class
	};
	
	@Override
	default Class<?>[] sources() {

		return null;
	}
	
	@Override
	default boolean accepts(final String variant, final BaseObject attributes, final Class<?> source) {

		return true;
	}
	
	@Override
	default Class<?>[] targets() {
		
		return AcmPluginFactory.TARGETS;
	}
}
