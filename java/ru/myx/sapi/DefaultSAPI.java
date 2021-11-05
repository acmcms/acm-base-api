package ru.myx.sapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.w3c.dom.Element;

import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseDate;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.binary.TransferBuffer;
import ru.myx.ae3.binary.TransferCollector;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.extra.External;
import ru.myx.ae3.flow.BinaryMessage;
import ru.myx.ae3.flow.CharacterMessage;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Dom;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.help.Html;
import ru.myx.ae3.help.Message;
import ru.myx.ae3.help.Text;
import ru.myx.ae3.mime.MimeType;
import ru.myx.ae3.reflect.Reflect;
import ru.myx.ae3.xml.Xml;
import ru.myx.sapi.default_sapi.Function_For;
import ru.myx.sapi.default_sapi.Function_ForArray;
import ru.myx.sapi.default_sapi.Function_ForHash;
import ru.myx.sapi.default_sapi.Function_binaryDigest;
import ru.myx.sapi.default_sapi.Function_binarySize;
import ru.myx.sapi.default_sapi.Function_mapMergeRecursive;
import ru.myx.sapi.default_sapi.Function_mapPutRecursive;
import ru.myx.sapi.default_sapi.Function_toBoolean;
import ru.myx.sapi.default_sapi.Function_toDouble;
import ru.myx.sapi.default_sapi.Function_toNumber;
import ru.myx.sapi.default_sapi.Function_toObject;
import ru.myx.sapi.default_sapi.Function_toPrimitive;
import ru.myx.util.Base64;

/** Title: Base Implementations: Default call scope Description: Copyright:
 *
 * @author Alexander I. Kharitchev
 * @version 1.0 */
@SuppressWarnings("deprecation")
public final class DefaultSAPI {
	
	/**
	 *
	 */
	public static final BaseObject DEFAULT;
	
	static {
		DEFAULT = new BaseNativeObject(Reflect.classToBasePrototype(DefaultSAPI.class));
		DefaultSAPI.DEFAULT.baseDefine("binaryDigest", new Function_binaryDigest(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("binarySize", new Function_binarySize(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("For", new Function_For(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("ForArray", new Function_ForArray(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("ForHash", new Function_ForHash(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("mapMergeRecursive", new Function_mapMergeRecursive(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("mapPutRecursive", new Function_mapPutRecursive(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("toBoolean", new Function_toBoolean(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("toDouble", new Function_toDouble(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("toNumber", new Function_toNumber(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("toObject", new Function_toObject(), BaseProperty.ATTRS_MASK_NNN);
		DefaultSAPI.DEFAULT.baseDefine("toPrimitive", new Function_toPrimitive(), BaseProperty.ATTRS_MASK_NNN);
	}
	
	/** @param s
	 * @return string */
	public static final String afterPoint(final String s) {
		
		if (s == null) {
			return null;
		}
		final int length = s.length();
		if (length <= 3 || s.charAt(0) > '9') {
			return s.trim();
		}
		final int pos = s.lastIndexOf('.', 3);
		return pos == -1
			? s.trim()
			: s.substring(pos + 1).trim();
	}
	
	/** @param list
	 * @param page
	 * @return list */
	public static final List<?> ArraySplit(final List<?> list, final int page) {
		
		if (list == null) {
			return null;
		}
		if (list.size() <= page) {
			return Collections.singletonList(list);
		}
		final List<List<?>> result = new ArrayList<>();
		for (int position = 0;;) {
			if (position + page >= list.size()) {
				result.add(list.subList(position, list.size()));
				break;
			}
			result.add(list.subList(position, position + page));
			position += page;
		}
		return result;
	}
	
	/** @param array
	 * @param page
	 * @return array */
	public static final Object[][] ArraySplit(final Object[] array, final int page) {
		
		if (array == null) {
			return null;
		}
		if (array.length <= page) {
			return new Object[][]{
					array
			};
		}
		final List<Object[]> result = new ArrayList<>();
		for (int position = 0;;) {
			if (position + page >= array.length) {
				final Object[] sub = new Object[array.length - position];
				System.arraycopy(array, position, sub, 0, sub.length);
				result.add(sub);
				break;
			}
			final Object[] sub = new Object[page];
			System.arraycopy(array, position, sub, 0, page);
			result.add(sub);
			position += page;
		}
		return result.toArray(new Object[result.size()][]);
	}
	
	/** @param string
	 * @param page
	 * @return array */
	public static final Object[] ArraySplit(final String string, final int page) {
		
		return DefaultSAPI.ArraySplit(string.split(","), page);
	}
	
	/** @param o
	 * @return string */
	public static final String Base64ToString(final Object o) {
		
		final byte[] result = Base64.decode(Convert.Any.toString(o, ""), false);
		return result != null
			? new String(result, Engine.CHARSET_UTF8)
			: "";
	}
	
	/** BigNumberFormat(Number).
	 * <P>
	 * '1000000' -> '1 000 000';
	 *
	 * @param o
	 * @return string
	 *
	 * @deprecated */
	@Deprecated
	public static final String BigNumberFormat(final Object o) {
		
		final StringBuilder Number = new StringBuilder().append(Convert.Any.toLong(o, 0L));
		for (int i = Number.length(); i > 0; i -= 3) {
			Number.insert(i, ' ');
		}
		return Number.toString();
	}
	
	/** @param binary
	 * @return string */
	public static final CharSequence binaryToString(final Object binary) {
		
		return DefaultSAPI.binaryToString(binary, Engine.CHARSET_UTF8);
	}
	
	/** @param binary
	 * @param defCharset
	 * @return string */
	public static final CharSequence binaryToString(final Object binary, final Charset defCharset) {
		
		if (binary == null) {
			return null;
		}
		if (binary instanceof byte[]) {
			try {
				return new String((byte[]) binary, defCharset.toString());
			} catch (final UnsupportedEncodingException e1) {
				throw new RuntimeException(e1);
			}
		}
		if (binary instanceof TransferBuffer) {
			return ((TransferBuffer) binary).toString(defCharset);
		}
		if (binary instanceof TransferCopier) {
			return ((TransferCopier) binary).toString(defCharset);
		}
		if (binary instanceof BaseMessage) {
			final BaseMessage message = (BaseMessage) binary;
			if (message.isCharacter()) {
				return ((CharacterMessage<?>) message).getText();
			}
			final String encoding = Message.subAttributeValue(message, "Content-Type", "charset", "");
			Charset charset = Charset.forName(encoding);
			if (charset == null) {
				charset = defCharset;
			}
			if (message.isBinary()) {
				return ((BinaryMessage<?>) message).getBinary().toString(charset);
			}
			if (message.isFile()) {
				return Transfer.createBuffer(message.getFile()).toString(charset);
			}
			if (message.isEmpty()) {
				return "";
			}
			return String.valueOf(message.getObject());
		}
		return binary.toString();
	}
	
	/** @param binary
	 * @param defEncoding
	 * @return string
	 * @throws UnsupportedEncodingException */
	public static final CharSequence binaryToString(final Object binary, final String defEncoding) throws UnsupportedEncodingException {
		
		if (binary == null) {
			return null;
		}
		if (binary instanceof byte[]) {
			return new String((byte[]) binary, defEncoding);
		}
		if (binary instanceof TransferBuffer) {
			return ((TransferBuffer) binary).toString(defEncoding);
		}
		if (binary instanceof TransferCopier) {
			return ((TransferCopier) binary).toString(defEncoding);
		}
		if (binary instanceof BaseMessage) {
			final BaseMessage message = (BaseMessage) binary;
			if (message.isCharacter()) {
				return message.toCharacter().getText();
			}
			final String encoding = Message.subAttributeValue(message, "Content-Type", "charset", defEncoding);
			if (message.isBinary()) {
				return message.toBinary().getBinary().toString(encoding);
			}
			if (message.isFile()) {
				return Transfer.createBuffer(message.getFile()).toString(encoding);
			}
			if (message.isEmpty()) {
				return "";
			}
			return String.valueOf(message.getObject());
		}
		return binary.toString();
	}
	
	/** Capitalize(String).
	 * <P>
	 * 'A big bROWN fOx' -> 'A Big Brown Fox'
	 *
	 * @param o
	 * @return string */
	public static final String Capitalize(final String o) {
		
		return o == null
			? null
			: Text.capitalize(o);
	}
	
	/** @param result
	 * @return object */
	public static final Object Character(final Object result) {
		
		return DefaultSAPI.Character(result, "?");
	}
	
	/** @param result
	 * @param defaultValue
	 * @return object */
	public static final Object Character(final Object result, final Object defaultValue) {
		
		if (result instanceof Number) {
			return new String(new char[]{
					(char) ((Number) result).intValue()
			});
		}
		try {
			return new String(new char[]{
					(char) Integer.parseInt(result.toString().trim())
			});
		} catch (final Throwable e) {
			return defaultValue;
		}
	}
	
	/** @param o
	 * @return int code */
	public static final int charCode(final Object o) {
		
		return Convert.Any.toString(o, " ").charAt(0);
	}
	
	/** CurrentTime().
	 * <P>
	 * Returns formatted string representing current time. Format: default='HH:mm:ss'
	 *
	 * @return string */
	public static final String CurrentTime() {
		
		final SimpleDateFormat DF = new SimpleDateFormat("HH:mm:ss");
		return DF.format(new Date());
	}
	
	/** CurrentTime(Format).
	 * <P>
	 * Returns formatted string representing current time. Format: default='HH:mm:ss'
	 *
	 * @param o
	 * @return string */
	public static final String CurrentTime(final Object o) {
		
		final SimpleDateFormat DF = new SimpleDateFormat(Convert.Any.toString(o, "HH:mm:ss"));
		return DF.format(new Date());
	}
	
	/** @param date
	 * @return string */
	public static final String DateFormat(final Object date) {
		
		return DefaultSAPI.DateFormat(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	/** DateFormat(Date[, Format]).
	 * <P>
	 * Date: long, java.util.Date Format: default='yyyy-MM-dd HH:mm:ss'
	 *
	 * @param object
	 * @param format
	 * @return string */
	public static final String DateFormat(final Object object, final String format) {
		
		long date;
		try {
			if (object == null) {
				date = 0;
			} else if (object instanceof Date) {
				date = ((Date) object).getTime();
			} else {
				date = Convert.Any.toLong(object, 0L);
			}
		} catch (final Throwable e) {
			date = Convert.Any.toLong(object, 0L);
		}
		final SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date(date));
	}
	
	/** @param o
	 * @return long */
	public static final long dateToNumeric(final Object o) {
		
		if (o == null) {
			return 0;
		}
		if (o instanceof Number) {
			return ((Number) o).longValue();
		}
		if (o instanceof Date) {
			return ((Date) o).getTime();
		}
		return 0;
	}
	
	/** @param binary
	 * @return
	 * @throws IOException */
	public static final TransferCopier decompressGzip(final Object binary) throws IOException {
		
		if (binary == null) {
			return null;
		}
		final TransferBuffer buffer = Transfer.createBufferFromBinary(binary);
		final int length = (int) buffer.remaining();
		if (length == 0) {
			return TransferCopier.NUL_COPIER;
		}
		final GZIPInputStream input = new GZIPInputStream(buffer.toInputStream(), length);
		final TransferCollector collector = Transfer.createCollector();
		Transfer.toStream(input, collector.getOutputStream(), true);
		/** collector is closed by ^^^ */
		return collector.toCloneFactory();
	}
	
	/** @param o
	 * @return element */
	public static final Element DomParse(final Object o) {
		
		return Dom.toElement(Convert.Any.toString(o, "<empty/>"));
	}
	
	/** @param o
	 * @return String */
	public static final String DomToString(final Object o) {
		
		return Dom.toXmlCompact((Element) o);
	}
	
	/** @param o
	 * @return string */
	@Deprecated
	public static final String enchanceHtml(final Object o) {
		
		return Html.enhanceHtml(Convert.Any.toString(o, ""), null);
	}
	
	/** @param o
	 * @param hrefAttributes
	 * @return string */
	@Deprecated
	public static final String enchanceHtml(final Object o, final String hrefAttributes) {
		
		return Html.enhanceHtml(Convert.Any.toString(o, ""), hrefAttributes);
	}
	
	private static final String fill(final char c, final int count) {
		
		final StringBuilder buffer = new StringBuilder();
		for (int i = count; i > 0; --i) {
			buffer.append(c);
		}
		return buffer.toString();
	}
	
	/** @param o
	 * @return string */
	public static final String formatBigDecimal(final Object o) {
		
		final StringBuilder Number = new StringBuilder().append(Convert.Any.toLong(o, 0L));
		for (int i = Number.length(); i > 0; i -= 3) {
			Number.insert(i, ' ');
		}
		return Number.toString();
	}
	
	/** @param o
	 * @return string */
	public static final String formatByteSize(final Object o) {
		
		return Format.Compact.toBytes(Convert.Any.toLong(o, 0L));
	}
	
	/** @param value
	 * @param size
	 * @return string */
	public static final String formatFraction(final Object value, final int size) {
		
		if (value == null) {
			return "-";
		}
		final double doubleValue = Convert.Any.toDouble(value, Double.NaN);
		if (Double.isNaN(doubleValue)) {
			return "-";
		}
		final String string = String.valueOf(doubleValue);
		final int pos = string.indexOf('.');
		if (pos == -1) {
			return string + '.' + DefaultSAPI.fill('0', size);
		}
		final int least = string.length() - pos - 1;
		if (least == size) {
			return string;
		}
		if (least > size) {
			return string.substring(0, pos + 1 + size);
		}
		return string + DefaultSAPI.fill('0', size - least);
	}
	
	/** @param period
	 * @return string */
	public static final String formatPeriod(final long period) {
		
		return Format.Compact.toPeriod(period);
	}
	
	/** @param o
	 * @return string */
	public static final String formatRoundByteSize(final Object o) {
		
		final long Number = Convert.Any.toLong(o, 0L);
		final String s = Format.Compact.toBytes(Number);
		if (s.startsWith("0")) {
			return s;
		}
		final int pos = s.indexOf('.');
		if (pos == -1) {
			return s;
		}
		final char c = s.charAt(s.length() - 1);
		return Character.isDigit(c) || c == ' '
			? s.substring(0, pos)
			: s.substring(0, pos) + ' ' + c;
	}
	
	/** @param ctx
	 * @param key
	 * @return object
	 *
	 * @deprecated */
	@Deprecated
	public static final BaseObject Get(final ExecProcess ctx, final String key) {
		
		assert ctx != null : "NULL context!";
		return ctx.baseGet(key, BaseObject.UNDEFINED);
	}
	
	/** @param source
	 * @return int
	 *
	 * @deprecated */
	@Deprecated
	public static final int GetOccuranceCount(final String source) {
		
		return DefaultSAPI.GetOccuranceCount(source, "\n", 10);
	}
	
	/** @param source
	 * @param token
	 * @return int
	 *
	 * @deprecated */
	@Deprecated
	public static final int GetOccuranceCount(final String source, final String token) {
		
		return DefaultSAPI.GetOccuranceCount(source, token, 10);
	}
	
	/** GetOccuranceCount(String [, Token [, Max]]).
	 * <P>
	 * Returns the number than Token occurs in the string. Number is limited by Max value. Token:
	 * default='\n' Max: default=10
	 *
	 * @param source
	 * @param token
	 * @param max
	 * @return int
	 *
	 * @deprecated */
	@Deprecated
	public static final int GetOccuranceCount(final String source, final String token, final int max) {
		
		if (source == null) {
			return 0;
		}
		final int tokenLength = token.length();
		int LookingSince = 0;
		int count = 0;
		for (; count < max;) {
			final int pos = source.indexOf(token, LookingSince);
			if (pos == -1) {
				return count;
			}
			LookingSince = pos + tokenLength;
			count++;
		}
		return count;
	}
	
	/** GetUserID().
	 * <P>
	 * Returns UserID of current user.
	 *
	 * @param ctx
	 *
	 * @return string
	 *
	 * @deprecated */
	@Deprecated
	public static final String GetUserID(final ExecProcess ctx) {
		
		return Context.getUserId(ctx);
	}
	
	/** @param object
	 * @param defaultValue
	 * @return string */
	public static final String guessContentTypeFromBinary(final Object object, final String defaultValue) {
		
		final Object binary = object instanceof External
			? ((External) object).toBinary()
			: object;
		if (binary instanceof BaseMessage) {
			final BaseObject attributes = ((BaseMessage) binary).getAttributes();
			final String header = attributes == null
				? ""
				: Base.getString(attributes, "Content-Type", "").trim();
			if (header.length() > 0) {
				return header;
			}
			final String name = attributes == null
				? ""
				: Base.getString(attributes, "Content-Name", "").trim();
			if (name.length() > 0) {
				return MimeType.forName(name, defaultValue);
			}
			final String disposition = Message.subAttributeValue((BaseMessage) binary, "Content-Disposition", "filename", "").trim();
			if (disposition.length() > 0) {
				return MimeType.forName(disposition, defaultValue);
			}
		}
		return defaultValue;
	}
	
	/** HashCode(Object).
	 * <P>
	 * Returns Hash code of String representation of an object passed
	 *
	 * @param o
	 * @return int */
	public static final int hashCode(final Object o) {
		
		return o == null
			? 0
			: o.hashCode();
	}
	
	/** @param o
	 * @return boolean */
	/** HexHashCode(Object).
	 * <P>
	 * Returns hexadecimal Hash code of String representation of an object passed
	 *
	 * @param o
	 * @return string */
	public static final String HexHashCode(final Object o) {
		
		return o == null
			? "0"
			: Integer.toHexString(o.hashCode());
	}
	
	/** @param o
	 * @return string */
	public static final String HtmlToString(final Object o) {
		
		return Html.cleanHtml(
				Convert.Any.toString(o, "").replace("<p>", "\r\n\r\n").replace("<P>", "\r\n\r\n").replace("<p/>", "\r\n\r\n").replace("<P/>", "\r\n\r\n").replace("</p>", "")
						.replace("</P>", "").replace("<br>", "\r\n").replace("<BR>", "\r\n").replace("<br/>", "\r\n").replace("<BR/>", "\r\n").replace("</br>", "")
						.replace("</BR>", ""));
	}
	
	/** @param o
	 * @return int */
	public static final int Int(final Object o) {
		
		return Convert.Any.toInt(o, 0);
	}
	
	/** @param o
	 * @param defaultValue
	 * @return int */
	public static final int Int(final Object o, final Object defaultValue) {
		
		return Convert.Any.toInt(o, Convert.Any.toInt(defaultValue, 0));
	}
	
	/** Integer(Object[,Default]).
	 * <P>
	 * Default: default = 0
	 *
	 * @param o
	 * @return long */
	public static final long Integer(final Object o) {
		
		return Convert.Any.toLong(o, 0);
	}
	
	/** Integer(Object[,Default]).
	 * <P>
	 * Default: default = 0
	 *
	 * @param o
	 * @param defaultValue
	 * @return long */
	public static final long Integer(final Object o, final Object defaultValue) {
		
		return Convert.Any.toLong(o, Convert.Any.toLong(defaultValue, 0));
	}
	
	/** @param str
	 * @param limit
	 * @return string */
	public static final String LimitString(final Object str, final int limit) {
		
		return DefaultSAPI.LimitString(str, limit, "");
	}
	
	/** LimitString(String , MaxLength [, Suffix]).
	 * <P>
	 * Returns string with length less or equal the MaxLength specified with suffix appended in the
	 * case of truncation.
	 *
	 * @param str
	 * @param limit
	 * @param suffix
	 * @return string */
	public static final String LimitString(final Object str, final int limit, final String suffix) {
		
		if (str == null) {
			return null;
		}
		final String string = Convert.Any.toString(str, "");
		if (string.length() < limit) {
			return string;
		}
		return Text.limitString(string, limit, suffix);
	}
	
	/** @param o
	 * @return string */
	public static final String mapToXml(final BaseObject o) {
		
		return o == null
			? null
			: Xml.toXmlString("data", o, false);
	}
	
	/** @param o
	 * @param root
	 * @return string */
	public static final String mapToXml(final BaseObject o, final String root) {
		
		return o == null
			? null
			: Xml.toXmlString(root, o, false);
	}
	
	/** @param o
	 * @param root
	 * @param readable
	 * @return string */
	public static final String mapToXml(final BaseObject o, final String root, final boolean readable) {
		
		return o == null
			? null
			: Xml.toXmlString(root, o, readable);
	}
	
	/** @param o
	 * @return date instance */
	public static final Date numericToDate(final Object o) {
		
		if (o == null) {
			return new Date(0);
		}
		if (o instanceof Number) {
			return new Date(((Number) o).longValue());
		}
		if (o instanceof Date) {
			return (Date) o;
		}
		return new Date(Long.parseLong(o.toString()));
	}
	
	/** @param object
	 * @return date
	 * @throws ParseException */
	public static final BaseDate parseDate(final Object object) throws ParseException {
		
		return DefaultSAPI.parseDate(object, "yyyy-MM-dd HH:mm:ss");
	}
	
	/** @param object
	 * @param format
	 * @return date
	 * @throws ParseException */
	public static final BaseDate parseDate(final Object object, final String format) throws ParseException {
		
		final String date = Convert.Any.toString(object, "");
		if (date == null || date.length() == 0 || date.equalsIgnoreCase("NOW")) {
			return BaseDate.NOW;
		}
		if (date.equalsIgnoreCase("UNDEFINED")) {
			return BaseDate.UNKNOWN;
		}
		return Base.forDate(new SimpleDateFormat(format).parse(date));
	}
	
	/** @param o
	 * @return date */
	public static final long parsePeriod(final Object o) {
		
		return Convert.Any.toPeriod(o, 0);
	}
	
	/** replace(String,What,To);
	 *
	 * @param s
	 * @param what
	 * @param to
	 * @return string */
	public static final String replace(final String s, final String what, final String to) {
		
		if (s == null) {
			return null;
		}
		String result = s;
		int pointer = s.length();
		for (;;) {
			final int pos = result.lastIndexOf(what, pointer);
			if (pos == -1) {
				return result;
			}
			result = result.substring(0, pos) + to + result.substring(pos + what.length());
			if (pos == 0) {
				return result;
			}
			pointer = pos - 1;
		}
	}
	
	/** replaceRegex(String,What,To);
	 *
	 * @param s
	 * @param what
	 * @param to
	 * @return string */
	public static final String replaceRegex(final String s, final String what, final String to) {
		
		if (s == null) {
			return null;
		}
		return s.replaceAll(what, to);
	}
	
	/** @param text
	 * @param minChars
	 * @param maxChars
	 * @return string */
	public static final String sentenceLeft(final String text, final int minChars, final int maxChars) {
		
		return DefaultSAPI.sentenceLeft(text, minChars, maxChars, "...");
	}
	
	/** @param text
	 * @param minChars
	 * @param maxChars
	 * @param append
	 * @return string */
	public static final String sentenceLeft(final String text, final int minChars, final int maxChars, final String append) {
		
		if (text == null) {
			return null;
		}
		final int length = text.length();
		if (length <= minChars) {
			return text;
		}
		int lastWord = -1;
		boolean word = false;
		boolean point = false;
		for (int i = minChars; i < length; ++i) {
			if (i == maxChars) {
				if (i == length) {
					return text;
				}
				if (lastWord != -1) {
					return text.substring(0, lastWord) + append;
				}
				return text.substring(0, i - 1) + append;
			}
			final char c = text.charAt(i);
			if (c == ';' || Character.isWhitespace(c)) {
				if (point) {
					if (i == length) {
						return text;
					}
					return text.substring(0, i - 1) + append;
				}
				if (word) {
					lastWord = i;
				}
				point = false;
				word = false;
				continue;
			}
			if (c == ',' || Character.isLetterOrDigit(c)) {
				point = false;
				word = true;
				continue;
			}
			if (c == '.' || c == '!' || c == '?' || c == ';') {
				point = true;
			}
			word = false;
		}
		if (lastWord == -1) {
			return text;
		}
		return text.substring(0, lastWord) + append;
	}
	
	/** @param list
	 * @return list */
	public static final List<?> split(final List<?> list) {
		
		return list;
	}
	
	/** @param list
	 * @param dividerRegex
	 * @return list */
	public static final List<?> split(final List<?> list, final String dividerRegex) {
		
		if (dividerRegex == null) {
			throw new NullPointerException();
		}
		return list;
	}
	
	/** @param set
	 * @return list */
	public static final List<?> split(final Set<?> set) {
		
		return set == null
			? null
			: Arrays.asList(set.toArray());
	}
	
	/** @param set
	 * @param dividerRegex
	 * @return list */
	public static final List<?> split(final Set<?> set, final String dividerRegex) {
		
		if (dividerRegex == null) {
			throw new NullPointerException();
		}
		return set == null
			? null
			: Arrays.asList(set.toArray());
	}
	
	/** @param string
	 * @return list */
	public static final List<?> split(final String string) {
		
		return DefaultSAPI.split(string, "\n");
	}
	
	/** split(String [, Divider]).
	 * <P>
	 * Returns java.util.List object containing splitted string elements.
	 *
	 * Divider: default=','
	 *
	 * @param string
	 * @param divider
	 * @return list */
	public static final List<?> split(final String string, final String divider) {
		
		if (string == null) {
			return null;
		}
		final List<String> result = new ArrayList<>();
		final int length = divider.length();
		if (length == 1) {
			for (final StringTokenizer st = new StringTokenizer(string, divider); st.hasMoreTokens();) {
				result.add(st.nextToken());
			}
		} else {
			int start = 0;
			int end = 0;
			for (;;) {
				start = string.indexOf(divider, end);
				if (start == -1) {
					break;
				}
				result.add(string.substring(end, start));
				end = start + length;
			}
			if (end < string.length()) {
				result.add(string.substring(end));
			}
		}
		return result;
	}
	
	/** @param csvLine
	 * @return list */
	public static final List<?> splitCSV(final String csvLine) {
		
		return csvLine == null
			? null
			: FormatSAPI.splitCsvLine(csvLine, ',');
	}
	
	/** @param csvLine
	 * @param splitter
	 * @return list */
	public static final List<?> splitCSV(final String csvLine, final char splitter) {
		
		return csvLine == null
			? null
			: FormatSAPI.splitCsvLine(csvLine, splitter);
	}
	
	/** @param csvLine
	 * @param splitter
	 * @return list */
	public static final List<?> splitCSV(final String csvLine, final String splitter) {
		
		return csvLine == null
			? null
			: FormatSAPI.splitCsvLine(csvLine, splitter);
	}
	
	/** @param text
	 * @return list */
	public static final List<?> splitLines(final Object text) {
		
		return text == null
			? null
			: FormatSAPI.splitLines(text);
	}
	
	/** @param text
	 * @return list */
	public static final List<?> splitLinesIgnoreQuotes(final Object text) {
		
		return text == null
			? null
			: FormatSAPI.splitLinesIgnoreQuotes(text);
	}
	
	/** @param list
	 * @param dividerRegex
	 * @return list */
	public static final List<?> splitRegex(final List<?> list, final String dividerRegex) {
		
		if (dividerRegex == null) {
			throw new NullPointerException();
		}
		return list;
	}
	
	/** @param string
	 * @return list */
	public static final List<?> splitRegex(final String string) {
		
		return DefaultSAPI.splitRegexp(string, ",");
	}
	
	/** splitRegex(String [, DividerRegex]).
	 * <P>
	 * Returns java.util.List object containing splitted string elements.
	 *
	 * Divider: default=','
	 *
	 * @param string
	 * @param dividerRegex
	 * @return list */
	public static final List<?> splitRegex(final String string, final String dividerRegex) {
		
		if (string == null) {
			return null;
		}
		return Arrays.asList(string.split(dividerRegex));
	}
	
	/** splitRegexp(String [, DividerRegex]).
	 * <P>
	 * Returns java.util.List object containing splitted string elements.
	 *
	 * Divider: default=','
	 *
	 * @param string
	 * @param dividerRegex
	 * @return list */
	public static final List<?> splitRegexp(final String string, final String dividerRegex) {
		
		if (string == null) {
			return null;
		}
		return Arrays.asList(string.split(dividerRegex));
	}
	
	/** StartsWithOneOf(String, PrefixList, PrefixListDivider).
	 * <P>
	 * Returns true if given String starts with one of Prefixes specified.
	 *
	 * @param what
	 * @param list
	 * @param divider
	 * @return booleab */
	public static final boolean StartsWithOneOf(final String what, final String list, final String divider) {
		
		if (what == null) {
			return false;
		}
		for (final StringTokenizer st = new StringTokenizer(list, divider); st.hasMoreElements();) {
			final String S = st.nextToken();
			if (S.length() == 0) {
				continue;
			}
			if (what.startsWith(S)) {
				return true;
			}
		}
		return false;
	}
	
	/** @param o
	 * @return string */
	public static final String StringToBase64(final Object o) {
		
		return Base64.encode(Convert.Any.toString(o, "").getBytes(Engine.CHARSET_UTF8), false);
	}
	
	/** @param textObject
	 * @return copier */
	public static final TransferCopier stringToBinary(final Object textObject) {
		
		return DefaultSAPI.stringToBinary(textObject, Engine.CHARSET_UTF8);
	}
	
	/** @param textObject
	 * @param charset
	 * @return copier */
	public static final TransferCopier stringToBinary(final Object textObject, final Charset charset) {
		
		if (textObject == null) {
			return TransferCopier.NUL_COPIER;
		}
		try {
			return Transfer.wrapCopier(textObject.toString().getBytes(charset.toString()));
		} catch (final UnsupportedEncodingException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	/** @param textObject
	 * @param encoding
	 * @return copier
	 * @throws IOException */
	public static final TransferCopier stringToBinary(final Object textObject, final String encoding) throws IOException {
		
		if (textObject == null) {
			return TransferCopier.NUL_COPIER;
		}
		return Transfer.wrapCopier(textObject.toString().getBytes(encoding));
	}
	
	/** StringToHtml(String).
	 * <P>
	 *
	 * @param o
	 * @return string */
	public static final String StringToHtml(final Object o) {
		
		if (o == null) {
			return null;
		}
		return Convert.Any.toString(o, "")//
				.replace("\r", "")//
				.replace(">\n<", "><")//
				.replace("<br>", "\n")//
				.replace("<p>", "\n\n")//
				.replace("</p>", "")//
				.replace("&#13;", "\n")//
				.replace(" \n", "\n")//
				.replace("\n\n\n", "\n\n")//
				.replace("  ", "&nbsp;")//
				.replace("\n\n", "<p>")//
				.replace("\n", "<br>");
	}
	
	/** StringToSafeHtml(String).
	 * <P>
	 * Replaces:
	 *
	 * <pre>
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *                                                                                         '&lt;' -&gt; '&amp;lt;'
	 *                                                                                         '\r' -&gt; ''
	 *                                                                                         '\n\n' -&gt; '&lt;p&gt;'
	 *                                                                                         '\n' -&gt; '&lt;br&gt;'
	 *                                                                                         '  ' -&gt; ' &amp;nbsp;'
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 * </pre>
	 *
	 * @param o
	 * @return string */
	public static final String StringToSafeHtml(final Object o) {
		
		final String string = Convert.Any.toString(o, null);
		if (string == null) {
			return null;
		}
		return string.replace("\r", "").replace(">\n<", "><").replace("<br>", "\n").replace("<p>", "\n\n").replace("</p>", "").replace("&#13;", "\n").replace(" \n", "\n")
				.replace("\n\n\n", "\n\n").replace("  ", " &nbsp;").replace("\t", " &nbsp; &nbsp;").replace("<", "&lt;").replace("\n\n", "<p>").replace("\n", "<br>");
	}
	
	/** @param o
	 * @return string */
	public static final String StringToWml(final Object o) {
		
		if (o == null) {
			return null;
		}
		final String source = o.toString();
		final int length = source.length();
		final StringBuilder result = new StringBuilder(length << 1);
		for (int i = 0; i < length; ++i) {
			final char c = source.charAt(i);
			if (c == '\n') {
				result.append("<br/>");
			} else //
			if (c == '&') {
				result.append("&amp;");
			} else //
			if (c > 127) {
				result.append("&#x").append(Integer.toHexString(c)).append(';');
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}
	
	/** @param o
	 * @return string */
	public static final String textToIdentifier(final String o) {
		
		if (o == null) {
			return null;
		}
		final char[] s = o.toLowerCase().trim().toCharArray();
		final int length = s.length;
		final StringBuilder result = new StringBuilder(length);
		char previous = 0;
		for (int i = 0; i < length; ++i) {
			final char c = s[i];
			if (c == previous) {
				continue;
			}
			if (Character.isLetterOrDigit(c)) {
				previous = c;
				result.append(c);
			}
		}
		return result.toString();
	}
	
	/** @param o
	 * @return string */
	public static final String textToIdentifier7bit(final String o) {
		
		if (o == null) {
			return null;
		}
		final char[] s = o.toLowerCase().trim().toCharArray();
		final int length = s.length;
		final StringBuilder result = new StringBuilder(length);
		char previous = 0;
		for (int i = 0; i < length; ++i) {
			final char c = s[i];
			if (c == previous) {
				continue;
			}
			if (Character.isLetterOrDigit(c)) {
				previous = c;
				result.append(c);
			}
		}
		final String text = result.toString();
		result.setLength(0);
		return Text.transliterate(result, text).append('_').append(Integer.toString(text.hashCode() & 0x7FFFFFFF, 36)).toString().replace('\'', '_');
	}
	
	/** toBinary(Number)
	 *
	 * @param o
	 * @return string */
	public static final String toBin(final Object o) {
		
		return Long.toBinaryString(Convert.Any.toLong(o, 0L));
	}
	
	/** @param o
	 * @return string */
	public static final String toBinary(final Object o) {
		
		return Long.toBinaryString(Convert.Any.toLong(o, 0L));
	}
	
	/** toHex(Number)
	 *
	 * @param o
	 * @return string */
	public static final String toHex(final Object o) {
		
		return Long.toHexString(Convert.Any.toLong(o, 0L));
	}
	
	/** @param o
	 * @return int */
	public static final int toInt32(final Object o) {
		
		return Convert.Any.toInt(o, 0);
	}
	
	/** @param o
	 * @return long */
	public static final long toInteger(final Object o) {
		
		return Convert.Any.toLong(o, 0L);
	}
	
	/** toOctal(Number)
	 *
	 * @param o
	 * @return string */
	public static final String toOct(final Object o) {
		
		return Long.toOctalString(Convert.Any.toLong(o, 0L));
	}
	
	/** @param o
	 * @return string */
	public static final String toString(final Object o) {
		
		return Convert.Any.toString(o, "null");
	}
	
	/** @param o
	 * @return short */
	public static final short toUint16(final Object o) {
		
		return Convert.Any.toShort(o, (short) 0);
	}
	
	/** @param o
	 * @return int */
	public static final int toUint32(final Object o) {
		
		return Convert.Any.toInt(o, 0);
	}
	
	/** @param string
	 * @return string */
	public static final String transliterate(final String string) {
		
		return Text.transliterate(string);
	}
	
	/** @param ctx
	 * @param o
	 * @return string */
	public static final String UrlToString(final ExecProcess ctx, final Object o) {
		
		return Text.decodeUri(Convert.Any.toString(o, ""), Context.getLanguage(ctx).getJavaEncoding());
	}
	
	/** @param o
	 * @return map */
	public static final BaseMap xmlToMap(final Object o) {
		
		final String xml = Convert.Any.toString(o, "").trim();
		return xml.length() == 0
			? new BaseNativeObject()
			: Xml.toMap("scripting", xml, null, new BaseNativeObject(), null, null)
		// Xml.toBase( "scripting", xml, null, null, null )
		;
	}
	
	/** @param o
	 * @param namespace
	 * @return map */
	public static final BaseMap xmlToMap(final Object o, final String namespace) {
		
		final String xml = Convert.Any.toString(o, "").trim();
		return xml.length() == 0
			? new BaseNativeObject()
			: Xml.toMap("scripting", xml, namespace, new BaseNativeObject(), null, null)
		// Xml.toBase( "scripting", xml, namespace, null, null )
		;
	}
	
	/** @param o
	 * @return string */
	public static final String XmlToString(final Object o) {
		
		return Text.decodeXmlNodeValue(o);
	}
	
	/**
	 *
	 */
	private DefaultSAPI() {

		//
	}
	
	@Override
	public final String toString() {
		
		return "Default scope";
	}
}
