package ru.myx.ae1.types;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.cache.CacheL2;
import ru.myx.ae3.control.command.SimpleCommand;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.exec.ExecCallable;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ProgramPart;
import ru.myx.ae3.exec.fn.ExecAlwaysInstance;
import ru.myx.ae3.exec.fn.ExecAlwaysStatic;
import ru.myx.ae3.exec.fn.ExecAuto;
import ru.myx.ae3.exec.fn.ExecBufferedInstance;
import ru.myx.ae3.exec.fn.ExecBufferedStatic;
import ru.myx.ae3.exec.fn.ExecCachedInstance;
import ru.myx.ae3.exec.fn.ExecCachedStatic;
import ru.myx.ae3.exec.fn.ExecDeferredInstance;
import ru.myx.ae3.exec.fn.ExecDeferredStatic;
import ru.myx.ae3.exec.fn.ExecEmpty;
import ru.myx.ae3.exec.fn.ExecOnceStatic;
import ru.myx.ae3.exec.fn.FunctionExecutionType;
import ru.myx.ae3.exec.fn.FunctionExecutor;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.reflect.ControlType;

/*
 * Created on 16.01.2005
 */
final class TypeCommand extends SimpleCommand {
	
	private static final ControlFieldset<?> NULL_FIELDSET = ControlFieldset.createFieldset();
	
	private final ControlFieldset<?> arguments;
	
	private ProgramPart check;
	
	private final String checkExpression;
	
	private final FunctionExecutionType execution;
	
	private ControlFieldset<?> fieldset;
	
	private final BaseObject form;
	
	private final String icon;
	
	private final boolean instance;
	
	private final String key;
	
	private final String keyType;
	
	private final ControlType<?, ?> result;
	
	private ExecCallable script;
	
	private final BaseObject scriptSource;
	
	private final Server server;
	
	private final Type<?> type;
	
	TypeCommand(final Server server, final Type<?> type, final String key, final String keyType, final BaseObject attributes) {

		assert server != null : "Server is NULL";
		attributes.baseDefine("id", key);
		attributes.baseDefine("typeInstance", type);
		attributes.baseDefine("commandItself", this);
		this.setAttributes(attributes);
		this.server = server;
		this.type = type;
		this.keyType = keyType;
		this.key = this.getKey();
		/** convert - cause we need a human readable form supported as well */
		this.instance = !Convert.MapEntry.toBoolean(attributes, "static", false);
		this.execution = FunctionExecutionType.getExecutionType(Base.getString(attributes, "execute", ""), FunctionExecutionType.ALWAYS);
		this.checkExpression = Base.getString(attributes, "enable", "true").trim();
		this.form = attributes.baseGet("form", BaseObject.UNDEFINED);
		this.scriptSource = attributes.baseGet("script", BaseObject.UNDEFINED);
		this.arguments = ControlFieldset.getFieldsetFromMap(attributes, "arguments");
		this.result = Control.getTypeByName(Base.getString(attributes, "result", "Object").trim());
		this.icon = Base.getString(attributes, "icon", null);
	}
	
	@Override
	public BaseObject baseGetSubstitution() {
		
		return this.getAttributes();
	}
	
	/** !!! NO USE
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
	 * @throws Exception */
	public final boolean enable(final ExecProcess ctx, final BaseObject object) throws Exception {
		
		return this.getCheck().callNE0(ctx, object).baseToJavaBoolean();
	}
	
	@Override
	public Class<?> execResultClassJava() {
		
		return this.result.getJavaClass();
	}
	
	@Override
	public BaseObject execScope() {
		
		/** Only because all of the executors actually do return this value. */
		/** executes in real current scope */
		return ExecProcess.GLOBAL;
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
				final ProgramPart renderer = this.scriptSource.baseValue() instanceof ProgramPart
					? (ProgramPart) this.scriptSource.baseValue()
					/** FIXME: it looks that there's no language specified here! */
					: this.server.createRenderer("TYPE/COMMAND{" + sourceURI + "}", this.scriptSource);
				/** TODO: make 'type' context, with server context as a parent */
				final ExecProcess parentContext = this.type.getTypeContext();
				switch (this.execution) {
					case ALWAYS : // always
						this.script = this.instance
							? new ExecAlwaysInstance(
									this.result, //
									this.arguments,
									renderer,
									parentContext)
							: new ExecAlwaysStatic(
									this.type, //
									this.result,
									this.arguments,
									renderer,
									parentContext);
						break;
					case BUFFERED : // buffered
					{
						final long delay = Convert.MapEntry.toPeriod(this.getAttributes(), "delay", 0L);
						final long period = Convert.MapEntry.toPeriod(this.getAttributes(), "period", 0L);
						this.script = this.instance
							? new ExecBufferedInstance(sourceURI, this.arguments, renderer, delay, period, parentContext)
							: new ExecBufferedStatic(sourceURI, this.type, this.arguments, renderer, delay, period, parentContext);
					}
						break;
					case DEFERRED : // deferred
					{
						final long delay = Convert.MapEntry.toPeriod(this.getAttributes(), "delay", 0L);
						final long period = Convert.MapEntry.toPeriod(this.getAttributes(), "period", 0L);
						this.script = this.instance
							? new ExecDeferredInstance(sourceURI, this.arguments, renderer, delay, period, parentContext)
							: new ExecDeferredStatic(sourceURI, this.type, this.arguments, renderer, delay, period, parentContext);
					}
						break;
					case ONCE : // once
					{
						if (this.instance) {
							final CacheL2<BaseObject> cache = this.server.getObjectCache();
							this.script = cache == null
								? new ExecAlwaysInstance(this.result, this.arguments, renderer, parentContext)
								: new ExecCachedInstance(cache, this.result, this.arguments, renderer, sourceURI, 60_000L * 60L * 24L, parentContext);
						} else {
							this.script = new ExecOnceStatic(
									this.type, //
									this.result,
									renderer,
									parentContext);
						}
					}
						break;
					case CACHE : // cache
					{
						final long expiration = Convert.MapEntry.toPeriod(this.getAttributes(), "expire", 15L * 60_000L);
						if (expiration > 60_000L) {
							final CacheL2<BaseObject> cache = this.server.getObjectCache();
							this.script = cache == null
								? this.instance
									? new ExecAlwaysInstance(this.result, this.arguments, renderer, parentContext)
									: new ExecAlwaysStatic(this.type, this.result, this.arguments, renderer, parentContext)
								: this.instance
									? new ExecCachedInstance(cache, this.result, this.arguments, renderer, sourceURI, expiration, parentContext)
									: new ExecCachedStatic(cache, this.type, this.result, this.arguments, renderer, sourceURI, expiration, parentContext);
						} else {
							this.script = this.instance
								? new ExecAlwaysInstance(this.result, this.arguments, renderer, parentContext)
								: new ExecAlwaysStatic(this.type, this.result, this.arguments, renderer, parentContext);
						}
					}
						break;
					case AUTO : // auto
					{
						final long delay = Convert.MapEntry.toPeriod(this.getAttributes(), "delay", 0L);
						final long period = Convert.MapEntry.toPeriod(this.getAttributes(), "period", 0L);
						this.script = new ExecAuto(
								sourceURI, //
								renderer,
								delay,
								period,
								parentContext);
					}
						break;
					default : // always
						this.script = this.instance
							? new ExecAlwaysInstance(this.result, this.arguments, renderer, parentContext)
							: new ExecAlwaysStatic(this.type, this.result, this.arguments, renderer, parentContext);
						break;
				}
			}
		}
		return this.script;
	}
	
	@Override
	public final String getIcon() {
		
		return this.icon;
	}
	
	@Override
	public String toString() {
		
		return "[object " + this.baseClass() + "(" + "key=" + this.key + ", type=" + this.keyType + ")]";
	}
	
	final ProgramPart getCheck() {
		
		if (this.check == null) {
			synchronized (this.checkExpression) {
				if (this.check == null) {
					this.check = Evaluate.prepareFunctionObjectForExpression(
							this.checkExpression.length() == 0
								? "true"
								: this.checkExpression,
							null);
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
						this.fieldset = TypeCommand.NULL_FIELDSET;
					} else if (fields instanceof ControlFieldset<?>) {
						this.fieldset = (ControlFieldset<?>) fields;
					} else {
						final String commandFieldset = fields.baseToJavaString().trim();
						if (commandFieldset.length() == 0) {
							this.fieldset = TypeCommand.NULL_FIELDSET;
							return null;
						}
						final ControlFieldset<?> fieldset = ControlFieldset.materializeFieldset(commandFieldset);
						this.fieldset = fieldset.isEmpty()
							? TypeCommand.NULL_FIELDSET
							: fieldset;
					}
				}
			}
		}
		return this.fieldset == TypeCommand.NULL_FIELDSET
			? null
			: this.fieldset;
	}
	
	final BaseObject getForm() {
		
		return this.form;
	}
	
	final boolean needStart() {
		
		return this.execution == FunctionExecutionType.AUTO;
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
}
