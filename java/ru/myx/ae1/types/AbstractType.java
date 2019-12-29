/**
 *
 */
package ru.myx.ae1.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.storage.BaseChange;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae1.storage.ModuleInterface;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Create;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;

/**
 * @author myx
 *
 */
public abstract class AbstractType extends AbstractBasic<AbstractType> implements Type<AbstractType> {
	
	private static final Map<String, Integer> MAP_STATES = AbstractType.makeStateMap();
	
	private static final Set<String> EMPTY_SET_STRING = Collections.unmodifiableSet(new TreeSet<String>());
	
	private static final Map<String, Integer> makeStateMap() {
		
		final Map<String, Integer> result = new HashMap<>();
		result.put("draft", Integer.valueOf(ModuleInterface.STATE_DRAFT));
		result.put("черновик", Integer.valueOf(ModuleInterface.STATE_DRAFT));
		result.put("ready", Integer.valueOf(ModuleInterface.STATE_READY));
		result.put("готов", Integer.valueOf(ModuleInterface.STATE_READY));
		result.put("готовый", Integer.valueOf(ModuleInterface.STATE_READY));
		result.put("system", Integer.valueOf(ModuleInterface.STATE_SYSTEM));
		result.put("системный", Integer.valueOf(ModuleInterface.STATE_SYSTEM));
		result.put("active", Integer.valueOf(ModuleInterface.STATE_PUBLISH));
		result.put("actual", Integer.valueOf(ModuleInterface.STATE_PUBLISH));
		result.put("publish", Integer.valueOf(ModuleInterface.STATE_PUBLISH));
		result.put("published", Integer.valueOf(ModuleInterface.STATE_PUBLISH));
		result.put("активный", Integer.valueOf(ModuleInterface.STATE_PUBLISH));
		result.put("актуальный", Integer.valueOf(ModuleInterface.STATE_PUBLISH));
		result.put("опубликован", Integer.valueOf(ModuleInterface.STATE_PUBLISH));
		result.put("опубликованый", Integer.valueOf(ModuleInterface.STATE_PUBLISH));
		result.put("опубликованный", Integer.valueOf(ModuleInterface.STATE_PUBLISH));
		result.put("archive", Integer.valueOf(ModuleInterface.STATE_ARCHIVE));
		result.put("archived", Integer.valueOf(ModuleInterface.STATE_ARCHIVE));
		result.put("архив", Integer.valueOf(ModuleInterface.STATE_ARCHIVE));
		result.put("архивный", Integer.valueOf(ModuleInterface.STATE_ARCHIVE));
		result.put("dead", Integer.valueOf(ModuleInterface.STATE_DEAD));
		result.put("obsolete", Integer.valueOf(ModuleInterface.STATE_DEAD));
		result.put("устаревший", Integer.valueOf(ModuleInterface.STATE_DEAD));
		return result;
	}
	
	private final BaseObject data;
	
	final Object title;
	
	final String icon;
	
	private final boolean visible;
	
	private final boolean constant;
	
	private Set<String> evaluable = null;
	
	private Set<String> respondable = null;
	
	/**
	 *
	 */
	protected final Type<?> parent;
	
	/**
	 *
	 */
	protected final ExecProcess typeContext;
	
	/**
	 * @param parent
	 * @param parentContext
	 * @param key
	 * @param attributes
	 */
	protected AbstractType(final Type<?> parent, final ExecProcess parentContext, final String key, final BaseObject attributes) {
		this.parent = parent;
		this.typeContext = Exec.createProcess(parentContext, "Type Context" + key);
		this.key = key;
		this.data = new BaseNativeObject(this.basePrototype());
		if (attributes == null) {
			this.title = key;
			this.icon = "document";
			this.visible = true;
			this.constant = false;
		} else {
			this.title = Base.getJava(attributes, "title", key);
			this.icon = Base.getString(attributes, "icon", "document");
			this.visible = !"hidden".equals(Base.getString(attributes, "visibility", "").trim());
			this.constant = Convert.MapEntry.toBoolean(attributes, "final", false);
			this.setAttributes(attributes);
		}
	}
	
	@Override
	public final BaseObject baseGetSubstitution() {
		
		return this.data;
	}
	
	@Override
	public BaseObject basePrototype() {
		
		return this.parent == null
			? Type.PROTOTYPE
			: this.parent;
	}
	
	@Override
	public Object getCommandAdditionalResult(final BaseEntry<?> entry, final ControlCommand<?> command, final BaseObject arguments) {
		
		final Type<?> parentType = this.getParentType();
		return parentType == null
			? null
			: parentType.getCommandAdditionalResult(entry, command, arguments);
	}
	
	@Override
	public BaseObject getCommandAttributes(final String key) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType == null) {
			return null;
		}
		return parentType.getCommandAttributes(key);
	}
	
	@Override
	public ControlFieldset<?> getCommandFieldset(final String key) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType == null) {
			return null;
		}
		return parentType.getCommandFieldset(key);
	}
	
	@Override
	public ControlCommandset getCommandsAdditional(final BaseEntry<?> entry, ControlCommandset target, final Set<String> include, Set<String> exclude) {
		
		if (Base.hasKeys(this)) {
			final ExecProcess ctx = Exec.currentProcess();
			ctx.vmFrameEntryExFull();
			final int rBSB = ctx.ri0BSB;
			try {
				ctx.vmScopeDeriveContextFromGV();
				for (final Iterator<String> iterator = Base.keys(this); iterator.hasNext();) {
					final String key = iterator.next();
					final BaseObject object = this.baseGet(key, BaseObject.UNDEFINED);
					assert object != null : "NULL java object";
					if (!(object instanceof TypeCommand)) {
						continue;
					}
					final TypeCommand command = (TypeCommand) object;
					if ("hidden".equals(command.commandPermission())) {
						continue;
					}
					if (include == null || include.contains(key)) {
						if (exclude == null) {
							exclude = Create.tempSet();
						}
						if (exclude.add(key)) {
							try {
								if (command.getCheck().callNE0(ctx, entry).baseToJavaBoolean()) {
									if (target == null) {
										target = Control.createOptions();
									}
									target.add(
											Control.createCommand(key, command.getTitle()).setAttributes(command.getAttributes()).setAttribute("typeInstance", this)
													.setAttribute("commandItself", command).setAttribute("key", key));
								}
							} catch (final Throwable e) {
								Report.exception("TYPE", "Error while checking commands, type=" + this.getKey() + ", commandKey=" + this.getKey() + ", fieldKey=" + key, e);
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
		final Type<?> parentType = this.getParentType();
		return parentType == null
			? target
			: parentType.getCommandsAdditional(entry, target, include, exclude);
	}
	
	@Override
	public final Collection<String> getContentListingFields() {
		
		final String listing = Base.getString(this.getAttributes(), "listing", "").trim();
		if (listing.length() == 0) {
			final Type<?> parentType = this.getParentType();
			return parentType == null
				? null
				: parentType.getContentListingFields();
		}
		final List<String> result = new ArrayList<>();
		final StringTokenizer fields = new StringTokenizer(listing, ",");
		while (fields.hasMoreTokens()) {
			result.add(fields.nextToken().trim());
		}
		return result;
	}
	
	@Override
	public final BaseObject getData() {
		
		return this.data;
	}
	
	@Override
	public boolean getDefaultFolder() {
		
		final Type<?> parent = this.getParentType();
		/**
		 * human readable
		 */
		return Convert.MapEntry.toBoolean(this.getAttributes(), "folder", parent == null
			? false
			: parent.getDefaultFolder());
	}
	
	@Override
	public int getDefaultState() {
		
		final Type<?> parent = this.getParentType();
		return Convert.MapEntry.toInt(
				AbstractType.MAP_STATES, //
				Base.getJava(
						this.getAttributes(), //
						"state",
						""),
				parent == null
					? ModuleInterface.STATE_DRAFT
					: parent.getDefaultState());
	}
	
	@Override
	public boolean getDefaultVersioning() {
		
		/**
		 * human readable
		 */
		return Convert.MapEntry.toBoolean(this.getAttributes(), "versioning", false);
	}
	
	@Override
	public ControlFieldset<?> getFieldsetDelete() {
		
		final Type<?> parentType = this.getParentType();
		if (parentType == null) {
			return null;
		}
		return parentType.getFieldsetDelete();
	}
	
	@Override
	public Set<String> getFieldsEvaluable() {
		
		if (this.evaluable != null) {
			return this.evaluable;
		}
		synchronized (this) {
			if (this.evaluable != null) {
				return this.evaluable;
			}
			final ControlFieldset<?> fieldsetLoad = this.getFieldsetLoad();
			if (fieldsetLoad == null) {
				return this.evaluable = AbstractType.EMPTY_SET_STRING;
			}
			Set<String> evaluable = null;
			final int length = fieldsetLoad.size();
			for (int i = 0; i < length; ++i) {
				final ControlField field = fieldsetLoad.get(i);
				if ("evaluate".equals(field.getFieldClass())) {
					(evaluable == null
						? evaluable = new TreeSet<>()
						: evaluable).add(field.getKey());
				}
			}
			if (evaluable == null) {
				return this.evaluable = AbstractType.EMPTY_SET_STRING;
			}
			if (evaluable.size() == 1) {
				return this.evaluable = Collections.singleton(evaluable.iterator().next());
			}
			return this.evaluable = evaluable;
		}
	}
	
	@Override
	public final Set<String> getFieldsPublic() {
		
		if (this.respondable != null) {
			return this.respondable;
		}
		synchronized (this) {
			if (this.respondable != null) {
				return this.respondable;
			}
			final ControlFieldset<?> fieldsetLoad = this.getFieldsetLoad();
			if (fieldsetLoad == null) {
				return this.respondable = AbstractType.EMPTY_SET_STRING;
			}
			Set<String> respondable = null;
			final int length = fieldsetLoad.size();
			for (int i = 0; i < length; ++i) {
				final ControlField field = fieldsetLoad.get(i);
				/**
				 * human readable
				 */
				if (Convert.MapEntry.toBoolean(field.getAttributes(), "respond", false)) {
					if (respondable == null) {
						respondable = new HashSet<>();
					}
					field.fillFields(respondable);
				}
			}
			if (respondable == null || respondable.isEmpty()) {
				return this.respondable = AbstractType.EMPTY_SET_STRING;
			}
			if (respondable.size() == 1) {
				return this.respondable = Collections.singleton(respondable.iterator().next());
			}
			return this.respondable = respondable;
		}
	}
	
	@Override
	public final String getIcon() {
		
		return this.icon;
	}
	
	@Override
	public final Type<?> getParentType() {
		
		return this.parent;
	}
	
	@Override
	public BaseMessage getResource(final String key) throws Throwable {
		
		return null;
	}
	
	@Override
	public BaseObject getResponse(final ExecProcess process, final BaseEntry<?> entry, final BaseObject content) {
		
		return content;
	}
	
	@Override
	public abstract ReplyAnswer getResponse(final ServeRequest query, final BaseEntry<?> entry);
	
	@Override
	public final String getTitle() {
		
		return String.valueOf(this.title);
	}
	
	@Override
	public boolean getTypeBehaviorResponseFiltering() {
		
		return false;
	}
	
	@Override
	public ExecProcess getTypeContext() {
		
		return this.typeContext;
	}
	
	@Override
	public Collection<String> getValidChildrenTypeNames() {
		
		return null;
	}
	
	@Override
	public Collection<String> getValidParentsTypeNames() {
		
		return null;
	}
	
	@Override
	public Collection<Integer> getValidStateList() {
		
		final BaseObject stateList = this.getAttributes().baseGet("statelist", BaseObject.UNDEFINED);
		assert stateList != null : "NULL java value";
		if (stateList == BaseObject.UNDEFINED) {
			final Type<?> parentType = this.getParentType();
			return parentType == null
				? null
				: parentType.getValidStateList();
		}
		final BaseArray stateNames = Convert.MapEntry.toCollection(stateList, "state", null);
		if (stateNames == null) {
			return null;
		}
		Set<Integer> result = null;
		final int length = stateNames.length();
		for (int i = 0; i < length; ++i) {
			final BaseObject element = stateNames.baseGet(i, BaseObject.UNDEFINED);
			final int state = Convert.MapEntry.toInt(AbstractType.MAP_STATES, element, -1);
			if (state != -1) {
				if (result == null) {
					result = Create.tempSet();
				}
				result.add(Integer.valueOf(state));
			}
		}
		return result;
	}
	
	@Override
	public boolean hasDeletionForm() {
		
		final Type<?> parentType = this.getParentType();
		return parentType == null
			? false
			: parentType.hasDeletionForm();
	}
	
	@Override
	public final boolean isClientVisible() {
		
		return this.visible;
	}
	
	@Override
	public final boolean isFinal() {
		
		return this.constant;
	}
	
	@Override
	public boolean isInstance(final String typeName) {
		
		int counter = 128;
		for (Type<?> currentType = this;;) {
			final String current = currentType.getKey();
			if (--counter < 0) {
				throw new RuntimeException("Type heirarchy recursion detected!");
			}
			if (current.equals(typeName)) {
				return true;
			}
			final Collection<String> replacements = currentType.getReplacements();
			if (replacements != null) {
				for (final String replacement : replacements) {
					if (replacement.equals(typeName)) {
						return true;
					}
				}
			}
			final Type<?> nextType = currentType.getParentType();
			if (nextType == null || currentType == nextType) {
				return false;
			}
			currentType = nextType;
		}
	}
	
	@Override
	public boolean isValidState(final Object state) {
		
		final Collection<Integer> stateList = this.getValidStateList();
		if (stateList == null) {
			return true;
		}
		final Integer stateIndex = Integer.valueOf(Convert.MapEntry.toInt(AbstractType.MAP_STATES, state, ModuleInterface.STATE_DRAFT));
		return stateList.contains(stateIndex);
	}
	
	@Override
	public void scriptCommandFormPrepare(final String key, final BaseEntry<?> entry, final BaseObject parameters) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType != null) {
			parentType.scriptCommandFormPrepare(key, entry, parameters);
		}
	}
	
	@Override
	public void scriptCommandFormSubmit(final String key, final BaseEntry<?> entry, final BaseObject parameters) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType != null) {
			parentType.scriptCommandFormSubmit(key, entry, parameters);
		}
	}
	
	@Override
	public void scriptPrepareCreate(final BaseChange change, final BaseObject data) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType != null) {
			parentType.scriptPrepareCreate(change, data);
		}
	}
	
	@Override
	public void scriptPrepareDelete(final BaseEntry<?> entry, final BaseObject data) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType != null) {
			parentType.scriptPrepareDelete(entry, data);
		}
	}
	
	@Override
	public void scriptPrepareModify(final BaseEntry<?> entry, final BaseChange change, final BaseObject data) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType == null) {
			data.baseDefineImportAllEnumerable(entry.getData());
		} else {
			parentType.scriptPrepareModify(entry, change, data);
		}
	}
	
	@Override
	public void scriptSubmitCreate(final BaseChange change, final BaseObject data) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType == null) {
			final BaseObject target = change.getData();
			if (target != data) {
				target.baseDefineImportAllEnumerable(data);
			}
		} else {
			parentType.scriptSubmitCreate(change, data);
		}
	}
	
	@Override
	public void scriptSubmitDelete(final BaseEntry<?> entry, final BaseObject data) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType == null) {
			// empty
		} else {
			parentType.scriptSubmitDelete(entry, data);
		}
	}
	
	@Override
	public void scriptSubmitModify(final BaseEntry<?> entry, final BaseChange change, final BaseObject data) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType == null) {
			final BaseObject target = change.getData();
			if (target != data) {
				target.baseDefineImportAllEnumerable(data);
			}
		} else {
			parentType.scriptSubmitModify(entry, change, data);
		}
	}
	
	@Override
	public void typeStart() {
		
		// ignore
	}
	
	@Override
	public void typeStop() {
		
		// ignore
	}
}
