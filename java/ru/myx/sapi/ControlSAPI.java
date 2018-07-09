package ru.myx.sapi;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlEntry;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.binary.TransferBuffer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.common.BodyAccessBinary;
import ru.myx.ae3.common.BodyAccessCharacter;
import ru.myx.ae3.common.Value;
import ru.myx.ae3.control.ControlActor;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.ControlForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.ecma.Ecma;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.GuidStream;
import ru.myx.ae3.vfs.Entry;

/**
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
public final class ControlSAPI {
	/**
	 * 
	 */
	public static final ControlSAPI						INSTANCE								= new ControlSAPI();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_DATE_KEY_ASC			= new ComparatorBasicDateKeyAsc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_DATE_KEY_DESC			= new ComparatorBasicDateKeyDesc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_DATE_TITLE_ASC			= new ComparatorBasicDateTitleAsc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_DATE_TITLE_DESC		= new ComparatorBasicDateTitleDesc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_NUMERIC_KEY_ASC		= new ComparatorBasicNumericKeyAsc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_NUMERIC_KEY_DESC		= new ComparatorBasicNumericKeyDesc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_NUMERIC_TITLE_ASC		= new ComparatorBasicNumericTitleAsc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_NUMERIC_TITLE_DESC		= new ComparatorBasicNumericTitleDesc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_STRING_CI_KEY_ASC		= new ComparatorBasicStringCiKeyAsc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_STRING_CI_KEY_DESC		= new ComparatorBasicStringCiKeyDesc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_STRING_CI_TITLE_ASC	= new ComparatorBasicStringCiTitleAsc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_STRING_CI_TITLE_DESC	= new ComparatorBasicStringCiTitleDesc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_STRING_CS_KEY_ASC		= new ComparatorBasicStringCsKeyAsc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_STRING_CS_KEY_DESC		= new ComparatorBasicStringCsKeyDesc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_STRING_CS_TITLE_ASC	= new ComparatorBasicStringCsTitleAsc();
	
	private static final Comparator<ControlBasic<?>>	COMPARATOR_BASIC_STRING_CS_TITLE_DESC	= new ComparatorBasicStringCsTitleDesc();
	
	/**
	 * @param node
	 * @param path
	 * @return node
	 */
	public static ControlNode<?> childForPath(final ControlNode<?> node, final String path) {
		return Control.relativeNode( node, path );
	}
	
	/**
	 * @param object
	 * @return string
	 */
	public static String controlSum(final BaseObject object) {
		if (object == null || object == BaseObject.UNDEFINED) {
			return "-";
		}
		return "b" + GuidStream.toUniqueIdentifier( object, true );
	}
	
	/**
	 * @param title
	 * @param target
	 * @param fieldset
	 * @param result
	 *            <ol>
	 *            <li>undefined or null value to stop further processing</li>
	 *            <li>ControlForm to show form (by ctrl)</li>
	 *            <li>URL object for redirection (by ctrl)</li>
	 *            <li>String to display as message text (by ctrl)</li>
	 *            <li>BaseFunction returning one of above while having 'target'
	 *            as 'this' and command_id as its first argument</li>
	 *            </ol>
	 * @return form
	 */
	public static ControlForm<?> createSimpleForm(
			final BaseObject title,
			final BaseObject target,
			final ControlFieldset<?> fieldset,
			final BaseObject result) {
		return Control.createSimpleForm( title, target, fieldset, result );
	}
	
	/**
	 * @param ctx
	 * @param path
	 * @param options
	 * @return commandset
	 */
	@Deprecated
	public static ControlCommandset filterAccessible(
			final ExecProcess ctx,
			final String path,
			final ControlCommandset options) {
		return ControlSAPI.filterAccessibleCommands( ctx, path, options );
	}
	
	/**
	 * @param ctx
	 * @param basePath
	 * @param nodes
	 * @return node array
	 */
	@Deprecated
	public static List<ControlNode<?>> filterAccessible(
			final ExecProcess ctx,
			final String basePath,
			final List<ControlNode<?>> nodes) {
		return ControlSAPI.filterAccessibleHierarchy( ctx, basePath, nodes );
	}
	
	/**
	 * @param ctx
	 * @param path
	 * @param options
	 * @return commandset
	 */
	public static ControlCommandset filterAccessibleCommands(
			final ExecProcess ctx,
			final String path,
			final ControlCommandset options) {
		if (options == null) {
			return null;
		}
		final ControlCommandset result = Control.createOptions();
		for (final ControlCommand<?> command : options) {
			if (Convert.MapEntry.toBoolean( command.getAttributes(), "global", false )) {
				final ControlEntry<?> node = ControlSAPI.childForPath( Context.getServer( ctx ).getControlRoot(), path );
				if (node != null) {
					final Collection<String> locations = node.getLocationControlAll();
					if (locations != null) {
						boolean grant = true;
						for (final String location : locations) {
							if (!ControlSAPI.isAccessibleCommand( ctx, location, command )) {
								grant = false;
								break;
							}
						}
						if (grant) {
							result.add( command );
						}
					}
					continue;
				}
			}
			if (ControlSAPI.isAccessibleCommand( ctx, path, command )) {
				result.add( command );
			}
		}
		return result;
	}
	
	/**
	 * @param ctx
	 * @param basePath
	 * @param nodes
	 * @return node array
	 */
	public static List<ControlNode<?>> filterAccessibleHierarchy(
			final ExecProcess ctx,
			final String basePath,
			final List<ControlNode<?>> nodes) {
		if (nodes == null || nodes.isEmpty()) {
			return nodes;
		}
		final String path = basePath.endsWith( "/" )
				? basePath
				: basePath + '/';
		final AccessManager manager = Context.getServer( ctx ).getAccessManager();
		final int level = Context.getSessionState( ctx );
		List<ControlNode<?>> result = null;
		final int length = nodes.size();
		for (int i = 0; i < length; ++i) {
			final ControlNode<?> node = nodes.get( i );
			final boolean deny;
			{
				final String check1 = path + node.getKey();
				if (manager.securityCheck( level, check1, "$view_tree" ) != null) {
					deny = false;
				} else {
					final String check2 = node.getLocationControl();
					deny = check2 == null
							|| check1.equals( check2 )
							|| manager.securityCheck( level, check2, "$view_tree" ) == null;
				}
			}
			if (result == null) {
				if (deny) {
					/**
					 * first denied, now we have to create array and put all
					 * entries before current one
					 */
					result = BaseObject.createArray();
					for (int j = 0; j < i; j++) {
						result.add( nodes.get( j ) );
					}
					/**
					 * No need to add current element, it is denied
					 */
				}
				/**
				 * no need to add current element, it is either denied or we
				 * expect returning unmodified list
				 */
			} else {
				if (!deny) {
					result.add( node );
				}
			}
		}
		return result == null
				? nodes
				: result;
	}
	
	/**
	 * @param listing
	 * @param fieldName
	 * @param start
	 * @param end
	 * @return list
	 */
	public static List<ControlBasic<?>> filterByFieldDate(
			final List<ControlBasic<?>> listing,
			final String fieldName,
			final long start,
			final long end) {
		if (listing == null
				|| fieldName == null
				|| start == -1L
				&& end == -1L
				|| listing.isEmpty()
				|| fieldName.length() == 0) {
			return listing;
		}
		final List<ControlBasic<?>> result = new ArrayList<>();
		if (start == -1L) {
			if ("$title".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final long value = Convert.Any.toLong( current.getTitle(), 0L );
					if (value < end) {
						result.add( current );
					}
				}
			} else if ("$key".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final long value = Convert.Any.toLong( current.getKey(), 0L );
					if (value < end) {
						result.add( current );
					}
				}
			} else {
				for (final ControlBasic<?> current : listing) {
					final long value = Convert.MapEntry.toLong( current.getData(), fieldName, 0L );
					if (value < end) {
						result.add( current );
					}
				}
			}
		} else if (end == -1L) {
			if ("$title".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final long value = Convert.Any.toLong( current.getTitle(), 0L );
					if (value >= start) {
						result.add( current );
					}
				}
			} else if ("$key".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final long value = Convert.Any.toLong( current.getKey(), 0L );
					if (value >= start) {
						result.add( current );
					}
				}
			} else {
				for (final ControlBasic<?> current : listing) {
					final long value = Convert.MapEntry.toLong( current.getData(), fieldName, 0L );
					if (value >= start) {
						result.add( current );
					}
				}
			}
		} else {
			if ("$title".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final long value = Convert.Any.toLong( current.getTitle(), 0L );
					if (value >= start && value < end) {
						result.add( current );
					}
				}
			} else if ("$key".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final long value = Convert.Any.toLong( current.getKey(), 0L );
					if (value >= start && value < end) {
						result.add( current );
					}
				}
			} else {
				for (final ControlBasic<?> current : listing) {
					final long value = Convert.MapEntry.toLong( current.getData(), fieldName, 0L );
					if (value >= start && value < end) {
						result.add( current );
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * @param listing
	 * @param fieldName
	 * @param pattern
	 * @param ignoreCase
	 * @param ignorePrefix
	 * @param ignoreSuffix
	 * @return list
	 */
	public static List<ControlBasic<?>> filterByFieldString(
			final List<ControlBasic<?>> listing,
			final String fieldName,
			final String pattern,
			final boolean ignoreCase,
			final boolean ignorePrefix,
			final boolean ignoreSuffix) {
		if (listing == null
				|| fieldName == null
				|| pattern == null
				|| listing.isEmpty()
				|| fieldName.length() == 0
				|| pattern.length() == 0) {
			return listing;
		}
		final List<ControlBasic<?>> result = new ArrayList<>();
		if (ignoreCase) {
			final String patternCheck = pattern.toLowerCase();
			if (ignorePrefix && ignoreSuffix) {
				if ("$title".equals( fieldName )) {
					for (final ControlBasic<?> current : listing) {
						final String value = current.getTitle();
						if (value != null && value.toLowerCase().indexOf( patternCheck ) != -1) {
							result.add( current );
						}
					}
					return result;
				}
				if ("$key".equals( fieldName )) {
					for (final ControlBasic<?> current : listing) {
						final String value = current.getKey();
						if (value != null && value.toLowerCase().indexOf( patternCheck ) != -1) {
							result.add( current );
						}
					}
					return result;
				}
				for (final ControlBasic<?> current : listing) {
					final String value = Base.getString( current.getData(), fieldName, "" );
					if (value != null && value.toLowerCase().indexOf( patternCheck ) != -1) {
						result.add( current );
					}
				}
				return result;
			}
			if (ignorePrefix) {
				if ("$title".equals( fieldName )) {
					for (final ControlBasic<?> current : listing) {
						final String value = current.getTitle();
						if (value != null && value.toLowerCase().endsWith( patternCheck )) {
							result.add( current );
						}
					}
					return result;
				}
				if ("$key".equals( fieldName )) {
					for (final ControlBasic<?> current : listing) {
						final String value = current.getKey();
						if (value != null && value.toLowerCase().endsWith( patternCheck )) {
							result.add( current );
						}
					}
					return result;
				}
				for (final ControlBasic<?> current : listing) {
					final String value = Base.getString( current.getData(), fieldName, "" );
					if (value != null && value.toLowerCase().endsWith( patternCheck )) {
						result.add( current );
					}
				}
				return result;
			}
			if (ignoreSuffix) {
				if ("$title".equals( fieldName )) {
					for (final ControlBasic<?> current : listing) {
						final String value = current.getTitle();
						if (value != null && value.toLowerCase().startsWith( patternCheck )) {
							result.add( current );
						}
					}
					return result;
				}
				if ("$key".equals( fieldName )) {
					for (final ControlBasic<?> current : listing) {
						final String value = current.getKey();
						if (value != null && value.toLowerCase().startsWith( patternCheck )) {
							result.add( current );
						}
					}
					return result;
				}
				for (final ControlBasic<?> current : listing) {
					final String value = Base.getString( current.getData(), fieldName, "" );
					if (value != null && value.toLowerCase().startsWith( patternCheck )) {
						result.add( current );
					}
				}
				return result;
			}
			if ("$title".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final String value = current.getTitle();
					if (value != null && value.toLowerCase().equals( patternCheck )) {
						result.add( current );
					}
				}
				return result;
			}
			if ("$key".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final String value = current.getKey();
					if (value != null && value.toLowerCase().equals( patternCheck )) {
						result.add( current );
					}
				}
				return result;
			}
			for (final ControlBasic<?> current : listing) {
				final String value = Base.getString( current.getData(), fieldName, "" );
				if (value != null && value.toLowerCase().equals( patternCheck )) {
					result.add( current );
				}
			}
			return result;
		}
		if (ignorePrefix && ignoreSuffix) {
			if ("$title".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final String value = current.getTitle();
					if (value != null && value.indexOf( pattern ) != -1) {
						result.add( current );
					}
				}
				return result;
			}
			if ("$key".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final String value = current.getKey();
					if (value != null && value.indexOf( pattern ) != -1) {
						result.add( current );
					}
				}
				return result;
			}
			for (final ControlBasic<?> current : listing) {
				final String value = Base.getString( current.getData(), fieldName, "" );
				if (value != null && value.indexOf( pattern ) != -1) {
					result.add( current );
				}
			}
			return result;
		}
		if (ignorePrefix) {
			if ("$title".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final String value = current.getTitle();
					if (value != null && value.endsWith( pattern )) {
						result.add( current );
					}
				}
				return result;
			}
			if ("$key".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final String value = current.getKey();
					if (value != null && value.endsWith( pattern )) {
						result.add( current );
					}
				}
				return result;
			}
			for (final ControlBasic<?> current : listing) {
				final String value = Base.getString( current.getData(), fieldName, "" );
				if (value != null && value.endsWith( pattern )) {
					result.add( current );
				}
			}
			return result;
		}
		if (ignoreSuffix) {
			if ("$title".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final String value = current.getTitle();
					if (value != null && value.startsWith( pattern )) {
						result.add( current );
					}
				}
				return result;
			}
			if ("$key".equals( fieldName )) {
				for (final ControlBasic<?> current : listing) {
					final String value = current.getKey();
					if (value != null && value.startsWith( pattern )) {
						result.add( current );
					}
				}
				return result;
			}
			for (final ControlBasic<?> current : listing) {
				final String value = Base.getString( current.getData(), fieldName, "" );
				if (value != null && value.startsWith( pattern )) {
					result.add( current );
				}
			}
			return result;
		}
		if ("$title".equals( fieldName )) {
			for (final ControlBasic<?> current : listing) {
				final String value = current.getTitle();
				if (value != null && value.equals( pattern )) {
					result.add( current );
				}
			}
			return result;
		}
		if ("$key".equals( fieldName )) {
			for (final ControlBasic<?> current : listing) {
				final String value = current.getKey();
				if (value != null && value.equals( pattern )) {
					result.add( current );
				}
			}
			return result;
		}
		for (final ControlBasic<?> current : listing) {
			final String value = Base.getString( current.getData(), fieldName, "" );
			if (value != null && value.equals( pattern )) {
				result.add( current );
			}
		}
		return result;
	}
	
	/**
	 * @param path
	 * @return actor
	 */
	public final static ControlActor<?> getCommonActor(final String path) {
		return new CommonActor( path );
	}
	
	/**
	 * @param object
	 * @return string
	 */
	public static String getIcon(final Object object) {
		if (object == null) {
			return "";
		}
		if (object instanceof ControlBasic<?>) {
			final String icon = ((ControlBasic<?>) object).getIcon();
			if (icon != null) {
				return icon;
			}
			if (object instanceof ControlNode<?>) {
				return "container-unknown";
			}
			if (object instanceof ControlEntry<?>) {
				return "object-unknown";
			}
			if (object instanceof ControlForm<?>) {
				return "form-unknown";
			}
			if (object instanceof ControlCommand<?>) {
				return "command-unknown";
			}
			return "item-unknown";
		}
		if (object instanceof Entry) {
			final Entry entry = (Entry) object;
			if (entry.isContainer()) {
				return "container-unknown";
			}
			if (entry.isMount()) {
				return "share-public";
			}
			if (entry.isBinary()) {
				/**
				 * check contentType
				 */
				return "object-unknown";
			}
			return "object-unknown";
		}
		return null;
	}
	
	/**
	 * @param ctx
	 * @return actor
	 */
	public static final ControlActor<?> getQuickActor(final ExecProcess ctx) {
		return Context.getServer( ctx ).getControlQuickActor();
	}
	
	/**
	 * @param ctx
	 * @param path
	 * @param basic
	 * @return boolean
	 */
	public static boolean isAccessible(final ExecProcess ctx, final String path, final ControlBasic<?> basic) {
		if (basic == null || path == null) {
			return false;
		}
		if (basic instanceof ControlCommand<?>) {
			return ControlSAPI.isAccessibleCommand( ctx, path, (ControlCommand<?>) basic );
		}
		if (basic instanceof ControlNode<?>) {
			return ControlSAPI.isAccessibleNode( ctx, path, (ControlNode<?>) basic );
		}
		if (basic instanceof ControlEntry<?>) {
			return ControlSAPI.isAccessibleEntry( ctx, path, (ControlEntry<?>) basic );
		}
		return true;
	}
	
	/**
	 * @param process
	 * @param path
	 * @param command
	 * @return boolean
	 */
	public static boolean isAccessibleCommand(
			final ExecProcess process,
			final String path,
			final ControlCommand<?> command) {
		if (command == null) {
			return false;
		}
		final String permission = command.commandPermission();
		if (permission == null || permission.trim().length() == 0) {
			return true;
		}
		final Context context = Context.getContext( process );
		return context.getServer().getAccessManager().securityCheck( context.getSessionState(), path, permission ) != null;
	}
	
	/**
	 * @param process
	 * @param path
	 * @param entry
	 * @return boolean
	 */
	public static boolean isAccessibleEntry(final ExecProcess process, final String path, final ControlEntry<?> entry) {
		if (entry == null) {
			return false;
		}
		if (entry instanceof ControlNode<?>) {
			return ControlSAPI.isAccessibleNode( process, path, (ControlNode<?>) entry );
		}
		final Context context = Context.getContext( process );
		return context.getServer().getAccessManager().securityCheck( context.getSessionState(), path, "$view_contents" ) != null;
	}
	
	/**
	 * @param process
	 * @param path
	 * @param node
	 * @return boolean
	 */
	public static boolean isAccessibleNode(final ExecProcess process, final String path, final ControlNode<?> node) {
		if (node == null) {
			return false;
		}
		final Context context = Context.getContext( process );
		return context.getServer().getAccessManager().securityCheck( context.getSessionState(), path, "$view_tree" ) != null;
	}
	
	/**
	 * @param process
	 * @param path
	 * @param permission
	 * @return boolean
	 */
	public static boolean isAccessiblePermission(final ExecProcess process, final String path, final String permission) {
		if (permission == null || permission.trim().length() == 0) {
			return true;
		}
		final Context context = Context.getContext( process );
		return context.getServer().getAccessManager().securityCheck( context.getSessionState(), path, permission ) != null;
	}
	
	/**
	 * @param object
	 * @return boolean
	 */
	public static boolean isEntry(final Object object) {
		return object instanceof ControlEntry<?>;
	}
	
	/**
	 * @param object
	 * @return boolean
	 */
	public static boolean isForm(final Object object) {
		return object instanceof ControlForm<?>;
	}
	
	/**
	 * @param object
	 * @return boolean
	 */
	public static boolean isListing(final BaseObject object) {
		return object != null && object.baseArray() != null;
	}
	
	/**
	 * @param object
	 * @return boolean
	 */
	public static boolean isNode(final Object object) {
		return object instanceof ControlNode<?>;
	}
	
	/**
	 * @param object
	 * @return boolean
	 */
	public static boolean isUrl(final Object object) {
		return object instanceof URL;
	}
	
	/**
	 * @param ctx
	 * @param entry
	 * @return
	 */
	public static ControlNode<?> nodeForObject(final ExecProcess ctx, final ControlEntry<?> entry) {
		if (entry == null) {
			return null;
		}
		if (entry instanceof ControlNode<?>) {
			return (ControlNode<?>) entry;
		}
		return ControlSAPI.childForPath( Context.getServer( ctx ).getControlRoot(), entry.getLocationControl() );
	}
	
	/**
	 * @param listing
	 * @param fieldName
	 * @param descending
	 * @return list
	 */
	public static List<ControlBasic<?>> sortByFieldDate(
			final List<ControlBasic<?>> listing,
			final String fieldName,
			final boolean descending) {
		if (listing == null || fieldName == null || listing.isEmpty() || fieldName.length() == 0) {
			return listing;
		}
		final List<ControlBasic<?>> result = new ArrayList<>();
		final Comparator<ControlBasic<?>> comparator;
		if ("$title".equals( fieldName )) {
			comparator = descending
					? ControlSAPI.COMPARATOR_BASIC_DATE_TITLE_DESC
					: ControlSAPI.COMPARATOR_BASIC_DATE_TITLE_ASC;
		} else if ("$key".equals( fieldName )) {
			comparator = descending
					? ControlSAPI.COMPARATOR_BASIC_DATE_KEY_DESC
					: ControlSAPI.COMPARATOR_BASIC_DATE_KEY_ASC;
		} else {
			comparator = descending
					? (Comparator<ControlBasic<?>>) new ComparatorBasicDateDesc( fieldName )
					: new ComparatorBasicDateAsc( fieldName );
		}
		result.addAll( listing );
		Collections.sort( result, comparator );
		return result;
	}
	
	/**
	 * @param listing
	 * @param fieldName
	 * @param descending
	 * @return list
	 */
	public static List<ControlBasic<?>> sortByFieldNumeric(
			final List<ControlBasic<?>> listing,
			final String fieldName,
			final boolean descending) {
		if (listing == null || fieldName == null || listing.isEmpty() || fieldName.length() == 0) {
			return listing;
		}
		final List<ControlBasic<?>> result = new ArrayList<>();
		final Comparator<ControlBasic<?>> comparator;
		if ("$title".equals( fieldName )) {
			comparator = descending
					? ControlSAPI.COMPARATOR_BASIC_NUMERIC_TITLE_DESC
					: ControlSAPI.COMPARATOR_BASIC_NUMERIC_TITLE_ASC;
		} else if ("$key".equals( fieldName )) {
			comparator = descending
					? ControlSAPI.COMPARATOR_BASIC_NUMERIC_KEY_DESC
					: ControlSAPI.COMPARATOR_BASIC_NUMERIC_KEY_ASC;
		} else {
			comparator = descending
					? (Comparator<ControlBasic<?>>) new ComparatorBasicNumericDesc( fieldName )
					: new ComparatorBasicNumericAsc( fieldName );
		}
		result.addAll( listing );
		Collections.sort( result, comparator );
		return result;
	}
	
	/**
	 * @param listing
	 * @param fieldName
	 * @param ignoreCase
	 * @param descending
	 * @return list
	 */
	public static List<ControlBasic<?>> sortByFieldString(
			final List<ControlBasic<?>> listing,
			final String fieldName,
			final boolean ignoreCase,
			final boolean descending) {
		if (listing == null || fieldName == null || listing.isEmpty() || fieldName.length() == 0) {
			return listing;
		}
		final List<ControlBasic<?>> result = new ArrayList<>();
		final Comparator<ControlBasic<?>> comparator;
		if ("$title".equals( fieldName )) {
			comparator = descending
					? ignoreCase
							? ControlSAPI.COMPARATOR_BASIC_STRING_CI_TITLE_DESC
							: ControlSAPI.COMPARATOR_BASIC_STRING_CS_TITLE_DESC
					: ignoreCase
							? ControlSAPI.COMPARATOR_BASIC_STRING_CI_TITLE_ASC
							: ControlSAPI.COMPARATOR_BASIC_STRING_CS_TITLE_ASC;
		} else if ("$key".equals( fieldName )) {
			comparator = descending
					? ignoreCase
							? ControlSAPI.COMPARATOR_BASIC_STRING_CI_KEY_DESC
							: ControlSAPI.COMPARATOR_BASIC_STRING_CS_KEY_DESC
					: ignoreCase
							? ControlSAPI.COMPARATOR_BASIC_STRING_CI_KEY_ASC
							: ControlSAPI.COMPARATOR_BASIC_STRING_CS_KEY_ASC;
		} else {
			comparator = descending
					? ignoreCase
							? (Comparator<ControlBasic<?>>) new ComparatorBasicStringCiDesc( fieldName )
							: new ComparatorBasicStringCsDesc( fieldName )
					: ignoreCase
							? (Comparator<ControlBasic<?>>) new ComparatorBasicStringCiAsc( fieldName )
							: new ComparatorBasicStringCsAsc( fieldName );
		}
		result.addAll( listing );
		Collections.sort( result, comparator );
		return result;
	}
	
	/**
	 * @param object
	 * @return string
	 */
	public static String toLine(final Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof byte[]) {
			return "-= binary array (" + ((byte[]) object).length + " bytes) =-";
		}
		if (object instanceof TransferBuffer) {
			return "-= transfer buffer (" + ((TransferBuffer) object).remaining() + " bytes remaining) =-";
		}
		if (object instanceof TransferCopier) {
			return "-= transfer copier (" + ((TransferCopier) object).length() + " bytes) =-";
		}
		if (object instanceof BaseMessage) {
			final BaseMessage message = (BaseMessage) object;
			final BaseObject attributes = message.getAttributes();
			final String type = message.isCharacter()
					? "text message, "
							+ Format.Compact.toDecimal( ((BodyAccessCharacter) message).getCharacterContentLength() )
							+ " chars"
					: message.isBinary()
							? "binary message, "
									+ Format.Compact.toBytes( ((BodyAccessBinary) message).getBinaryContentLength() )
									+ " bytes"
							: message.isFile()
									? "file message, "
											+ Format.Compact.toBytes( message.getFile().length() )
											+ " bytes"
									: message.isEmpty()
											? "empty message"
											: "other message";
			return attributes == null || !Base.hasKeys( attributes )
					? "-= " + type + " =-"
					: "-= " + type + " (attributes: " + Ecma.toEcmaSourceCompact( attributes ) + "') =-";
		}
		if (object instanceof Date) {
			return Format.Web.date( (Date) object );
		}
		if (object instanceof BasePrimitive<?>) {
			final String string = Ecma.toEcmaSourceCompact( (BaseObject) object );
			return string.length() > 100
					? string.substring( 0, 100 ) + "..."
					: string;
		}
		if (object instanceof Number || object instanceof Boolean || object instanceof CharSequence) {
			/**
			 * just for EcmaFormat ^^^
			 */
			return ControlSAPI.toLine( Base.forUnknown( object ) );
		}
		if (object instanceof Value<?>) {
			final Object baseValue = ((Value<?>) object).baseValue();
			if (baseValue != object) {
				return ControlSAPI.toLine( baseValue );
			}
		}
		if (object instanceof BaseMap) {
			return Format.Describe.toEcmaSource( (BaseMap) object, "" );
		}
		{
			final String string = Ecma.toEcmaSourceCompact( object.toString() );
			return string.length() > 100
					? string.substring( 0, 100 ) + "..."
					: string;
		}
	}
	
	/**
	 * @param object
	 * @param limit
	 * @return string
	 */
	public static String toLine(final Object object, final int limit) {
		if (object == null) {
			return null;
		}
		if (object instanceof byte[]) {
			return "-= binary array (" + ((byte[]) object).length + " bytes) =-";
		}
		if (object instanceof TransferBuffer) {
			return "-= transfer buffer (" + ((TransferBuffer) object).remaining() + " bytes remaining) =-";
		}
		if (object instanceof TransferCopier) {
			return "-= transfer copier (" + ((TransferCopier) object).length() + " bytes) =-";
		}
		if (object instanceof BaseMessage) {
			final BaseMessage message = (BaseMessage) object;
			return "-= binary data (attributes: " + Ecma.toEcmaSourceCompact( message.getAttributes() ) + "') =-";
		}
		if (object instanceof Date) {
			return Format.Web.date( (Date) object );
		}
		if (object instanceof BasePrimitive<?>) {
			final String string = Ecma.toEcmaSourceCompact( (BaseObject) object );
			return string.length() > limit
					? string.substring( 0, limit ) + "..."
					: string;
		}
		if (object instanceof Number || object instanceof Boolean || object instanceof CharSequence) {
			/**
			 * just for EcmaFormat ^^^
			 */
			return ControlSAPI.toLine( Base.forUnknown( object ), limit );
		}
		if (object instanceof BaseMap) {
			final String string = Format.Describe.toEcmaSource( (BaseMap) object, "" );
			return string.length() > limit
					? string.substring( 0, limit ) + "..."
					: string;
		}
		if (object instanceof Value<?>) {
			final Object baseValue = ((Value<?>) object).baseValue();
			if (baseValue != object) {
				return ControlSAPI.toLine( baseValue, limit );
			}
		}
		final String string = object.toString();
		return string.length() > limit
				? string.substring( 0, limit ) + "..."
				: string;
	}
	
	private ControlSAPI() {
		// prevent
	}
	
	@Override
	public String toString() {
		return "[object ControlSAPI]";
	}
}
