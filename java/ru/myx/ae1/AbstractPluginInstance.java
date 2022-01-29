package ru.myx.ae1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import ru.myx.ae1.know.Server;
import ru.myx.ae3.Engine;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.ecma.Ecma;
import ru.myx.ae3.help.Dom;
import ru.myx.ae3.help.Validate;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.xml.Xml;

/**
 * @author myx
 * 		
 */
public abstract class AbstractPluginInstance implements PluginInstance {
	
	private static final File FOLDER_SETTINGS_PRIVATE = new File(new File(Engine.PATH_PRIVATE, "settings"), "plugins");
	
	private static final File FOLDER_SETTINGS_PROTECTED = new File(new File(Engine.PATH_PROTECTED, "settings"), "plugins");
	static {
		AbstractPluginInstance.FOLDER_SETTINGS_PROTECTED.mkdirs();
		AbstractPluginInstance.FOLDER_SETTINGS_PRIVATE.mkdirs();
	}
	
	private final Object settingsLock = new Object();
	
	private Server server;
	
	private File settingsPrivateFile = null;
	
	private long settingsPrivateDate = 0L;
	
	private BaseObject settingsPrivateOriginal = null;
	
	private BaseObject settingsPrivateEffective = null;
	
	private Properties pluginProtectedProperties = null;
	
	private File settingsProtectedFile = null;
	
	private long settingsProtectedDate = 0L;
	
	private BaseObject settingsProtectedOriginal = null;
	
	private BaseObject settingsProtectedEffective = null;
	
	/**
	 * @return map
	 */
	public final BaseObject commitPrivateSettings() {
		
		if (AbstractPluginInstance.FOLDER_SETTINGS_PRIVATE.equals(AbstractPluginInstance.FOLDER_SETTINGS_PROTECTED)) {
			return this.commitProtectedSettings();
		}
		if (this.settingsPrivateOriginal == null) {
			return null;
		}
		if (Validate.mapsEqual(this.settingsPrivateEffective, this.settingsPrivateOriginal)) {
			return this.settingsPrivateEffective;
		}
		synchronized (this.settingsLock) {
			/**
			 * needs to be frozen.
			 */
			final BaseNativeObject newOriginal = new BaseNativeObject();
			newOriginal.baseDefineImportAllEnumerable(this.settingsPrivateEffective);
			//
			this.settingsPrivateOriginal = newOriginal;
			try {
				try (final FileOutputStream out = new FileOutputStream(this.settingsPrivateFile)) {
					Dom.toXmlReadableStream(Xml.toElement("settings", newOriginal, true), out);
				}
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		return this.settingsPrivateEffective;
	}
	
	/**
	 * @return map
	 */
	public final BaseObject commitProtectedSettings() {
		
		if (this.settingsProtectedOriginal == null) {
			return null;
		}
		if (Validate.mapsEqual(this.settingsProtectedEffective, this.settingsProtectedOriginal)) {
			return this.settingsPrivateEffective;
		}
		synchronized (this.settingsLock) {
			/**
			 * needs to be frozen.
			 */
			final BaseNativeObject newOriginal = new BaseNativeObject();
			newOriginal.baseDefineImportAllEnumerable(this.settingsProtectedEffective);
			//
			this.settingsProtectedOriginal = newOriginal;
			try {
				try (final FileOutputStream out = new FileOutputStream(this.settingsProtectedFile)) {
					Dom.toXmlReadableStream(Xml.toElement("settings", newOriginal, true), out);
				}
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		return this.settingsProtectedEffective;
	}
	
	@Override
	public void destroy() {
		
		// empty
	}
	
	/**
	 * @return server
	 */
	public final Server getServer() {
		
		return this.server;
	}
	
	/**
	 * @return map
	 */
	public final BaseObject getSettingsPrivate() {
		
		if (AbstractPluginInstance.FOLDER_SETTINGS_PRIVATE.equals(AbstractPluginInstance.FOLDER_SETTINGS_PROTECTED)) {
			return this.getSettingsProtected();
		}
		if (this.settingsPrivateOriginal == null || this.settingsPrivateDate != 0L && this.settingsPrivateDate != this.settingsPrivateFile.lastModified()) {
			synchronized (this.settingsLock) {
				if (this.settingsPrivateOriginal == null || this.settingsPrivateDate != 0L && this.settingsPrivateDate != this.settingsPrivateFile.lastModified()) {
					final String pluginId = Base.getString(this.getSettingsProtected(), "id", "").trim();
					if (this.settingsPrivateFile == null) {
						final File settingsFolder = new File(AbstractPluginInstance.FOLDER_SETTINGS_PRIVATE, this.server.getZoneId());
						settingsFolder.mkdirs();
						this.settingsPrivateFile = new File(settingsFolder, pluginId + ".xml");
					}
					if (pluginId.length() == 0) {
						this.settingsPrivateEffective = new BaseNativeObject();
						this.settingsPrivateDate = 0L;
						this.settingsPrivateOriginal = new BaseNativeObject();
					} else {
						if (this.settingsPrivateFile.exists()) {
							final BaseObject settings = new BaseNativeObject();
							try {
								Xml.toMap(
										"pluginSettings(" + this.settingsPrivateFile.getName() + ")",
										Transfer.createCopier(this.settingsPrivateFile),
										StandardCharsets.UTF_8,
										null,
										settings,
										null,
										null);
							} catch (final Throwable t) {
								Report.exception(
										"PLUGIN",
										"Error while reading settings, none or partially read",
										"file=" + this.settingsPrivateFile + ", settings=" + Ecma.toEcmaSourceCompact(settings),
										t);
							}
							this.settingsPrivateEffective = new BaseNativeObject(settings);
							this.settingsPrivateDate = this.settingsPrivateFile.lastModified();
							this.settingsPrivateOriginal = settings;
						} else {
							this.settingsPrivateEffective = new BaseNativeObject();
							this.settingsPrivateDate = 0L;
							this.settingsPrivateOriginal = new BaseNativeObject();
						}
					}
				}
			}
		}
		return this.settingsPrivateEffective;
	}
	
	/**
	 * @return map
	 */
	public final BaseObject getSettingsProtected() {
		
		if (this.settingsProtectedOriginal == null || this.settingsProtectedDate != 0L && this.settingsProtectedDate != this.settingsProtectedFile.lastModified()) {
			synchronized (this.settingsLock) {
				if (this.settingsProtectedOriginal == null || this.settingsProtectedDate != 0L && this.settingsProtectedDate != this.settingsProtectedFile.lastModified()) {
					final String pluginId = this.pluginProtectedProperties == null
						? ""
						: this.pluginProtectedProperties.getProperty("id", "").trim();
					if (this.settingsProtectedFile == null) {
						final File settingsFolder = new File(AbstractPluginInstance.FOLDER_SETTINGS_PROTECTED, this.server.getZoneId());
						settingsFolder.mkdirs();
						this.settingsProtectedFile = new File(settingsFolder, pluginId + ".xml");
					}
					if (pluginId.length() == 0) {
						this.settingsProtectedEffective = new BaseNativeObject();
						this.settingsProtectedDate = 0L;
						this.settingsProtectedOriginal = new BaseNativeObject();
					} else {
						if (this.settingsProtectedFile.exists()) {
							final BaseObject settings = new BaseNativeObject();
							try {
								Xml.toMap(
										"pluginSettings(" + this.settingsProtectedFile.getName() + ")",
										Transfer.createCopier(this.settingsProtectedFile),
										StandardCharsets.UTF_8,
										null,
										settings,
										null,
										null);
							} catch (final Throwable t) {
								Report.exception(
										"PLUGIN",
										"Error while reading settings, none or partially read",
										"file=" + this.settingsProtectedFile + ", settings=" + Ecma.toEcmaSourceCompact(settings),
										t);
							}
							this.settingsProtectedEffective = new BaseNativeObject(settings);
							this.settingsProtectedDate = this.settingsProtectedFile.lastModified();
							this.settingsProtectedOriginal = settings;
						} else {
							this.settingsProtectedEffective = new BaseNativeObject();
							this.settingsProtectedDate = 0L;
							this.settingsProtectedOriginal = new BaseNativeObject();
						}
					}
				}
			}
		}
		return this.settingsProtectedEffective;
	}
	
	@Override
	public void register() {
		
		// empty
	}
	
	/**
	 * 
	 */
	public void setup() {
		
		// empty
	}
	
	@Override
	public final void setup(final Server server, final Properties info) throws IllegalArgumentException {
		
		if (info == null) {
			throw new NullPointerException("info is null!");
		}
		this.server = server;
		{
			this.pluginProtectedProperties = info;
			final BaseObject settings = this.getSettingsProtected();
			final BaseObject lastProperties = settings.baseGet("intern-last-properties", BaseObject.UNDEFINED);
			assert lastProperties != null : "NULL java value";
			final BaseObject targetSettings;
			if (Base.hasKeys(lastProperties)) {
				targetSettings = new BaseNativeObject();
				targetSettings.baseDefineImportAllEnumerable(settings);
			} else {
				targetSettings = settings;
			}
			final BaseObject sourceProperties = new BaseNativeObject();
			for (final Object entry : info.keySet()) {
				final String key = String.valueOf(entry);
				final String property = info.getProperty(String.valueOf(key));
				sourceProperties.baseDefine(key, property);
				if (settings.baseGet(key, null) == null) {
					targetSettings.baseDefine(key, property);
				}
			}
			if (Base.hasKeys(lastProperties)) {
				if (Validate.mapsEqual(lastProperties, sourceProperties)) {
					this.settingsProtectedEffective.baseDefineImportAllEnumerable(targetSettings);
				} else {
					this.settingsProtectedEffective.baseDefineImportAllEnumerable(sourceProperties);
				}
			}
			this.settingsProtectedEffective.baseDefine("intern-last-properties", sourceProperties);
			this.commitProtectedSettings();
		}
		this.setup();
	}
	
	@Override
	public void start() {
		
		// empty
	}
	
	@Override
	public String toString() {
		
		return "[plugin " + this.getClass().getName() + "]";
	}
}
