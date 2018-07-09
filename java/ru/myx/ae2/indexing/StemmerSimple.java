/*
 * Created on 08.02.2006
 */
package ru.myx.ae2.indexing;

import java.util.List;

/**
 * @author myx
 * 
 */
class StemmerSimple {
	static final String transform(final String original) {
		String word = original;
		{
			for (;;) {
				boolean changed = false;
				if (word.endsWith( "ая" )
						|| word.endsWith( "ия" )
						|| word.endsWith( "ие" )
						|| word.endsWith( "ые" )
						|| word.endsWith( "ый" )
						|| word.endsWith( "ои" )
						|| word.endsWith( "ов" )
						|| word.endsWith( "ок" )
						|| word.endsWith( "ам" )
						|| word.endsWith( "ся" )
						|| word.endsWith( "ое" )) {
					word = word.substring( 0, word.length() - 2 );
					changed = true;
				}
				if (word.endsWith( "мом" ) || word.endsWith( "ыми" ) || word.endsWith( "ого" )) {
					word = word.substring( 0, word.length() - 3 );
					changed = true;
				}
				if (word.startsWith( "при" )) {
					word = word.substring( 3 );
					changed = true;
				}
				if (word.startsWith( "пере" ) || word.startsWith( "теле" )) {
					word = word.substring( 4 );
					changed = true;
				}
				if (word.endsWith( "у" )
						|| word.endsWith( "а" )
						|| word.endsWith( "о" )
						|| word.endsWith( "и" )
						|| word.endsWith( "е" )
						|| word.endsWith( "ы" )
						|| word.endsWith( "s" )) {
					word = word.substring( 0, word.length() - 1 );
					changed = true;
				}
				if (!changed) {
					break;
				}
			}
		}
		{
			final char[] source = word.toCharArray();
			final char[] chars = new char[source.length * 2];
			char prev = 0;
			int t = 0;
			for (int l = source.length - 1, s = 0; l >= 0; l--) {
				final char c = source[s++];
				final char r;
				switch (c) {
				case 'щ':
				case 'ж':
					r = 'ш';
					break;
				case 'ф':
					r = 'в';
					break;
				case 'г':
					r = 'к';
					break;
				case 'д':
					r = 'т';
					break;
				case 'ю':
					r = 'у';
					break;
				case 'й':
				case 'ы':
				case 'ь':
					r = 'и';
					break;
				case 'h':
					if (prev == 'k' || prev == 'c' || prev == 's') {
						--t;
						r = 'h';
					} else {
						r = c;
					}
					break;
				case 'а':
				case 'à':
				case 'á':
				case 'â':
				case 'ã':
				case 'ä':
				case 'å':
				case 'æ':
				case 'ā':
				case 'ă':
				case 'ą':
				case 'о':
				case 'o':
				case 'ò':
				case 'ó':
				case 'ô':
				case 'õ':
				case 'ö':
				case 'ō':
				case 'ŏ':
				case 'ő':
				case 'я':
					r = 'a';
					break;
				case 'с':
				case 'ç':
				case 'ć':
				case 'ĉ':
				case 'ċ':
				case 'č':
				case 'з':
					r = 'c';
					break;
				case 'ё':
				case 'е':
				case 'è':
				case 'é':
				case 'ê':
				case 'ë':
				case 'ē':
				case 'ĕ':
				case 'ė':
				case 'ę':
				case 'ě':
				case 'i':
				case 'j':
				case 'y':
				case 'э':
					r = 'e';
					break;
				case 'ś':
				case 'ŝ':
				case 'ş':
				case 'š':
					r = 's';
					break;
				default:
					r = c;
				}
				if (prev != r) {
					prev = r;
					chars[t] = r;
					t++;
				}
			}
			word = new String( chars, 0, t );
		}
		return word;
	}
	
	static final List<String> transformContents(final List<String> target, final List<String> contents) {
		for (final String current : contents) {
			target.add( StemmerSimple.transform( current ) );
		}
		return target;
	}
}
