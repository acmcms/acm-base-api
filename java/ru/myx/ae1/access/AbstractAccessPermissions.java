/*
 * Created on 10.05.2006
 */
package ru.myx.ae1.access;

import java.util.ArrayList;
import java.util.List;

import ru.myx.ae3.access.AccessPermission;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.access.AccessPreset;
import ru.myx.ae3.base.BaseObject;

/**
 * @author myx
 * 
 */
public abstract class AbstractAccessPermissions implements AccessPermissions {
	final List<AccessPermission>	permissions	= new ArrayList<>();
	
	final List<AccessPreset>		presets		= new ArrayList<>();
	
	@Override
	public AccessPermissions addPermission(final AccessPermission permission) {
		synchronized (this.permissions) {
			this.permissions.add( permission );
		}
		return this;
	}
	
	@Override
	public AccessPermissions addPermission(final String key, final BaseObject title) {
		synchronized (this.permissions) {
			this.permissions.add( new AccessPermission() {
				@Override
				public String getKey() {
					return key;
				}
				
				@Override
				public BaseObject getTitle() {
					return title;
				}
				
				@Override
				public boolean isForControl() {
					return true;
				}
			} );
		}
		return this;
	}
	
	@Override
	public AccessPermissions addPermission(final String key, final BaseObject title, final boolean forControl) {
		synchronized (this.permissions) {
			this.permissions.add( new AccessPermission() {
				@Override
				public String getKey() {
					return key;
				}
				
				@Override
				public BaseObject getTitle() {
					return title;
				}
				
				@Override
				public boolean isForControl() {
					return forControl;
				}
			} );
		}
		return this;
	}
	
	@Override
	public AccessPermissions addPreset(final AccessPreset preset) {
		synchronized (this.presets) {
			this.presets.add( preset );
		}
		return this;
	}
	
	@Override
	public AccessPermissions addPreset(final String[] permissions, final BaseObject title) {
		synchronized (this.presets) {
			this.presets.add( new AccessPreset() {
				@Override
				public String[] getPermissions() {
					return permissions;
				}
				
				@Override
				public BaseObject getTitle() {
					return title;
				}
			} );
		}
		return this;
	}
	
	@Override
	public AccessPermission[] getAllPermissions() {
		synchronized (this.permissions) {
			return this.permissions.isEmpty()
					? null
					: this.permissions.toArray( new AccessPermission[this.permissions.size()] );
		}
	}
	
	@Override
	public AccessPreset[] getPresets() {
		synchronized (this.presets) {
			return this.presets.isEmpty()
					? null
					: this.presets.toArray( new AccessPreset[this.presets.size()] );
		}
	}
}
