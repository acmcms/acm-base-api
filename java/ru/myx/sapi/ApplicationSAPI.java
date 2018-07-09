package ru.myx.sapi;

import java.util.Collection;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlEntry;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.know.Server;
import ru.myx.ae1.know.ZoneServer;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostDataSubstitution;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.produce.Produce;
import ru.myx.ae3.produce.Reproducible;
import ru.myx.ae3.reflect.Reflect;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.skinner.Skinner;
import ru.myx.ae3.vfs.Entry;

/**
 * Title: System Runtime interfaces Description: Copyright: Copyright (c) 2001
 *
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
public final class ApplicationSAPI implements BaseHostDataSubstitution<BaseObject>, Reproducible {
	
	
	private static final BaseObject PROTOTYPE = Reflect.classToBasePrototype(ApplicationSAPI.class);
	
	private static final String getFinderResultLocationImpl(final ExecProcess ctx, final Server server, final ServeRequest query, final String request) {
		
		
		if (request.startsWith("/_finder/")) {
			return ApplicationSAPI.getFinderResultLocationImpl(ctx, server, query, request.substring("/_finder/".length()));
		}
		if (request.startsWith("/")) {
			return ApplicationSAPI.getFinderResultLocationImpl(ctx, server, query, request.substring(1));
		}
		if (request.startsWith("path/")) {
			final String id = request.substring(5).trim();
			if (id.length() == 0) {
				return "/";
			}
			final ControlEntry<?> node = Control.relativeNode(server.getControlRoot(), id);
			if (node == null) {
				return null;
			}
			final String location = node.getLocationControl();
			if (location == null) {
				return null;
			}
			return server.fixLocation(ctx, location, false);
		}
		if (request.startsWith("locate/")) {
			final String id = request.substring(7);
			final String finderIdentity;
			final String totalParameter;
			{
				final int pos = id.indexOf('/');
				finderIdentity = id.substring(0, pos);
				totalParameter = id.substring(pos + 1);
			}
			final String finderParameter;
			final String suffixParameter;
			{
				final int pos = totalParameter.indexOf('/');
				if (pos == -1) {
					finderParameter = totalParameter;
					suffixParameter = null;
				} else {
					finderParameter = totalParameter.substring(0, pos);
					suffixParameter = totalParameter.substring(pos + 1);
				}
			}
			final ControlEntry<?> entry = Produce.object(ControlEntry.class, finderIdentity, null, finderParameter);
			if (entry == null) {
				return null;
			}
			final String location = entry.getLocationControl();
			if (location == null) {
				return null;
			}
			final String queryParameter = query.getParameterString();
			if (suffixParameter == null && queryParameter == null) {
				return server.fixLocation(ctx, location, false);
			}
			if (suffixParameter == null) {
				return server.fixLocation(ctx, location, false) + '?' + queryParameter;
			}
			final String urlPrefix = server.fixLocation(ctx, location, false);
			final String url;
			if (urlPrefix.endsWith("/")) {
				url = urlPrefix + suffixParameter;
			} else {
				url = urlPrefix + '/' + suffixParameter;
			}
			if (queryParameter == null) {
				return url;
			}
			return url + '?' + queryParameter;
		}
		if (request.startsWith("search/")) {
			return null;
		}
		if (request.startsWith("shared/")) {
			return null;
		}
		return null;
	}
	
	/**
	 * @param process
	 * @param server
	 * @param query
	 * @param request
	 * @return string
	 */
	public static final ReplyAnswer getFinderResultReplyImpl(final ExecProcess process, final Server server, final ServeRequest query, final String request) {
		
		
		if (request.startsWith("/_finder/")) {
			return ApplicationSAPI.getFinderResultReplyImpl(process, server, query, request.substring("/_finder/".length()));
		}
		if (request.startsWith("/")) {
			return ApplicationSAPI.getFinderResultReplyImpl(process, server, query, request.substring(1));
		}
		if (request.startsWith("path/")) {
			final String id = request.substring(5).trim();
			if (id.length() == 0) {
				return Reply.redirect("APPLICATION", query, true, "/");
			}
			final ControlEntry<?> node = Control.relativeNode(server.getControlRoot(), id);
			if (node == null) {
				return null;
			}
			final String location = node.getLocationControl();
			if (location == null) {
				return null;
			}
			return Reply.redirect("APPLICATION", query, true, server.fixLocation(process, location, false));
		}
		if (request.startsWith("locate/")) {
			final String id = request.substring(7);
			final String finderIdentity;
			final String totalParameter;
			{
				final int pos = id.indexOf('/');
				finderIdentity = id.substring(0, pos);
				totalParameter = id.substring(pos + 1);
			}
			final String finderParameter;
			final String suffixParameter;
			{
				final int pos = totalParameter.indexOf('/');
				if (pos == -1) {
					finderParameter = totalParameter;
					suffixParameter = null;
				} else {
					finderParameter = totalParameter.substring(0, pos);
					suffixParameter = totalParameter.substring(pos + 1);
				}
			}
			final ControlEntry<?> entry = Produce.object(ControlEntry.class, finderIdentity, null, finderParameter);
			if (entry == null) {
				return null;
			}
			final String location = entry.getLocationControl();
			if (location == null) {
				return null;
			}
			final String queryParameter = query.getParameterString();
			if (suffixParameter == null && queryParameter == null) {
				return Reply.redirect("APPLICATION", query, true, server.fixLocation(process, location, false));
			}
			if (suffixParameter == null) {
				return Reply.redirect("APPLICATION", query, true, server.fixLocation(process, location, false) + '?' + queryParameter);
			}
			final String urlPrefix = server.fixLocation(process, location, false);
			final String url;
			if (urlPrefix.endsWith("/")) {
				url = urlPrefix + suffixParameter;
			} else {
				url = urlPrefix + '/' + suffixParameter;
			}
			if (queryParameter == null) {
				return Reply.redirect("APPLICATION", query, true, url);
			}
			return Reply.redirect("APPLICATION", query, true, url + '?' + queryParameter);
		}
		if (request.startsWith("search/")) {
			return null;
		}
		if (request.startsWith("shared/")) {
			return null;
		}
		return null;
	}
	
	/**
	 * @return string
	 */
	public static final String getSystemCountryCode() {
		
		
		return System.getProperty("user.country");
	}
	
	/**
	 * @return string
	 */
	public static final String getSystemLanguage() {
		
		
		return System.getProperty("user.language");
	}
	
	/**
	 * @return string
	 */
	public static final String getSystemUserName() {
		
		
		return System.getProperty("user.name");
	}
	
	private final ZoneServer server;
	
	/**
	 * @param server
	 */
	public ApplicationSAPI(final ZoneServer server) {
		this.server = server;
	}
	
	@Override
	public BaseObject baseGetSubstitution() {
		
		
		return Base.forUnknown(this.server.getProperties());
	}
	
	@Override
	public BaseObject basePrototype() {
		
		
		return ApplicationSAPI.PROTOTYPE;
	}
	
	/**
	 * @param o
	 * @return object
	 */
	public final Object get(final Object o) {
		
		
		return this.server.getProperties().get(o);
	}
	
	/**
	 * @param share
	 * @return node
	 */
	public final ControlNode<?> getControlNodeForShareName(final String share) {
		
		
		return this.server.getControlNodeForShare(share);
	}
	
	/**
	 * @return share array
	 */
	public final Share<?>[] getDomainSharePoints() {
		
		
		return this.server.filterAllowedShares(this.server.getSharings());
	}
	
	/**
	 * @param all
	 * @return share array
	 */
	public final Share<?>[] getDomainSharePoints(final boolean all) {
		
		
		if (all) {
			return this.server.getSharings();
		}
		return this.server.filterAllowedShares(this.server.getSharings());
	}
	
	/**
	 * @param o
	 * @return double
	 */
	public final double getDouble(final Object o) {
		
		
		return Convert.Any.toDouble(this.server.getProperties().get(o), 0);
	}
	
	/**
	 * @param o
	 * @param defaultValue
	 * @return double
	 */
	public final double getDouble(final Object o, final double defaultValue) {
		
		
		return Convert.Any.toDouble(this.server.getProperties().get(o), defaultValue);
	}
	
	/**
	 * @param request
	 * @return entry
	 */
	public final ControlEntry<?> getFinderResultEntry(final String request) {
		
		
		if (request == null) {
			return null;
		}
		if (request.startsWith("/_finder/")) {
			return this.getFinderResultEntry(request.substring("/_finder/".length()));
		}
		if (request.startsWith("/")) {
			return this.getFinderResultEntry(request.substring(1));
		}
		if (request.startsWith("path/")) {
			final String id = request.substring(5).trim();
			return Control.relativeNode(this.server.getControlRoot(), id);
		}
		if (request.startsWith("locate/")) {
			final String id = request.substring(7);
			final String finderIdentity;
			final String totalParameter;
			{
				final int pos = id.indexOf('/');
				finderIdentity = id.substring(0, pos);
				totalParameter = id.substring(pos + 1);
			}
			final String finderParameter;
			{
				final int pos = totalParameter.indexOf('/');
				finderParameter = pos == -1
					? totalParameter
					: totalParameter.substring(0, pos);
			}
			return Produce.object(ControlEntry.class, finderIdentity, null, finderParameter);
		}
		if (request.startsWith("search/")) {
			return null;
		}
		if (request.startsWith("shared/")) {
			return null;
		}
		return null;
	}
	
	/**
	 * @param ctx
	 * @param request
	 * @return string
	 */
	public final String getFinderResultLocation(final ExecProcess ctx, final String request) {
		
		
		return request != null
			? ApplicationSAPI.getFinderResultLocationImpl(ctx, this.server, Context.getRequest(Exec.currentProcess()), request)
			: null;
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public final int getInt(final Object o) {
		
		
		return Convert.Any.toInt(this.server.getProperties().get(o), 0);
	}
	
	/**
	 * @param o
	 * @param defaultValue
	 * @return int
	 */
	public final int getInt(final Object o, final int defaultValue) {
		
		
		return Convert.Any.toInt(this.server.getProperties().get(o), defaultValue);
	}
	
	/**
	 * @param o
	 * @return long
	 */
	public final long getLong(final Object o) {
		
		
		return Convert.Any.toLong(this.server.getProperties().get(o), 0);
	}
	
	/**
	 * @param o
	 * @param defaultValue
	 * @return long
	 */
	public final long getLong(final Object o, final long defaultValue) {
		
		
		return Convert.Any.toLong(this.server.getProperties().get(o), defaultValue);
	}
	
	/**
	 * @param name
	 * @return
	 */
	public final Skinner getSkinner(final String name) {
		
		
		return this.server.getSkinner(name);
	}
	
	/**
	 * @return
	 */
	public final Collection<String> getSkinnerNames() {
		
		
		return this.server.getSkinnerNames();
	}
	
	/**
	 * @param o
	 * @return string
	 */
	public final String getString(final Object o) {
		
		
		return Convert.Any.toString(this.server.getProperties().get(o), "");
	}
	
	/**
	 * @param o
	 * @param defaultValue
	 * @return string
	 */
	public final String getString(final Object o, final String defaultValue) {
		
		
		return Convert.Any.toString(this.server.getProperties().get(o), defaultValue);
	}
	
	/**
	 *
	 * @return
	 */
	public Entry getVfsRootEntry() {
		
		
		return this.server.getVfsRootEntry();
	}
	
	/**
	 *
	 * @return
	 */
	public Entry getVfsZoneEntry() {
		
		
		return this.server.getVfsZoneEntry();
	}
	
	/**
	 *
	 * @return
	 */
	public Entry getVfsZoneLibEntry() {
		
		
		return this.server.getVfsZoneLibEntry();
	}
	
	/**
	 * @param o
	 * @param value
	 * @return object
	 */
	public final Object put(final Object o, final Object value) {
		
		
		if (value == null) {
			this.server.getProperties().remove(o);
		} else {
			this.server.getProperties().put(o, value);
		}
		return value;
	}
	
	/**
	 *
	 * @param moduleName
	 * @return
	 */
	public Entry requireResolveVfsModule(final String moduleName) {
		
		
		return this.server.requireResolveVfsModule(moduleName);
	}
	
	/**
	 *
	 * @param pathName
	 * @return
	 */
	public Entry requireResolveVfsEntry(final String pathName) {
		
		
		return this.server.requireResolveVfsEntry(pathName);
	}
	
	@Override
	public final String restoreFactoryIdentity() {
		
		
		return "ACM_API_RT3_FACTORY_APPLICATION";
	}
	
	@Override
	public final String restoreFactoryParameter() {
		
		
		return "CURRENT";
	}
	
	@Override
	public String toString() {
		
		
		return "[object " + this.getClass().getSimpleName() + "(" + this.server.getDomainId() + ")]";
	}
}
