package ru.myx.sapi;

import ru.myx.ae1.access.AccessGroup;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;

/**
 * @author
 * @version 1.0
 */
public final class UserSAPI {
	/**
	 * 
	 */
	public static final UserSAPI	INSTANCE					= new UserSAPI();
	
	/**
     * 
     */
	public static final int			US_UNAUTHORIZED				= AuthLevels.AL_UNAUTHORIZED;
	
	/**
     * 
     */
	public static final int			US_AUTHORIZED_AUTOMATICALLY	= AuthLevels.AL_AUTHORIZED_AUTOMATICALLY;
	
	/**
     * 
     */
	public static final int			US_AUTHORIZED				= AuthLevels.AL_AUTHORIZED_NORMAL;
	
	/**
     * 
     */
	public static final int			US_AUTHORIZED_HIGH			= AuthLevels.AL_AUTHORIZED_HIGH;
	
	/**
	 * @param ctx
	 * @return user identifier
	 */
	public static final String ensureAutomaticAuthorization(final ExecProcess ctx) {
		final Context context = Context.getContext( ctx );
		context.getServer().ensureAuthorization( AuthLevels.AL_AUTHORIZED_AUTOMATICALLY );
		return context.getUserId();
	}
	
	/**
	 * @param process
	 * @return current user's language
	 */
	public static final String getLanguage(final ExecProcess process) {
		return Context.getLanguage( process ).getName();
	}
	
	/**
	 * @param process
	 * @return current user state
	 */
	public static final int getState(final ExecProcess process) {
		return Context.getSessionState( process );
	}
	
	/**
	 * @param ctx
	 * @return current user
	 */
	public static final AccessUser<?> getUser(final ExecProcess ctx) {
		final Context context = Context.getContext( ctx );
		context.ensureAuthorization( AuthLevels.AL_AUTHORIZED_AUTOMATICALLY );
		return context.getUser();
	}
	
	/**
	 * @param process
	 * @return current user identifier
	 */
	public static final String getUserID(final ExecProcess process) {
		return Context.getUserId( process );
	}
	
	/**
	 * 
	 * @param ctx
	 */
	public static final void invalidate(final ExecProcess ctx) {
		final Context context = Context.getContext( ctx );
		context.invalidateUser();
	}
	
	/**
	 * Vlad 21:10
	 * 
	 * а что должно происходить когда я вызываю User.requestAuthorization(); ?
	 * 
	 * Alexander I. Kharitchev 21:10
	 * 
	 * он проверяет надо ли авторизовывать, авторизует или перенеправляет на
	 * логиновую форму.
	 * 
	 * если пользователь уже авторизован до AuthLevels.AL_AUTHORIZED_NORMAL -
	 * авторизовывать не надо
	 * 
	 * @param ctx
	 * @return user identifier
	 */
	public static final String requestAuthorization(final ExecProcess ctx) {
		final Context context = Context.getContext( ctx );
		context.getServer().ensureAuthorization( AuthLevels.AL_AUTHORIZED_NORMAL );
		return context.getUserId();
	}
	
	/**
	 * @param process
	 * @param group
	 * @return user identifier
	 */
	public static final String requestGroup(final ExecProcess process, final Object group) {
		final Context context = Context.getContext( process );
		final Server server = context.getServer();
		server.ensureAuthorization( AuthLevels.AL_AUTHORIZED_NORMAL );
		final String groupId = group instanceof AccessGroup<?>
				? ((AccessGroup<?>) group).getKey()
				: Convert.Any.toString( group, "def.supervisor" );
		if (!server.getAccessManager().isInGroup( context.getUserId(), groupId )) {
			context.invalidateAuth();
			server.ensureAuthorization( AuthLevels.AL_AUTHORIZED_NORMAL );
			throw new RuntimeException( "Authorization error!" );
		}
		return context.getUserId();
	}
	
	/**
	 * @param ctx
	 * @return user identifier
	 */
	public static final String requestHighAuthorization(final ExecProcess ctx) {
		final Context context = Context.getContext( ctx );
		context.getServer().ensureAuthorization( AuthLevels.AL_AUTHORIZED_HIGH );
		return context.getUserId();
	}
	
	/**
	 * @param ctx
	 * @param authType
	 * @param parameters
	 * @return user identifier
	 */
	public static final String requestSpecificAuthentication(
			final ExecProcess ctx,
			final String authType,
			final BaseObject parameters) {
		final Context context = Context.getContext( ctx );
		// TODO: parameters to be put in session
		context.getServer().ensureAuthorization( AuthLevels.AL_AUTHORIZED_AUTOMATICALLY/*
																						 * ,
																						 * parameters
																						 */);
		return context.getUserId();
	}
	
	/**
	 * @param ctx
	 * @param authType
	 * @param parameters
	 * @return user identifier
	 */
	public static final String requestSpecificAuthorization(
			final ExecProcess ctx,
			final String authType,
			final BaseObject parameters) {
		final Context context = Context.getContext( ctx );
		// TODO: parameters to be put in session
		context.getServer().ensureAuthorization( AuthLevels.AL_AUTHORIZED_AUTOMATICALLY/*
																						 * ,
																						 * parameters
																						 */);
		return context.getUserId();
	}
	
	/**
	 * @param ctx
	 * @param language
	 */
	public static final void setLanguage(final ExecProcess ctx, final String language) {
		final Context context = Context.getContext( ctx );
		context.getServer().ensureAuthorization( UserSAPI.US_AUTHORIZED_AUTOMATICALLY );
		final AccessUser<?> user = Context.getUser( ctx );
		user.setLanguage( language );
		user.commit();
	}
	
	/**
     */
	private UserSAPI() {
		// ignore
	}
	
}
