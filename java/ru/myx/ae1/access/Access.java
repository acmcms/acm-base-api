/*
 * Created on 30.04.2004
 */
package ru.myx.ae1.access;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.access.AccessPermission;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.control.ControlLookupStatic;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class Access {
	
	
	/**
	 * 
	 */
	public static final BaseHostLookup AUTHORIZATION_TYPES;
	
	/**
	 * 
	 */
	public static final AccessManager DEFAULT_MANAGER;
	
	/**
	 * 
	 */
	public static final BaseHostLookup GROUP_OR_ANY;
	
	/**
	 * 
	 */
	public static final BaseHostLookup GROUPS;
	
	/**
	 * 
	 */
	public static final BaseHostLookup HM_USERS;
	
	/**
	 * 
	 */
	private static final BaseObject PERM_SECURITY;
	
	/**
	 * 
	 */
	private static final BaseObject PERM_VIEW_CONTENTS;
	
	static {
		HM_USERS = new BaseHostLookup() {
			
			
			@Override
			public BaseObject baseGetLookupValue(final BaseObject key) {
				
				
				final AccessUser<?> U = Context.getServer(Exec.currentProcess()).getAccessManager().getUser(String.valueOf(key), false);
				return U != null
					? Base.forString(U.getLogin() + " (" + U.getEmail() + ")")
					: key;
			}
			
			@Override
			public Iterator<String> baseKeysOwn() {
				
				
				final AccessUser<?>[] users = Context.getServer(Exec.currentProcess()).getAccessManager()
						.searchByType(UserTypes.UT_HANDMADE, UserTypes.UT_HANDMADE, SortMode.SM_EMAIL);
				final List<String> keys = new ArrayList<>(users.length);
				for (final AccessUser<?> U : users) {
					keys.add(U.getKey());
				}
				return keys.iterator();
			}
			
			@Override
			public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
				
				
				final AccessUser<?>[] users = Context.getServer(Exec.currentProcess()).getAccessManager()
						.searchByType(UserTypes.UT_HANDMADE, UserTypes.UT_HANDMADE, SortMode.SM_EMAIL);
				final List<BasePrimitive<?>> keys = new ArrayList<>(users.length);
				for (final AccessUser<?> U : users) {
					keys.add(Base.forString(U.getKey()));
				}
				return keys.iterator();
			}
			
			@Override
			public String toString() {
				
				
				return "[Lookup: Users]";
			}
		};
		
		AUTHORIZATION_TYPES = new ControlLookupStatic()
				.putAppend(
						"" + AuthLevels.AL_UNAUTHORIZED,
						MultivariantString.getString("No authorization (anyone)", Collections.singletonMap("ru", "Нет авторизации (любой пользователь)")))
				.putAppend(
						"" + AuthLevels.AL_AUTHORIZED_AUTOMATICALLY,
						MultivariantString.getString("Automatically (no login if known)", Collections.singletonMap("ru", "Автоматическая (по возможности без логина)")))
				.putAppend(
						"" + AuthLevels.AL_AUTHORIZED_NORMAL,
						MultivariantString.getString(
								"Normal (web-form, including 3rd party auth)",
								Collections.singletonMap("ru", "Нормальная (форма на сайте, включая авторизацию через сторонние сервисы)")))
				.putAppend(
						"" + AuthLevels.AL_AUTHORIZED_NORMAL_LOCAL,
						MultivariantString.getString("Normal (web-form, local login and password)", Collections.singletonMap("ru", "Нормальная (форма на сайте)")))
				.putAppend(
						"" + AuthLevels.AL_AUTHORIZED_HIGH,
						MultivariantString.getString("High (protocol auth, local login, high password)", Collections.singletonMap("ru", "Высокая (средствами протокола)")))
				.putAppend("" + AuthLevels.AL_AUTHORIZED_SYSTEM_EXCLUSIVE, MultivariantString.getString("System (not user applicable)", Collections.singletonMap(
						"ru",
						"Системная (непременимая к пользователю)")));
		
		PERM_SECURITY = MultivariantString.getString("View/Modify security settings", Collections.singletonMap("ru", "Просмотр/Изменение настроек безопасности"));
		
		PERM_VIEW_CONTENTS = MultivariantString.getString("View node contents", Collections.singletonMap("ru", "Отображение содержимого папки"));
		
		DEFAULT_MANAGER = new AnomymousAccessManager();
		
		GROUPS = new LookupGroups(false);
		
		GROUP_OR_ANY = new LookupGroups(true);
	}
	
	/**
	 * @return permissions
	 */
	public static final AccessPermissions createPermissionsDefault() {
		
		
		final AccessPermissions result = Access.createPermissionsEmpty();
		result.addPermission("$view_contents", Access.PERM_VIEW_CONTENTS);
		result.addPermission("$modify_security", Access.PERM_SECURITY);
		return result;
	}
	
	/**
	 * @return permissions
	 */
	public static final AccessPermissions createPermissionsEmpty() {
		
		
		return new AbstractAccessPermissions() {
			// empty
		};
	}
	
	/**
	 * @return permissions
	 */
	public static final AccessPermissions createPermissionsLocal() {
		
		
		final AccessPermissions result = Access.createPermissionsDefault();
		final AccessPermission[] common = Context.getServer(Exec.currentProcess()).getCommonPermissions();
		if (common != null) {
			for (final AccessPermission element : common) {
				result.addPermission(element);
			}
		}
		result.addPermission("$view_contents", Access.PERM_VIEW_CONTENTS);
		result.addPermission("$modify_security", Access.PERM_SECURITY);
		return result;
	}
	
	/**
	 * @param manager
	 * @param login
	 * @param password
	 * @param passwordType
	 * @return
	 */
	public static final AccessUser<?> getUserByLoginCheckPassword(final AccessManager manager, final String login, final String password, final PasswordType passwordType) {
		
		
		if (login == null) {
			return null;
		}
		final String loginCorrect = login.trim().toLowerCase();
		final AccessUser<?> user = manager.getUserByLogin(loginCorrect, false);
		if (user == null) {
			Report.audit("ACCESS", "LOGIN-DENIED", "login=" + loginCorrect + ", reason: no such user!");
			return null;
		}
		if (!user.checkPassword(password, passwordType)) {
			Report.audit("ACCESS", "LOGIN-DENIED", "login=" + loginCorrect + ", reason: wrong password!");
			return null;
		}
		if (user.getType() == UserTypes.UT_HALF_REGISTERED) {
			user.setActive();
			user.groupAdd(manager.getGroup("def.registered", true));
			user.commit();
			Report.event("ACCESS", "FIRST_LOGIN", "login=" + loginCorrect + ", userid=" + user.getKey());
		}
		Report.audit("ACCESS", "LOGIN-GRANTED", "login=" + loginCorrect + ", userid=" + user.getKey());
		return user;
	}
	
	private Access() {
		// ignore
	}
}
