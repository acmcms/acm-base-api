/*
 * Created on 30.07.2003
 */
package ru.myx.ae2.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ru.myx.ae3.help.Text;
import ru.myx.query.OneCondition;
import ru.myx.query.OneConditionSimple;
import ru.myx.query.Syntax;

/**
 * @author myx
 */
public final class IndexingParser {
	private static final Comparator<Condition>	COLLECTION_SIZE	= new CollectionSizeComparator();
	
	private static final String					T_NOT_SWITCH	= "~";
	
	/**
	 * Преобразовывает иерархическую структуру данных возвращаемую методом
	 * parse() в линейную структуру содержащую наборы слов и их словоформ
	 * объедененных условием "и" расположенные в наборе объедененном условием
	 * "или". наборы второго уровня представленны массивом из двух объектов
	 * java.util.Set - первый это условия получения основного множества, второй
	 * - условия для получения множества, которое необходимо вычесть из
	 * основного.
	 * 
	 * @param parsed
	 * @param level
	 * @return 2D conditions
	 */
	public static final Condition[][] normalize(final List<Object> parsed, final int level) {
		if (level >= 512) {
			throw new IllegalArgumentException( "Too hard search expression!" );
		}
		final int size = parsed.size();
		{
			int start = 0;
			boolean prevNot = false;
			for (int i = 0; i < size; ++i) {
				final Object current = parsed.get( i );
				if (current == Syntax.T_OR) {
					start = i + 1;
				} else if (current instanceof List<?>) {
					final List<?> list = (List<?>) current;
					int end = size;
					for (int j = i + 1; j < size; j++) {
						if (parsed.get( j ) == Syntax.T_OR) {
							end = j;
							break;
						}
					}
					final List<Object> next = new ArrayList<>();
					if (start > 0) {
						next.addAll( parsed.subList( 0, start ) );
					}
					if (prevNot) {
						parsed.set( i - 1, IndexingParser.T_NOT_SWITCH );
					}
					{
						List<List<Object>> parts = null;
						List<Object> part = new ArrayList<>();
						for (int j = 0; j < list.size(); j++) {
							if (list.get( j ) == Syntax.T_OR) {
								if (parts == null) {
									parts = new ArrayList<>();
								}
								parts.add( part );
								part = new ArrayList<>();
							} else {
								part.add( list.get( j ) );
							}
						}
						if (part.size() > 0) {
							if (parts == null) {
								parts = Collections.singletonList( part );
							} else {
								parts.add( part );
							}
						}
						if (parts == null) {
							continue;
						}
						for (int j = 0; j < parts.size(); j++) {
							if (j > 0) {
								next.add( Syntax.T_OR );
							}
							if (i > start) {
								next.addAll( parsed.subList( start, i ) );
							}
							next.addAll( parts.get( j ) );
							if (i < end) {
								next.addAll( parsed.subList( i + 1, end ) );
							}
							if (prevNot) {
								next.add( IndexingParser.T_NOT_SWITCH );
							}
						}
					}
					if (end < size) {
						next.addAll( parsed.subList( end, size ) );
					}
					return IndexingParser.normalize( next, level + 1 );
				} else {
					prevNot = current == Syntax.T_NOT;
				}
			}
		}
		{
			boolean prevNot = false;
			boolean not = false;
			List<Condition> partsMain = null;
			List<Condition> partsSub = null;
			Condition partMain = new Condition();
			Condition partSub = new Condition();
			for (int j = 0; j < size; j++) {
				final Object object = parsed.get( j );
				if (object == null) {
					throw new IllegalArgumentException( "Invalid token(" + j + "), null token: " + parsed );
				}
				if (object == Syntax.T_OR) {
					if (!partMain.isEmpty()) {
						if (partsMain == null) {
							partsMain = new ArrayList<>();
						}
						partsMain.add( partMain );
						partMain = new Condition();
					}
					if (!partSub.isEmpty()) {
						if (partsSub == null) {
							partsSub = new ArrayList<>();
						}
						partsSub.add( partSub );
						partSub = new Condition();
					}
				} else {
					if (object == Syntax.T_AND) {
						// ignore
					} else if (object == IndexingParser.T_NOT_SWITCH) {
						not = !not;
					} else if (object != Syntax.T_NOT) {
						if (!(object instanceof OneCondition)) {
							throw new IllegalArgumentException( "Invalid token("
									+ j
									+ "), wrong class ("
									+ object.getClass().getName()
									+ "): "
									+ parsed );
						}
						final OneCondition current = (OneCondition) object;
						if (prevNot ^ not) {
							partSub.add( current );
							prevNot = false;
						} else {
							partMain.add( current );
						}
					} else {
						prevNot = true;
					}
				}
			}
			if (!partMain.isEmpty()) {
				if (partsMain == null) {
					partsMain = Collections.singletonList( partMain );
				} else {
					partsMain.add( partMain );
				}
			}
			if (!partSub.isEmpty()) {
				if (partsSub == null) {
					partsSub = Collections.singletonList( partSub );
				} else {
					partsSub.add( partSub );
				}
			}
			return new Condition[][] { partsMain != null
					? partsMain.toArray( new Condition[partsMain.size()] )
					: null, partsSub != null
					? partsSub.toArray( new Condition[partsSub.size()] )
					: null, };
		}
	}
	
	/**
	 * Фильтрует массив наборов условий на предмет удаления больших наборов
	 * условий целиком содержащих меньшие наборы.
	 * 
	 * @param conditions
	 * @return condition array
	 */
	public static final OneCondition[][] optimize(final Condition[] conditions) {
		if (conditions == null) {
			return null;
		}
		final int length = conditions.length;
		if (length == 0) {
			return null;
		}
		if (length == 1) {
			final OneCondition[][] result = new OneCondition[1][];
			final Condition current = conditions[0];
			if (current != null) {
				result[0] = current.toArray( new OneCondition[current.size()] );
			}
			return result;
		}
		Arrays.sort( conditions, IndexingParser.COLLECTION_SIZE );
		int size = conditions.length;
		for (int i = conditions.length - 1; i > 0; --i) {
			for (int j = 0; j < i; j++) {
				final Condition bigger = conditions[i];
				final Condition smaller = conditions[j];
				if (bigger.containsAll( smaller )) {
					conditions[i] = null;
					size--;
					break;
				}
			}
		}
		Arrays.sort( conditions, Condition.COMPARATOR_CONDITIONS );
		final OneCondition[][] result = new OneCondition[size][];
		for (int i = conditions.length - 1, j = size; i >= 0; --i) {
			final Condition current = conditions[i];
			if (current != null) {
				result[--j] = current.toArray( new OneCondition[current.size()] );
			}
		}
		return result;
	}
	
	/**
	 * Разбирает строку как отдельные слова, морфо-формы слов, объекты
	 * синтаксиса и группы всего вышеперечисленного.. Поддерживает OR - символ
	 * '|' и '/' Поддерживает AND - оператор по умолчанию, может быть явно
	 * указан символом '&' Поддерживает ВКЛ/ОТКЛ морфо-преобразований - символом
	 * '"' Группы указываются символами '(' и ')' и возвращаются объектами типа
	 * java.util.List, поддерживается вложенность, результат представлен
	 * коренной группой.
	 * 
	 * @param text
	 * @param minimum
	 * @param stemmer
	 * @param dictionary
	 * @return list
	 */
	public static final List<Object> parse(
			final List<?> text,
			final int minimum,
			final IndexingStemmer stemmer,
			final IndexingDictionary dictionary) {
		final boolean anyForms = text.size() < 3;
		final List<Object> parsed = new ArrayList<>();
		List<String> stemmedWords = null;
		for (final Iterator<?> i = text.iterator(); i.hasNext();) {
			final Object current = i.next();
			if (current instanceof List<?>) {
				final List<?> part = IndexingParser.parse( (List<?>) current, 0, stemmer, dictionary );
				if (part != null && !part.isEmpty()) {
					parsed.add( part );
				} else {
					if (!parsed.isEmpty()) {
						parsed.remove( parsed.size() - 1 );
					} else if (i.hasNext()) {
						i.next();
					}
				}
			} else if (current instanceof OneCondition) {
				final OneCondition condition = (OneCondition) current;
				// final String operand = condition.getOperand();
				final boolean exact = condition.isExact();
				final String value = condition.getValue();
				if (Math.max( value.indexOf( '?' ), value.indexOf( '*' ) ) > -1 && dictionary != null) {
					final int[] codesExact = dictionary.getPatternCodes( value + ':' + condition.getField(),
							true,
							exact );
					final int[] codesInexact = exact
							? null
							: dictionary.getPatternCodes( value + ':' + condition.getField(), false, false );
					if ((codesExact == null || codesExact.length == 0)
							&& (codesInexact == null || codesInexact.length == 0)) {
						if (!parsed.isEmpty()) {
							parsed.remove( parsed.size() - 1 );
						} else if (i.hasNext()) {
							i.next();
						}
					} else if (codesExact == null && codesInexact != null && codesInexact.length == 1) {
						parsed.add( new OneConditionSimple( false,
								condition.getField(),
								condition.getOperator(),
								Integer.toString( codesInexact[0], 36 ),
								value ) );
					} else if (codesInexact == null && codesExact != null && codesExact.length == 1) {
						parsed.add( new OneConditionSimple( true,
								condition.getField(),
								condition.getOperator(),
								Integer.toString( codesExact[0], 36 ),
								value ) );
					} else {
						final List<Object> variants = new ArrayList<>( (codesExact == null
								? 0
								: codesExact.length) + (codesInexact == null
								? 0
								: codesInexact.length) );
						if (codesExact != null) {
							for (int j = codesExact.length - 1, c = 0; j >= 0; j--, c++) {
								if (c > 0) {
									variants.add( Syntax.T_OR );
								}
								variants.add( new OneConditionSimple( true, condition.getField(), condition
										.getOperator(), Integer.toString( codesExact[j], 36 ), value ) );
							}
						}
						if (codesInexact != null) {
							for (int j = codesInexact.length - 1, c = 0; j >= 0; j--, c++) {
								if (c > 0) {
									variants.add( Syntax.T_OR );
								}
								variants.add( new OneConditionSimple( false, condition.getField(), condition
										.getOperator(), Integer.toString( codesInexact[j], 36 ), value ) );
							}
						}
						parsed.add( variants );
					}
				} else {
					if (dictionary == null) {
						parsed.add( condition );
					} else if (anyForms || exact) {
						final int mainCode;
						{
							final String checker = Text.limitString( value, 50 )
									+ ':'
									+ Text.limitString( condition.getField(), 29 );
							if (exact) {
								mainCode = dictionary.getWordCode( checker, true, true );
							} else {
								final int check = dictionary.getWordCode( checker, false, false );
								mainCode = check <= 0
										? dictionary.getWordCode( checker, true, true )
										: check;
							}
						}
						if (mainCode <= 0) {
							if (!parsed.isEmpty()) {
								parsed.remove( parsed.size() - 1 );
							} else if (i.hasNext()) {
								i.next();
							}
						} else {
							if (exact || value.length() < 3) {
								// replace : to operand to make equals
								// condition.
								parsed.add( new OneConditionSimple( exact, condition.getField(), condition
										.getOperator(), Integer.toString( mainCode, 36 ), value ) );
							} else {
								if (stemmedWords == null) {
									stemmedWords = new ArrayList<>();
								}
								stemmer.fillForms( value, stemmedWords );
								final int stemmedCount = stemmedWords.size();
								if (stemmedCount == 0) {
									parsed.add( new OneConditionSimple( exact, condition.getField(), condition
											.getOperator(), Integer.toString( mainCode, 36 ), value ) );
								} else {
									final List<Object> variants = new ArrayList<>( stemmedCount + 1 );
									variants.add( new OneConditionSimple( exact, condition.getField(), condition
											.getOperator(), Integer.toString( mainCode, 36 ), value ) );
									for (int j = stemmedCount - 1; j >= 0; j--) {
										final String stemmed = stemmedWords.get( j );
										final int stemmedCode = dictionary.getWordCode( Text.limitString( stemmed, 50 )
												+ ':'
												+ Text.limitString( condition.getField(), 29 ), false, false );
										if (stemmedCode != 0) {
											variants.add( Syntax.T_OR );
											variants.add( new OneConditionSimple( exact,
													condition.getField(),
													condition.getOperator(),
													Integer.toString( stemmedCode, 36 ),
													stemmed ) );
										}
									}
									stemmedWords.clear();
									parsed.add( variants.size() == 1
											? variants.get( 0 )
											: variants );
								}
							}
						}
					} else {
						if (value.length() < 3) {
							if (!parsed.isEmpty()) {
								parsed.remove( parsed.size() - 1 );
							} else if (i.hasNext()) {
								i.next();
							}
						} else {
							if (stemmedWords == null) {
								stemmedWords = new ArrayList<>();
							}
							stemmer.fillForms( value, stemmedWords );
							final int stemmedCount = stemmedWords.size();
							if (stemmedCount == 0) {
								final int mainCode;
								{
									final String checker = Text.limitString( value, 50 )
											+ ':'
											+ Text.limitString( condition.getField(), 29 );
									if (exact) {
										mainCode = dictionary.getWordCode( checker, true, false );
									} else {
										final int check = dictionary.getWordCode( checker, false, false );
										mainCode = check == 0
												? dictionary.getWordCode( checker, true, false )
												: check;
									}
								}
								if (mainCode == 0) {
									if (!parsed.isEmpty()) {
										parsed.remove( parsed.size() - 1 );
									} else if (i.hasNext()) {
										i.next();
									}
								} else {
									parsed.add( new OneConditionSimple( exact, condition.getField(), condition
											.getOperator(), Integer.toString( mainCode, 36 ), value ) );
								}
							} else {
								final List<Object> variants = new ArrayList<>( stemmedCount );
								for (int j = stemmedCount - 1; j >= 0; j--) {
									final String stemmed = stemmedWords.get( j );
									final int stemmedCode = dictionary.getWordCode( Text.limitString( stemmed, 50 )
											+ ':'
											+ Text.limitString( condition.getField(), 29 ), false, false );
									if (stemmedCode != 0) {
										if (!variants.isEmpty()) {
											variants.add( Syntax.T_OR );
										}
										variants.add( new OneConditionSimple( exact, condition.getField(), condition
												.getOperator(), Integer.toString( stemmedCode, 36 ), stemmed ) );
									}
								}
								stemmedWords.clear();
								if (variants.isEmpty()) {
									if (!parsed.isEmpty()) {
										parsed.remove( parsed.size() - 1 );
									} else if (i.hasNext()) {
										i.next();
									}
								} else {
									parsed.add( variants.size() == 1
											? variants.get( 0 )
											: variants );
								}
							}
						}
					}
				}
			} else {
				parsed.add( current );
			}
		}
		if (parsed.size() < minimum) {
			return null;
		}
		return parsed;
	}
	
	private IndexingParser() {
		// ignore
	}
}
