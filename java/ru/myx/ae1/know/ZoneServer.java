package ru.myx.ae1.know;

import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.vfs.Entry;

/**
 * @author myx
 * 
 */
public interface ZoneServer extends Server {
	
	/**
	 * @param sharings
	 * @return
	 */
	Share<?>[] filterAllowedShares(Share<?>[] sharings);
	
	/**
	 * @param share
	 * @return
	 */
	ControlNode<?> getControlNodeForShare(String share);
	
	/**
	 * 
	 * @return
	 */
	Share<?>[] getSharings();
	
	/**
	 * container at %VFS_ROOT%/site/%ZONE_NAME%
	 * 
	 * This entry supposed to be requested frequently so instead of resolving
	 * its path, it is directly available here.
	 * 
	 * @return
	 */
	Entry getVfsZoneEntry();
	
	/**
	 * container at %ZONE_ENTRY%/lib
	 * 
	 * This entry supposed to be requested frequently so instead of resolving
	 * its path, it is directly available here.
	 * 
	 * @return
	 */
	Entry getVfsZoneLibEntry();
	
	/**
	 * 
	 * @param moduleName
	 * @return
	 */
	Entry requireResolveVfsModule(final String moduleName);
	
	/**
	 * 
	 * @param pathName
	 * @return
	 */
	Entry requireResolveVfsEntry(final String pathName);
}
