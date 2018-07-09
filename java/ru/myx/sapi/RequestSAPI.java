package ru.myx.sapi;

import java.util.Iterator;

import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Act;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostDataSubstitution;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseMapEditable;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.binary.TransferBuffer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.flow.BinaryMessage;
import ru.myx.ae3.flow.CharacterMessage;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.help.Message;
import ru.myx.ae3.help.QueryString;
import ru.myx.ae3.reflect.Reflect;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.AbstractServeRequestMutable;
import ru.myx.ae3.serve.Request;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.skinner.Skinner;

/**
 * @author myx
 *
 */
public final class RequestSAPI implements BaseHostDataSubstitution<BaseObject> {
	
	
	static final class RequestSkinLocal extends AbstractServeRequestMutable<RequestSkinLocal> {
		
		
		private final Skinner skinner;

		RequestSkinLocal(
				final String owner,
				final String verb,
				final String resource,
				final BaseObject attributes,
				final BaseObject parameters,
				final Skinner skinner,
				final ServeRequest current) {
			
			super(owner, verb, attributes);
			this.sessionID = current.getSessionID();
			this.userID = current.getUserID();
			this.setUrl(current.getUrl());
			this.setUrlBase(current.getUrlBase());

			this.setAttachment(current.getAttachment());

			this.setResourceIdentifier(resource);
			this.setParameters(parameters);

			this.skinner = skinner;
		}

		@Override
		public String toString() {
			
			
			return "QUERY:SKIN: " + this.verb + " " + this.skinner + " " + this.resourcePrefix + this.resourceIdentifier;
		}
	}

	/**
	 * The only instance of RequestSAPI class
	 */
	public static final RequestSAPI INSTANCE;

	/**
	 *
	 */
	public static final BaseObject PROTOTYPE;

	static {
		PROTOTYPE = Reflect.classToBasePrototype(RequestSAPI.class);
		{
			final BaseObject prototype = Reflect.classToBasePrototype(Request.class);
			for (final Iterator<? extends CharSequence> keys = prototype.baseKeysOwnAll(); keys.hasNext();) {
				final CharSequence key = keys.next();
				if (RequestSAPI.PROTOTYPE.baseFindProperty(key) == null) {
					RequestSAPI.PROTOTYPE.baseDefine(key, prototype.baseGet(key, BaseObject.UNDEFINED), BaseProperty.ATTRS_MASK_NNN);
				}
			}
		}
		INSTANCE = new RequestSAPI();
	}

	/**
	 * @param attributes
	 * @return
	 */
	private static BaseMapEditable createAttributes(final BaseObject attributes) {
		
		
		return attributes == null || attributes == BaseObject.NULL || attributes == BaseObject.UNDEFINED
			? BaseObject.createObject()
			: BaseObject.createObject(attributes);
	}

	/**
	 * @param o
	 * @return map
	 */
	public static final BaseObject extractMessageAttributes(final BaseMessage o) {
		
		
		return o.getAttributes();
	}

	/**
	 * @param ctx
	 * @return string array
	 */
	public static final String[] getArguments(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getArguments();
	}

	/**
	 * @param ctx
	 * @return object
	 */
	public static final Object getAttachment(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getAttachment();
	}

	/**
	 * @param ctx
	 * @return map
	 */
	public static final BaseObject getAttributes(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getAttributes();
	}

	/**
	 * @param ctx
	 * @return request
	 */
	public static final ServeRequest getCurrentRequest(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx);
	}

	/**
	 * @param ctx
	 * @return map
	 */
	public static final BaseObject getData(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getParameters();
	}

	/**
	 * @param ctx
	 * @return date
	 */
	public static final long getDate(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getDate();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getEventTypeId(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getEventTypeId();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getLanguage(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getLanguage();
	}

	/**
	 * @param message
	 * @param name
	 * @return string
	 */
	public static final String getMessageAttribute(final BaseMessage message, final String name) {
		
		
		return Message.cleanAttributeValue(message, name, "");
	}

	/**
	 * @param message
	 * @param name
	 * @param defaultValue
	 * @return string
	 */
	public static final String getMessageAttribute(final BaseMessage message, final String name, final String defaultValue) {
		
		
		return Message.cleanAttributeValue(message, name, defaultValue);
	}

	/**
	 * @param message
	 * @param name
	 * @param subName
	 * @return string
	 */
	public static final String getMessageSubAttribute(final BaseMessage message, final String name, final String subName) {
		
		
		return Message.subAttributeValue(message, name, subName, "");
	}

	/**
	 * @param message
	 * @param name
	 * @param subName
	 * @param defaultValue
	 * @return string
	 */
	public static final String getMessageSubAttribute(final BaseMessage message, final String name, final String subName, final String defaultValue) {
		
		
		return Message.subAttributeValue(message, name, subName, defaultValue);
	}

	/**
	 * @param ctx
	 * @return string
	 */
	@Deprecated
	public static final String getOwner(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getEventTypeId();
	}

	/**
	 * @param ctx
	 * @return map
	 */
	public static final BaseObject getParameters(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getParameters();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getParameterString(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getParameterString();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getProtocolName(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getProtocolName();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getProtocolVariant(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getProtocolVariant();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getResourceIdentifier(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getResourceIdentifier();
	}

	/**
	 * @param process
	 * @return string
	 */
	public static final String getResourcePrefix(final ExecProcess process) {
		
		
		return Context.getRequest(process).getResourcePrefix();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getSessionID(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getSessionID();
	}

	/**
	 * @param ctx
	 * @return map
	 */
	public static final BaseObject getSettings(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getSettings();
	}

	/**
	 * @param ctx
	 * @return object
	 */
	public static final Object getSharedObject(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getAttachment();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getSourceAddress(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getSourceAddress();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getSourceAddressExact(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getSourceAddressExact();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getSubject(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getSubject();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getTarget(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getTarget();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getTargetExact(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getTargetExact();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getTitle(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getTitle();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getUrl(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getUrl();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getUrlBase(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getUrlBase();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getUserAgent(final ExecProcess ctx) {
		
		
		return Base.getString(Context.getRequest(ctx).getAttributes(), "User-Agent", null);
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getUserID(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getUserID();
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String getVerb(final ExecProcess ctx) {
		
		
		return Context.getRequest(ctx).getVerb();
	}

	/**
	 * @param url
	 * @param name
	 * @return string
	 */
	public static final String modifyQueryStringParameter(final String url, final String name) {
		
		
		return RequestSAPI.modifyQueryStringParameter(url, name, null);
	}

	/**
	 * @param url
	 * @param name
	 * @param value
	 * @return string
	 */
	public static final String modifyQueryStringParameter(final String url, final String name, final Object value) {
		
		
		final int pos = url.indexOf('?');
		final BaseMapEditable result = BaseObject.createObject(null);
		if (pos == -1) {
			if (value == null) {
				return url;
			}
			result.putAppend(name, Base.forUnknown(value));
			return url + '?' + QueryString.toQueryString(result, Engine.CHARSET_UTF8);
		}
		final BaseObject parameters = QueryString.parseQueryString(url.substring(pos + 1), Engine.CHARSET_UTF8);
		result.baseDefineImportOwnEnumerable(parameters);
		if (value == null) {
			result.baseDelete(name);
		} else {
			result.baseDefine(name, String.valueOf(value), BaseProperty.ATTRS_MASK_WED);
		}
		if (!result.baseHasKeysOwn()) {
			return url.substring(0, pos);
		}
		return url.substring(0, pos + 1) + QueryString.toQueryString(result, Engine.CHARSET_UTF8);
	}

	/**
	 * @param process
	 * @param argumentsMap
	 *            <code>
	 *            {
	 *            	timeout : 60,
	 *            	verb : 'GET',
	 *            	skinner : 'skin-failover',
	 *            	resource : '/',
	 *            	parameters : null,
	 *            	attributes : {
	 *            		"Via" : "RequestSAPI"
	 *            	}
	 *            }
	 * </code>
	 * @return not allowed to return NULL answer
	 * @throws Throwable
	 */
	public static final ReplyAnswer querySkin(final ExecProcess process, final BaseObject argumentsMap) throws Throwable {
		
		
		return RequestSAPI.querySkin(
				process, //
				Base.getLong(argumentsMap, "timeout", 60),
				Base.getString(argumentsMap, "verb", "GET"),
				Base.getJava(argumentsMap, "skinner", "skin-failover"),
				Base.getString(argumentsMap, "resource", "/"),
				Base.get(argumentsMap, "parameters", BaseObject.UNDEFINED),
				Base.get(argumentsMap, "attributes", BaseObject.UNDEFINED));
	}

	/**
	 *
	 * @param process
	 * @param timeout
	 * @param skinnerObj
	 * @param query
	 * @return allowed to return NULL answer
	 */
	public static final ReplyAnswer querySkin(final ExecProcess process, final long timeout, final Object skinnerObj, final ServeRequest query) {
		
		
		final Skinner skinner = skinnerObj instanceof Skinner
			? (Skinner) skinnerObj
			/**
			 * <code>
			: skinnerObj instanceof Value<?> && ((Value<?>) skinnerObj).baseValue() instanceof Skinner
					? (Skinner) ((Value<?>) skinnerObj).baseValue()
					</code>
			 */
			: Context.getServer(process).getSkinner(String.valueOf(skinnerObj));
		if (skinner == null) {
			throw new IllegalArgumentException("No such skinner: " + skinnerObj);
		}
		final ExecProcess context = Exec.createProcess(process, "Request.querySkin");
		Context.replaceQuery(context, query);
		try {
			return Act.run(context, arg -> {
				
				ReplyAnswer answer = skinner.onQuery(query);
				if (answer != null) {
					answer = skinner.handleReply(answer);
					if (answer != null) {
						return answer;
					}
				}
				/**
				 * allowed to return NULL answer
				 */
				return Reply.string("QUERY:SKIN", query, "not found: skin=" + skinner + ", query=" + query).setCode(Reply.CD_UNKNOWN);
			}, null);
		} catch (final AbstractReplyException e) {
			return e.getReply();
		} catch (final Throwable t) {
			Report.exception("QUERY:SKIN", "Unhandled exception while querying: skin=" + skinnerObj + ", query=" + query, t);
			return Reply.string("QUERY:SKIN", query, Format.Throwable.toText(t)).setCode(Reply.CD_EXCEPTION);
		}
	}

	/**
	 * @param process
	 * @param timeout
	 * @param verb
	 * @param skinnerObj
	 *            skinner or skinner name
	 * @param resource
	 * @param parameters
	 * @param attributes
	 * @return not allowed to return NULL answer
	 * @throws Throwable
	 */
	public static final ReplyAnswer querySkin(final ExecProcess process,
			final long timeout,
			final String verb,
			final Object skinnerObj,
			final String resource,
			final BaseObject parameters,
			final BaseObject attributes) throws Throwable {
		
		
		final Skinner skinner = skinnerObj instanceof Skinner
			? (Skinner) skinnerObj
			/**
			 * <code>
			: skinnerObj instanceof Value<?> && ((Value<?>) skinnerObj).baseValue() instanceof Skinner
					? (Skinner) ((Value<?>) skinnerObj).baseValue()
					</code>
			 */
			: Context.getServer(process).getSkinner(String.valueOf(skinnerObj));
		if (skinner == null) {
			throw new IllegalArgumentException("No such skinner: " + skinnerObj);
		}
		final ServeRequest query = new RequestSkinLocal(
				"LOCAL", //
				verb,
				resource,
				attributes instanceof BaseMap
					? attributes
					: BaseObject.createObject(),
				parameters instanceof BaseMap
					? parameters
					: BaseObject.createObject(),
				skinner,
				Context.getRequest(process));
		return RequestSAPI.querySkin(process, timeout, skinnerObj, query);
	}

	/**
	 * @param ctx
	 * @param o
	 * @param attributes
	 * @return answer
	 * @throws Exception
	 */
	public static final ReplyAnswer replyBinary(final ExecProcess ctx, final Object o, final BaseObject attributes) throws Exception {
		
		
		final ServeRequest query = Context.getRequest(ctx);
		final String eventTypeId = "RSAPI/RB//" + query.getEventTypeId();
		if (o == null) {
			return Reply
					.empty(
							eventTypeId, //
							query,
							RequestSAPI.createAttributes(attributes))//
					.setFinal();
		}
		if (o instanceof TransferCopier) {
			return Reply.binary(eventTypeId, query, RequestSAPI.createAttributes(attributes), ((TransferCopier) o).nextCopy())//
					.setFinal();
		}
		if (o instanceof TransferBuffer) {
			return Reply
					.binary(
							eventTypeId, //
							query,
							RequestSAPI.createAttributes(attributes),
							(TransferBuffer) o)//
					.setFinal();
		}
		if (o instanceof BaseMessage) {
			final BinaryMessage<?> message = ((BaseMessage) o).toBinary();
			final BaseObject messageAttributes = message.getAttributes();
			final ReplyAnswer result = Reply.binary(eventTypeId, query, BaseObject.createObject(messageAttributes), message.getBinary());
			if (attributes != null) {
				result.setAttributes(attributes);
			}
			return result.setFinal();
		}
		return Reply
				.object(
						eventTypeId, //
						query,
						RequestSAPI.createAttributes(attributes),
						o)//
				.setFinal();
	}

	/**
	 * @param ctx
	 * @param location
	 * @param moved
	 * @return answer
	 */
	public static final ReplyAnswer replyRedirect(final ExecProcess ctx, final String location, final boolean moved) {
		
		
		final ServeRequest query = Context.getRequest(ctx);
		return Reply.redirect("RSAPI/RRD//" + query.getEventTypeId(), query, moved, Context.getServer(ctx).fixUrl(location)).setFinal();
	}

	/**
	 * @param ctx
	 * @return answer
	 */
	public static final ReplyAnswer replyRetry(final ExecProcess ctx) {
		
		
		final ServeRequest query = Context.getRequest(ctx);
		return Reply.redirect("RSAPI/RTR//" + query.getEventTypeId(), query, false, query.getUrl()).setFinal();
	}

	/**
	 * @param process
	 * @param o
	 * @param attributes
	 * @return answer
	 * @throws Exception
	 */
	public static final ReplyAnswer replyTextual(final ExecProcess process, final Object o, final BaseObject attributes) throws Exception {
		
		
		final ServeRequest query = Context.getRequest(process);
		final String eventTypeId = "RSAPI/RT//" + query.getEventTypeId();
		if (o == null || o == BaseObject.NULL || o == BaseObject.UNDEFINED) {
			return Reply.empty(eventTypeId, query, RequestSAPI.createAttributes(attributes));
		}
		if (o instanceof BaseMessage) {
			final CharacterMessage<?> message = ((BaseMessage) o).toCharacter();
			final BaseObject messageAttributes = message.getAttributes();
			final ReplyAnswer result = Reply.character(eventTypeId, query, BaseObject.createObject(messageAttributes), message.getText());
			if (attributes != null) {
				result.setAttributes(attributes);
			}
			return result;
		}
		return Reply.character(eventTypeId, query, RequestSAPI.createAttributes(attributes), o.toString());
	}

	/**
	 * @param ctx
	 * @return string
	 */
	public static final String Root(final ExecProcess ctx) {
		
		
		return Convert.MapEntry.toString(Context.getServer(ctx).getProperties(), "SiteRoot", null);
	}

	/**
	 * @param ctx
	 * @param object
	 * @return object
	 */
	public static final Object setAttachment(final ExecProcess ctx, final Object object) {
		
		
		return Context.getRequest(ctx).setAttachment(object);
	}

	/**
	 * @param ctx
	 * @param settings
	 * @return request
	 */
	public static final ServeRequest setSettings(final ExecProcess ctx, final BaseObject settings) {
		
		
		return Context.getRequest(ctx).setSettings(settings);
	}

	/**
	 * @param ctx
	 * @param object
	 * @return object
	 */
	public static final Object setSharedObject(final ExecProcess ctx, final Object object) {
		
		
		return Context.getRequest(ctx).setAttachment(object);
	}

	@Override
	public BaseObject baseGetSubstitution() {
		
		
		return Context.getRequest(Exec.currentProcess()).getParameters();
	}

	@Override
	public BaseObject basePrototype() {
		
		
		return RequestSAPI.PROTOTYPE;
	}
}
