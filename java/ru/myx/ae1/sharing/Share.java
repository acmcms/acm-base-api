/*
 * Created on 08.06.2004
 */
package ru.myx.ae1.sharing;

import java.util.Comparator;

import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.know.Language;
import ru.myx.ae3.reflect.Reflect;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * @param <T>
 */
public interface Share<T extends Share<?>> extends ControlBasic<T> {
	/**
	 * 
	 */
	public static final BaseObject			PROTOTYPE			= new BaseNativeObject( Reflect.classToBasePrototype( Share.class ) );
	
	/**
     * 
     */
	public static final int						LM_NONE				= -1;
	
	/**
     * 
     */
	public static final int						LM_AUTO				= 0;
	
	/**
     * 
     */
	public static final int						LM_LANG				= 1;
	
	/**
	 * priority share comparator
	 */
	public static final Comparator<Share<?>>	COMPARATOR_SHARE	= new ComparatorShare();
	
	/**
	 * @return access type
	 */
	public AccessType getAccessType();
	
	/**
	 * @return string
	 */
	public String getAlias();
	
	/**
	 * @return auth type
	 */
	public AuthType getAuthType();
	
	/**
	 * @return boolean
	 */
	public boolean getCommandMode();
	
	@Override
	public BaseObject getData();
	
	@Override
	public String getKey();
	
	/**
	 * Returns NULL when language mode is not static
	 * 
	 * @return
	 */
	public Language getlanguage();
	
	/**
	 * @return integer
	 */
	public int getLanguageMode();
	
	/**
	 * @return string
	 */
	public String getLanguageName();
	
	/**
	 * @return string
	 */
	public String getPath();
	
	/**
	 * @return secure type
	 */
	public SecureType getSecureType();
	
	/**
	 * @return string
	 */
	public String getSkinner();
	
	@Override
	public String getTitle();
	
	@Override
	public String toString();
}
