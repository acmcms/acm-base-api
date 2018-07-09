package ru.myx.ae1.control;

import java.util.Iterator;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostEmpty;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.base.BasePrimitiveString;
import ru.myx.ae3.control.field.AbstractField;
import ru.myx.ae3.exec.Exec;

/**
 * interfaces Description: Copyright: Copyright (c) 2001
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
public class FieldBaseOwner extends AbstractField<FieldBaseOwner, String, BasePrimitiveString> {
	
	
	static final class OwnerLookup extends BaseHostLookup {
		
		
		static final class UserParameterRetriever extends BaseHostEmpty {
			
			
			private final Object userID;
			
			private final BaseObject def;
			
			private final String[] parameterList;
			
			UserParameterRetriever(final Object userID, final BaseObject def, final String[] parameterList) {
				this.userID = userID;
				this.def = def;
				this.parameterList = parameterList;
			}
			
			@Override
			public final BasePrimitiveString baseToString() {
				
				
				final Server server = Context.getServer(Exec.currentProcess());
				final AccessUser<?> user = server.getAccessManager().getUser(this.userID.toString(), true);
				if (user == null) {
					return this.def.baseToString();
				}
				Object value = null;
				for (final String element : this.parameterList) {
					if (element.equalsIgnoreCase("LOGIN")) {
						if ((value = user.getLogin()) != null) {
							break;
						}
					}
				}
				if (value == null) {
					return this.def.baseToString();
				}
				return Base.forString(value.toString());
			}
			
			@Override
			public final String toString() {
				
				
				final Server server = Context.getServer(Exec.currentProcess());
				final AccessUser<?> user = server.getAccessManager().getUser(this.userID.toString(), true);
				if (user == null) {
					return this.def.baseToJavaString();
				}
				Object value = null;
				for (final String element : this.parameterList) {
					if (element.equalsIgnoreCase("LOGIN")) {
						if ((value = user.getLogin()) != null) {
							break;
						}
					}
				}
				if (value == null) {
					return this.def.baseToJavaString();
				}
				return value.toString();
			}
		}
		
		static final class UserProfileParameterRetriever extends BaseHostEmpty {
			
			
			private final String[] parameterList;
			
			private final BaseObject def;
			
			private final Object userID;
			
			private final String profileName;
			
			UserProfileParameterRetriever(final String[] parameterList, final BaseObject def, final Object userID, final String profileName) {
				this.parameterList = parameterList;
				this.def = def;
				this.userID = userID;
				this.profileName = profileName;
			}
			
			@Override
			public final BasePrimitiveString baseToString() {
				
				
				final Server server = Context.getServer(Exec.currentProcess());
				final AccessUser<?> user = server.getAccessManager().getUser(this.userID.toString(), true);
				if (user == null) {
					return this.def.baseToString();
				}
				final BaseObject profile = user.getProfile(this.profileName, false);
				if (profile == null) {
					return this.def.baseToString();
				}
				for (final String element : this.parameterList) {
					final BaseObject value = profile.baseGet(element, BaseObject.UNDEFINED);
					assert value != null : "NULL java object";
					if (value != BaseObject.UNDEFINED) {
						final String result = value.toString();
						if (result.length() > 0) {
							return Base.forString(result);
						}
					}
				}
				return this.def.baseToString();
			}
			
			@Override
			public final String toString() {
				
				
				final Server server = Context.getServer(Exec.currentProcess());
				final AccessUser<?> user = server.getAccessManager().getUser(this.userID.toString(), true);
				if (user == null) {
					return this.def.baseToJavaString();
				}
				final BaseObject profile = user.getProfile(this.profileName, false);
				if (profile == null) {
					return this.def.baseToJavaString();
				}
				for (final String element : this.parameterList) {
					final BaseObject value = profile.baseGet(element, BaseObject.UNDEFINED);
					assert value != null : "NULL java object";
					if (value != BaseObject.UNDEFINED) {
						final String result = value.toString();
						if (result.length() > 0) {
							return result;
						}
					}
				}
				return this.def.baseToJavaString();
			}
		}
		
		private final static BaseObject createUserParameterRetriever(final Object userID, final String[] parameterList, final BaseObject def) {
			
			
			if (userID == null) {
				return Base.forString("-");
			}
			return new UserParameterRetriever(userID, def, parameterList);
		}
		
		private final static BaseObject createUserProfileParameterRetriever(final Object userID, final String profileName, final String[] parameterList, final BaseObject def) {
			
			
			if (userID == null) {
				return Base.forString("-");
			}
			return new UserProfileParameterRetriever(parameterList, def, userID, profileName);
		}
		
		private final String[] PROFILE_NAME_LIST = new String[]{
				"Name", "name", "Nick", "nick", "Email", "email"
		};
		
		private final String[] USER_NAME_LIST = new String[]{
				"Login", "login", "Email", "email"
		};
		
		@Override
		public final BaseObject baseGetLookupValue(final BaseObject key) {
			
			
			return OwnerLookup
					.createUserProfileParameterRetriever(
							key,
							"",
							this.PROFILE_NAME_LIST,
							OwnerLookup.createUserParameterRetriever(key, this.USER_NAME_LIST, Base.forString("- unknown -")))//
					/**
					 * TODO: looks like those ^^^^ two classes are not really
					 * needed!
					 */
					.baseToString();
		}
		
		@Override
		public boolean baseHasKeysOwn() {
			
			
			return false;
		}
		
		@Override
		public Iterator<String> baseKeysOwn() {
			
			
			return BaseObject.ITERATOR_EMPTY;
		}
		
		@Override
		public Iterator<? extends CharSequence> baseKeysOwnAll() {
			
			
			return this.baseKeysOwn();
		}
		
		@Override
		public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
			
			
			return BaseObject.ITERATOR_EMPTY_PRIMITIVE;
		}
		
		@Override
		public String toString() {
			
			
			return "[Lookup: User Name Retriever]";
		}
	}
	
	private static final BaseHostLookup USER_NAME_LOOKUP = new OwnerLookup();
	
	FieldBaseOwner() {
		this.setAttributeIntern("type", "string");
		this.setAttributeIntern("lookup", FieldBaseOwner.USER_NAME_LOOKUP);
		this.setAttributeIntern("disabled", true);
		this.recalculate();
	}
	
	@Override
	public FieldBaseOwner cloneField() {
		
		
		return new FieldBaseOwner().setAttributes(this.getAttributes());
	}
	
	@Override
	public BasePrimitiveString dataRetrieve(final BaseObject argument, final BaseObject fieldsetContext) {
		
		
		assert argument != null : "NULL java object";
		return argument == BaseObject.UNDEFINED
			? Base.forString(Context.getUserId(Exec.currentProcess()))
			: argument.baseToString();
	}
	
	@Override
	public BaseObject dataStore(final BaseObject object, final BaseObject fieldsetContext) {
		
		
		return Base.forString(Context.getUserId(Exec.currentProcess()));
	}
	
	@Override
	public String getFieldClass() {
		
		
		return "owner";
	}
	
	@Override
	public String getFieldClassTitle() {
		
		
		return "Owner value";
	}
	
	@Override
	public boolean getFieldTypeAvailability(final String type) {
		
		
		return "transparent".equals(type);
	}
	
	@Override
	public boolean isConstant() {
		
		
		return false;
	}
	
}
