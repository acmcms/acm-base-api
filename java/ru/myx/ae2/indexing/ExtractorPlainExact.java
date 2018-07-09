/*
 * Created on 08.02.2006
 */
package ru.myx.ae2.indexing;

import java.util.Collection;

/** @author myx */
public class ExtractorPlainExact {

	private static final int ST_WHITESPACE = 0;

	private static final int ST_CHARS = 1;

	private static final int ST_COMPOUND_START = 2;

	private static final int ST_COMPOUND = 3;

	/** @param result
	 * @param text
	 * @return collection */
	public static final Collection<String> extractContent(final Collection<String> result, final String text) {

		final int length = text.length();
		final StringBuilder token = new StringBuilder();
		int state = ExtractorPlainExact.ST_WHITESPACE;
		for (int i = 0; i < length; ++i) {
			final char current = text.charAt(i);
			switch (state) {
				case ST_WHITESPACE : {
					if (current == '-' || current == '_' || current == '/' || current == '.') {
						// ignore
					} else if (current == '$' || Character.isLetterOrDigit(current)) {
						token.append(Character.toLowerCase(current));
						state = ExtractorPlainExact.ST_CHARS;
					} else {
						// ignore
					}
				}
					break;
				case ST_CHARS : {
					if (current == '-' || current == '_' || current == '/' || current == '.' || current == '*' || current == '?') {
						token.append(current);
						state = ExtractorPlainExact.ST_COMPOUND_START;
					} else if (current == '$' || Character.isLetterOrDigit(current)) {
						token.append(Character.toLowerCase(current));
					} else {
						result.add(token.toString());
						token.setLength(0);
						state = ExtractorPlainExact.ST_WHITESPACE;
					}
				}
					break;
				case ST_COMPOUND_START : {
					if (current == '?') {
						token.append(current);
					} else if (current == '$' || Character.isLetterOrDigit(current)) {
						final char character = Character.toLowerCase(current);
						token.append(character);
						state = ExtractorPlainExact.ST_CHARS;
					} else {
						if (token.length() > 0) {
							result.add(token.substring(0, token.length() - 1));
							token.setLength(0);
						}
						state = ExtractorPlainExact.ST_WHITESPACE;
					}
				}
					break;
				default :
			}
		}
		if ((state == ExtractorPlainExact.ST_CHARS || state == ExtractorPlainExact.ST_COMPOUND) && token.length() > 0) {
			result.add(token.toString());
		}
		if (state == ExtractorPlainExact.ST_COMPOUND_START && token.length() > 0) {
			result.add(token.substring(0, token.length() - 1));
		}
		return result;
	}
}
