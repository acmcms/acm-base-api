package ru.myx.ae1.types;

/*
 * Created on 16.07.2004 Window - Preferences - Java - Code Style - Code
 * Templates
 */
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.know.Server;
import ru.myx.ae1.storage.BaseChange;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae3.act.Act;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseFunction;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.binary.TransferBuffer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.control.ControlForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecArgumentsEmpty;
import ru.myx.ae3.exec.ExecNonMaskedException;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ProgramPart;
import ru.myx.ae3.exec.ResultHandler;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Create;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;

/**
 *
 */
final class TypeImpl extends AbstractType {
	
	private static final ControlFieldset<?> NULL_FIELDSET = ControlFieldset.createFieldset();
	
	/**
	 *
	 */
	public static final int HT_DEFAULT = 0;
	
	/**
	 *
	 */
	public static final int HT_PARENT = 1;
	
	/**
	 *
	 */
	public static final int HT_ANY = 2;
	
	/**
	 *
	 */
	public static final int HT_ALL = 3;
	
	/**
	 *
	 */
	public static final String[] HT_NAMES = {
			"default", "parent", "any", "all"
	};
	
	private static final String NULL_SORT = "history";
	
	private static final Set<Integer> NULL_STATE_LIST = Create.tempSet();
	
	private static final ControlFieldset<?> getFieldsetFromMap(final BaseObject map, final String name, final ControlFieldset<?> defaultValue) {
		
		if (map == null || map.baseIsPrimitive()) {
			return defaultValue;
		}
		final BaseObject object = map.baseGet(name, BaseObject.UNDEFINED);
		assert object != null : "NULL java object";
		if (object.baseIsPrimitive()) {
			return object.baseIsPrimitiveString()
				? ControlFieldset.materializeFieldset(object.baseToJavaString())
				: defaultValue;
		}
		if (object instanceof ControlFieldset<?>) {
			return defaultValue == null
				? (ControlFieldset<?>) object
				: ControlFieldset.createFieldset(defaultValue, (ControlFieldset<?>) object);
		}
		final BaseObject fieldset = object.baseGet("fieldset", BaseObject.UNDEFINED);
		assert fieldset != null : "NULL java object";
		if (!fieldset.baseIsPrimitive()) {
			final BaseObject fields = fieldset.baseGet("field", BaseObject.UNDEFINED);
			assert fields != null : "NULL java object";
			if (fields != BaseObject.UNDEFINED) {
				final ControlFieldset<?> result = ControlFieldset.createFieldset();
				if (fields.baseArray() != null) {
					final BaseArray array = fields.baseArray();
					for (int i = 0; i < array.length(); ++i) {
						final BaseObject current = array.baseGet(i, BaseObject.UNDEFINED);
						assert current != null : "NULL java object";
						if (!current.baseIsPrimitive()) {
							final String type = Convert.MapEntry.toString(current, "class", "object");
							result.addField(ControlFieldFactory.createField(type, current));
						}
					}
				}
				if (!fields.baseIsPrimitive()) {
					final String type = Convert.MapEntry.toString(fields, "class", "object");
					result.addField(ControlFieldFactory.createField(type, fields));
				}
				return defaultValue == null
					? result
					: ControlFieldset.createFieldset(defaultValue, result);
			}
		}
		return defaultValue == null
			? ControlFieldset.materializeFieldset(String.valueOf(object))
			: ControlFieldset.createFieldset(defaultValue, ControlFieldset.materializeFieldset(String.valueOf(object)));
	}
	
	private static final Map<?, ?> getMapFromMap(final Map<?, ?> map, final String name, final Map<?, ?> defaultValue) {
		
		final Object object = map.get(name);
		if (object == null) {
			return defaultValue;
		}
		if (object instanceof Map<?, ?>) {
			return (Map<?, ?>) object;
		}
		return defaultValue;
	}
	
	private final long modified;
	
	private final Collection<String> replacements;
	
	private final Map<?, ?> responseBehavior;
	
	private final ProgramPart responseScript;
	
	private final ProgramPart responseFilter;
	
	private final ControlFieldset<?> fieldsetLoad;
	
	private final Set<String> childrenTypes;
	
	private final Set<String> parentsTypes;
	
	private final BaseObject formModify;
	
	private ControlFieldset<?> fieldsetModify;
	
	private final Object scriptTriggerModify;
	
	private ProgramPart scriptTriggerModifyPrepared;
	
	private final BaseObject formCreate;
	
	private ControlFieldset<?> fieldsetCreate;
	
	private final Object scriptTriggerCreate;
	
	private ProgramPart scriptTriggerCreatePrepared;
	
	private final BaseObject formDelete;
	
	private final Object scriptTriggerDelete;
	
	private ProgramPart scriptTriggerDeletePrepared;
	
	private final TypeCommand[] commands;
	
	private final boolean privateResponse;
	
	private final boolean anonymousResponse;
	
	private final Function<String, BaseMessage> resources;
	
	private int behaviorHandleToParent = -1;
	
	private int behaviorHandleAllIncoming = -1;
	
	private int behaviorHandleAnyThrough = -1;
	
	private int behaviorAutoRecalculate = -1;
	
	private String behaviorSort = null;
	
	private long behaviorCacheServerTtl = -2L;
	
	private int behaviorCacheClientTtl = -2;
	
	private Collection<Integer> stateList;
	
	private final Server server;
	
	private final BaseObject typeInheritancePrototype;
	
	private TypeConstructor constructor;
	
	TypeImpl(
			final Server server,
			final Type<?> parentType,
			final Type<?> parentTypeDefault,
			final String key,
			final long modified,
			final BaseMap scheme,
			final Function<String, BaseMessage> resources) {
		
		super(parentType == null
			? parentTypeDefault
			: parentType, server.getRootContext(), key, scheme);
		assert server != null : "Server is NULL";
		this.typeInheritancePrototype = new BaseNativeObject(parentType == null
			? parentTypeDefault == null
				? null
				: parentTypeDefault.getTypePrototypeObject()
			: parentType.getTypePrototypeObject());
		this.server = server;
		this.resources = resources;
		this.modified = this.parent == null
			? modified
			// already includes modification dates of all hierarchy
			: Math.max(this.parent.getTypeModificationDate(), modified);
		{
			final BaseArray replacements = Convert.MapEntry.toCollection(scheme, "replacement", (BaseArray) null);
			if (replacements == null) {
				this.replacements = null;
			} else {
				final int size = replacements.length();
				this.replacements = Create.tempSet(size);
				for (int i = 0; i < size; ++i) {
					this.replacements.add(replacements.baseGet(i, BaseObject.UNDEFINED).baseToJavaString());
				}
			}
		}
		final Map<?, ?> schemeRespond = TypeImpl.getMapFromMap(scheme, "respond", null);
		if (schemeRespond == null || schemeRespond.isEmpty()) {
			this.responseBehavior = null;
			this.responseFilter = null;
			this.responseScript = null;
		} else {
			Map<?, ?> responseBehavior = TypeImpl.getMapFromMap(schemeRespond, "behavior", null);
			if (responseBehavior == null) {
				responseBehavior = TypeImpl.getMapFromMap(schemeRespond, "behaviour", null);
			}
			this.responseBehavior = responseBehavior;
			this.responseFilter = server.createRenderer("TYPE{" + key + "}/RESPOND-FILTER", Convert.MapEntry.toObject(schemeRespond, "filter", null));
			this.responseScript = server.createRenderer("TYPE{" + key + "}/RESPOND-SCRIPT", Convert.MapEntry.toObject(schemeRespond, "script", null));
		}
		this.fieldsetLoad = TypeImpl.getFieldsetFromMap(scheme, "load", this.parent == null
			? ControlFieldset.createFieldset()
			: this.parent.getFieldsetLoad());
		final BaseObject schemeModify = scheme.baseGet("modify", BaseObject.UNDEFINED);
		assert schemeModify != null : "NULL java object";
		if (Base.hasKeys(schemeModify)) {
			this.formModify = schemeModify.baseGet("form", BaseObject.UNDEFINED);
			this.scriptTriggerModify = Base.getJava(schemeModify, "trigger", null);
			this.fieldsetModify = null;
		} else {
			this.formModify = BaseObject.UNDEFINED;
			this.scriptTriggerModify = null;
			this.fieldsetModify = TypeImpl.NULL_FIELDSET;
		}
		final BaseObject schemeCreate = scheme.baseGet("create", BaseObject.UNDEFINED);
		assert schemeCreate != null : "NULL java object";
		if (schemeCreate == BaseObject.UNDEFINED) {
			this.formCreate = this.formModify;
			this.scriptTriggerCreate = this.scriptTriggerModify;
			this.fieldsetCreate = this.fieldsetModify;
		} else {
			this.formCreate = schemeCreate.baseGet("form", this.formModify);
			this.scriptTriggerCreate = Base.getJava(schemeCreate, "trigger", this.scriptTriggerModify);
			this.fieldsetCreate = null;
		}
		final BaseObject schemeDelete = scheme.baseGet("delete", BaseObject.UNDEFINED);
		assert schemeDelete != null : "NULL java object";
		if (schemeDelete == BaseObject.UNDEFINED) {
			this.formDelete = BaseObject.UNDEFINED;
			this.scriptTriggerDelete = null;
		} else {
			this.formDelete = schemeDelete.baseGet("form", BaseObject.UNDEFINED);
			this.scriptTriggerDelete = Base.getJava(schemeDelete, "trigger", null);
		}
		{
			final BaseObject childrenObject = scheme.baseGet("children", null);
			if (childrenObject == null) {
				this.childrenTypes = null;
			} else if (childrenObject.baseIsPrimitive() || !Base.hasKeys(childrenObject)) {
				this.childrenTypes = Collections.emptySet();
			} else {
				final BaseArray childrenTypesArray = Convert.MapEntry.toCollection(childrenObject, "type", null);
				if (childrenTypesArray == null) {
					this.childrenTypes = Collections.emptySet();
				} else {
					final int size = childrenTypesArray.length();
					this.childrenTypes = Create.tempSet(size);
					for (int i = 0; i < size; ++i) {
						final BaseObject candidate = childrenTypesArray.baseGet(i, BaseObject.UNDEFINED);
						if (candidate.baseIsPrimitiveString()) {
							this.childrenTypes.add(candidate.baseToJavaString());
						}
					}
				}
			}
		}
		{
			final BaseObject parentsObject = scheme.baseGet("parents", BaseObject.UNDEFINED);
			if (parentsObject == BaseObject.UNDEFINED) {
				this.parentsTypes = null;
			} else if (parentsObject.baseIsPrimitive() || !Base.hasKeys(parentsObject)) {
				this.parentsTypes = Collections.emptySet();
			} else {
				final BaseArray parentsTypesArray = Convert.MapEntry.toCollection(parentsObject, "type", null);
				if (parentsTypesArray == null) {
					this.parentsTypes = Collections.emptySet();
				} else {
					final int size = parentsTypesArray.length();
					this.parentsTypes = Create.tempSet(size);
					for (int i = 0; i < size; ++i) {
						final BaseObject candidate = parentsTypesArray.baseGet(i, BaseObject.UNDEFINED);
						if (candidate.baseIsPrimitiveString()) {
							this.parentsTypes.add(candidate.baseToJavaString());
						}
					}
				}
			}
		}
		{
			final ControlFieldset<?> fieldsetStatic = TypeImpl.getFieldsetFromMap(scheme, "static", ControlFieldset.createFieldset());
			if (fieldsetStatic != null) {
				final BaseObject map = new BaseNativeObject();
				fieldsetStatic.dataStore(map, map);
				for (final Iterator<String> iterator = Base.keys(map); iterator.hasNext();) {
					final String current = iterator.next();
					this.baseDefine(current, map.baseGet(current, BaseObject.UNDEFINED), BaseProperty.ATTRS_MASK_WND);
				}
			}
		}
		final String keyType = key + ":" + modified;
		{
			final BaseObject schemeRender = scheme.baseGet("render", BaseObject.UNDEFINED);
			if (!schemeRender.baseIsPrimitive()) {
				this.typeInheritancePrototype.baseDefine(//
						"render",
						new TypeCommand(//
								server,
								this,
								"render",
								keyType,
								BaseMap.create()//
										.putAppend("arguments", ControlFieldset.createFieldset().addField(ControlFieldFactory.createFieldString("mode", "Mode", null)))//
										.putAppend("key", "render")//
										.putAppend("script", schemeRender.baseGet("script", BaseObject.UNDEFINED))//
										.putAppend("permission", "hidden")//
						), //
						BaseProperty.ATTRS_MASK_WND//
				);
			}
		}
		final List<TypeCommand> commandList = new ArrayList<>();
		{
			final BaseArray schemeCommand = Convert.MapEntry.toCollection(scheme, "command", (BaseArray) null);
			if (schemeCommand != null && !schemeCommand.isEmpty()) {
				final int length = schemeCommand.length();
				final BaseObject[] commands = new BaseObject[length];
				for (int i = 0; i < length; ++i) {
					final BaseObject commandAttributes = schemeCommand.baseGet(i, BaseObject.UNDEFINED);
					commands[i] = commandAttributes;
					final String commandKey = Base.getString(commandAttributes, "key", "").trim();
					if (commandKey.length() == 0) {
						Report.warning("TYPE", "Command without key, skipping! Type=" + key);
						continue;
					}
					final String type = Base.getString(commandAttributes, "type", "").trim();
					commandAttributes.baseDefine("permission", type.length() == 0
						? "hidden"
						: type);
					if (commandKey.equals(key)) {
						final TypeConstructor command = new TypeConstructor(server, this, commandKey, keyType, commandAttributes);
						command.baseDefine(
								"prototype", //
								this,
								BaseProperty.ATTRS_MASK_NNN);
						// assert command.baseGet( "prototype" ) == this;
						this.constructor = command;
						continue;
					}
					final TypeCommand command = new TypeCommand(server, this, commandKey, keyType, commandAttributes);
					this.baseDefine(
							commandKey, //
							command,
							BaseProperty.ATTRS_MASK_WND);
					/** human readable */
					final boolean instance = !Convert.MapEntry.toBoolean(commandAttributes, "static", false);
					if (instance) {
						this.typeInheritancePrototype.baseDefine(
								commandKey, //
								command,
								BaseProperty.ATTRS_MASK_WND);
					}
					if (parentTypeDefault != null
							/** human readable */
							&& Convert.MapEntry.toBoolean(commandAttributes, "export", false)) {
						final BaseObject prototypeObject = instance
							? parentTypeDefault.getTypePrototypeObject()
							: parentTypeDefault;
						if (prototypeObject != null && (prototypeObject.baseGetOwnProperty(commandKey) == null
								|| prototypeObject.baseGet(commandKey, BaseObject.UNDEFINED).baseGetOwnProperty("export-" + type) != null)) {
							command.baseDefine("export-" + type, BaseObject.TRUE, BaseProperty.ATTRS_MASK_NNN);
							prototypeObject.baseDefine(
									commandKey, //
									command,
									BaseProperty.ATTRS_MASK_WND);
						}
					}
					if (command.needStart() || !"hidden".equals(command.commandPermission())) {
						commandList.add(command);
					}
				}
			}
		}
		this.commands = commandList.isEmpty()
			? null
			: commandList.toArray(new TypeCommand[commandList.size()]);
		this.privateResponse = !Convert.MapEntry.toBoolean(this.responseBehavior, "public", true);
		this.anonymousResponse = !Convert.MapEntry.toBoolean(this.responseBehavior, "anonymous", true);
	}
	
	@Override
	public BaseFunction baseCall() {
		
		return this.constructor;
	}
	
	@Override
	public BaseFunction baseConstruct() {
		
		return this.constructor;
	}
	
	@Override
	public final Object getCommandAdditionalResult(final BaseEntry<?> entry, final ControlCommand<?> command, final BaseObject arguments) {
		
		{
			// run foreign command - type knows how to execute it!
			final Object typeInstance = Base.getJava(command.getAttributes(), "typeInstance", null);
			if (typeInstance != null && typeInstance != this) {
				return ((Type<?>) typeInstance).getCommandAdditionalResult(entry, command, arguments);
			}
		}
		final Object kindaCommand = Base.getJava(command.getAttributes(), "commandItself", command);
		if (kindaCommand == null) {
			return null;
		}
		if (!(kindaCommand instanceof TypeCommand)) {
			throw new IllegalArgumentException("Wrong command type: " + command.getKey() + ", type=" + kindaCommand.getClass().getName());
		}
		final TypeCommand typeCommand = (TypeCommand) kindaCommand;
		if (arguments == null || !Base.hasKeys(arguments)) {
			final ControlFieldset<?> commandFieldset = typeCommand.getFieldset();
			if (commandFieldset != null) {
				return new TypeCommandForm(this, entry, command, typeCommand.getKey(), commandFieldset);
			}
		}
		final ExecProcess ctx = Exec.currentProcess();
		try {
			final Object o = typeCommand.callNEX(ctx, entry, ctx.argumentsMap(arguments)).baseValue();
			if (o == null) {
				return null;
			}
			if (o instanceof ControlForm<?>) {
				return o;
			}
			if (o instanceof URL) {
				return o;
			}
			final String s = o.toString();
			if (s == null) {
				return null;
			}
			final String trimmed = s.trim();
			return trimmed.length() > 0
				? trimmed
				: null;
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final BaseObject getCommandAttributes(final String key) {
		
		final BaseObject kindaCommand = this.baseGet(key, BaseObject.UNDEFINED);
		if (!(kindaCommand instanceof TypeCommand)) {
			return null;
		}
		return ((TypeCommand) kindaCommand).getAttributes();
	}
	
	@Override
	public final ControlCommandset getCommandsAdditional(final BaseEntry<?> entry, ControlCommandset target, final Set<String> include, Set<String> exclude) {
		
		if (this.commands != null) {
			final ExecProcess ctx = Exec.currentProcess();
			ctx.vmFrameEntryExFull();
			final int rBSB = ctx.ri0BSB;
			try {
				ctx.vmScopeDeriveContextFromGV();
				for (final TypeCommand command : this.commands) {
					if (command == null) {
						continue;
					}
					if ("hidden".equals(command.commandPermission())) {
						continue;
					}
					if (include == null || include.contains(command.getKey())) {
						if (exclude == null) {
							exclude = Create.tempSet();
						}
						if (exclude.add(command.getKey())) {
							try {
								if (command.getCheck().callNE0(ctx, entry).baseToJavaBoolean()) {
									if (target == null) {
										target = Control.createOptions();
									}
									target.add(command);
								}
							} catch (final Throwable e) {
								Report.exception("TYPE", "Error while checking commands, type=" + this.getKey() + ", command=" + command.getKey(), e);
							}
						}
					}
				}
			} finally {
				assert ctx.ri0BSB == rBSB : "Error while checking commands, type=" + this.getKey() + ", commandKey=" + this.getKey() + ", fieldKey=" + this.key
						+ " stack base broken: SB(1)=" + ctx.ri0BSB + ", SB(2)=" + rBSB;
				ctx.vmFrameLeave();
			}
		}
		return super.getCommandsAdditional(entry, target, include, exclude);
	}
	
	@Override
	public final ControlFieldset<?> getFieldsetCreate() {
		
		if (this.fieldsetCreate == null) {
			synchronized (this.formCreate) {
				if (this.fieldsetCreate == null) {
					final ControlFieldset<?> fieldset = TypeImpl.getFieldsetFromMap(this.formCreate, "fields", null);
					if (fieldset == null) {
						this.fieldsetCreate = TypeImpl.getFieldsetFromMap(this.formModify, "fields", this.parent == null
							? null
							: this.parent.getFieldsetCreate());
						if (this.fieldsetCreate == null) {
							this.fieldsetCreate = TypeImpl.NULL_FIELDSET;
						}
					} else {
						if (this.parent == null) {
							this.fieldsetCreate = fieldset;
						} else {
							final ControlFieldset<?> parentFieldset = this.parent.getFieldsetCreate();
							this.fieldsetCreate = parentFieldset == null
								? fieldset
								: ControlFieldset.createFieldset(parentFieldset, fieldset);
						}
					}
				}
			}
		}
		return this.fieldsetCreate == TypeImpl.NULL_FIELDSET
			? null
			: this.fieldsetCreate;
	}
	
	@Override
	public final ControlFieldset<?> getFieldsetDelete() {
		
		final ControlFieldset<?> fieldset;
		final String fieldsetSource = Base.getString(this.formDelete, "fields", "").trim();
		if (fieldsetSource.length() == 0) {
			fieldset = TypeImpl.NULL_FIELDSET;
		} else {
			final ControlFieldset<?> fieldsetTry = ControlFieldset.materializeFieldset(fieldsetSource);
			fieldset = fieldsetTry.isEmpty()
				? TypeImpl.NULL_FIELDSET
				: fieldsetTry;
		}
		if (this.parent == null) {
			return fieldset;
		}
		if (fieldset == TypeImpl.NULL_FIELDSET) {
			return this.parent.getFieldsetDelete();
		}
		final ControlFieldset<?> parentFieldset = this.parent.getFieldsetDelete();
		if (parentFieldset == null || parentFieldset.isEmpty()) {
			return fieldset;
		}
		return ControlFieldset.createFieldset(fieldset, parentFieldset);
	}
	
	@Override
	public final ControlFieldset<?> getFieldsetLoad() {
		
		return this.fieldsetLoad;
	}
	
	@Override
	public final ControlFieldset<?> getFieldsetProperties() {
		
		if (this.fieldsetModify == null) {
			synchronized (this.formModify) {
				if (this.fieldsetModify == null) {
					final ControlFieldset<?> fieldset = TypeImpl.getFieldsetFromMap(this.formModify, "fields", this.parent == null
						? null
						: this.parent.getFieldsetProperties());
					if (fieldset == null) {
						this.fieldsetModify = TypeImpl.NULL_FIELDSET;
						return null;
					}
					this.fieldsetModify = fieldset;
					return fieldset;
				}
			}
		}
		return this.fieldsetModify == TypeImpl.NULL_FIELDSET
			? null
			: this.fieldsetModify;
	}
	
	@Override
	public final Collection<String> getReplacements() {
		
		return this.replacements;
	}
	
	@Override
	public final BaseMessage getResource(final String key) throws Throwable {
		
		return this.resources.apply(key);
	}
	
	@Override
	public final BaseObject getResponse(final ExecProcess process, final BaseEntry<?> entry, final BaseObject content) {
		
		if (this.responseFilter == null) {
			if (this.parent == null) {
				return content;
			}
			return this.parent.getResponse(process, entry, content);
		}
		final ExecProcess ctx = Exec.createProcess(process, "type's response filter, type=" + this.getKey());
		ctx.vmFrameEntryExCall(true, entry, this.responseFilter, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContext(this.typeContext.ri10GV);
		ctx.contextCreateMutableBinding("content", content, false);
		try {
			return Act.run(ctx, new Function<ProgramPart, BaseObject>() {
				
				@Override
				public BaseObject apply(final ProgramPart responseFilter) {
					
					return responseFilter.execCallPreparedInilne(ctx);
				}
			}, this.responseFilter);
		} catch (final ExecNonMaskedException e) {
			/** it is very important not to wrap this type of exceptions. */
			throw e;
		} catch (final Exception t) {
			throw new RuntimeException(t);
		}
	}
	
	@Override
	public final ReplyAnswer getResponse(final ServeRequest query, final BaseEntry<?> entry) {
		
		final ExecProcess ctx = Exec.currentProcess();
		final Context context = Context.getContext(ctx);
		if (this.anonymousResponse && context.getSessionState() < AuthLevels.AL_AUTHORIZED_NORMAL) {
			this.server.ensureAuthorization(AuthLevels.AL_AUTHORIZED_NORMAL);
		}
		if (this.responseScript == null) {
			return this.parent == null
				? null
				: this.parent.getResponse(query, entry);
		}
		final BaseObject flags = context.getFlags();
		final String title = entry.getTitle();
		final String name = entry.getKey();
		final String key = entry.getGuid();
		final long modified = Math.max(entry.getModified(), this.modified);
		final BaseObject rendered;
		try {
			ctx.vmFrameEntryExCall(true, entry, this.responseScript, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
			ctx.vmScopeDeriveContext(this.typeContext);
			try {
				rendered = this.responseScript.execCallPreparedInilne(ctx);
			} catch (final ExecNonMaskedException e) {
				throw e;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			if (rendered == BaseObject.NULL || rendered == BaseObject.UNDEFINED) {
				return null;
			}
			if (rendered.baseIsPrimitiveString()) {
				final BaseMap result = new BaseNativeObject();
				result.baseDefine("title", title);
				result.baseDefine("body", rendered);
				final ReplyAnswer response = Reply
						.object(
								"IMPL", //
								query,
								result)//
						.setTitle(title)//
						.setContentName(name)//
						.setContentID(key)//
						.setLastModified(modified)//
						.setFlags(flags);
				assert result.baseDefine(
						"X-Debug-Created-By", //
						"type response, string, typeName=" + this.getKey(),
						BaseProperty.ATTRS_MASK_WED) : "debug";
				assert response.addAttribute(
						"X-Debug-Created-By", //
						"type response, string, typeName=" + this.getKey()) == response : "debug";
				return this.privateResponse
					? response.setPrivate()
					: response;
			}
			if (rendered instanceof ReplyAnswer) {
				return this.privateResponse
					? ((ReplyAnswer) rendered).setPrivate()
					: (ReplyAnswer) rendered;
			}
			if (rendered instanceof BaseMessage) {
				final ReplyAnswer response = Reply.object("IMPL", query, rendered)//
						.setTitle(title)//
						.setContentName(name)//
						.setContentID(key)//
						.setLastModified(modified)//
						.setFlags(flags);
				assert response.addAttribute(
						"X-Debug-Created-By", //
						"type response, BaseMessage, typeName=" + this.getKey() + ", messageClass=" + rendered.getClass().getName()) == response : "debug";
				return this.privateResponse
					? response.setPrivate()
					: response;
			}
			final Object object = rendered.baseValue();
			if (object instanceof ReplyAnswer) {
				final ReplyAnswer response = (ReplyAnswer) object;
				assert response.addAttribute(
						"X-Debug-Created-By", //
						"type response, via, already reply, typeName=" + this.getKey() + ", bufferClass=" + object.getClass().getName()) == response : "debug";
				return this.privateResponse
					? response.setPrivate()
					: response;
			}
			if (object instanceof TransferBuffer) {
				final ReplyAnswer response = Reply
						.binary(
								"IMPL", //
								query,
								(TransferBuffer) object)//
						.setTitle(title)//
						.setContentName(name)//
						.setContentID(key)//
						.setLastModified(modified)//
						.setFinal()//
						.setFlags(flags);
				assert response.addAttribute(
						"X-Debug-Created-By", //
						"type response, TransferBuffer, typeName=" + this.getKey() + ", bufferClass=" + object.getClass().getName()) == response : "debug";
				return this.privateResponse
					? response.setPrivate()
					: response;
			}
			if (object instanceof TransferCopier) {
				final ReplyAnswer response = Reply
						.binary(
								"IMPL", //
								query,
								(TransferCopier) object)//
						.setTitle(title)//
						.setContentName(name)//
						.setContentID(key)//
						.setLastModified(modified)//
						.setFinal()//
						.setFlags(flags);
				assert response.addAttribute(
						"X-Debug-Created-By", //
						"type response, TransferCopier, typeName=" + this.getKey() + ", copierClass=" + object.getClass().getName()) == response : "debug";
				return this.privateResponse
					? response.setPrivate()
					: response;
			}
			{
				final ReplyAnswer response = Reply
						.object(
								"IMPL", //
								query,
								object)//
						.setTitle(title)//
						.setContentName(name)//
						.setContentID(key)//
						.setLastModified(modified)//
						.setFlags(flags);
				assert response.addAttribute(
						"X-Debug-Created-By", //
						"type response, generic, typeName=" + this.getKey() + (object == null
							? ", object is null or undefined"
							: ", objectClass=" + object.getClass().getName())) == response : "debug";
				return this.privateResponse
					? response.setPrivate()
					: response;
			}
		} catch (final AbstractReplyException e) {
			if (e.getCode() == Reply.CD_LOOKAT) {
				return e.getReply();
			}
			final ReplyAnswer response = e.getReply()//
					.setContentName(name)//
					.setContentID(key)//
					.setLastModified(modified);
			assert response.addAttribute(
					"X-Debug-Created-By", //
					"type response, reply exception, typeName=" + this.getKey() + ", responseClass=" + response.getClass().getName()) == response : "debug";
			return this.privateResponse
				? response.setPrivate()
				: response;
		} catch (final Error e) {
			Report.exception("IMPL", "Error while building entry response, type=" + this.getKey() + "\n\tguid=" + entry.getGuid(), e);
			throw e;
		} catch (final RuntimeException e) {
			Report.exception("IMPL", "Error while building entry response, type=" + this.getKey() + "\n\tguid=" + entry.getGuid(), e);
			throw e;
		} catch (final Throwable t) {
			Report.exception("IMPL", "Error while building entry response, type=" + this.getKey() + "\n\tguid=" + entry.getGuid(), t);
			throw new RuntimeException(t);
		}
	}
	
	@Override
	public final boolean getTypeBehaviorAutoRecalculate() {
		
		if (this.behaviorAutoRecalculate == -1) {
			final boolean autoRecalculate = Convert.MapEntry.toBoolean(this.getAttributes(), "autotouch", false);
			this.behaviorAutoRecalculate = autoRecalculate
				? 1
				: 0;
			return autoRecalculate;
		}
		return this.behaviorAutoRecalculate != 0;
	}
	
	@Override
	public final boolean getTypeBehaviorHandleAllIncoming() {
		
		if (this.behaviorHandleAllIncoming == -1) {
			if (this.responseBehavior == null && this.parent != null) {
				return this.parent.getTypeBehaviorHandleAllIncoming();
			}
			final int handle = Convert.MapEntry.toInt(this.responseBehavior, "handle", TypeImpl.HT_NAMES, 0);
			this.behaviorHandleAllIncoming = handle > TypeImpl.HT_ANY
				? 1
				: 0;
			return handle > TypeImpl.HT_ANY;
		}
		return this.behaviorHandleAllIncoming != 0;
	}
	
	@Override
	public final boolean getTypeBehaviorHandleAnyThrough() {
		
		if (this.behaviorHandleAnyThrough == -1) {
			if (this.responseBehavior == null && this.parent != null) {
				return this.parent.getTypeBehaviorHandleAnyThrough();
			}
			final int handle = Convert.MapEntry.toInt(this.responseBehavior, "handle", TypeImpl.HT_NAMES, 0);
			this.behaviorHandleAnyThrough = handle >= TypeImpl.HT_ANY
				? 1
				: 0;
			return handle >= TypeImpl.HT_ANY;
		}
		return this.behaviorHandleAnyThrough != 0;
	}
	
	@Override
	public final boolean getTypeBehaviorHandleToParent() {
		
		if (this.behaviorHandleToParent == -1) {
			if (this.responseBehavior == null && this.parent != null) {
				return this.parent.getTypeBehaviorHandleToParent();
			}
			final int handle = Convert.MapEntry.toInt(this.responseBehavior, "handle", TypeImpl.HT_NAMES, 0);
			this.behaviorHandleToParent = handle == TypeImpl.HT_PARENT
				? 1
				: 0;
			return handle == TypeImpl.HT_PARENT;
		}
		return this.behaviorHandleToParent != 0;
	}
	
	@Override
	public final String getTypeBehaviorListingSort() {
		
		if (this.behaviorSort == null) {
			final String sort = Base.getString(this.getAttributes(), "sort", TypeImpl.NULL_SORT);
			this.behaviorSort = sort == TypeImpl.NULL_SORT || TypeImpl.NULL_SORT.equals(sort)
				? TypeImpl.NULL_SORT
				: sort;
		}
		return this.behaviorSort;
	}
	
	@Override
	public final int getTypeBehaviorResponseCacheClientTtl() {
		
		if (this.behaviorCacheClientTtl == -2) {
			if (this.responseBehavior == null && this.parent != null) {
				return this.parent.getTypeBehaviorResponseCacheClientTtl();
			}
			this.behaviorCacheClientTtl = (int) (Convert.MapEntry.toPeriod(this.responseBehavior, "ttl", 2L * 60L * 60L * 1000L) / 1000L);
		}
		return this.behaviorCacheClientTtl;
	}
	
	@Override
	public final long getTypeBehaviorResponseCacheServerTtl() {
		
		if (this.behaviorCacheServerTtl == -2) {
			if (this.responseBehavior == null && this.parent != null) {
				return this.parent.getTypeBehaviorResponseCacheServerTtl();
			}
			this.behaviorCacheServerTtl = (int) (Convert.MapEntry.toPeriod(this.responseBehavior, "cache", 2L * 60L * 60L * 1000L) / 1000L);
		}
		return this.behaviorCacheServerTtl;
	}
	
	@Override
	public final boolean getTypeBehaviorResponseFiltering() {
		
		return this.responseFilter != null || this.parent != null && this.parent.getTypeBehaviorResponseFiltering();
	}
	
	@Override
	public final long getTypeModificationDate() {
		
		return this.modified;
	}
	
	@Override
	public BaseObject getTypePrototypeObject() {
		
		return this.typeInheritancePrototype;
	}
	
	@Override
	public final Collection<String> getValidChildrenTypeNames() {
		
		if (this.childrenTypes == null) {
			if (this.parent == null) {
				return null;
			}
			return this.parent.getValidChildrenTypeNames();
		}
		return this.childrenTypes;
	}
	
	@Override
	public final Collection<String> getValidParentsTypeNames() {
		
		if (this.parentsTypes == null) {
			if (this.parent == null) {
				return null;
			}
			return this.parent.getValidParentsTypeNames();
		}
		return this.parentsTypes;
	}
	
	@Override
	public final Collection<Integer> getValidStateList() {
		
		if (this.stateList == null) {
			final Collection<Integer> stateList = super.getValidStateList();
			if (stateList == null) {
				this.stateList = TypeImpl.NULL_STATE_LIST;
				return null;
			}
			return this.stateList = stateList;
		}
		return this.stateList == TypeImpl.NULL_STATE_LIST
			? null
			: this.stateList;
	}
	
	@Override
	public final boolean hasDeletionForm() {
		
		assert this.formDelete != null : "NULL value is unexpected here!";
		return !this.formDelete.baseIsPrimitive() || this.parent != null && this.parent.hasDeletionForm();
	}
	
	@Override
	public final void onBeforeCreate(final BaseChange change, final BaseObject data) {
		
		final BaseObject changeData = change.getData();
		if (Base.getInt(changeData, "$state", -1) < 0) {
			// if (changeData.baseGet( "$state", null ) == null) {
			changeData.baseDefine(
					"$state", //
					this.getDefaultState(),
					BaseProperty.ATTRS_MASK_WED);
		}
		if (Base.getInt(changeData, "$folder", -1) < 0) {
			// if (changeData.baseGet( "$folder", null ) == null) {
			changeData.baseDefine(
					"$folder", //
					this.getDefaultFolder()
						? BaseObject.TRUE
						: BaseObject.FALSE,
					BaseProperty.ATTRS_MASK_WED);
		}
		if (this.parent != null) {
			this.parent.onBeforeCreate(change, data);
		}
		if (this.scriptTriggerCreate == null) {
			// empty
		} else {
			if (this.scriptTriggerCreatePrepared == null) {
				synchronized (this.scriptTriggerCreate) {
					if (this.scriptTriggerCreatePrepared == null) {
						this.scriptTriggerCreatePrepared = this.server.createRenderer("TYPE{" + this.getKey() + "}/CREATE-TRIGGER", this.scriptTriggerCreate);
					}
				}
			}
			final ProgramPart script = this.scriptTriggerCreatePrepared;
			final TypeChangeFilter filteredChange = new TypeChangeFilter(change);
			final ExecProcess ctx = Exec.currentProcess();
			ctx.vmFrameEntryExCall(true, new TypeEntryDummy(this, filteredChange), script, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
			ctx.vmScopeDeriveContext(this.typeContext);
			ctx.contextCreateMutableBinding("change", filteredChange, false);
			ctx.contextCreateMutableBinding("data", data, false);
			try {
				script.execCallPreparedInilne(ctx);
			} catch (final RuntimeException e) {
				throw e;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public final void onBeforeDelete(final BaseEntry<?> entry) {
		
		try {
			entry.baseCall("onBeforeEntryDelete", true);
		} catch (final Throwable e) {
			throw new RuntimeException("exception in onBeforeEntryDelete trigger of type: " + this.key, e);
		}
		if (this.scriptTriggerDelete == null) {
			// empty
		} else {
			if (this.scriptTriggerDeletePrepared == null) {
				synchronized (this.scriptTriggerDelete) {
					if (this.scriptTriggerDeletePrepared == null) {
						this.scriptTriggerDeletePrepared = this.server.createRenderer("TYPE{" + this.getKey() + "}/DELETE-TRIGGER", this.scriptTriggerDelete);
					}
				}
			}
			final ProgramPart script = this.scriptTriggerDeletePrepared;
			final ExecProcess ctx = Exec.currentProcess();
			ctx.vmFrameEntryExCall(true, entry, script, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
			ctx.vmScopeDeriveContext(this.typeContext);
			try {
				script.execCallPreparedInilne(ctx);
			} catch (final RuntimeException e) {
				throw e;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (this.parent != null) {
			this.parent.onBeforeDelete(entry);
		}
	}
	
	@Override
	public final void onBeforeModify(final BaseEntry<?> entry, final BaseChange change, final BaseObject data) {
		
		if (this.parent != null) {
			this.parent.onBeforeModify(entry, change, data);
		}
		if (this.scriptTriggerModify == null) {
			return;
		}
		if (this.scriptTriggerModifyPrepared == null) {
			synchronized (this.scriptTriggerModify) {
				if (this.scriptTriggerModifyPrepared == null) {
					this.scriptTriggerModifyPrepared = this.server.createRenderer("TYPE{" + this.getKey() + "}/MODIFY-TRIGGER", this.scriptTriggerModify);
				}
			}
		}
		final ProgramPart script = this.scriptTriggerModifyPrepared;
		final TypeChangeFilter filteredChange = new TypeChangeFilter(change);
		final ExecProcess ctx = Exec.createProcess(Exec.currentProcess(), "onBeforeModify");
		ctx.vmFrameEntryExCall(
				true, //
				entry == null
					? new TypeEntryDummy(this, filteredChange)
					: new TypeEntryFilter(entry, filteredChange),
				script,
				ExecArgumentsEmpty.INSTANCE,
				ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContext(this.typeContext);
		ctx.contextCreateMutableBinding("change", filteredChange, false);
		ctx.contextCreateMutableBinding("data", data, false);
		try {
			Act.run(ctx, new Function<ProgramPart, BaseObject>() {
				
				@Override
				public BaseObject apply(final ProgramPart program) {
					
					return program.execCallPreparedInilne(ctx);
				}
			}, script);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final void scriptCommandFormPrepare(final String key, final BaseEntry<?> entry, final BaseObject parameters) {
		
		final BaseObject kindaCommand = entry.baseGet(key, BaseObject.UNDEFINED);
		if (!(kindaCommand instanceof TypeCommand)) {
			return;
		}
		final TypeCommand typeCommand = (TypeCommand) kindaCommand;
		final BaseObject scriptSource = Base.get(typeCommand.getForm(), "prepare", BaseObject.UNDEFINED);
		
		if (scriptSource == BaseObject.UNDEFINED) {
			return;
		}
		
		final ProgramPart script = scriptSource.baseValue() instanceof ProgramPart
			? (ProgramPart) scriptSource.baseValue()
			/** FIXME: it looks that there's no language specified here! */
			: this.server.createRenderer("TYPE{" + this.getKey() + "}/CMD{" + key + "}-PREPARE", scriptSource);
		
		if (script == null) {
			return;
		}
		
		final ExecProcess ctx = Exec.createProcess(Exec.currentProcess(), "Type form prepare context");
		ctx.vmFrameEntryExCall(true, entry, script, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContext(this.typeContext);
		ctx.contextCreateMutableBinding("data", parameters, false);
		try {
			Act.run(ctx, new Function<ProgramPart, BaseObject>() {
				
				@Override
				public BaseObject apply(final ProgramPart program) {
					
					return program.execCallPreparedInilne(ctx);
				}
			}, script);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final void scriptCommandFormSubmit(final String key, final BaseEntry<?> entry, final BaseObject parameters) {
		
		final BaseObject kindaCommand = entry.baseGet(key, BaseObject.UNDEFINED);
		if (!(kindaCommand instanceof TypeCommand)) {
			return;
		}
		final TypeCommand typeCommand = (TypeCommand) kindaCommand;
		final BaseObject scriptSource = Base.get(typeCommand.getForm(), "submit", BaseObject.UNDEFINED);
		
		if (scriptSource == BaseObject.UNDEFINED) {
			return;
		}
		
		final ProgramPart script = scriptSource.baseValue() instanceof ProgramPart
			? (ProgramPart) scriptSource.baseValue()
			/** FIXME: it looks that there's no language specified here! */
			: this.server.createRenderer("TYPE{" + this.getKey() + "}/CMD{" + key + "}-SUBMIT", scriptSource);
		
		if (script == null) {
			return;
		}
		
		final ExecProcess ctx = Exec.createProcess(Exec.currentProcess(), "Type form submit context");
		ctx.vmFrameEntryExCall(true, entry, script, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContext(this.typeContext);
		ctx.contextCreateMutableBinding("data", parameters, false);
		try {
			Act.run(ctx, new Function<ProgramPart, BaseObject>() {
				
				@Override
				public BaseObject apply(final ProgramPart program) {
					
					return program.execCallPreparedInilne(ctx);
				}
			}, script);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final void scriptPrepareCreate(final BaseChange change, final BaseObject data) {
		
		if (this.parent != null) {
			this.parent.scriptPrepareCreate(change, data);
		}
		final BaseObject scriptSource;
		{
			final BaseObject check = Base.get(this.formCreate, "prepare", BaseObject.UNDEFINED);
			scriptSource = check != BaseObject.UNDEFINED
				? check
				: Base.get(this.formModify, "prepare", BaseObject.UNDEFINED);
		}
		
		if (scriptSource == BaseObject.UNDEFINED) {
			return;
		}
		
		final ProgramPart script = scriptSource.baseValue() instanceof ProgramPart
			? (ProgramPart) scriptSource.baseValue()
			/** FIXME: it looks that there's no language specified here! */
			: this.server.createRenderer("TYPE{" + this.getKey() + "}/CREATE-PREPARE", scriptSource);
		
		if (script == null) {
			return;
		}
		
		final ExecProcess ctx = Exec.currentProcess();
		ctx.vmFrameEntryExCall(true, new TypeEntryDummy(this, change), script, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContext(this.typeContext);
		ctx.contextCreateMutableBinding("change", Base.forUnknown(change), false);
		ctx.contextCreateMutableBinding("data", data, false);
		try {
			script.execCallPreparedInilne(ctx);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final void scriptPrepareDelete(final BaseEntry<?> entry, final BaseObject data) {
		
		if (this.parent != null) {
			this.parent.scriptPrepareDelete(entry, data);
		}
		final BaseObject scriptSource = Base.get(this.formDelete, "prepare", BaseObject.UNDEFINED);
		
		if (scriptSource == BaseObject.UNDEFINED) {
			return;
		}
		
		final ProgramPart script = scriptSource.baseValue() instanceof ProgramPart
			? (ProgramPart) scriptSource.baseValue()
			/** FIXME: it looks that there's no language specified here! */
			: this.server.createRenderer("TYPE{" + this.getKey() + "}/DELETE-PREPARE", scriptSource);
		
		if (script == null) {
			return;
		}
		
		final ExecProcess ctx = Exec.currentProcess();
		ctx.vmFrameEntryExCall(true, entry, script, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContext(this.typeContext);
		ctx.contextCreateMutableBinding("data", data, false);
		try {
			script.execCallPreparedInilne(ctx);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final void scriptPrepareModify(final BaseEntry<?> entry, final BaseChange change, final BaseObject data) {
		
		if (this.parent != null) {
			this.parent.scriptPrepareModify(entry, change, data);
		}
		final BaseObject scriptSource = Base.get(this.formModify, "prepare", BaseObject.UNDEFINED);
		
		if (scriptSource == BaseObject.UNDEFINED) {
			return;
		}
		
		final ProgramPart script = scriptSource.baseValue() instanceof ProgramPart
			? (ProgramPart) scriptSource.baseValue()
			/** FIXME: it looks that there's no language specified here! */
			: this.server.createRenderer("TYPE{" + this.getKey() + "}/MODIFY-PREPARE", scriptSource);
		
		if (script == null) {
			return;
		}
		
		final ExecProcess ctx = Exec.currentProcess();
		ctx.vmFrameEntryExCall(true, entry, script, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContext(this.typeContext);
		ctx.contextCreateMutableBinding("change", Base.forUnknown(change), false);
		ctx.contextCreateMutableBinding("data", data, false);
		try {
			script.execCallPreparedInilne(ctx);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final void scriptSubmitCreate(final BaseChange change, final BaseObject data) {
		
		if (this.parent != null) {
			this.parent.scriptSubmitCreate(change, data);
		}
		final BaseObject scriptSource = Base.get(this.formCreate, "submit", Base.get(this.formModify, "submit", BaseObject.UNDEFINED));
		
		if (scriptSource == BaseObject.UNDEFINED) {
			return;
		}
		
		final ProgramPart script = scriptSource.baseValue() instanceof ProgramPart
			? (ProgramPart) scriptSource.baseValue()
			/** FIXME: it looks that there's no language specified here! */
			: this.server.createRenderer("TYPE{" + this.getKey() + "}/CREATE-SUBMIT", scriptSource);
		
		if (script == null) {
			return;
		}
		
		final ExecProcess ctx = Exec.currentProcess();
		ctx.vmFrameEntryExCall(true, new TypeEntryDummy(this, change), script, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContext(this.typeContext);
		ctx.contextCreateMutableBinding("change", Base.forUnknown(change), false);
		ctx.contextCreateMutableBinding("data", data, false);
		try {
			script.execCallPreparedInilne(ctx);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final void scriptSubmitDelete(final BaseEntry<?> entry, final BaseObject data) {
		
		if (this.parent != null) {
			this.parent.scriptSubmitDelete(entry, data);
		}
		final BaseObject scriptSource = Base.get(this.formDelete, "submit", BaseObject.UNDEFINED);
		
		if (scriptSource == BaseObject.UNDEFINED) {
			return;
		}
		
		final ProgramPart script = scriptSource.baseValue() instanceof ProgramPart
			? (ProgramPart) scriptSource.baseValue()
			/** FIXME: it looks that there's no language specified here! */
			: this.server.createRenderer("TYPE{" + this.getKey() + "}/DELETE-SUBMIT", scriptSource);
		
		if (script == null) {
			return;
		}
		
		final ExecProcess ctx = Exec.currentProcess();
		ctx.vmFrameEntryExCall(true, entry, script, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContext(this.typeContext);
		ctx.contextCreateMutableBinding("data", data, false);
		try {
			script.execCallPreparedInilne(ctx);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final void scriptSubmitModify(final BaseEntry<?> entry, final BaseChange change, final BaseObject data) {
		
		if (this.parent != null) {
			this.parent.scriptSubmitModify(entry, change, data);
		}
		final BaseObject scriptSource = Base.get(this.formModify, "submit", BaseObject.UNDEFINED);
		
		if (scriptSource == BaseObject.UNDEFINED) {
			return;
		}
		
		final ProgramPart script = scriptSource.baseValue() instanceof ProgramPart
			? (ProgramPart) scriptSource.baseValue()
			/** FIXME: it looks that there's no language specified here! */
			: this.server.createRenderer("TYPE{" + this.getKey() + "}/MODIFY-SUBMIT", scriptSource);
		
		if (script == null) {
			return;
		}
		
		final ExecProcess ctx = Exec.currentProcess();
		ctx.vmFrameEntryExCall(true, entry, script, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContext(this.typeContext);
		ctx.contextCreateMutableBinding("change", Base.forUnknown(change), false);
		ctx.contextCreateMutableBinding("data", data, false);
		try {
			script.execCallPreparedInilne(ctx);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		
		return "[object " + this.baseClass() + "(" + this.getKey() + ")]";
	}
	
	@Override
	public final void typeStart() {
		
		if (this.commands != null) {
			for (final TypeCommand command : this.commands) {
				command.start();
			}
		}
	}
	
	@Override
	public final void typeStop() {
		
		if (this.commands != null) {
			for (final TypeCommand command : this.commands) {
				command.stop();
			}
		}
	}
	
}
