package ru.myx.ae1.session;

import java.util.concurrent.ConcurrentHashMap;

import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.status.StatusInfo;

/**
 * @author myx
 * 
 */
public final class SessionManagerDefault implements SessionManagerImpl {
	private int											stsCacheRequests	= 0;
	
	private int											stsCacheHits		= 0;
	
	private final ConcurrentHashMap<String, BaseMap>	sessions			= new ConcurrentHashMap<>();
	
	@Override
	public BaseMap session(final String sid) {
		this.stsCacheRequests++;
		{
			final BaseMap result = this.sessions.get( sid );
			if (result != null) {
				this.stsCacheHits++;
				return result;
			}
		}
		{
			final BaseMap session = new BaseNativeObject();
			final BaseMap result = this.sessions.putIfAbsent( sid, session );
			if (result != session) {
				this.stsCacheHits++;
			}
			return session;
		}
	}
	
	@Override
	public BaseMap sessionIfExists(final String sid) {
		this.stsCacheRequests++;
		{
			final BaseMap result = this.sessions.get( sid );
			if (result != null) {
				this.stsCacheHits++;
				return result;
			}
		}
		return null;
	}
	
	@Override
	public void statusFill(final StatusInfo status) {
		status.put( "Cache size", Format.Compact.toDecimal( this.sessions.size() ) );
		status.put( "Cache requests", Format.Compact.toDecimal( this.stsCacheRequests ) );
		status.put( "Cache hits", Format.Compact.toDecimal( this.stsCacheHits ) );
	}
}
