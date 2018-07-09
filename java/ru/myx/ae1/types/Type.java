/*
 * Created on 22.06.2004
 */
package ru.myx.ae1.types;

import java.util.Collection;
import java.util.Set;

import ru.myx.ae1.storage.BaseChange;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.reflect.Reflect;
import ru.myx.ae3.serve.ServeRequest;

/**
 * @author myx To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * @param <T>
 */
public interface Type<T extends Type<?>> extends ControlBasic<T> {
	
	/**
	 *
	 */
	static final BaseObject PROTOTYPE = /* new BaseNativeObject( */Reflect.classToBasePrototype(Type.class)/* ) */;
	
	/**
	 * @param entry
	 * @param command
	 * @param arguments
	 * @return object
	 */
	Object getCommandAdditionalResult(BaseEntry<?> entry, ControlCommand<?> command, BaseObject arguments);
	
	/**
	 * @param key
	 * @return map
	 */
	BaseObject getCommandAttributes(String key);
	
	/**
	 * @param key
	 * @return fieldset
	 */
	ControlFieldset<?> getCommandFieldset(String key);
	
	/**
	 * target can be null
	 *
	 * include can be null
	 *
	 * exclude can be null
	 *
	 * @param entry
	 * @param target
	 * @param include
	 * @param exclude
	 * @return commandset
	 */
	ControlCommandset getCommandsAdditional(BaseEntry<?> entry, ControlCommandset target, Set<String> include, Set<String> exclude);
	
	/**
	 * @return collection
	 */
	Collection<String> getContentListingFields();
	
	/**
	 * @return boolean
	 */
	boolean getDefaultFolder();
	
	/**
	 * @return int
	 */
	int getDefaultState();
	
	/**
	 * @return boolean
	 */
	boolean getDefaultVersioning();
	
	/**
	 * @return fieldset
	 */
	ControlFieldset<?> getFieldsetCreate();
	
	/**
	 * @return fieldset
	 */
	ControlFieldset<?> getFieldsetDelete();
	
	/**
	 * @return fieldset
	 */
	ControlFieldset<?> getFieldsetLoad();
	
	/**
	 * @return fieldset
	 */
	ControlFieldset<?> getFieldsetProperties();
	
	/**
	 * @return set
	 */
	Set<String> getFieldsEvaluable();
	
	/**
	 * @return set
	 */
	Set<String> getFieldsPublic();
	
	/**
	 * @return parent type
	 */
	Type<?> getParentType();
	
	/**
	 * @return collection
	 */
	Collection<String> getReplacements();
	
	/**
	 * @param key
	 * @return message or null
	 * @throws Throwable
	 */
	BaseMessage getResource(String key) throws Throwable;
	
	/**
	 * @param process
	 *            this process will be a parent process for execution (if any)
	 * @param entry
	 * @param content
	 * @return object
	 */
	BaseObject getResponse(ExecProcess process, BaseEntry<?> entry, BaseObject content);
	
	/**
	 * @param query
	 * @param entry
	 * @return response
	 */
	ReplyAnswer getResponse(ServeRequest query, BaseEntry<?> entry);
	
	/**
	 * @return boolean
	 */
	boolean getTypeBehaviorAutoRecalculate();
	
	/**
	 * @return boolean
	 */
	boolean getTypeBehaviorHandleAllIncoming();
	
	/**
	 * @return boolean
	 */
	boolean getTypeBehaviorHandleAnyThrough();
	
	/**
	 * @return boolean
	 */
	boolean getTypeBehaviorHandleToParent();
	
	/**
	 * @return string
	 */
	String getTypeBehaviorListingSort();
	
	/**
	 * @return int
	 */
	int getTypeBehaviorResponseCacheClientTtl();
	
	/**
	 * @return long
	 */
	long getTypeBehaviorResponseCacheServerTtl();
	
	/**
	 * @return boolean
	 */
	boolean getTypeBehaviorResponseFiltering();
	
	/**
	 *
	 * @return
	 */
	ExecProcess getTypeContext();
	
	/**
	 * @return date
	 */
	long getTypeModificationDate();
	
	/**
	 *
	 * @return type's prototype object - an object to be used in prototype chain
	 *         of an actual object.
	 */
	BaseObject getTypePrototypeObject();
	
	/**
	 * @return collection
	 */
	Collection<String> getValidChildrenTypeNames();
	
	/**
	 * @return collection
	 */
	Collection<String> getValidParentsTypeNames();
	
	/**
	 * @return collection
	 */
	Collection<Integer> getValidStateList();
	
	/**
	 * @return boolean
	 */
	boolean hasDeletionForm();
	
	/**
	 * @return boolean
	 */
	boolean isClientVisible();
	
	/**
	 * Final types are not allowed to extend
	 *
	 * @return boolean
	 */
	boolean isFinal();
	
	/**
	 * @param typeName
	 * @return boolean
	 */
	boolean isInstance(String typeName);
	
	/**
	 * @param state
	 * @return boolean
	 */
	boolean isValidState(Object state);
	
	/**
	 * @param change
	 * @param data
	 */
	default void onBeforeCreate(final BaseChange change, final BaseObject data) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType != null) {
			parentType.onBeforeCreate(change, data);
		}
	}
	
	/**
	 * @param entry
	 */
	default void onBeforeDelete(final BaseEntry<?> entry) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType != null) {
			parentType.onBeforeDelete(entry);
		}
	}
	
	/**
	 * @param entry
	 * @param change
	 * @param data
	 */
	default void onBeforeModify(final BaseEntry<?> entry, final BaseChange change, final BaseObject data) {
		
		final Type<?> parentType = this.getParentType();
		if (parentType != null) {
			parentType.onBeforeModify(entry, change, data);
		}
	}
	
	/**
	 * @param key
	 * @param entry
	 * @param parameters
	 */
	void scriptCommandFormPrepare(String key, BaseEntry<?> entry, BaseObject parameters);
	
	/**
	 * @param key
	 * @param entry
	 * @param parameters
	 */
	void scriptCommandFormSubmit(String key, BaseEntry<?> entry, BaseObject parameters);
	
	/**
	 * @param change
	 * @param data
	 */
	void scriptPrepareCreate(BaseChange change, BaseObject data);
	
	/**
	 * @param entry
	 * @param data
	 */
	void scriptPrepareDelete(BaseEntry<?> entry, BaseObject data);
	
	/**
	 * @param entry
	 * @param change
	 * @param data
	 */
	void scriptPrepareModify(BaseEntry<?> entry, BaseChange change, BaseObject data);
	
	/**
	 * @param change
	 * @param data
	 */
	void scriptSubmitCreate(BaseChange change, BaseObject data);
	
	/**
	 * @param entry
	 * @param data
	 */
	void scriptSubmitDelete(BaseEntry<?> entry, BaseObject data);
	
	/**
	 * @param entry
	 * @param change
	 * @param data
	 */
	void scriptSubmitModify(BaseEntry<?> entry, BaseChange change, BaseObject data);
	
	/**
	 *
	 */
	void typeStart();
	
	/**
	 *
	 */
	void typeStop();
	
	/**
	 * @param type
	 * @param data
	 * @return map
	 */
	static BaseObject getDataAccessMapDefaultImpl(final Type<?> type, final BaseObject data) {
		
		/**
		 * FIXME: this code is not working by design! evaluateFields.isEmpty()
		 */
		final ControlFieldset<?> typeLoadFieldset = type.getFieldsetLoad();
		final Set<String> evaluateFields = type.getFieldsEvaluable();
		if (evaluateFields.isEmpty()) {
			return data == null
				? BaseObject.UNDEFINED
				: data;
		}
		if (data == null) {
			final BaseObject result = new BaseNativeObject();
			if (evaluateFields.isEmpty()) {
				return result;
			}
			return new DataAccessEvaluate(typeLoadFieldset, result);
		}
		if (evaluateFields.isEmpty()) {
			final BaseObject result = new BaseNativeObject(data);
			return result;
		}
		return new DataAccessEvaluate(typeLoadFieldset, data);
	}
}
