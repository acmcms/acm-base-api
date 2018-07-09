package ru.myx.ae1.control;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.binary.TransferBuffer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.extra.External;
import ru.myx.ae3.flow.Flow;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.report.Report;

/** Title: System DocumentLevel for every level of 6 level model Description: Copyright: Copyright
 * (c) 2001 Company: -= MyX =-
 *
 * @author Alexander I. Kharitchev
 * @version 1.0 */
final class FieldBaseBinary extends AbstractField<FieldBaseBinary, Object, BaseMessage> {
	
	private boolean constant;
	
	private String id;
	
	private long max;
	
	private boolean strict;
	
	FieldBaseBinary() {
		
		this.setAttributeIntern("type", "binary");
		this.recalculate();
	}
	
	@Override
	public FieldBaseBinary cloneField() {
		
		return new FieldBaseBinary().setAttributes(this.getAttributes());
	}
	
	@Override
	public BaseMessage dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		
		assert argument != null : "NULL java object";
		if (argument == BaseObject.UNDEFINED && argument == BaseObject.NULL) {
			return Flow.NUL_MESSAGE;
		}
		if (argument instanceof BaseMessage) {
			return (BaseMessage) argument;
		}
		if (argument instanceof TransferBuffer) {
			final String contentType = Convert.MapEntry.toString(Exec.currentProcess(), this.id + "_contenttype", "").trim();
			return Flow.binary("FLD-BINARY", this.id, contentType, (TransferBuffer) argument);
		}
		if (argument instanceof TransferCopier) {
			final String contentType = Convert.MapEntry.toString(Exec.currentProcess(), this.id + "_contenttype", "").trim();
			return Flow.binary("FLD-BINARY", this.id, contentType, (TransferCopier) argument);
		}
		final Object value = argument.baseValue();
		if (value == null) {
			return Flow.NUL_MESSAGE;
		}
		if (value != argument) {
			return this.dataRetrieveJava(value, fieldsetContext);
		}
		assert false : "Should not be here: argumentClass=" + argument.getClass().getName() + ", valueClass=" + value.getClass().getName();
		return Flow.NUL_MESSAGE;
	}
	
	private final BaseMessage dataRetrieveJava(final Object argument, final BaseObject fieldsetContext) {
		
		if (argument == null) {
			return Flow.NUL_MESSAGE;
		}
		if (argument instanceof BaseObject) {
			return this.dataRetrieve((BaseObject) argument, fieldsetContext);
		}
		if (argument instanceof BaseMessage) {
			return (BaseMessage) argument;
		}
		if (argument instanceof TransferBuffer) {
			final String contentType = Convert.MapEntry.toString(Exec.currentProcess(), this.id + "_contenttype", "").trim();
			return Flow.binary("FLD-BINARY", this.id, contentType, (TransferBuffer) argument);
		}
		if (argument instanceof TransferCopier) {
			final String contentType = Convert.MapEntry.toString(Exec.currentProcess(), this.id + "_contenttype", "").trim();
			return Flow.binary("FLD-BINARY", this.id, contentType, (TransferCopier) argument);
		}
		if (argument instanceof byte[]) {
			final String contentType = Convert.MapEntry.toString(Exec.currentProcess(), this.id + "_contenttype", "").trim();
			return Flow.binary("FLD-BINARY", this.id, contentType, Transfer.createBuffer((byte[]) argument));
		}
		if (argument instanceof External) {
			return this.dataRetrieveJava(((External) argument).toBinary(), fieldsetContext);
		}
		if (argument instanceof Value<?>) {
			final Object value = ((Value<?>) argument).baseValue();
			if (value == null) {
				return Flow.NUL_MESSAGE;
			}
			if (value != argument) {
				return this.dataRetrieveJava(value, fieldsetContext);
			}
		}
		if (argument instanceof CharSequence) {
			/** No need to cast to CharSequence to call toString() method; */
			return Flow.character("FLD-BINARY", this.id, argument.toString());
		}
		{
			assert false : "Should not be here: argumentClass=" + argument.getClass().getName();
			return Flow.NUL_MESSAGE;
		}
	}
	
	@Override
	public BaseObject dataStore(final BaseObject argument, final BaseObject fieldsetContext) {
		
		assert argument != null : "NULL java object";
		if (argument == BaseObject.UNDEFINED || argument == BaseObject.NULL || argument == Flow.NUL_MESSAGE) {
			return BaseObject.UNDEFINED;
		}
		if (Report.MODE_DEBUG) {
			Report.devel(
					"RT3/FIELD_BINARY/STORE",
					"id=" + this.getKey() + ", arg_class=" + argument.getClass().getName() + (fieldsetContext == null
						? ""
						: ", ctx_class=" + fieldsetContext.getClass().getName()));
		}
		if (argument instanceof BaseMessage) {
			return argument;
		}
		final Object value = argument.baseValue();
		if (value == null) {
			return BaseObject.NULL;
		}
		// target.put( this.id + "_original_class", value.getClass().getName()
		// );
		if (value instanceof java.io.InputStream) {
			return Flow.binary(
					"FLD-BINARY",
					this.id,
					Convert.MapEntry.toString(fieldsetContext, this.id + "_contenttype", "application/octet-stream"),
					Transfer.createBuffer((java.io.InputStream) value));
		}
		if (value.getClass() == String.class) {
			return Flow.binary("FLD-BINARY", this.id, "text/plain; charset=UTF-8", Transfer.createBuffer(value.toString().getBytes(Engine.CHARSET_UTF8)));
		}
		if (value instanceof TransferBuffer) {
			return Flow.binary("FLD-BINARY", this.id, Convert.MapEntry.toString(fieldsetContext, this.id + "_contenttype", "application/octet-stream"), (TransferBuffer) value);
		}
		if (value instanceof TransferCopier) {
			return Flow.binary("FLD-BINARY", this.id, Convert.MapEntry.toString(fieldsetContext, this.id + "_contenttype", "application/octet-stream"), (TransferCopier) value);
		}
		if (value instanceof byte[]) {
			return Flow.binary(
					"FLD-BINARY",
					this.id,
					Convert.MapEntry.toString(fieldsetContext, this.id + "_contenttype", "application/octet-stream"),
					Transfer.createBuffer((byte[]) value));
		}
		throw new IllegalArgumentException("Cannot store to a binary field an object of unknown type: " + value.getClass().getName());
	}
	
	@Override
	public String dataValidate(final BaseObject source) {
		
		try {
			this.dataValidate(Base.getJava(source, this.id, null));
			return null;
		} catch (final Throwable t) {
			return t.getMessage();
		}
	}
	
	protected void dataValidate(final Object value) {
		
		if (this.constant || value == null) {
			return;
		}
		if (Report.MODE_DEBUG) {
			Report.devel("RT3/FIELD_BLOB/VALIDATE", "value_class=" + value.getClass().getName());
		}
		try {
			if (value instanceof InputStream) {
				final long length = ((InputStream) value).available();
				if (length > this.max) {
					throw new IllegalArgumentException("Object is larger than maximum allowed! Object size equals to " + length + " while limited to " + this.max);
				}
			} else if (value instanceof TransferBuffer) {
				final long length = ((TransferBuffer) value).remaining();
				if (length > this.max) {
					throw new IllegalArgumentException("Object is larger than maximum allowed! Object size equals to " + length + " while limited to " + this.max);
				}
			} else if (value instanceof TransferCopier) {
				final long length = ((TransferCopier) value).length();
				if (length > this.max) {
					throw new IllegalArgumentException("Object is larger than maximum allowed! Object size equals to " + length + " while limited to " + this.max);
				}
			} else if (value instanceof BaseMessage) {
				final BaseMessage request = (BaseMessage) value;
				if (request.isFile()) {
					final File in = request.getFile();
					if (in != null) {
						final long length = in.length();
						if (length > this.max) {
							throw new IllegalArgumentException("Object is larger than maximum allowed! Object size equals to " + length + " while limited to " + this.max);
						}
					}
				} else if (request.isBinary()) {
					final long length = request.toBinary().getBinaryContentLength();
					if (length > this.max) {
						throw new IllegalArgumentException("Object is larger than maximum allowed! Object size equals to " + length + " while limited to " + this.max);
					}
				}
			} else if (value.getClass() == String.class) {
				final long length = ((String) value).length();
				if (length > this.max) {
					throw new IllegalArgumentException("Object is larger than maximum allowed! Object size equals to " + length + " while limited to " + this.max);
				}
			} else {
				throw new IllegalArgumentException("Wrong data type (" + value.getClass().getName() + ")!");
			}
		} catch (final IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public void fillFields(final Set<String> target) {
		
		super.fillFields(target);
		if (this.strict) {
			target.add(this.id + "_size");
			target.add(this.id + "_contenttype");
			target.add(this.id + "_contentname");
		}
	}
	
	@Override
	public ControlFieldset<?> getFieldAttributesFieldset() {
		
		final ControlFieldset<?> fieldset = ControlFieldset.createFieldset();
		fieldset.addField(ControlFieldFactory.createFieldBoolean("constant", MultivariantString.getString("Constant", Collections.singletonMap("ru", "Константа")), false));
		// fieldset
		// .addField(Control.createFieldBoolean("strict", "Strict", false));
		// fieldset.addField(Control.createFieldBoolean("storeextension",
		// "Store extension",
		// false));
		fieldset.addField(ControlFieldFactory.createFieldInteger("max", "Max length", 32768));
		fieldset.addField(ControlFieldFactory.createFieldBinary("default", "Default value", 65536));
		return fieldset;
	}
	
	@Override
	public String getFieldClass() {
		
		return "binary";
	}
	
	@Override
	public String getFieldClassTitle() {
		
		return "Binary data";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		
		return "binary".equals(type);
	}
	
	@Override
	public boolean isConstant() {
		
		return this.constant;
	}
	
	@Override
	protected void recalculate() {
		
		this.id = this.getKey();
		/** no the same as getBoolean - string value 'false', '0' etc should be treated as false! */
		this.constant = Convert.MapEntry.toBoolean(this.getAttributes(), "constant", false);
		/** no the same as getBoolean - string value 'false', '0' etc should be treated as false! */
		this.strict = Convert.MapEntry.toBoolean(this.getAttributes(), "strict", false);
		/** Not same as getInteger, human input allowed, values like 30m are valid */
		this.max = Convert.MapEntry.toLong(this.getAttributes(), "max", 32768);
	}
}
