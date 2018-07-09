/*
 * Created on 15.10.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.sapi;

import ru.myx.ae3.base.BaseHostObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.sapi.create_sapi.Function_array;
import ru.myx.sapi.create_sapi.Function_counter;
import ru.myx.sapi.create_sapi.Function_fifo;
import ru.myx.sapi.create_sapi.Function_formattedRandom;
import ru.myx.sapi.create_sapi.Function_guid;
import ru.myx.sapi.create_sapi.Function_list;
import ru.myx.sapi.create_sapi.Function_map;
import ru.myx.sapi.create_sapi.Function_mapClone;
import ru.myx.sapi.create_sapi.Function_mapFilter;
import ru.myx.sapi.create_sapi.Function_mapFor;
import ru.myx.sapi.create_sapi.Function_message;
import ru.myx.sapi.create_sapi.Function_sessionTransferUrl;
import ru.myx.sapi.create_sapi.Function_set;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CreateSAPI extends BaseHostObject {
	
	
	/**
	 * 
	 */
	public CreateSAPI() {
		
		this.baseDefine("array", new Function_array(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("counter", new Function_counter(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("fifo", new Function_fifo(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("formattedRandom", new Function_formattedRandom(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("guid", new Function_guid(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("list", new Function_list(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("map", new Function_map(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("mapClone", new Function_mapClone(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("mapFilter", new Function_mapFilter(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("mapFor", new Function_mapFor(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("message", new Function_message(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("sessionTransferUrl", new Function_sessionTransferUrl(), BaseProperty.ATTRS_MASK_NNN);
		this.baseDefine("set", new Function_set(), BaseProperty.ATTRS_MASK_NNN);
	}
}
