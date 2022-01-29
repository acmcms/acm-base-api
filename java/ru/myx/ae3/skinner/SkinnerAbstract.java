package ru.myx.ae3.skinner;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostObject;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.base.BaseString;
import ru.myx.ae3.common.BodyAccessCharacter;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.eval.collection.RenderCollection;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.help.Text;
import ru.myx.ae3.l2.LayoutDefinition;
import ru.myx.ae3.l2.TargetContext;
import ru.myx.ae3.l2.skin.Skin;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.vfs.Entry;

/** @author myx */
public abstract class SkinnerAbstract extends BaseHostObject implements Skinner {
	
	private static final String OWNER = "SKINNER-NEW-ABSTRACT";
	
	/**
	 *
	 */
	protected String defaultDocument;
	
	/**
	 *
	 */
	protected String defaultContentType;
	
	/**
	 *
	 */
	protected String defaultDefaultDocument;
	
	/**
	 *
	 */
	protected String defaultDefaultContentType;
	
	/** skin id */
	protected final String name;
	
	/**
	 *
	 */
	protected ControlFieldset<?> fieldset = ControlFieldset.createFieldset();
	
	/**
	 *
	 */
	protected String prototypeSkin;
	
	/**
	 *
	 */
	protected boolean requireAuth;
	
	/**
	 *
	 */
	protected boolean requireSecure;
	
	/**
	 *
	 */
	protected BaseMap settings = new BaseNativeObject();
	
	/**
	 *
	 */
	protected boolean useFieldset = false;
	
	/**
	 *
	 */
	protected Map<String, String> importMappings;
	
	/**
	 *
	 */
	protected boolean ownSkin = true;
	
	/**
	 *
	 */
	protected BaseObject title;
	
	/**
	 *
	 */
	protected RenderCollection service;
	
	/**
	 *
	 */
	protected RenderCollection templates;
	
	/**
	 *
	 */
	protected RenderCollection layouts;
	
	/** @param name
	 * @param title
	 * @param service
	 * @param templates
	 * @param layouts */
	public SkinnerAbstract(final String name, final BaseObject title, final RenderCollection service, final RenderCollection templates, final RenderCollection layouts) {
		
		super(Skinner.PROTOTYPE);
		this.name = name;
		this.title = title;
		this.service = service;
		this.templates = templates;
		this.layouts = layouts;
	}
	
	@Override
	public abstract LayoutDefinition<TargetContext<?>> getLayoutDefinition(final String name);
	
	@Override
	public final String getName() {
		
		return this.name;
	}
	
	@Override
	public abstract Entry getRoot();
	
	@Override
	public Skin getSkinParent() {
		
		return this.prototypeSkin != null
			? Context.getServer(Exec.currentProcess()).getSkinner(this.prototypeSkin)
			: null;
	}
	
	@Override
	public final BaseMap getSkinSettings() {
		
		return this.settings;
	}
	
	@Override
	public final ControlFieldset<?> getSkinSettingsFieldset() {
		
		return this.fieldset;
	}
	
	@Override
	public final BaseObject getTitle() {
		
		return this.title;
	}
	
	@Override
	public final ReplyAnswer handleReply(final ReplyAnswer reply) {
		
		if (reply == null) {
			return null;
		}
		int loopsLeft = 1000;
		for (ReplyAnswer response = reply;;) {
			if (--loopsLeft <= 0) {
				throw new RuntimeException("Too many skinner loops, skin=" + this + "\n\treply=" + reply);
			}
			if (response.isFinal()) {
				/** <code>
				System.out.println( ">>>>> handleReply, ["
						+ System.identityHashCode( response )
						+ "], response="
						+ response.getClass().getName()
						+ ", final=true" );
				 </code> */
				return response;
			}
			final int code = response.getCode();
			if (code / 100 == 3) {
				/** <code>
				System.out.println( ">>>>> handleReply, ["
						+ System.identityHashCode( response )
						+ "], response="
						+ response.getClass().getName()
						+ ", redirect=true" );
				 </code> */
				return response;
			}
			/** <code>
			System.out.println( ">>>>> handleReply, ["
					+ System.identityHashCode( response )
					+ "], response="
					+ response.getClass().getName() );
			 </code> */
			final ReplyAnswer newResponse = this.handleReplyOnce(response);
			if (response == newResponse) {
				/** <code>
				System.out.println( ">>>>> handleReply, ["
						+ System.identityHashCode( response )
						+ "], response="
						+ response.getClass().getName()
						+ ", stop" );
				 </code> */
				return code == 200
					? this.handleServiceFinalReplyException(response.setFinal())
					: response.setFinal();
			}
			if (newResponse == null) {
				/** <code>
				System.out.println( ">>>>> handleReply, ["
						+ System.identityHashCode( response )
						+ "], response="
						+ response.getClass().getName()
						+ ", null" );
				 </code> */
				return null;
			}
			response = newResponse;
			if (response.getCode() < 300 && code >= 400) {
				/** <code>
				System.out.println( ">>>>> handleReply, ["
						+ System.identityHashCode( response )
						+ "], response="
						+ response.getClass().getName()
						+ ", code="
						+ code );
				 </code> */
				response.setCode(code);
			}
		}
	}
	
	/** Please:
	 *
	 * if (result == content) { return response; }
	 *
	 *
	 * @param response
	 * @param content
	 * @return */
	protected abstract ReplyAnswer handleReplyMapOnce(final ReplyAnswer response, final BaseObject content);
	
	/* { */
	/** The code<code>
			if (this.prototypeSkin != null) {
				final Skinner skinner = Context.getServer().getSkinner( this.prototypeSkin );
				if (skinner != null) {
					return skinner.handleReply[Map]Once( response[, context] );
				}
			}
			</code> is not needed, it is done indirectly in 'handleReplyOnce' method
	 * implementation */
	// return response;
	// }
	
	@Override
	public final ReplyAnswer handleReplyOnce(final ReplyAnswer response) {
		
		/** response attributes */
		final BaseObject attributes = response.getAttributes();
		/** context object */
		final BaseObject content;
		{
			/** Binary -- not now 8-( */
			if (response.isBinary() || response.isFile()) {
				/** <code>
				System.out.println( ">>>>> handleReplyOnce, ["
						+ System.identityHashCode( response )
						+ "], skinClass="
						+ this.getClass().getName()
						+ ", reply="
						+ response.getClass().getName()
						+ ", objectClass="
						+ (objectClass == null
								? null
								: objectClass.getName())
						+ ", binary or file" );
				 </code> */
				/** same response means 'stop processing' */
				return response;
			}
			/** Text */
			if (response.isCharacter()) {
				/** <code>
				System.out.println( ">>>>> handleReplyOnce, ["
						+ System.identityHashCode( response )
						+ "], skinClass="
						+ this.getClass().getName()
						+ ", reply="
						+ response.getClass().getName()
						+ ", objectClass="
						+ (objectClass == null
								? null
								: objectClass.getName())
						+ ", textual" );
				 </code> */
				final CharSequence body = ((BodyAccessCharacter) response).getText();
				assert body != null : "Text response cannot provide NULL text!";
				content = new BaseNativeObject();
				assert content.baseDefine(
						"x-debug-created-from", //
						"text response, class=" + response.getClass().getName(),
						BaseProperty.ATTRS_MASK_WED) : "debug";
				content.baseDefine("subject", response.getSubject());
				content.baseDefine("title", response.getTitle());
				content.baseDefine("template", Base.get(attributes, "template", BaseObject.UNDEFINED));
				content.baseDefine("body", body);
			} else
			/** Empty */
			if (response.isEmpty()) {
				/** <code>
				System.out.println( ">>>>> handleReplyOnce, ["
						+ System.identityHashCode( response )
						+ "], skinClass="
						+ this.getClass().getName()
						+ ", reply="
						+ response.getClass().getName()
						+ ", objectClass="
						+ (objectClass == null
								? null
								: objectClass.getName())
						+ ", empty" );
				 </code> */
				final CharSequence body = BaseString.EMPTY;
				content = new BaseNativeObject();
				assert content.baseDefine(
						"x-debug-created-from", //
						"empty response, class=" + response.getClass().getName(),
						BaseProperty.ATTRS_MASK_WED) : "debug";
				content.baseDefine("subject", response.getSubject());
				content.baseDefine("title", response.getTitle());
				content.baseDefine("template", Base.get(attributes, "template", BaseObject.UNDEFINED));
				content.baseDefine(
						"body",
						body != null
							? body
							: "");
			} else
			/** object response */
			if (response.getObjectClass() != null) {
				final Object object = response.getObject();
				assert object != null : "NULL object value even though object class is not null";
				/** will not skin embedded messages - kind of attachment protocol */
				if (object instanceof BaseMessage) {
					/** <code>
					System.out.println( ">>>>> handleReplyOnce, ["
							+ System.identityHashCode( response )
							+ "], skinClass="
							+ this.getClass().getName()
							+ ", reply="
							+ response.getClass().getName()
							+ ", objectClass="
							+ objectClass.getName()
							+ ", message" );
					 </code> */
					/** same response means 'stop processing' */
					return response;
				}
				/** Java Map, goes before BaseObject - it is designed for BaseNative and
				 * BaseIdentity */
				if (object instanceof BaseMap) {
					/** <code>
					System.out.println( ">>>>> handleReplyOnce, ["
							+ System.identityHashCode( response )
							+ "], skinClass="
							+ this.getClass().getName()
							+ ", reply="
							+ response.getClass().getName()
							+ ", objectClass="
							+ objectClass.getName()
							+ ", native || identity" );
					 </code> */
					final BaseObject baseObject = (BaseObject) object;
					if (Base.getBoolean(baseObject, "template", false)) {
						content = baseObject;
					} else //
					if (attributes != null) {
						final BaseObject fromAttributes = attributes.baseGet("template", BaseObject.UNDEFINED);
						if (fromAttributes == BaseObject.UNDEFINED) {
							content = baseObject;
						} else {
							content = BaseObject.createObject(baseObject);
							content.baseDefine("template", fromAttributes);
						}
					} else {
						content = baseObject;
					}
				} else
				/** Java Map, goes before BaseObject - it is designed for BaseNative and
				 * BaseIdentity */
				if (object instanceof Map<?, ?>) {
					/** <code>
					System.out.println( ">>>>> handleReplyOnce, ["
							+ System.identityHashCode( response )
							+ "], skinClass="
							+ this.getClass().getName()
							+ ", reply="
							+ response.getClass().getName()
							+ ", objectClass="
							+ objectClass.getName()
							+ ", map" );
					 </code> */
					/** clone */
					final Map<?, ?> map = (Map<?, ?>) object;
					content = new BaseNativeObject();
					assert content.baseDefine(
							"x-debug-created-from", //
							"object, Map<K,V>, mapClass=" + map.getClass().getName() + ", responseClass=" + response.getClass().getName(),
							BaseProperty.ATTRS_MASK_WED) : "debug";
					content.baseDefine("subject", response.getSubject());
					content.baseDefine("title", response.getTitle());
					content.baseDefine("template", Base.get(attributes, "template", BaseObject.UNDEFINED));
					for (final Map.Entry<?, ?> current : map.entrySet()) {
						content.baseDefine((String) current.getKey(), Base.forUnknown(current.getValue()));
					}
				} else
				/** BaseObject */
				if (object instanceof BaseObject) {
					final BaseObject base = (BaseObject) object;
					if (base.baseIsPrimitive()) {
						/** <code>
						System.out.println( ">>>>> handleReplyOnce, ["
								+ System.identityHashCode( response )
								+ "], skinClass="
								+ this.getClass().getName()
								+ ", reply="
								+ response.getClass().getName()
								+ ", objectClass="
								+ objectClass.getName()
								+ ", realObjectClass="
								+ object.getClass().getName()
								+ ", base primitive" );
						 </code> */
						final String text = base.baseToJavaString();
						content = new BaseNativeObject();
						assert content.baseDefine(
								"x-debug-created-from", //
								"object, BasePrimitive, objectClass=" + base.getClass().getName() + ", responseClass=" + response.getClass().getName()) : "debug";
						content.baseDefine("subject", response.getSubject());
						content.baseDefine("title", response.getTitle());
						content.baseDefine("template", Base.get(attributes, "template", BaseObject.UNDEFINED));
						content.baseDefine("body", text);
					} else {
						final Object baseValue = base.baseValue();
						if (baseValue != base && baseValue != null) {
							/** <code>
							System.out.println( ">>>>> handleReplyOnce, ["
									+ System.identityHashCode( response )
									+ "], was base value, object="
									+ object.getClass().getName()
									+ ", base="
									+ baseValue.getClass().getName() );
							 </code> */
							return Reply.object(SkinnerAbstract.OWNER, response.getQuery(), BaseObject.createObject(attributes), baseValue);
						}
						/** <code>
						System.out.println( ">>>>> handleReplyOnce, ["
								+ System.identityHashCode( response )
								+ "], skinClass="
								+ this.getClass().getName()
								+ ", reply="
								+ response.getClass().getName()
								+ ", objectClass="
								+ objectClass.getName()
								+ ", realObjectClass="
								+ object.getClass().getName()
								+ ", base" );
						 </code> */
						content = new BaseNativeObject(base);
						assert content.baseDefine(
								"x-debug-created-from", //
								"object, BaseObject<V>, objectClass=" + base.getClass().getName() + ", responseClass=" + response.getClass().getName()) : "debug";
						if (!Base.getBoolean(content, "subject", false)) {
							content.baseDefine("subject", response.getSubject());
						}
						if (!Base.getBoolean(content, "title", false)) {
							content.baseDefine("title", response.getTitle());
						}
						if (attributes != null && !Base.getBoolean(content, "template", false)) {
							final BaseObject fromAttributes = attributes.baseGet("template", BaseObject.UNDEFINED);
							if (fromAttributes != BaseObject.UNDEFINED) {
								content.baseDefine("template", fromAttributes);
							}
						}
					}
				} else
				/** exotic conversion */
				{
					if (object instanceof Value<?>) {
						final Object base = ((Value<?>) object).baseValue();
						if (base != null && base != object) {
							/** <code>
							System.out.println( ">>>>> handleReplyOnce, ["
									+ System.identityHashCode( response )
									+ "], was value, object="
									+ object.getClass().getName()
									+ ", base="
									+ base.getClass().getName() );
							 </code> */
							return Reply.object(
									SkinnerAbstract.OWNER, //
									response.getQuery(),
									attributes,
									base);
						}
					}
					/** <code>
					System.out.println( ">>>>> handleReplyOnce, ["
							+ System.identityHashCode( response )
							+ "], skinClass="
							+ this.getClass().getName()
							+ ", reply="
							+ response.getClass().getName()
							+ ", objectClass="
							+ objectClass.getName()
							+ ", convert object to text" );
					 </code> */
					assert !(response.getObject() instanceof BaseMessage) : "Response should report correct class, responseClass=" + response.getClass().getName()
							+ ", objectClass=" + response.getObject().getClass();
					content = new BaseNativeObject();
					content.baseDefine("subject", response.getSubject());
					content.baseDefine("title", response.getTitle());
					content.baseDefine("template", Base.get(attributes, "template", BaseObject.UNDEFINED));
					try {
						final CharSequence text = response.toCharacter().getText();
						content.baseDefine("body", text);
					} catch (final Throwable e) {
						content.baseDefine("body", e.toString());
					}
				}
			} else
			/** exotic conversion */
			{
				/** <code>
				System.out.println( ">>>>> handleReplyOnce, ["
						+ System.identityHashCode( response )
						+ "], skinClass="
						+ this.getClass().getName()
						+ ", reply="
						+ response.getClass().getName()
						+ ", objectClass=null"
						+ ", convert unknown to text" );
				 </code> */
				assert !(response.getObject() instanceof BaseMessage) : "Response should report correct class, responseClass=" + response.getClass().getName() + ", objectClass="
						+ response.getObject().getClass();
				content = new BaseNativeObject();
				assert content.baseDefine(
						"x-debug-created-from", //
						"object, dummy, responseClass=" + response.getClass().getName()) : "debug";
				content.baseDefine("subject", response.getSubject());
				content.baseDefine("title", response.getTitle());
				content.baseDefine("template", Base.get(attributes, "template", BaseObject.UNDEFINED));
				try {
					final CharSequence text = response.toCharacter().getText();
					content.baseDefine("body", text);
				} catch (final Throwable e) {
					content.baseDefine("body", e.toString());
				}
			}
			
			if (this.useFieldset) {
				this.fieldset.dataRetrieve(content, content);
			}
		}
		/** call map */
		{
			final ReplyAnswer answer = this.handleReplyMapOnce(response, content);
			if (answer != null && answer != response) {
				return answer;
			}
		}
		/** failover to prototype */
		if (this.prototypeSkin != null) {
			final Skinner skinner = Context.getServer(Exec.currentProcess()).getSkinner(this.prototypeSkin);
			if (skinner != null && skinner != this) {
				try {
					final ReplyAnswer answer = skinner.handleReplyOnce(response);
					if (answer != null && answer != response) {
						return answer;
					}
				} catch (final AbstractReplyException e) {
					final ReplyAnswer answer = e.getReply();
					if (answer != null && answer != response) {
						return answer;
					}
				}
			}
		}
		return response;
	}
	
	/** Implement it when you need to finalize (modify) service messages that are going to be
	 * returned 'as is' otherwise.
	 *
	 * Only 'final' and 'successful' (code == 200) replies from 'service' collection will be passed.
	 *
	 * @param reply
	 * @return */
	@SuppressWarnings("static-method")
	protected ReplyAnswer handleServiceFinalReplyException(final ReplyAnswer reply) {
		
		return reply;
	}
	
	@Override
	public final boolean isAbstract() {
		
		final BaseMap skinSettings = this.getSkinSettings();
		assert skinSettings != null : "NULL java value for skin settings, class=" + this.getClass().getName();
		return Convert.MapEntry.toBoolean(skinSettings, "abstract", this.isAbstractDefault());
	}
	
	/** @return */
	@SuppressWarnings("static-method")
	protected boolean isAbstractDefault() {
		
		return false;
	}
	
	@Override
	public final ReplyAnswer onQuery(final ServeRequest query) {
		
		final String request = query.getResourceIdentifier();
		if (request == null) {
			// super. is already called!
			return null;
		}
		final int length = request.length();
		if (length == 0 || '/' != request.charAt(0)) {
			/** both: not a proxy and invalid query */
			return Reply.string(SkinnerAbstract.OWNER, query, "Bad query URI: " + request) //
					.setCode(Reply.CD_BADQUERY)//
					.setFinal();
		}
		if (length > 6 && "!/".regionMatches(0, request, 1, 2)) {
			final String keyword;
			{
				final int pos = request.indexOf('/', 3); // "/!/'
				keyword = pos == -1
					? null
					: request.substring(3, pos);
			}
			if (keyword != null) {
				switch (keyword.length()) {
					case 4 : {
						if ("skin".equals(keyword)) {
							final int pos = request.indexOf('/', 8); // "/!/skin/"
							if (pos == -1) {
								break;
							}
							final String skinnerName = request.substring(8, pos);
							final Skinner skinner = Context.getServer(Exec.currentProcess()).getSkinner(skinnerName);
							if (skinner == null) {
								return Reply.string(
										SkinnerAbstract.OWNER, //
										query,
										"Bad skin chooser request, not found: " + skinnerName) //
										.setCode(Reply.CD_UNKNOWN);
							}
							query.shiftRequested(pos, true);
							// System.out.println(" >>> >>>>> 0: " + this + ", skinner: " +
							// skinner);
							return skinner.handleReply(skinner.onQuery(query));
						}
						break;
					}
					default :
				}
			}
			return Reply.string(SkinnerAbstract.OWNER, query, "Bad skin chooser request, invalid format: " + request).setCode(Reply.CD_UNKNOWN);
		}
		{
			/** only for the first time. 8-) */
			if (query.getAttributes().baseGet("skinner", null) == null) {
				query.setAttribute("skinner", this);
			}
		}
		if (this.requireSecure() && !Convert.MapEntry.toBoolean(query.getAttributes(), "Secure", false)) {
			final ReplyAnswer redirection = query.toSecureChannel();
			return redirection == null
				? Reply.string(
						SkinnerAbstract.OWNER, //
						query,
						"Not secure interface: try another one!") //
						.setCode(Reply.CD_DENIED)
				: redirection;
		}
		if (this.requireAuth()) {
			final ExecProcess process = Exec.currentProcess();
			final Server server = Context.getServer(process);
			server.ensureAuthorization(AuthLevels.AL_AUTHORIZED_HIGH);
			final AccessUser<?> user = Context.getUser(process);
			if (user == null) {
				return Reply.stringForbidden(SkinnerAbstract.OWNER, query, "Access denied!");
			}
			final BaseObject session = Context.getSessionData(process);
			final String key = "session-" + query.getTarget();
			if (!Base.getBoolean(session, key, false)) {
				/** TODO - authorization should do enough AUDIT on it's own, pass some info for
				 * logging. <code>
				 * </code> */
				Report.audit(SkinnerAbstract.OWNER, "SESSION-START", query.getSourceAddress() + ',' + user.getKey() + ',' + user.getLogin());
				session.baseDefine(key, "started");
			}
			if (length == 9 && request.equals("/re-login")) {
				Context.getSessionData(process).baseDelete(key);
				final String url = Text.encodeUri("index.htm", StandardCharsets.UTF_8);
				server.ensureAuthorization(AuthLevels.AL_AUTHORIZED_HIGHER);
				final String from = Base.getString(query.getParameters(), "uid", null);
				final String name = Context.getUserId(process);
				if (from.equals(name)) {
					return Reply.stringUnauthorized(
							SkinnerAbstract.OWNER, //
							query,
							"control");
				}
				return Reply.redirect(
						SkinnerAbstract.OWNER, //
						query,
						false,
						server.fixUrl(url))//
						.setPrivate() //
						.setSessionID(Context.getSessionId(process)) //
				;
			}
		}
		{
			final ReplyAnswer answer = this.renderQueryImpl(query);
			// System.out.println(" >>> >>>>> D: " + this + ", answer:" + answer);
			return answer;
		}
	}
	
	/** @param query
	 * @return */
	protected ReplyAnswer renderQueryImpl(final ServeRequest query) {
		
		final String request = query.getResourceIdentifier();
		if (request == null || request.length() <= 1) {
			// super. is already called!
			return null;
		}
		/**
		 *
		 */
		assert request.charAt(0) == '/' : "Paths should start with / aren't they? resourceId: " + request + ", resourcePrefix: " + query.getResourcePrefix() + ", urlBase: "
				+ query.getUrlBase() + ", urlOriginal: " + query.getUrl();
		/** do not call prototype skin for 'default' queries */
		final Map<String, String> importMappings = this.importMappings;
		// System.out.println(" >>> >>>>> 1: " + this + ", importMappings: " + importMappings);
		if (this.prototypeSkin != null) {
			final Skinner skinner = Context.getServer(Exec.currentProcess()).getSkinner(this.prototypeSkin);
			// System.out.println(" >>> >>>>> 2: " + this + ", skinner: " + skinner);
			if (skinner != null && skinner != this) {
				final ReplyAnswer answer = skinner.onQuery(query);
				if (answer != null || importMappings == null) {
					// System.out.println(" >>> >>>>> 3: " + this + ", answer: " + answer);
					return answer;
				}
				
				assert request.equals(query.getResourceIdentifier()) //
				: "Changed resource identifier (" + Format.Ecma.string(request) + " -> " + Format.Ecma.string(query.getResourceIdentifier()) + ") with no reply: skin=" + skinner;
				
			}
		}
		/**
		 *
		 */
		if (importMappings != null && request.length() > 1) {
			final int pos = request.indexOf('/', 1);
			if (pos == -1) {
				final String key = importMappings.get(request.substring(1));
				if (key != null) {
					return Reply.redirect(SkinnerAbstract.OWNER, query, true, query.getResourcePrefix() + request + '/');
				}
			} else {
				final String key = importMappings.get(request.substring(1, pos));
				if (key != null) {
					query.shiftRequested(pos, true);
					final Skinner skinner = Context.getServer(Exec.currentProcess()).getSkinner(key);
					if (skinner == null) {
						return Reply.stringUnknown(
								SkinnerAbstract.OWNER, //
								query,
								"import for '" + query.getResourcePrefix() + "' mapped to '" + key + "' but no corresponding module found");
					}
					final ReplyAnswer answer = skinner.onQuery(query);
					// System.out.println(" >>> >>>>> 4: " + this + ", answer: " + answer);
					return answer == null
						? skinner.handleReply(
								Reply.stringUnknown(
										"SKIN-ABSTRACT-IMPORT", //
										query,
										"Not found: " + query.getResourceIdentifier()))
						: this.ownSkin
							? skinner.handleReply(answer)
							: answer;
				}
			}
		}
		// super. is already called!
		return null;
	}
	
	/** Returns false by default. Will enforce HIGH authorization for secure interface if overridden
	 * and returns true.
	 *
	 * @return */
	@Override
	public final boolean requireAuth() {
		
		return this.requireAuth;
	}
	
	/** Returns false by default. Will enforce check for secure interface if overridden and returns
	 * true.
	 *
	 * @return */
	@Override
	public final boolean requireSecure() {
		
		return this.requireSecure;
	}
}
