/*
 * Created on 05.05.2006
 */
package ru.myx.ae3.act;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ru.myx.ae1.know.Server;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.produce.Reproducible;

final class ContextSupportProperties extends Properties implements Reproducible {
	
	private static final long serialVersionUID = 3763100781967847472L;

	protected static final Properties getProperties() {
		
		final Server server = Context.getServer(Exec.currentProcess());
		return server == null
			? Context.SYSTEM_DEFAULT_PROPERTIES
			: server.getProperties();
	}

	@Override
	public void clear() {
		
		ContextSupportProperties.getProperties().clear();
	}

	@Override
	public Object clone() {
		
		return ContextSupportProperties.getProperties().clone();
	}

	@Override
	public boolean contains(final Object arg0) {
		
		synchronized (this) {
			return ContextSupportProperties.getProperties().contains(arg0);
		}
	}

	@Override
	public boolean containsKey(final Object arg0) {
		
		return ContextSupportProperties.getProperties().containsKey(arg0);
	}

	@Override
	public boolean containsValue(final Object arg0) {
		
		return ContextSupportProperties.getProperties().containsValue(arg0);
	}

	@Override
	public Enumeration<Object> elements() {
		
		return ContextSupportProperties.getProperties().elements();
	}

	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		
		return ContextSupportProperties.getProperties().entrySet();
	}

	@Override
	public boolean equals(final Object arg0) {
		
		return ContextSupportProperties.getProperties().equals(arg0);
	}

	@Override
	public Object get(final Object arg0) {
		
		return ContextSupportProperties.getProperties().get(arg0);
	}

	@Override
	public String getProperty(final String arg0) {
		
		return ContextSupportProperties.getProperties().getProperty(arg0);
	}

	@Override
	public String getProperty(final String arg0, final String arg1) {
		
		return ContextSupportProperties.getProperties().getProperty(arg0, arg1);
	}

	@Override
	public int hashCode() {
		
		return ContextSupportProperties.getProperties().hashCode();
	}

	@Override
	public boolean isEmpty() {
		
		return ContextSupportProperties.getProperties().isEmpty();
	}

	@Override
	public Enumeration<Object> keys() {
		
		return ContextSupportProperties.getProperties().keys();
	}

	@Override
	public Set<Object> keySet() {
		
		return ContextSupportProperties.getProperties().keySet();
	}

	@Override
	public void list(final PrintStream arg0) {
		
		ContextSupportProperties.getProperties().list(arg0);
	}

	@Override
	public void list(final PrintWriter arg0) {
		
		ContextSupportProperties.getProperties().list(arg0);
	}

	@Override
	public void load(final InputStream arg0) throws IOException {
		
		ContextSupportProperties.getProperties().load(arg0);
	}

	@Override
	public Enumeration<?> propertyNames() {
		
		return ContextSupportProperties.getProperties().propertyNames();
	}

	@Override
	public Object put(final Object arg0, final Object arg1) {
		
		return ContextSupportProperties.getProperties().put(arg0, arg1);
	}

	@Override
	public void putAll(final Map<? extends Object, ? extends Object> arg0) {
		
		ContextSupportProperties.getProperties().putAll(arg0);
	}

	@Override
	public Object remove(final Object arg0) {
		
		return ContextSupportProperties.getProperties().remove(arg0);
	}

	@Override
	public String restoreFactoryIdentity() {
		
		return "CURRENT_SERVER_DATA_MAP_FACTORY";
	}

	@Override
	public String restoreFactoryParameter() {
		
		return "CTX_PROPERTIES";
	}

	@Deprecated
	@Override
	public void save(final OutputStream arg0, final String arg1) {
		
		throw new NoSuchMethodError("Deprecated!");
	}

	@Override
	public Object setProperty(final String arg0, final String arg1) {
		
		return ContextSupportProperties.getProperties().setProperty(arg0, arg1);
	}

	@Override
	public int size() {
		
		return ContextSupportProperties.getProperties().size();
	}

	@Override
	public void store(final OutputStream arg0, final String arg1) throws IOException {
		
		ContextSupportProperties.getProperties().store(arg0, arg1);
	}

	@Override
	public String toString() {
		
		return ContextSupportProperties.getProperties().toString();
	}

	@Override
	public Collection<Object> values() {
		
		return ContextSupportProperties.getProperties().values();
	}
}
