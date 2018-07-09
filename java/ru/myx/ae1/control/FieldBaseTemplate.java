package ru.myx.ae1.control;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;

// добавить скриптовый язык какой в атрибут
final class FieldBaseTemplate extends FieldBaseString {
	FieldBaseTemplate() {
		this.setAttributeIntern( "type", "text" );
		this.setAttributeIntern( "variant", "template" );
		this.setAttributeIntern( "max", 128 * 1024 );
		this.recalculate();
	}
	
	@Override
	public FieldBaseString cloneField() {
		final FieldBaseTemplate result = new FieldBaseTemplate();
		result.setAttributes( this.getAttributes() );
		return result;
	}
	
	@Override
	public String dataValidate(final BaseObject source) {
		super.dataValidate( source );
		try {
			final BaseObject valueObject = source.baseGet( this.getKey(), BaseObject.UNDEFINED );
			assert valueObject != null : "NULL java value";
			if (valueObject != BaseObject.UNDEFINED && valueObject != BaseObject.NULL) {
				Context.getServer( Exec.currentProcess() ).createRenderer( "FLD/TEMPLATE{id=" + this.getKey() + "}", valueObject.toString() );
			}
			return null;
		} catch (final Throwable t) {
			return t.getMessage();
		}
	}
	
	@Override
	public String getFieldClass() {
		return "template";
	}
	
	@Override
	public String getFieldClassTitle() {
		return "Script/Template";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		return "text".equals( type );
	}
}
