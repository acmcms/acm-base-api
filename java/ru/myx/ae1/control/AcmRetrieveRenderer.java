package ru.myx.ae1.control;

import java.util.Map;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecStateCode;
import ru.myx.ae3.exec.Instruction;
import ru.myx.ae3.help.Convert;

/*
 * Created on 03.09.2005
 */
final class AcmRetrieveRenderer implements Instruction {
	
	
	private final ControlFieldset<?> fieldset;
	
	/**
	 * @param fieldset
	 */
	AcmRetrieveRenderer(final ControlFieldset<?> fieldset) {
		this.fieldset = fieldset == null
			? ControlFieldset.createFieldset()
			: fieldset;
	}
	
	@Override
	public ExecStateCode execCall(final ExecProcess ctx) throws Exception {
		
		
		final BaseObject result = new BaseNativeObject();
		final Object alternativeObject = ctx.rb4CT;
		if (alternativeObject != null) {
			if (alternativeObject instanceof BaseObject) {
				final BaseObject alternative = Convert.Any.toAny(alternativeObject);
				this.fieldset.dataRetrieve(alternative, result);
				ctx.ra0RB = result;
				return null;
			}
			if (alternativeObject instanceof Map<?, ?>) {
				final Map<String, Object> alternative = Convert.Any.toAny(alternativeObject);
				this.fieldset.dataRetrieve(Base.forUnknown(alternative), result);
				ctx.ra0RB = result;
				return null;
			}
			if (alternativeObject instanceof ControlBasic<?>) {
				this.fieldset.dataRetrieve((ControlBasic<?>) alternativeObject, result);
				ctx.ra0RB = result;
				return null;
			}
		}
		this.fieldset.dataRetrieve(ctx, result);
		ctx.ra0RB = result;
		return null;
	}
	
	@Override
	public int getOperandCount() {
		
		
		return 0;
	}
	
	@Override
	public int getResultCount() {
		
		
		return 1;
	}
	
	@Override
	public String toCode() {
		
		
		return "RETRIEVE-CODE";
	}
}
