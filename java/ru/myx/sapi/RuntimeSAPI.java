package ru.myx.sapi;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlEntry;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.know.Know;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.base.BaseHostObject;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.TransferBuffer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.email.Email;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.flow.BinaryMessage;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Text;
import ru.myx.ae3.know.Language;
import ru.myx.ae3.produce.Produce;
import ru.myx.ae3.reflect.Reflect;
import ru.myx.ae3.report.Report;
import ru.myx.sapi.runtime_sapi.Function_listLookup;

/**
 * @author Alexander I. Kharitchev
 * 
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public final class RuntimeSAPI extends BaseHostObject {
	
	
	/**
	 *
	 */
	public static final BaseObject PROTOTYPE;

	static {
		PROTOTYPE = new BaseNativeObject(Reflect.classToBasePrototype(RuntimeSAPI.class));
		RuntimeSAPI.PROTOTYPE.baseDefine("listLookup", new Function_listLookup());
	}

	/**
	 * @param owner
	 * @param type
	 * @param text
	 */
	public static final void audit(final String owner, final String type, final String text) {
		
		
		Report.audit(owner, type, text);
	}

	/**
	 * @param ctx
	 * @param expresion
	 * @return boolean
	 */
	public static final boolean evaluateBoolean(final ExecProcess ctx, final String expresion) {
		
		
		return Evaluate.evaluateBoolean(expresion, ctx, null);
	}

	/**
	 * @param ctx
	 * @param expresion
	 * @return object
	 * @throws Throwable
	 */
	public static final BaseObject evaluateObject(final ExecProcess ctx, final String expresion) throws Throwable {
		
		
		return Evaluate.evaluateObject(expresion, ctx, null);
	}

	/**
	 * @param ctx
	 * @param expresion
	 * @throws Throwable
	 */
	public static final void evaluateVoid(final ExecProcess ctx, final String expresion) throws Throwable {
		
		
		Evaluate.evaluateVoid(expresion, ctx, null);
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getDefaultCharacterEncoding(final ExecProcess ctx) {
		
		
		return Context.getLanguage(ctx).getCommonEncoding();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getLanguage(final ExecProcess ctx) {
		
		
		return Context.getLanguage(ctx).getName();
	}

	/**
	 * @param ctx
	 * @return languages
	 */
	public static final Language[] getLanguages(final ExecProcess ctx) {
		
		
		final String[] languages = Context.getServer(ctx).getLanguages();
		final Language[] result = new Language[languages.length];
		for (int i = languages.length - 1; i >= 0; --i) {
			result[i] = Language.getLanguage(languages[i]);
		}
		return result;
	}

	/**
	 * @param ctx
	 * @param o
	 * @return string
	 */
	public static final String getLanguageSelectionUrl(final ExecProcess ctx, final Object o) {
		
		
		return "/_sys/" + Convert.Any.toString(o, Context.getLanguage(ctx).getName()) + ",language";
	}

	/**
	 * @param ctx
	 * @param o
	 * @param redirection
	 * @return string
	 */
	public static final String getLanguageSelectionUrl(final ExecProcess ctx, final Object o, final String redirection) {
		
		
		return RuntimeSAPI.getLanguageSelectionUrl(ctx, Convert.Any.toString(o, Context.getLanguage(ctx).getName())) + (redirection == null || redirection.length() == 0
			? ""
			: "?back=" + Text.encodeUriComponent(redirection, Engine.CHARSET_UTF8));
	}

	/**
	 * @return string
	 */
	public static final String GetLogoutUrl() {
		
		
		return "_sys/logout";
	}

	/**
	 * @param back
	 * @return string
	 */
	public static final String GetLogoutUrl(final Object back) {
		
		
		return "_sys/logout" + (back == null || back.toString().length() == 0
			? ""
			: "?back=" + back);
	}

	/**
	 * @return string
	 */
	public static final String getRuntimeBuild() {
		
		
		return String.valueOf(Know.systemBuild());
	}

	/**
	 * @return string
	 */
	public static final String getRuntimeName() {
		
		
		return "acm.environment ][";
	}

	/**
	 * @return string
	 */
	public static final String getRuntimeVersion() {
		
		
		return Know.systemVersion();
	}

	/**
	 * @return ACM
	 */
	public static final String getSystemName() {
		
		
		return "ACM";
	}

	/**
	 * @return Engine.VERSION_STRING
	 */
	public static final String getSystemVersion() {
		
		
		return Engine.VERSION_STRING;
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getUserID(final ExecProcess ctx) {
		
		
		return Context.getUserId(ctx);
	}

	/**
	 * @param owner
	 * @param type
	 * @param text
	 */
	public static final void log(final String owner, final String type, final String text) {
		
		
		Report.event(owner, type, text);
	}

	/**
	 * @param process
	 * @param o
	 * @param attributes
	 * @throws Exception
	 */
	public static final void Return(final ExecProcess process, final Object o, final BaseObject attributes) throws Exception {
		
		
		if (o == null) {
			throw Reply.exceptionEmpty("RT-RETURN", Context.getRequest(process)).setAttributes(attributes).setFinal();
		}
		if (o.getClass() == String.class) {
			throw Reply.exceptionString("RT-RETURN", Context.getRequest(process), (String) o).setAttributes(attributes).setFinal();
		}
		if (o instanceof TransferCopier) {
			throw Reply.exceptionBinary("RT-RETURN", Context.getRequest(process), ((TransferCopier) o).nextCopy()).setAttributes(attributes).setFinal();
		}
		if (o instanceof TransferBuffer) {
			throw Reply.exceptionBinary("RT-RETURN", Context.getRequest(process), (TransferBuffer) o).setAttributes(attributes).setFinal();
		}
		if (o instanceof BaseMessage) {
			final BinaryMessage<?> message = ((BaseMessage) o).toBinary();
			final BaseObject messageAttributes = message.getAttributes();
			final AbstractReplyException result = Reply.exceptionBinary("RT-RETURN", Context.getRequest(process), message.getBinary());
			if (messageAttributes != null) {
				result.setAttributes(messageAttributes);
			}
			if (attributes != null) {
				result.setAttributes(attributes);
			}
			throw result.setFinal();
		}
		if (o instanceof Value<?>) {
			final Object baseValue = ((Value<?>) o).baseValue();
			if (baseValue != null && baseValue != o) {
				RuntimeSAPI.Return(process, ((Value<?>) o).baseValue(), attributes);
				throw new UnsupportedOperationException("Cannot be here!");
			}
		}
		throw Reply.exception(Reply.object(
				"RT-RETURN", //
				Context.getRequest(process),
				BaseObject.createObject(attributes),
				o) //
				.setFinal());
	}

	/**
	 * @param fieldName
	 * @param phrase
	 * @return string
	 */
	public static final String searchPrepareField(final String fieldName, final String phrase) {
		
		
		if (fieldName.length() == 0) {
			return phrase;
		}
		final StringBuilder result = new StringBuilder();
		final StringBuilder buffer = new StringBuilder();
		boolean collecting = true;
		for (int i = 0; i < phrase.length(); ++i) {
			final char c = phrase.charAt(i);
			if (collecting) {
				if (!Character.isJavaIdentifierPart(c)) {
					if (buffer.length() > 0) {
						result.append(' ').append(fieldName).append(':').append(buffer.toString());
						buffer.setLength(0);
					}
					collecting = false;
				} else {
					buffer.append(c);
				}
			} else {
				if (Character.isJavaIdentifierPart(c)) {
					collecting = true;
					buffer.append(c);
				}
			}
		}
		if (buffer.length() > 0) {
			result.append(' ').append(fieldName).append(':').append(buffer.toString());
		}
		return result.toString();
	}

	/**
	 * @param ctx
	 * @param language
	 */
	public static final void setLanguage(final ExecProcess ctx, final String language) {
		
		
		final Context context = Context.getContext(ctx);
		context.getServer().ensureAuthorization(AuthLevels.AL_AUTHORIZED_AUTOMATICALLY);
		final AccessUser<?> user = context.getUser();
		user.setLanguage(language);
		user.commit();
	}

	/**
	 *
	 */
	public final String ENTRANCE;

	private final RuntimeEnvironment RTE;

	private final Server server;

	/**
	 * @param server
	 * @param RTE
	 */
	public RuntimeSAPI(final Server server, final RuntimeEnvironment RTE) {
		super(RuntimeSAPI.PROTOTYPE);
		this.server = server;
		this.RTE = RTE;
		this.ENTRANCE = server.getProperty("entrance", "DEFAULT");
	}

	/**
	 * @param ctx
	 * @param url
	 * @return string
	 */
	public final String getLinkagePrivatePath(final ExecProcess ctx, final String url) {
		
		
		if (url == null || url.length() == 0) {
			return "";
		}
		final String currentPrefix = Context.getRequest(ctx).getTargetExact();
		if (url.startsWith("http://" + currentPrefix)) {
			return url.substring(("http://" + currentPrefix).length() + 1);
		}
		if (url.startsWith("https://" + currentPrefix)) {
			return url.substring(("https://" + currentPrefix).length() + 1);
		}
		if (url.startsWith("/_finder/path/")) {
			return url.substring("/_finder/path".length());
		}
		if (url.startsWith("/_finder/locate/")) {
			final String id = url.substring("/_finder/locate/".length());
			final int pos = id.indexOf('/');
			final String finderIdentity = id.substring(0, pos);
			final String finderParameter = id.substring(pos + 1);
			final ControlEntry<?> entry = Produce.object(ControlEntry.class, finderIdentity, null, finderParameter);
			if (entry == null) {
				return url;
			}
			return this.server.getControlShareRootLocationForControlLocation(entry.getLocationControl());
		}
		return url;
	}

	/**
	 * @param path
	 * @return string
	 */
	public final String getLinkagePublicUrl(final String path) {
		
		
		if (path == null || path.length() == 0) {
			return "";
		}
		final String relative;
		if (path.charAt(0) == '/') {
			if (path.length() == 1) {
				return "";
			}
			relative = path.endsWith("/")
				? path.substring(1, path.length() - 1)
				: path.substring(1);
		} else {
			relative = path.endsWith("/")
				? path.substring(0, path.length() - 1)
				: path;
		}
		if (relative.length() == 0) {
			return "";
		}
		final ControlNode<?> root = this.server.getControlShareRoot();
		final int pos = relative.lastIndexOf('/');
		if (pos <= 0) {
			final ControlEntry<?> node = Control.relativeNode(root, relative);
			if (node == null) {
				return "/_finder/path/" + relative;
			}
			final String finder = node.restoreFactoryIdentity();
			if (finder == null) {
				return "/_finder/path/" + relative;
			}
			return "/_finder/locate/" + finder + '/' + node.restoreFactoryParameter();
		}
		final String nodePath = relative.substring(0, pos);
		final String entryName = relative.substring(pos + 1);
		final ControlNode<?> node = Control.relativeNode(root, nodePath);
		if (node == null) {
			return "/_finder/path/" + relative;
		}
		final ControlEntry<?> entry = node.getContentEntry(entryName);
		if (entry == null) {
			return "/_finder/path/" + relative;
		}
		final String finder = entry.restoreFactoryIdentity();
		if (finder == null) {
			return "/_finder/path/" + relative;
		}
		return "/_finder/locate/" + finder + '/' + entry.restoreFactoryParameter();
	}

	/**
	 * @return node
	 */
	public final ControlNode<?> getLinkageRootNode() {
		
		
		return this.server.getControlShareRoot();
	}

	/**
	 * @return string array
	 */
	public final String[] getLinkageRootPoints() {
		
		
		return this.server.getControlSharePoints();
	}

	/**
	 * @return string
	 */
	public final String getMainEntranceUrl() {
		
		
		return this.ENTRANCE;
	}

	/**
	 * @return runtime
	 */
	public final RuntimeEnvironment getRuntime() {
		
		
		return this.RTE;
	}

	/**
	 * @param map
	 * @return boolean
	 */
	public final boolean SendMail(final BaseObject map) {
		
		
		return this.RTE.getEmailSender().sendEmail(new Email(map));
	}

	@Override
	public final String toString() {
		
		
		return "[object RuntimeSAPI(server=" + this.server + ")]";
	}
}
