/*
 * Created on 07.07.2003 To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.ae1.control;

import java.util.Iterator;
import java.util.Map;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseAbstract;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseObjectNoOwnProperties;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.base.BasePrimitiveString;
import ru.myx.ae3.base.BaseString;
import ru.myx.ae3.base.ToPrimitiveHint;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ExecStateCode;
import ru.myx.ae3.exec.ResultHandler;
import ru.myx.ae3.know.Language;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MultivariantString extends BaseAbstract implements BaseString<CharSequence>, BaseObjectNoOwnProperties {
	
	
	/**
	 * @param def
	 * @param variants
	 * @return object
	 */
	public static final BaseString<?> getString(final String def, final BaseObject variants) {
		
		
		if (variants == null || variants.baseIsPrimitive()) {
			return Base.forString(def);
		}
		final int maxIndex = Language.ordinalMax();
		final BasePrimitiveString[] notations = new BasePrimitiveString[maxIndex];
		for (final Iterator<String> iterator = Base.keys(variants); iterator.hasNext();) {
			final String key = iterator.next();
			final Language language = Language.getLanguage(key);
			if (language != null) {
				notations[language.ordinal()] = variants.baseGet(key, BaseObject.UNDEFINED).baseToString();
			}
		}
		return new MultivariantString(Base.forString(def), notations);
	}
	
	/**
	 * @param def
	 * @param variants
	 * @return object
	 */
	public static final BaseString<?> getString(final String def, final BaseArray variants) {
		
		
		if (variants == null || !Base.hasKeys(variants)) {
			return Base.forString(def);
		}
		final int maxIndex = Language.ordinalMax();
		final BasePrimitiveString[] notations = new BasePrimitiveString[maxIndex];
		for (final Iterator<String> iterator = Base.keys(variants); iterator.hasNext();) {
			final String key = iterator.next();
			final Language language = Language.getLanguage(key);
			if (language != null) {
				notations[language.ordinal()] = variants.baseGet(key, BaseObject.UNDEFINED).baseToString();
			}
		}
		return new MultivariantString(Base.forString(def), notations);
	}
	
	/**
	 * @param def
	 * @param variants
	 * @return object
	 */
	public static final BaseString<?> getString(final String def, final Map<String, String> variants) {
		
		
		if (variants == null || variants.size() == 0) {
			return Base.forString(def);
		}
		final int maxIndex = Language.ordinalMax();
		final BasePrimitiveString[] notations = new BasePrimitiveString[maxIndex];
		for (final Map.Entry<String, String> current : variants.entrySet()) {
			final Language language = Language.getLanguage(current.getKey());
			if (language != null) {
				notations[language.ordinal()] = Base.forString(current.getValue());
			}
		}
		return new MultivariantString(Base.forString(def), notations);
	}
	
	private final BasePrimitiveString def;
	
	private final BasePrimitiveString[] notations;
	
	private MultivariantString(final BasePrimitiveString def, final BasePrimitiveString[] notations) {
		
		this.def = def;
		this.notations = notations;
	}
	
	@Override
	public String baseClass() {
		
		
		return this.def.baseClass();
	}
	
	@Override
	public BaseObject baseGet(final int index, final BaseObject defaultValue) {
		
		
		return this.baseToString().baseGet(index, defaultValue);
	}
	
	@Override
	public ExecStateCode vmPropertyRead(final ExecProcess ctx, final int index, final BaseObject originalIfKnown, final BaseObject defaultValue, final ResultHandler store) {
		
		
		return this.baseToString().vmPropertyRead(ctx, index, originalIfKnown, defaultValue, store);
	}
	
	@Override
	public BasePrimitive<?> baseToPrimitive(ToPrimitiveHint hint) {
		
		
		return this.baseToString();
	}
	
	@Override
	public BasePrimitiveString baseToString() {
		
		
		final Language language = Context.getLanguage(Exec.currentProcess());
		if (language != null) {
			final int index = language.ordinal();
			if (index < this.notations.length) {
				final BasePrimitiveString result = this.notations[index];
				if (result != null) {
					return result;
				}
			}
		}
		return this.def;
	}
	
	@Override
	public final String baseValue() {
		
		
		final Language language = Context.getLanguage(Exec.currentProcess());
		if (language != null) {
			final int index = language.ordinal();
			if (index < this.notations.length) {
				final BasePrimitiveString result = this.notations[index];
				if (result != null) {
					return result.baseValue();
				}
			}
		}
		return this.def.baseValue();
	}
	
	@Override
	public char charAt(final int index) {
		
		
		return this.toString().charAt(index);
	}
	
	@Override
	public boolean equals(final Object obj) {
		
		
		return obj == this || obj instanceof MultivariantString && this.def.equals(((MultivariantString) obj).def);
	}
	
	@Override
	public String get(final int i) {
		
		
		return this.baseToString().get(i);
	}
	
	@Override
	public int hashCode() {
		
		
		return this.def.hashCode();
	}
	
	@Override
	public boolean isEmpty() {
		
		
		return this.baseToString().isEmpty();
	}
	
	@Override
	public int length() {
		
		
		return this.toString().length();
	}
	
	@Override
	public CharSequence subSequence(final int start, final int end) {
		
		
		return this.toString().subSequence(start, end);
	}
	
	@Override
	public String toString() {
		
		
		final Language language = Context.getLanguage(Exec.currentProcess());
		if (language != null) {
			final int index = language.ordinal();
			if (index < this.notations.length) {
				final BasePrimitiveString result = this.notations[index];
				if (result != null) {
					return result.baseValue();
				}
			}
		}
		return this.def.baseValue();
	}
	
}
