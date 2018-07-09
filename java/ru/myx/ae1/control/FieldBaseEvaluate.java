package ru.myx.ae1.control;

import ru.myx.ae3.act.Act;
/*
 * Created on 26.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecArgumentsEmpty;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ProgramPart;
import ru.myx.ae3.exec.ResultHandler;

/**
 * @author myx
 *
 *
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
class FieldBaseEvaluate extends AbstractField<FieldBaseEvaluate, Object, BaseObject> {
	
	
	private String argument;
	
	private String expression;
	
	private String id;
	
	private ProgramPart prepared;
	
	FieldBaseEvaluate() {
		this.setAttributeIntern("type", "transparent");
		this.recalculate();
	}
	
	@Override
	public FieldBaseEvaluate cloneField() {
		
		
		return new FieldBaseEvaluate().setAttributes(this.getAttributes());
	}
	
	@Override
	public BaseObject dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		
		
		assert argument != null : "NULL java object";
		
		final ExecProcess ctx = Exec.createProcess(Exec.currentProcess(), "Field (" + this.id + ") evaluation Context: " + this.expression);
		ctx.vmFrameEntryExCall(true, fieldsetContext, this.prepared, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
		ctx.vmScopeDeriveContextFromGV();
		if (fieldsetContext != null) {
			ctx.contextCreateMutableBinding("record", fieldsetContext, false);
		}
		ctx.contextCreateMutableBinding(this.argument, argument, false);
		try {
			return Act.run(ctx, prepared -> prepared.execCallPreparedInilne(ctx), this.prepared);
		} catch (final AbstractReplyException e) {
			throw e;
		} catch (final Throwable e) {
			throw new RuntimeException("EVALUATE: " + this.expression, e);
		}
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		
		
		return BaseObject.UNDEFINED;
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		
		
		return ControlFieldset.createFieldset()
				.addField(
						ControlFieldFactory.createFieldString("expression", "Expression", "100-21/7")
								.setFieldHint("Fieldset data is accessible via Record map-object. May be NULL for new documents."))
				.addField(
						ControlFieldFactory.createFieldString("argument", "Argument name", "argument")
								.setFieldHint("Fieldset data is accessible via Record map-object. May be NULL for new documents."));
	}
	
	@Override
	public String getFieldClass() {
		
		
		return "evaluate";
	}
	
	@Override
	public String getFieldClassTitle() {
		
		
		return "Evaluate expression";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		
		
		return "transparent".equals(type);
	}
	
	@Override
	public boolean isConstant() {
		
		
		return true;
	}
	
	@Override
	public void recalculate() {
		
		
		this.id = this.getKey();
		this.argument = Base.getString(this.getAttributes(), "argument", "argument");
		this.expression = Base.getString(this.getAttributes(), "expression", "'no expression!'");
		this.prepared = Evaluate.prepareFunctionObjectForExpression(this.expression, null);
	}
}
