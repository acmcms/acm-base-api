/*
 * Created on 30.07.2003
 */
package ru.myx.ae2.indexing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import ru.myx.ae1.storage.ModuleInterface;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Create;
import ru.myx.ae3.help.Text;
import ru.myx.ae3.indexing.ExtractorPlainVariant;
import ru.myx.ae3.reflect.Reflect;
import ru.myx.ae3.report.Report;
import ru.myx.util.HashMapPrimitiveInt;

/**
 * @author myx
 */
public final class Indexing {
	private final IndexingStemmer		stemmer;
	
	private final IndexingDictionary	dictionary;
	
	private final String				tnIndices;
	
	/**
	 * @param stemmer
	 * @param dictionary
	 * @param tnIndices
	 */
	public Indexing(final IndexingStemmer stemmer, final IndexingDictionary dictionary, final String tnIndices) {
		this.stemmer = stemmer == null
				? IndexingStemmer.DUMMY_STEMMER
				: stemmer;
		this.dictionary = dictionary == null
				? IndexingDictionary.DICT_HASHCODE
				: dictionary;
		this.tnIndices = tnIndices;
	}
	
	private final void addText(
			final HashMapPrimitiveInt<AtomicInteger> wordsExact,
			final HashMapPrimitiveInt<AtomicInteger> words,
			final String key,
			final String text,
			final int value,
			final int min,
			final Object attachment) {
		if (text == null || text.length() == 0) {
			return;
		}
		List<String> stemmedWords = null;
		final List<String> content = new ArrayList<>();
		if (words != null) {
			if (text.charAt( 0 ) == '#') {
				ExtractorPlainVariant.extractContent( content, text );
			} else {
				ExtractorHtmlVariant.extractContent( content, text );
			}
		} else {
			ExtractorPlainExact.extractContent( content, text );
		}
		final Map<String, AtomicInteger> weights = Create.treeMap();
		WeighterContrastStemmed.analyzeContent( weights, content );
		for (final Map.Entry<String, AtomicInteger> current : weights.entrySet()) {
			final String word = current.getKey();
			if (word.length() < min) {
				continue;
			}
			final int mainCode = this.dictionary.storeWordCode( Text.limitString( word, 50 )
					+ ':'
					+ Text.limitString( key, 29 ),
					true,
					attachment );
			if (mainCode != 0) {
				{
					final AtomicInteger counter = wordsExact.get( mainCode );
					if (counter == null) {
						wordsExact.put( mainCode, new AtomicInteger( value * 3 * current.getValue().intValue() ) );
					} else {
						counter.addAndGet( value * 3 * current.getValue().intValue() );
					}
				}
				if (words != null) {
					if (stemmedWords == null) {
						stemmedWords = new ArrayList<>();
					}
					this.stemmer.fillForms( word, stemmedWords );
					if (!stemmedWords.isEmpty()) {
						for (final String wrd : stemmedWords) {
							if (wrd.length() == 0) {
								continue;
							}
							final int stemmedCode = this.dictionary.storeWordCode( Text.limitString( wrd, 50 )
									+ ':'
									+ Text.limitString( key, 29 ), false, attachment );
							if (stemmedCode != 0) {
								final AtomicInteger counter = words.get( stemmedCode );
								if (counter == null) {
									words.put( stemmedCode, new AtomicInteger( value * current.getValue().intValue() ) );
								} else {
									counter.addAndGet( value * current.getValue().intValue() );
								}
							}
						}
						stemmedWords.clear();
					}
				}
			}
		}
	}
	
	private final void addTextCheck(
			final HashMapPrimitiveInt<AtomicInteger> wordsExact,
			final HashMapPrimitiveInt<AtomicInteger> words,
			final String key,
			final Object element,
			final int weight,
			final int min,
			final boolean prepare,
			final Object attachment) {
		if (element == null) {
			return;
		}
		final Object value;
		if (element instanceof Value<?>) {
			value = ((Value<?>) element).baseValue();
			if (value == null) {
				return;
			}
		} else {
			value = element;
		}
		if (value.getClass() == String.class
				|| value instanceof CharSequence
				|| value instanceof Number
				|| value instanceof Boolean) {
			this.addText( wordsExact, words, key, String.valueOf( value ), weight, min, attachment );
		} else if (value instanceof Collection<?>) {
			final Collection<?> valueCollection = (Collection<?>) value;
			if (!valueCollection.isEmpty()) {
				for (final Object element2 : valueCollection) {
					if (element2 == null) {
						continue;
					}
					this.addTextCheck( wordsExact, words, key, element2, weight, min, prepare, attachment );
				}
			}
		} else if (value instanceof Object[]) {
			final Object[] valueArray = (Object[]) value;
			if (valueArray.length > 0) {
				for (int iterator = valueArray.length - 1; iterator >= 0; iterator--) {
					final Object element2 = valueArray[iterator];
					if (element2 == null) {
						continue;
					}
					this.addTextCheck( wordsExact, words, key, element2, weight, min, prepare, attachment );
				}
			}
		}
	}
	
	private final int doCreateIndex(
			final Connection conn,
			final boolean fullText,
			final Map<String, Object> fields,
			final Set<String> systemKeys,
			final HashMapPrimitiveInt<AtomicInteger> map) {
		final StringBuilder allFieldNames = new StringBuilder();
		final StringBuilder keywords = new StringBuilder();
		if (fullText) {
			keywords.append( Convert.MapEntry.toString( fields, "KEYWORDS", "" ) );
		}
		final int words = 0;
		final Map<String, Object> allKeys = new TreeMap<>();
		if (systemKeys != null) {
			for (final String current : systemKeys) {
				allKeys.put( current, current );
			}
		}
		for (final String key : fields.keySet()) {
			if (key.length() == 0 || key.charAt( 0 ) == '$') {
				continue;
			}
			final String storeKey = Text.limitString( key, 29 );
			final Object names = allKeys.get( storeKey );
			if (names == null) {
				allKeys.put( storeKey, key );
			} else if (names.getClass() == String.class) {
				final Set<String> newNames = new MultipleSet();
				newNames.add( (String) names );
				newNames.add( key );
				allKeys.put( storeKey, newNames );
			} else if (names instanceof MultipleSet) {
				((MultipleSet) names).add( key );
			}
		}
		for (final Map.Entry<String, Object> current : allKeys.entrySet()) {
			final String storeKey = current.getKey();
			final Object kindaKeys = current.getValue();
			final Collection<String> keys;
			if (kindaKeys instanceof MultipleSet) {
				keys = (MultipleSet) kindaKeys;
			} else {
				keys = Collections.singleton( (String) kindaKeys );
			}
			for (final String key : keys) {
				if (key.length() == 0) {
					continue;
				}
				final Object value;
				{
					final Object valueReal = fields.get( key );
					if (valueReal == null) {
						continue;
					}
					if (valueReal instanceof Value<?>) {
						value = ((Value<?>) valueReal).baseValue();
					} else {
						value = valueReal;
					}
				}
				if (value == null) {
					continue;
				}
				if (value.getClass() == String.class || value instanceof CharSequence) {
					final String valueString = value.toString().trim();
					if (valueString.length() > 0) {
						if (key.charAt( 0 ) != '$') {
							allFieldNames.append( ' ' ).append( key );
						}
						if (!fullText) {
							keywords.append( ' ' ).append( value );
						}
						this.addText( map, null, storeKey, valueString, 1, 1, conn );
					}
				} else if (value instanceof Number || value instanceof Boolean) {
					if (key.charAt( 0 ) != '$') {
						allFieldNames.append( ' ' ).append( key );
					}
					if (!fullText) {
						keywords.append( ' ' ).append( value );
					}
					this.addText( map, null, storeKey, value.toString(), 1, 1, conn );
				} else if (value instanceof Collection<?>) {
					final Collection<?> valueCollection = (Collection<?>) value;
					if (!valueCollection.isEmpty()) {
						if (key.charAt( 0 ) != '$') {
							allFieldNames.append( ' ' ).append( key );
						}
						for (final Object element : valueCollection) {
							if (element == null) {
								continue;
							}
							this.addTextCheck( map, null, storeKey, element, 1, 1, true, conn );
						}
					}
				} else if (value instanceof Object[]) {
					final Object[] valueArray = (Object[]) value;
					if (valueArray.length > 0) {
						if (key.charAt( 0 ) != '$') {
							allFieldNames.append( ' ' ).append( key );
						}
						for (int iterator = valueArray.length - 1; iterator >= 0; iterator--) {
							final Object element = valueArray[iterator];
							if (element == null) {
								continue;
							}
							this.addTextCheck( map, null, storeKey, element, 1, 1, true, conn );
						}
					}
				} else {
					if (key.charAt( 0 ) != '$') {
						allFieldNames.append( ' ' ).append( key );
					}
				}
			}
		}
		if (keywords.length() > 0) {
			this.addText( map, map, "$text", keywords.toString(), 1, 2, conn );
		}
		if (allFieldNames.length() > 0) {
			this.addText( map, null, "$afields", allFieldNames.toString(), 1, 1, conn );
		}
		return words;
	}
	
	/**
	 * @param conn
	 * @param luid
	 * @throws SQLException
	 */
	public void doDelete(final Connection conn, final int luid) throws SQLException {
		if (luid == -1) {
			return;
		}
		try (final Statement st = conn.createStatement()) {
			st.executeUpdate( "DELETE FROM " + this.tnIndices + " WHERE luid=" + luid );
		}
	}
	
	/**
	 * @param conn
	 * @param parent
	 * @param hierarchy
	 * @param state
	 * @param systemKeys
	 * @param fields
	 * @param fullText
	 * @param luid
	 * @throws SQLException
	 */
	public void doIndex(
			final Connection conn,
			final String parent,
			final Set<?> hierarchy,
			final int state,
			final Set<String> systemKeys,
			final Map<String, Object> fields,
			final boolean fullText,
			final int luid) throws SQLException {
		if (luid == -1) {
			return;
		}
		if (state == 0) {
			if (conn != null) {
				this.doDelete( conn, luid );
			} else {
				System.out.println( "IDX: DELETE FROM " + this.tnIndices + " WHERE luid=" + luid );
			}
			return;
		}
		final HashMapPrimitiveInt<AtomicInteger> map = new HashMapPrimitiveInt<>();
		this.doCreateIndex( conn, fullText, fields, systemKeys, map );
		if (parent != null) {
			this.addText( map, null, "$parent", parent, 1, 1, conn );
		}
		if (hierarchy != null) {
			this.addText( map, null, "$hierarchy", hierarchy.toString(), 1, 1, conn );
		}
		switch (state) {
		case ModuleInterface.STATE_ARCHIEVED: {
			this.addText( map, null, "$state", "public archive archieve", 1, 1, conn );
			break;
		}
		case ModuleInterface.STATE_DEAD: {
			this.addText( map, null, "$state", "dead", 1, 1, conn );
			break;
		}
		case ModuleInterface.STATE_PUBLISHED: {
			this.addText( map, null, "$state", "public archieve listable", 1, 1, conn );
			break;
		}
		case ModuleInterface.STATE_SYSTEM: {
			this.addText( map, null, "$state", "public listable system", 1, 1, conn );
			break;
		}
		default: {
			// empty
		}
		}
		this.store( conn, map, luid );
	}
	
	/**
	 * @return
	 */
	public IndexingDictionary getDictionary() {
		return this.dictionary;
	}
	
	final String getIndicesTableName() {
		return this.tnIndices;
	}
	
	/**
	 * @return
	 */
	public IndexingStemmer getStemmer() {
		return this.stemmer;
	}
	
	/**
	 * @return version
	 */
	@SuppressWarnings("static-method")
	public int getVersion() {
		return 22;
	}
	
	private final void store(final Connection conn, final HashMapPrimitiveInt<AtomicInteger> words, final int luid)
			throws SQLException {
		final int size = words.size();
		if (size == 0) {
			if (conn == null) {
				System.out.println( "IDX: DELETE FROM " + this.tnIndices + " WHERE luid=" + luid );
			} else {
				this.doDelete( conn, luid );
			}
			return;
		}
		int batchCounter = 0;
		final boolean batchSupported = conn == null
				? false
				: conn.getMetaData().supportsBatchUpdates();
		try (final Statement st = conn == null
				? null
				: conn.createStatement()) {
			final Set<Integer> delete = Create.tempSet();
			final HashMapPrimitiveInt<AtomicInteger> update = new HashMapPrimitiveInt<>();
			{
				final String query = "SELECT code,weight FROM " + this.tnIndices + " WHERE luid=" + luid;
				if (st == null) {
					System.out.println( "IDX: " + query );
				} else {
					try {
						try (final ResultSet rs = st.executeQuery( query )) {
							while (rs.next()) {
								final int code = rs.getInt( 1 );
								final AtomicInteger existing = words.remove( code );
								if (existing == null) {
									delete.add( Reflect.getInteger( code ) );
								} else {
									final int weight = rs.getInt( 2 );
									if (existing.intValue() != weight) {
										update.put( code, existing );
									}
								}
							}
						}
					} catch (final SQLException e) {
						Report.exception( "INDEXING", "Error while loading word info, intFields=true"
								+ ", luid="
								+ luid, e );
						throw e;
					}
				}
			}
			if (!delete.isEmpty()) {
				final String query = "DELETE FROM "
						+ this.tnIndices
						+ " WHERE luid="
						+ luid
						+ " AND code in ("
						+ Text.join( delete, "," )
						+ ")";
				if (st == null) {
					System.out.println( "IDX: " + query );
				} else {
					try {
						if (batchSupported) {
							st.addBatch( query );
							if (batchCounter++ > 1024) {
								st.executeBatch();
								st.clearBatch();
								batchCounter = 0;
							}
						} else {
							st.executeUpdate( query );
						}
					} catch (final SQLException e) {
						Report.exception( "INDEXING", "Error while deleting indices, luid="
								+ luid
								+ ", codes="
								+ delete, e );
						throw e;
					}
				}
			}
			if (!update.isEmpty()) {
				for (final HashMapPrimitiveInt.Entry<AtomicInteger> entry : update.entrySet()) {
					final String query = "UPDATE "
							+ this.tnIndices
							+ " SET weight="
							+ entry.getValue()
							+ " WHERE luid="
							+ luid
							+ " AND code="
							+ entry.getKey();
					if (st == null) {
						System.out.println( "IDX: " + query );
					} else {
						try {
							if (batchSupported) {
								st.addBatch( query );
								if (batchCounter++ > 1024) {
									st.executeBatch();
									st.clearBatch();
									batchCounter = 0;
								}
							} else {
								st.executeUpdate( query );
							}
						} catch (final SQLException e) {
							Report.exception( "INDEXING", "Error while updating indices, luid="
									+ luid
									+ ", words<code,weight>="
									+ update, e );
							throw e;
						}
					}
				}
			}
			if (!words.isEmpty()) {
				for (final HashMapPrimitiveInt.Entry<AtomicInteger> entry : words.entrySet()) {
					final String query = "INSERT INTO "
							+ this.tnIndices
							+ "(code,luid,weight) VALUES ("
							+ entry.getKey()
							+ ','
							+ luid
							+ ','
							+ entry.getValue()
							+ ')';
					if (st == null) {
						System.out.println( "IDX: " + query );
					} else {
						try {
							if (batchSupported) {
								st.addBatch( query );
								if (batchCounter++ > 1024) {
									st.executeBatch();
									st.clearBatch();
									batchCounter = 0;
								}
							} else {
								st.executeUpdate( query );
							}
						} catch (final SQLException e) {
							Report.exception( "INDEXING", "Error while inserting indices, luid="
									+ luid
									+ ", words<code,weight>="
									+ words, e );
							throw e;
						}
					}
				}
			}
			if (st != null && batchCounter > 0) {
				try {
					st.executeBatch();
				} catch (final Throwable t) {
					// ignore
				}
			}
		}
	}
}
