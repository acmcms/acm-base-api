package ru.myx.ae1.know;

import java.util.function.Function;

import ru.myx.ae3.act.Act;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ProgramAssembly;
import ru.myx.ae3.exec.ProgramPart;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.EntryBinary;
import ru.myx.ae3.vfs.Storage;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.renderer.ecma.AcmEcmaLanguageImpl;
import ru.myx.sapi.ApplicationSAPI;

/** @author myx */
public abstract class AbstractZoneServer extends AbstractServer implements ZoneServer {
	
	/** @param server
	 * @param context
	 */
	public static final void acmJailInitializeRootContext(final ZoneServer server, final ExecProcess context) {
		
		final Entry global = Storage.PUBLIC.relative("resources/lib/ru.acmcms.internal/domain/domain-global.js", null);
		if (global == null) {
			Report.info("DOMAIN", "domain: " + server.getZoneId() + ", init domain-global.js: not found");
			return;
		}
		{
			final EntryBinary binary = global.toBinary();
			if (Report.MODE_DEBUG) {
				Report.info("DOMAIN", "domain: " + server.getZoneId() + ", init domain-global.js: " + Format.Compact.toBytes(binary.getBinaryContentLength()) + "bytes");
			}
			try {
				final ProgramAssembly assembly = new ProgramAssembly();
				Evaluate.compileProgramInline(
						AcmEcmaLanguageImpl.INSTANCE,
						"public, domain-global.js initialisation script",
						binary.getBinaryContent().baseValue().toString(),
						assembly);
				final ExecProcess ctx = Exec.createProcess(context, "public domain-global.js initialisation context");
				ctx.vmFrameEntryExCode();
				ctx.vmScopeDeriveLocals();
				ctx.contextCreateMutableBinding("server", Base.forUnknown(server), false);
				Act.run(ctx, new Function<ProgramPart, BaseObject>() {

					@Override
					public BaseObject apply(ProgramPart program) {

						return program.execCallPreparedInilne(ctx);
					}
				}, assembly.toProgram(0));
				if (Report.MODE_DEBUG) {
					Report.debug("DOMAIN", "domain: " + server.getZoneId() + ", domain initialization finished.");
				}
			} catch (final Throwable e) {
				Report.exception("DOMAIN", "While executing domain-global.js for: " + server.getZoneId(), e);
			}
		}
	}
	
	/**
	 *
	 */
	protected Entry vfsZone;
	
	/**
	 *
	 */
	protected Entry vfsZoneLib;
	
	/** @param id
	 * @param mainDomain
	 * @param context
	 */
	protected AbstractZoneServer(final String id, final String mainDomain, final ExecProcess context) {
		
		super(id, mainDomain, context);
		final ExecProcess rootContext = this.getRootContext();
		final BaseObject global = rootContext.ri10GV;
		// Rewrite own copies of global objects.
		// Pointless to double-init same prototypes, can't use replacements due
		// to java singleton PROTOTYPEs
		// TODO: Ecma.setupGlobalObject( global );
		{
			final ApplicationSAPI applicationApi = new ApplicationSAPI(this);
			global.baseDefine("Application", applicationApi, BaseProperty.ATTRS_MASK_WND);
			global.baseDefine("ApplicationAPI", applicationApi, BaseProperty.ATTRS_MASK_NEN);
		}
	}
	
	@Override
	public Entry getVfsZoneEntry() {
		
		{
			final Entry entry = this.vfsZone;
			if (entry != null) {
				return entry;
			}
		}
		synchronized (this) {
			{
				final Entry entry = this.vfsZone;
				if (entry != null) {
					return entry;
				}
			}
			{
				final Entry folder = this.getVfsRootEntry();
				final Entry entry = folder.relativeFolderEnsure("site/" + this.zoneId);
				return this.vfsZone = entry;
			}
		}
	}
	
	@Override
	public Entry getVfsZoneLibEntry() {
		
		{
			final Entry entry = this.vfsZoneLib;
			if (entry != null) {
				return entry;
			}
		}
		synchronized (this) {
			{
				final Entry entry = this.vfsZoneLib;
				if (entry != null) {
					return entry;
				}
			}
			{
				final Entry folder = this.getVfsZoneEntry();
				final Entry entry = folder.relative("lib", TreeLinkType.PUBLIC_TREE_REFERENCE);
				return this.vfsZoneLib = entry;
			}
		}
	}
	
	@Override
	public Entry requireResolveVfsEntry(final String pathName) {
		
		{
			final Entry zoneLib = this.getVfsZoneLibEntry();
			if (zoneLib != null) {
				final Entry vfsEntry = zoneLib.relative(pathName, null);
				if (vfsEntry != null) {
					return vfsEntry;
				}
			}
		}
		{
			final Entry vfsEntry = Storage.UNION_LIB.relative(pathName, null);
			if (vfsEntry != null) {
				return vfsEntry;
			}
		}
		return null;
	}
	
	@Override
	public Entry requireResolveVfsModule(final String moduleName) {
		
		{
			final Entry entry = this.requireResolveVfsEntry(moduleName + ".js");
			if (entry != null) {
				return entry;
			}
		}
		if (!moduleName.endsWith("/index")) {
			{
				final Entry entry = this.requireResolveVfsEntry(moduleName + "/index.js");
				if (entry != null) {
					return entry;
				}
			}
		}
		return null;
	}
}
