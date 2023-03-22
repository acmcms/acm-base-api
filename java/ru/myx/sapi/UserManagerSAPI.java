/*
 * Created on 30.12.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.sapi;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessGroup;
import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.access.PasswordType;
import ru.myx.ae1.access.SortMode;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecProcess;

/** @author myx */
public final class UserManagerSAPI {

	/**
	 *
	 */
	public static UserManagerSAPI INSTANCE = new UserManagerSAPI();

	/** @see AuthLevels */
	public static final int AUTH_LEVEL_UNAUTHORIZED = AuthLevels.AL_UNAUTHORIZED;

	/** @see AuthLevels */
	public static final int AUTH_LEVEL_AUTOMATICALLY = AuthLevels.AL_AUTHORIZED_AUTOMATICALLY;

	/** @see AuthLevels */
	public static final int AUTH_LEVEL_AUTHORIZED_3RDPARTY = AuthLevels.AL_AUTHORIZED_3RDPARTY;

	/** @see AuthLevels */
	public static final int AUTH_LEVEL_AUTHORIZED = AuthLevels.AL_AUTHORIZED_NORMAL;

	/** @see AuthLevels */
	public static final int AUTH_LEVEL_AUTHORIZED_NORMAL = AuthLevels.AL_AUTHORIZED_NORMAL;

	/** @see AuthLevels */
	public static final int AUTH_LEVEL_AUTHORIZED_HIGH = AuthLevels.AL_AUTHORIZED_HIGH;

	/** @see AuthLevels */
	public static final int AUTH_LEVEL_AUTHORIZED_HIGHER = AuthLevels.AL_AUTHORIZED_HIGHER;

	/** @see AuthLevels */
	public static final int AUTH_LEVEL_SYSTEM_EXCLUSIVE = AuthLevels.AL_AUTHORIZED_SYSTEM_EXCLUSIVE;

	/** All possible chars for representing a number as a String */
	private static final char[] digits = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
	};

	private static final int MIN_RADIX = 2;

	private static final int MAX_RADIX = 10 + 26 + 26;

	/** @param ctx
	 * @param password
	 * @return boolean */
	public static boolean checkPassword(final ExecProcess ctx, final String password) {

		final Context context = Context.getContext(ctx);
		if (context.getSessionState() < AuthLevels.AL_AUTHORIZED_AUTOMATICALLY) {
			return false;
		}
		final String userId = context.getUserId();
		final AccessManager manager = context.getServer().getAccessManager();
		final String login = manager.getUser(userId, true).getLogin();
		return Access.getUserByLoginCheckPassword(manager, login, password, PasswordType.NORMAL) != null;
	}

	/** @param ctx
	 * @param userId
	 * @return */
	public static final boolean deleteUserByID(final ExecProcess ctx, final String userId) {

		return Context.getServer(ctx).getAccessManager().deleteUser(userId);
	}

	/** @param ctx
	 * @param login
	 * @return */
	public static final boolean deleteUserByLogin(final ExecProcess ctx, final String login) {

		if (login == null || login.isBlank()) {
			return false;
		}
		final AccessUser<?>[] ids = Context.getServer(ctx).getAccessManager().search(login, null, -1, -1, SortMode.SM_LOGIN);
		for (int i = ids.length - 1; i >= 0; --i) {
			final AccessUser<?> user = ids[i];
			if (user != null) {
				if (login.equals(user.getLogin())) {
					Context.getServer(ctx).getAccessManager().deleteUser(user.getKey());
				}
			}
		}
		return ids.length > 0;
	}

	/** @param ctx
	 * @param mail
	 * @return */
	public static final boolean deleteUserByMail(final ExecProcess ctx, final String mail) {

		if (mail == null || mail.isBlank()) {
			return false;
		}
		final AccessManager accessManager = Context.getServer(ctx).getAccessManager();
		final AccessUser<?>[] ids = accessManager.search(null, mail, -1, -1, SortMode.SM_LOGIN);
		for (int i = ids.length - 1; i >= 0; --i) {
			final AccessUser<?> user = ids[i];
			if (user != null) {
				if (mail.equals(user.getEmail())) {
					accessManager.deleteUser(user.getKey());
				}
			}
		}
		return ids.length > 0;
	}

	/** @return password */
	public static final String generatePassword() {

		return UserManagerSAPI.toString(46656 + (Engine.createRandom() & 0x7FFFFFFF) % 1632960, 36)
				+ UserManagerSAPI.toString(46656 + (Engine.createRandom() & 0x7FFFFFFF) % 1632960, 36);
	}

	/** @param length
	 * @param smallLetters
	 * @param bigLetters
	 * @return password */
	public static final String generatePassword(final int length, final boolean smallLetters, final boolean bigLetters) {

		final StringBuilder result = new StringBuilder();
		final int base = smallLetters && bigLetters
			? UserManagerSAPI.MAX_RADIX
			: smallLetters || bigLetters
				? 36
				: 10;
		for (int left = length; left > 0;) {
			final int current = left > 5
				? 5
				: left;
			left -= current;
			final int add = (int) Math.pow(base, current - 1);
			final int mod = (int) Math.pow(base, current) - add;
			result.append(UserManagerSAPI.toString(add + (Engine.createRandom() & 0x7FFFFFFF) % mod, base));
		}
		if (bigLetters && !smallLetters) {
			return result.toString().toUpperCase();
		}
		return result.toString();
	}

	/** @param ctx
	 * @return group array */
	public static AccessGroup<?>[] getAllGroups(final ExecProcess ctx) {

		return Context.getServer(ctx).getAccessManager().getAllGroups();
	}

	/** @param ctx
	 * @param groupId
	 * @param create
	 * @return group */
	public static AccessGroup<?> getGroup(final ExecProcess ctx, final String groupId, final boolean create) {

		return Context.getServer(ctx).getAccessManager().getGroup(groupId, create);
	}

	/** @param ctx
	 * @param userId
	 * @return user */
	public static AccessUser<?> getUser(final ExecProcess ctx, final String userId) {

		return Context.getServer(ctx).getAccessManager().getUser(userId, true);
	}

	/** @param ctx
	 * @param userId
	 * @param create
	 * @return user */
	public static AccessUser<?> getUser(final ExecProcess ctx, final String userId, final boolean create) {

		return Context.getServer(ctx).getAccessManager().getUser(userId, create);
	}

	/** @param ctx
	 * @param login
	 * @return user */
	public static AccessUser<?> getUserByLogin(final ExecProcess ctx, final String login) {

		return Context.getServer(ctx).getAccessManager().getUserByLogin(login, true);
	}

	/** @param ctx
	 * @param login
	 * @param create
	 * @return user */
	public static AccessUser<?> getUserByLogin(final ExecProcess ctx, final String login, final boolean create) {

		return Context.getServer(ctx).getAccessManager().getUserByLogin(login, create);
	}

	/** @param ctx
	 * @param data
	 * @return string
	 * @throws Exception */
	public static final String registerUser(final ExecProcess ctx, final BaseObject data) throws Exception {

		final String login = Base.getString(data, "login", "").trim().toLowerCase();
		if (login.length() > 0) {
			final String email = Base.getString(data, "email", "").trim().toLowerCase();
			final String password = Base.getString(data, "password", UserManagerSAPI.generatePassword()).trim();
			return Context.getServer(ctx).registerUser(null, login, email, password, data);
		}
		return null;
	}

	/** @param ctx
	 * @param guid
	 * @param login
	 * @param email
	 * @param password
	 * @param data
	 * @return string
	 * @throws Exception */
	public static final String registerUser(final ExecProcess ctx, final String guid, final String login, final String email, final String password, final BaseObject data)
			throws Exception {

		if (login.length() > 0) {
			final String passwordToUse = password == null
				? UserManagerSAPI.generatePassword()
				: password.trim();
			return Context.getServer(ctx).registerUser(guid, login, email.toLowerCase(), passwordToUse, data);
		}
		return null;
	}

	/** @param ctx
	 * @param login
	 * @param email
	 * @param lastLogonStart
	 * @param lastLogonEnd
	 * @return user array */
	public static AccessUser<?>[] searchUsers(final ExecProcess ctx, final String login, final String email, final long lastLogonStart, final long lastLogonEnd) {

		return Context.getServer(ctx).getAccessManager().search(login, email, lastLogonStart, lastLogonEnd, SortMode.SM_LOGIN);
	}

	/** Returns a string representation of the first argument in the radix specified by the second
	 * argument.
	 * <p>
	 * If the radix is smaller than <code>Character.MIN_RADIX</code> or larger than
	 * <code>Character.MAX_RADIX</code>, then the radix <code>10</code> is used instead.
	 * <p>
	 * If the first argument is negative, the first element of the result is the ASCII minus
	 * character <code>'-'</code> (<code>'&#92;u002D'</code>). If the first argument is not
	 * negative, no sign character appears in the result.
	 * <p>
	 * The remaining characters of the result represent the magnitude of the first argument. If the
	 * magnitude is zero, it is represented by a single zero character <code>'0'</code>
	 * (<code>'&#92;u0030'</code>); otherwise, the first character of the representation of the
	 * magnitude will not be the zero character. The following ASCII characters are used as digits:
	 * <blockquote>
	 *
	 * <pre>
	 *
	 *
	 *                                         0123456789abcdefghijklmnopqrstuvwxyz
	 *
	 *
	 * </pre>
	 *
	 * </blockquote> These are <code>'&#92;u0030'</code> through <code>'&#92;u0039'</code> and
	 * <code>'&#92;u0061'</code> through <code>'&#92;u007A'</code>. If <code>radix</code> is
	 * <var>N</var>, then the first <var>N</var> of these characters are used as radix-<var>N</var>
	 * digits in the order shown. Thus, the digits for hexadecimal (radix 16) are
	 * <code>0123456789abcdef</code>. If uppercase letters are desired, the
	 * {@link java.lang.String#toUpperCase()} method may be called on the result: <blockquote>
	 *
	 * <pre>
	 * Integer.toString(n, 16).toUpperCase()
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * @param i
	 *            an integer to be converted to a string.
	 * @param radix
	 *            the radix to use in the string representation.
	 * @return a string representation of the argument in the specified radix.
	 * @see java.lang.Character#MAX_RADIX
	 * @see java.lang.Character#MIN_RADIX */
	private static String toString(int i, final int radix) {

		if (radix < UserManagerSAPI.MIN_RADIX || radix > UserManagerSAPI.MAX_RADIX) {
			return Integer.toString(i);
		}
		/* Use the faster version */
		if (radix == 10) {
			return Integer.toString(i);
		}
		final char buf[] = new char[33];
		final boolean negative = i < 0;
		int charPos = 32;
		if (!negative) {
			i = -i;
		}
		while (i <= -radix) {
			buf[charPos--] = UserManagerSAPI.digits[-(i % radix)];
			i /= radix;
		}
		buf[charPos] = UserManagerSAPI.digits[-i];
		if (negative) {
			buf[--charPos] = '-';
		}
		return new String(buf, charPos, 33 - charPos);
	}

	/**
	 */
	private UserManagerSAPI() {

		//
	}
}
