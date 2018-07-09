package ru.myx.ae3.skinner;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseNativeArray;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.TransferBuffer;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ProgramPart;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.l2.LayoutDefinition;
import ru.myx.ae3.l2.TargetContext;
import ru.myx.ae3.l2.skin.SkinImpl;
import ru.myx.ae3.mime.MimeType;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.xml.Xml;

/**
 * @author myx
 * 
 */
public abstract class SkinnerVfsAbstract extends SkinnerAbstract {
	
	
	private static final String OWNER = "SKINNER-VFS-ABSTRACT";

	private static final byte[] GIF_1X1_TRANS = new byte[]{
			(byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x39, (byte) 0x61, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x21, (byte) 0xF9, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x2C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x02,
			(byte) 0x44, (byte) 0x01, (byte) 0x00, (byte) 0x3B
	};

	private static final BaseObject GIF_ATTRIBUTES = Reply.wrap(SkinnerVfsAbstract.OWNER, null, SkinnerVfsAbstract.GIF_1X1_TRANS).setContentType("image/gif").setTimeToLiveWeeks(60)
			.setLastModified(0).setFinal()//
			.getAttributes();
	
	/**
	 *
	 */
	protected Charset charset;

	/**
	 * root vfs entry
	 */
	protected final Entry folder;

	/**
	 *
	 */
	protected long fieldsetDate = -1L;

	/**
	 *
	 */
	protected Entry fieldsetFile;

	/**
	 *
	 */
	protected String previousType = null;

	/**
	 *
	 */
	protected long settingsDate = -1L;

	/**
	 *
	 */
	protected Entry settingsFile;

	/**
	 *
	 */
	protected Map<String, LayoutDefinition<TargetContext<?>>> layouts;

	/**
	 * @param name
	 *            - skin id
	 * @param defaultDefaultDocument
	 * @param defaultDefaultContentType
	 * @param folder
	 *            - root vfs entry
	 */
	public SkinnerVfsAbstract(final String name, final String defaultDefaultDocument, final String defaultDefaultContentType, final Entry folder) {
		super(name, BaseObject.UNDEFINED, null, null, null);
		this.folder = folder;
		this.charset = Engine.CHARSET_DEFAULT;
		this.defaultDocument = this.defaultDefaultDocument = defaultDefaultDocument;
		this.defaultContentType = this.defaultDefaultContentType = defaultDefaultContentType;
		this.layouts = SkinImpl.loadLayouts(SkinImpl.getLayoutsFolder(this.folder));
		this.settingsFile = folder.relative("skin.settings.xml", TreeLinkType.PUBLIC_TREE_REFERENCE);
		this.fieldsetFile = folder.relative("skin.fieldset.xml", TreeLinkType.PUBLIC_TREE_REFERENCE);
	}

	/**
	 * @param identifier
	 *            - without leading slash
	 * @param query
	 * @return
	 */
	protected final ReplyAnswer getEntryAsReply(final String identifier, final ServeRequest query) {
		
		
		final Entry file = this.folder.relative(identifier, null);
		if (file == null || !file.isExist()) {
			return null;
		}
		final TransferBuffer buffer = file.isBinary()
			? file.toBinary().getBinaryContent().baseValue().nextCopy()
			: null;
		if (buffer == null) {
			return null;
		}
		return (buffer.hasRemaining()
			? Reply.binary(
					SkinnerVfsAbstract.OWNER, //
					query,
					buffer,
					identifier.substring(identifier.lastIndexOf('/') + 1)) //
					.setLastModified(file.getLastModified()) //
					.setTimeToLiveWeeks(1)
			: Reply.empty(SkinnerVfsAbstract.OWNER, query) //
					.setNoCaching()//
		) //
				.setFinal();
	}

	@Override
	public final LayoutDefinition<TargetContext<?>> getLayoutDefinition(final String name) {
		
		
		return this.layouts.get(name);
	}

	@Override
	public final Entry getRoot() {
		
		
		return this.folder;
	}

	/**
	 * @param query
	 * @return
	 */
	@Override
	protected ReplyAnswer renderQueryImpl(final ServeRequest query) {
		
		
		final String request;
		{
			final String requestOriginal = query.getResourceIdentifier();
			request = requestOriginal.length() <= 1
				? this.defaultDocument
				: requestOriginal.substring(1);
			/**
			 * we do not want to change query's resource identifier.
			 */
		}
		if (request.length() == 17 && "skin.settings.xml".equals(request)) {
			return Reply.stringForbidden(SkinnerVfsAbstract.OWNER, query, "File is protected!");
		}
		if (request.length() > 5 && request.charAt(0) == 's' && request.startsWith("skin/")) {
			return Reply.stringForbidden(SkinnerVfsAbstract.OWNER, query, "Folder is protected!");
		}
		final ExecProcess process = Exec.currentProcess();
		try {
			if (request.length() > 7 && request.charAt(0) == '$' && request.startsWith("$files/")) {
				final ReplyAnswer reply = this.getEntryAsReply(request, query);
				return reply == null
					? super.renderQueryImpl(query)
					: reply;
			}
			if (request.startsWith("icons/")) {
				String current = request;
				for (;;) {
					{
						final ReplyAnswer reply = this.getEntryAsReply(current, query);
						if (reply != null) {
							return reply;
						}
					}
					final int pos = current.lastIndexOf('-');
					if (pos == -1) {
						/**
						 * check parent and import mappings
						 */
						final ReplyAnswer reply = super.renderQueryImpl(query);
						return reply == null
							? Reply.wrap(SkinnerVfsAbstract.OWNER, query, SkinnerVfsAbstract.GIF_ATTRIBUTES, SkinnerVfsAbstract.GIF_1X1_TRANS)
							: reply;
					}
					final int pos_suffix = current.indexOf('.');
					current = pos_suffix == -1
						? current.substring(0, pos)
						: current.substring(0, pos) + current.substring(pos_suffix);
				}
			}
			if (this.service != null) {
				final ProgramPart template = this.service.prepare(request);
				if (template == null) {
					return super.renderQueryImpl(query);
				}
				final BaseObject response = template.callNE0(process, this);
				if (response == null || response == BaseObject.UNDEFINED) {
					return super.renderQueryImpl(query);
				}
				if (response instanceof ReplyAnswer) {
					final ReplyAnswer reply = (ReplyAnswer) response;
					if (reply.getCode() == 200 && reply.isFinal()) {
						return this.handleServiceFinalReplyException(reply);
					}
					return reply;
				}
				if (response instanceof CharSequence || response.baseIsPrimitive()) {
					final BaseObject flags = Context.getFlags(process);
					return Reply
							.string(
									SkinnerVfsAbstract.OWNER, //
									query,
									response.toString()) //
							.setContentType(MimeType.forName(request, "text/html")) //
							.setEncoding(Engine.CHARSET_UTF8) //
							.setFlags(flags);
				}
				{
					final BaseObject flags = Context.getFlags(process);
					return Reply
							.object(
									SkinnerVfsAbstract.OWNER, //
									query,
									response)//
							.setFlags(flags);
				}
			}
			return super.renderQueryImpl(query);
		} catch (final AbstractReplyException e) {
			final BaseObject flags = Context.getFlags(process);
			return e.getReply()//
					.addAttribute("via", "SkinnerVfsAbstract")//
					.setFlags(flags);
		} catch (final Throwable e) {
			Report.exception(
					SkinnerVfsAbstract.OWNER, //
					"Unexpected exception while rendering user response",
					"URL=" + query.getUrl(),
					e);
			final BaseObject flags = Context.getFlags(process);
			flags.baseDefine("exception", Base.forThrowable(e));
			return Reply
					.string(
							SkinnerVfsAbstract.OWNER, //
							query,
							Format.Throwable.toText("Unexpected exception while rendering user response", e))//
					.setCode(Reply.CD_EXCEPTION)//
					.setEncoding(Engine.CHARSET_UTF8)//
					.setFlags(flags);
		}
	}

	@Override
	public boolean scan() {
		
		
		if (this.folder == null || !this.folder.isExist() || !this.folder.isContainer()) {
			return false;
		}
		if (this.settingsFile.isExist()) {
			if (this.settingsDate != this.settingsFile.getLastModified()) {
				this.settingsDate = this.settingsFile.getLastModified();
				this.settings = Xml.toMap(
						"skinnerScan(" + this.name + ")", //
						this.settingsFile.toBinary().getBinaryContent().baseValue(),
						null /*
								 * Engine.CHARSET_DEFAULT
								 */,
						null,
						new BaseNativeObject(),
						null,
						null);
				final String type = Base.getString(this.settings, "type", "");
				if (this.previousType != null && !this.previousType.equals(type)) {
					return false;
				}
				this.previousType = type;
				/**
				 * charset property
				 */
				this.charset = Charset.forName(Base.getString(this.settings, "charset", Engine.ENCODING_DEFAULT));
				/**
				 * prototype property
				 */
				{
					final String prototypeSkin = Base.getString(this.settings, "prototype", "skin-standard").trim();
					this.prototypeSkin = prototypeSkin.length() == 0
						? null
						: prototypeSkin;
				}
				/**
				 * imports
				 */
				{
					final BaseArray importMappings;
					{
						final BaseObject parameter = this.settings.baseGet("import", BaseObject.UNDEFINED);
						final BaseArray array = parameter.baseArray();
						if (array != null) {
							importMappings = array;
						} else //
						if (Base.getBoolean(parameter, "package", false)) {
							importMappings = new BaseNativeArray(parameter);
						} else {
							importMappings = BaseObject.createArray(0);
						}
					}
					Map<String, String> mapping = null;
					for (int i = importMappings.length() - 1; i >= 0; --i) {
						final BaseObject oneImport = importMappings.baseGet(i, BaseObject.UNDEFINED);
						final String packageName = Base.getString(oneImport, "package", "").trim();
						final String namespaceName = Base.getString(oneImport, "namespace", "").trim();
						if (packageName.length() == 0 || namespaceName.length() == 0) {
							continue;
						}
						if (mapping == null) {
							mapping = new TreeMap<>();
						}
						mapping.put(namespaceName, packageName);
					}
					this.importMappings = mapping;
				}
				/**
				 * other properties
				 */
				this.defaultDocument = Base.getString(this.settings, "default", this.defaultDefaultDocument);
				this.defaultContentType = Base.getString(this.settings, "contentType", this.defaultDefaultContentType);
				/**
				 * not JS rules: 'false' '0' 'no' should mean FALSE
				 */
				this.requireSecure = Convert.MapEntry.toBoolean(this.settings, "secure", false);
				/**
				 * not JS rules: 'false' '0' 'no' should mean FALSE
				 */
				this.requireAuth = Convert.MapEntry.toBoolean(this.settings, "auth", false);

				this.title = this.settings.baseGet("title", this.title);
			}
		} else {
			if (this.settingsDate != -1L) {
				this.settingsDate = -1L;
				this.settings = new BaseNativeObject();
				this.charset = Engine.CHARSET_DEFAULT;
				this.prototypeSkin = null;
				this.importMappings = null;
				this.defaultDocument = this.defaultDefaultDocument;
				this.defaultContentType = this.defaultDefaultContentType;
				this.requireSecure = false;
				this.requireAuth = false;
				this.title = BaseObject.UNDEFINED;
			}
		}
		try {
			if (this.fieldsetFile.isExist()) {
				if (this.fieldsetDate != this.fieldsetFile.getLastModified()) {
					this.fieldsetDate = this.fieldsetFile.getLastModified();
					this.fieldset = ControlFieldset.materializeFieldset(this.fieldsetFile.toBinary().getBinaryContent().baseValue().toString(this.charset));
					this.useFieldset = this.fieldset.size() > 0;
				}
			} else {
				if (this.fieldsetDate != -1L) {
					this.fieldsetDate = -1L;
					this.fieldset = this.fieldset.isEmpty()
						? this.fieldset
						: ControlFieldset.createFieldset();
					this.useFieldset = false;
				}
			}
		} catch (final Throwable t) {
			Report.exception(SkinnerVfsAbstract.OWNER, "[" + this.name + "] error while scanning for fieldset changes", t);
		}
		return true;
	}

}
