/*
 * Created on 08.02.2006
 */
package ru.myx.ae2.indexing;

import java.util.Collection;

// !!! не удаляет содержимое SCRIPT и STYLE
/** @author myx */
public class ExtractorHtmlVariant {
	
	private static final int ST_WHITESPACE = 0;
	
	private static final int ST_CHARS = 1;
	
	private static final int ST_COMPOUND_START = 2;
	
	private static final int ST_COMPOUND = 3;
	
	private static final int ST_COMPOUND_CONTINUE = 4;
	
	private static final int ST_CAPI_COMPOUND = 5;
	
	private static final int ST_CHARS_UPPER = 6;
	
	private static final int ST_COMPOUND_UPPER_START = 7;
	
	private static final int ST_COMPOUND_UPPER = 8;
	
	private static final int ST_COMPOUND_UPPER_CONTINUE = 9;
	
	private static final int ST_TAG_START = 10;
	
	private static final int ST_TAG_BODY = 11;
	
	private static final int ST_ENTITY = 12;
	
	/** @param result
	 * @param text
	 * @return collection */
	public static final Collection<String> extractContent(final Collection<String> result, final String text) {
		
		final int length = text.length();
		final StringBuilder token = new StringBuilder();
		final StringBuilder compound = new StringBuilder();
		int state = ExtractorHtmlVariant.ST_WHITESPACE;
		for (int i = 0; i < length; ++i) {
			final char current = text.charAt(i);
			switch (state) {
				case ST_TAG_START : {
					if (current != '/' && !Character.isLetter(current)) {
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					} else {
						state = ExtractorHtmlVariant.ST_TAG_BODY;
					}
				}
					break;
				case ST_TAG_BODY : {
					if (current == '>') {
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				case ST_ENTITY : {
					if (current != '#' && !Character.isLetterOrDigit(current)) {
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				case ST_WHITESPACE : {
					if (current == '-' || current == '_' || current == '/' || current == '.') {
						// ignore
					} else if (current == '<') {
						state = ExtractorHtmlVariant.ST_TAG_START;
					} else if (current == '&') {
						state = ExtractorHtmlVariant.ST_ENTITY;
					} else if (current == '$' || Character.isLetterOrDigit(current)) {
						token.append(Character.toLowerCase(current));
						state = ExtractorHtmlVariant.ST_CHARS_UPPER;
					} else {
						// ignore
					}
				}
					break;
				case ST_CHARS : {
					if (current == '-' || current == '_' || current == '/' || current == '.' || current == '*' || current == '?') {
						result.add(token.toString());
						compound.append(token).append(current);
						token.setLength(0);
						state = ExtractorHtmlVariant.ST_COMPOUND_START;
					} else if (current == '<') {
						result.add(token.toString());
						token.setLength(0);
						state = ExtractorHtmlVariant.ST_TAG_START;
					} else if (current == '&') {
						result.add(token.toString());
						token.setLength(0);
						state = ExtractorHtmlVariant.ST_ENTITY;
					} else if (current == '$' || Character.isDigit(current)) {
						token.append(current);
					} else if (Character.isLetter(current)) {
						if (Character.isUpperCase(current)) {
							result.add(token.toString());
							final char charachter = Character.toLowerCase(current);
							compound.append(token).append(charachter);
							token.setLength(0);
							token.append(charachter);
							state = ExtractorHtmlVariant.ST_CAPI_COMPOUND;
						} else {
							token.append(current);
						}
					} else {
						result.add(token.toString());
						token.setLength(0);
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				case ST_COMPOUND_START : {
					if (current == '?') {
						compound.append(current);
						state = ExtractorHtmlVariant.ST_COMPOUND;
					} else if (current == '<') {
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_TAG_START;
					} else if (current == '&') {
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_ENTITY;
					} else if (current == '$') {
						token.append(current);
						compound.append(current);
						state = ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else if (Character.isDigit(current)) {
						token.append(current);
						compound.append(current);
						state = ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else if (Character.isLetter(current)) {
						final char character = Character.toLowerCase(current);
						token.append(character);
						compound.append(character);
						state = character == current
							? ExtractorHtmlVariant.ST_COMPOUND
							: ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else {
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				case ST_COMPOUND : {
					if (current == '-' || current == '_' || current == '/' || current == '.' || current == '*' || current == '?') {
						compound.append(current);
						if (token.length() > 0) {
							result.add(token.toString());
							token.setLength(0);
						}
						state = ExtractorHtmlVariant.ST_COMPOUND_CONTINUE;
					} else if (current == '<') {
						result.add(token.toString());
						result.add(compound.toString());
						token.setLength(0);
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_TAG_START;
					} else if (current == '&') {
						result.add(token.toString());
						result.add(compound.toString());
						token.setLength(0);
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_ENTITY;
					} else if (current == '$' || Character.isDigit(current)) {
						token.append(current);
						compound.append(current);
					} else if (Character.isLetter(current)) {
						final char character = Character.toLowerCase(current);
						if (current != character) {
							compound.append(character);
							if (token.length() > 0) {
								result.add(token.toString());
								token.setLength(0);
							}
							token.append(character);
						} else {
							token.append(current);
							compound.append(current);
						}
					} else {
						result.add(token.toString());
						result.add(compound.toString());
						token.setLength(0);
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				case ST_COMPOUND_CONTINUE : {
					if (current == '?') {
						compound.append(current);
					} else if (current == '<') {
						result.add(compound.substring(0, compound.length() - 1));
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_TAG_START;
					} else if (current == '&') {
						result.add(compound.substring(0, compound.length() - 1));
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_ENTITY;
					} else if (current == '$') {
						token.append(current);
						compound.append(current);
						state = ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else if (Character.isDigit(current)) {
						token.append(current);
						compound.append(current);
						state = ExtractorHtmlVariant.ST_COMPOUND;
					} else if (Character.isLetter(current)) {
						final char character = Character.toLowerCase(current);
						token.append(character);
						compound.append(character);
						state = current == character
							? ExtractorHtmlVariant.ST_COMPOUND
							: ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else {
						result.add(compound.substring(0, compound.length() - 1));
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				case ST_CAPI_COMPOUND : {
					if (current == '-' || current == '_' || current == '/' || current == '.') {
						if (token.length() > 0) {
							result.add(token.toString());
							token.setLength(0);
						}
						compound.append(current);
						state = ExtractorHtmlVariant.ST_COMPOUND_CONTINUE;
					} else if (current == '<') {
						result.add(token.toString());
						token.setLength(0);
						result.add(compound.toString());
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_TAG_START;
					} else if (current == '&') {
						result.add(token.toString());
						token.setLength(0);
						result.add(compound.toString());
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_ENTITY;
					} else if (current == '$' || Character.isDigit(current)) {
						token.append(current);
						compound.append(current);
					} else if (Character.isLetter(current)) {
						final char character = Character.toLowerCase(current);
						if (current != character) {
							compound.append(character);
							if (token.length() > 1) {
								result.add(token.toString());
								token.setLength(0);
							}
							token.append(character);
						} else {
							token.append(current);
							compound.append(current);
						}
					} else {
						result.add(token.toString());
						token.setLength(0);
						result.add(compound.toString());
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				case ST_CHARS_UPPER : {
					if (current == '-' || current == '_' || current == '/' || current == '.' || current == '*' || current == '?') {
						result.add(token.toString());
						compound.append(token).append(current);
						token.setLength(0);
						state = ExtractorHtmlVariant.ST_COMPOUND_UPPER_START;
					} else if (current == '<') {
						result.add(token.toString());
						token.setLength(0);
						state = ExtractorHtmlVariant.ST_TAG_START;
					} else if (current == '&') {
						result.add(token.toString());
						token.setLength(0);
						state = ExtractorHtmlVariant.ST_ENTITY;
					} else if (current == '$' || Character.isDigit(current)) {
						token.append(current);
					} else if (Character.isLetter(current)) {
						if (Character.isUpperCase(current)) {
							token.append(Character.toLowerCase(current));
						} else {
							token.append(current);
							state = ExtractorHtmlVariant.ST_CHARS;
						}
					} else {
						result.add(token.toString());
						token.setLength(0);
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				case ST_COMPOUND_UPPER_START : {
					if (current == '?') {
						compound.append(current);
						state = ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else if (current == '<') {
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_TAG_START;
					} else if (current == '&') {
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_ENTITY;
					} else if (current == '$' || Character.isDigit(current)) {
						token.append(current);
						compound.append(current);
						state = ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else if (Character.isLetter(current)) {
						final char character = Character.toLowerCase(current);
						token.append(character);
						compound.append(character);
						state = current == character
							? ExtractorHtmlVariant.ST_COMPOUND
							: ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else {
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				case ST_COMPOUND_UPPER : {
					if (current == '-' || current == '_' || current == '/' || current == '.' || current == '*' || current == '?') {
						compound.append(current);
						if (token.length() > 0) {
							result.add(token.toString());
							token.setLength(0);
						}
						state = ExtractorHtmlVariant.ST_COMPOUND_UPPER_CONTINUE;
					} else if (current == '<') {
						result.add(token.toString());
						result.add(compound.toString());
						token.setLength(0);
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_TAG_START;
					} else if (current == '&') {
						result.add(token.toString());
						result.add(compound.toString());
						token.setLength(0);
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_ENTITY;
					} else if (current == '$' || Character.isDigit(current)) {
						token.append(current);
						compound.append(current);
					} else if (Character.isLetter(current)) {
						final char character = Character.toLowerCase(current);
						if (character != current) {
							token.append(character);
							compound.append(character);
						} else {
							token.append(current);
							compound.append(current);
							state = ExtractorHtmlVariant.ST_COMPOUND;
						}
					} else {
						result.add(token.toString());
						result.add(compound.toString());
						token.setLength(0);
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				case ST_COMPOUND_UPPER_CONTINUE : {
					if (current == '?') {
						compound.append(current);
					} else if (current == '<') {
						result.add(compound.substring(0, compound.length() - 1));
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_TAG_START;
					} else if (current == '&') {
						result.add(compound.substring(0, compound.length() - 1));
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_ENTITY;
					} else if (current == '$') {
						token.append(current);
						compound.append(current);
						state = ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else if (Character.isDigit(current)) {
						token.append(current);
						compound.append(current);
						state = ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else if (Character.isLetter(current)) {
						final char character = Character.toLowerCase(current);
						token.append(character);
						compound.append(character);
						state = current == character
							? ExtractorHtmlVariant.ST_COMPOUND
							: ExtractorHtmlVariant.ST_COMPOUND_UPPER;
					} else {
						result.add(compound.substring(0, compound.length() - 1));
						compound.setLength(0);
						state = ExtractorHtmlVariant.ST_WHITESPACE;
					}
				}
					break;
				default :
			}
		}
		if ((state == ExtractorHtmlVariant.ST_CHARS || state == ExtractorHtmlVariant.ST_CHARS_UPPER || state == ExtractorHtmlVariant.ST_COMPOUND
				|| state == ExtractorHtmlVariant.ST_COMPOUND_UPPER) && token.length() > 0) {
			result.add(token.toString());
		}
		if ((state == ExtractorHtmlVariant.ST_COMPOUND || state == ExtractorHtmlVariant.ST_COMPOUND_UPPER || state == ExtractorHtmlVariant.ST_CAPI_COMPOUND)
				&& compound.length() > 0) {
			result.add(compound.toString());
		}
		if ((state == ExtractorHtmlVariant.ST_COMPOUND_CONTINUE || state == ExtractorHtmlVariant.ST_COMPOUND_UPPER_CONTINUE) && compound.length() > 0) {
			result.add(compound.substring(0, compound.length() - 1));
		}
		return result;
	}
}
