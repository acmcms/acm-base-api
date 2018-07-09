/**
 *
 */
package ru.myx.sapi.create_sapi;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;
import ru.myx.ae3.exec.ExecProcess;

/** @author myx
 *
 *         D - decimal digit<br>
 *         H - hexadecimal digit - upper case<br>
 *         h - hexadecimal digit - lower case<br>
 *         Z - 36-base digit - upper case<br>
 *         z - 36-base digit - lower case<br>
 *         \ - next character should be copied<br>
 *         all other characters just copied */
public final class Function_formattedRandom extends BaseFunctionAbstract implements ExecCallableBoth.JavaStringJ1 {
	
	@Override
	public String callSJ1(final BaseObject instance, final BaseObject argument) {
		
		final String format = argument.baseToJavaString();
		if (format == null) {
			return null;
		}
		final StringBuilder result = new StringBuilder();
		boolean copyNext = false;
		for (int i = 0, left = 0, value = 0; i < format.length(); ++i) {
			final char c = format.charAt(i);
			if (copyNext) {
				result.append(c);
				copyNext = false;
				continue;
			}
			switch (c) {
				case 'D' :
					if (left < 10) {
						value = Engine.createRandom();
						left = Integer.MAX_VALUE;
					}
					result.append((value & 0x7FFFFFFF) % 10);
					value /= 10;
					left /= 10;
					continue;
				case 'H' :
				case 'h' :
					if (left < 16) {
						value = Engine.createRandom();
						left = Integer.MAX_VALUE;
					}
					result.append(c == 'H'
						? Integer.toHexString((value & 0x7FFFFFFF) % 16).toUpperCase()
						: Integer.toHexString((value & 0x7FFFFFFF) % 16).toLowerCase());
					value /= 16;
					left /= 16;
					continue;
				case 'Z' :
				case 'z' :
					if (left < 36) {
						value = Engine.createRandom();
						left = Integer.MAX_VALUE;
					}
					result.append(c == 'Z'
						? Integer.toString((value & 0x7FFFFFFF) % 36, 36).toUpperCase()
						: Integer.toString((value & 0x7FFFFFFF) % 36, 36).toLowerCase());
					value /= 36;
					left /= 36;
					continue;
				case '\\' :
					copyNext = true;
					continue;
				default :
					result.append(c);
			}
		}
		return result.toString();
	}
	
	@Override
	public Class<? extends String> execResultClassJava() {
		
		return String.class;
	}

	@Override
	public BaseObject execScope() {
		
		/** executes in real current scope */
		return ExecProcess.GLOBAL;
	}
}
