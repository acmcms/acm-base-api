package ru.myx.ae1.types;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseFunction;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.command.SimpleCommand;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.exec.ExecCallable;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ProgramPart;
import ru.myx.ae3.exec.fn.ExecAlwaysInstance;
import ru.myx.ae3.exec.fn.ExecEmpty;
import ru.myx.ae3.exec.fn.FunctionExecutionType;
import ru.myx.ae3.exec.fn.FunctionExecutor;
import ru.myx.ae3.reflect.ControlType;

/*
 * Created on 16.01.2005
 */
final class TypeConstructor extends SimpleCommand {
	
	private static final ControlFieldset<?> NULL_FIELDSET = ControlFieldset.createFieldset();
	
	private final ControlFieldset<?> arguments;
	
	private ProgramPart check;
	
	private final String checkExpression;
	
	private ControlFieldset<?> fieldset;
	
	private final BaseObject form;
	
	private final String icon;
	
	private final String key;
	
	private final String keyType;
	
	private final ControlType<?, ?> result;
	
	private ExecCallable script;
	
	private final BaseObject scriptSource;
	
	private final Server server;
	
	private final Type<?> type;
	
	TypeConstructor(final Server server, final Type<?> type, final String key, final String keyType, final BaseObject attributes) {
		assert server != null : "Server is NULL";
		attributes.baseDefine("id", key);
		attributes.baseDefine("typeInstance", type);
		attributes.baseDefine("commandItself", this);
		this.setAttributes(attributes);
		this.server = server;
		this.keyType = keyType;
		this.key = this.getKey();
		assert FunctionExecutionType.getExecutionType(Base.getString(attributes, "execute", ""), FunctionExecutionType.ALWAYS) == FunctionExecutionType.ALWAYS;
		this.checkExpression = Base.getString(attributes, "enable", "true").trim();
		this.form = attributes.baseGet("form", BaseObject.UNDEFINED);
		this.scriptSource = attributes.baseGet("script", BaseObject.UNDEFINED);
		this.arguments = ControlFieldset.getFieldsetFromMap(attributes, "arguments");
		this.result = Control.getTypeByName(Base.getString(attributes, "result", "Object").trim());
		this.icon = Base.getString(attributes, "icon", null);
		this.type = type;
	}
	
	@Override
	public BaseFunction baseConstruct() {
		
		return this;
	}
	
	@Override
	public BaseObject baseConstructPrototype() {
		
		return this.type;
	}
	
	@Override
	public BaseObject baseGetSubstitution() {
		
		return this.getAttributes();
	}
	
	/**
	 * !!! NO USE
	 * <p>
	 * !!! rename to makesSense(object)
	 * <p>
	 * Created: 677+<br>
	 * Modified: 677+
	 * <p>
	 * Used in script menu checks an so on: <br>
	 * var show = !obj.method.enable || o.method.enable(obj);<br>
	 * var hide = obj.method.enable && !o.method.enable(obj);
	 *
	 * @param ctx
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public final boolean enable(final ExecProcess ctx, final BaseObject object) throws Exception {
		
		return this.getCheck().callNE0(ctx, object).baseToJavaBoolean();
	}
	
	@Override
	public Class<?> execResultClassJava() {
		
		return this.result.getJavaClass();
	}
	
	@Override
	public BaseObject execScope() {
		
		return this.server.getRootContext().ri10GV;
	}
	
	@Override
	public final ExecCallable function() {
		
		if (this.script == null) {
			if (this.scriptSource == BaseObject.UNDEFINED) {
				return this.script = new ExecEmpty(this.result);
			}
			synchronized (this.scriptSource) {
				if (this.script != null) {
					return this.script;
				}
				final String sourceURI = "M$" + this.keyType + ":" + this.key;
				final ProgramPart renderer = this.scriptSource instanceof ProgramPart
					? (ProgramPart) this.scriptSource
					/**
					 * FIXME: it looks that there's no language specified here!
					 */
					: this.server.createRenderer("TYPE/CONSTRUCTOR{" + sourceURI + "}", this.scriptSource);
				this.script = new ExecAlwaysInstance(
						this.result, //
						this.arguments,
						renderer,
						this.server.getRootContext());
			}
		}
		return this.script;
	}
	
	final ProgramPart getCheck() {
		
		if (this.check == null) {
			synchronized (this.checkExpression) {
				if (this.check == null) {
					this.check = Evaluate.prepareFunctionObjectForExpression(this.checkExpression.length() == 0
						? "true"
						: this.checkExpression, null);
				}
			}
		}
		return this.check;
	}
	
	final ControlFieldset<?> getFieldset() {
		
		if (this.fieldset == null) {
			synchronized (this.form) {
				if (this.fieldset == null) {
					final BaseObject fields = this.form.baseGet("fields", BaseObject.UNDEFINED);
					assert fields != null : "NULL java value";
					if (fields == BaseObject.UNDEFINED) {
						this.fieldset = TypeConstructor.NULL_FIELDSET;
					} else if (fields instanceof ControlFieldset<?>) {
						this.fieldset = (ControlFieldset<?>) fields;
					} else {
						final String commandFieldset = fields.baseToJavaString().trim();
						if (commandFieldset.length() == 0) {
							this.fieldset = TypeConstructor.NULL_FIELDSET;
							return null;
						}
						final ControlFieldset<?> fieldset = ControlFieldset.materializeFieldset(commandFieldset);
						this.fieldset = fieldset.isEmpty()
							? TypeConstructor.NULL_FIELDSET
							: fieldset;
					}
				}
			}
		}
		return this.fieldset == TypeConstructor.NULL_FIELDSET
			? null
			: this.fieldset;
	}
	
	final BaseObject getForm() {
		
		return this.form;
	}
	
	@Override
	public final String getIcon() {
		
		return this.icon;
	}
	
	final void start() {
		
		this.function();
		if (this.script instanceof FunctionExecutor) {
			((FunctionExecutor) this.script).start();
		}
	}
	
	final void stop() {
		
		if (this.script != null) {
			if (this.script instanceof FunctionExecutor) {
				((FunctionExecutor) this.script).stop();
			}
			this.script = null;
		}
	}
	
	@Override
	public String toString() {
		
		return "[object " + this.baseClass() + "(" + "key=" + this.key + ", type=" + this.keyType + ")]";
	}
}
