/**
 *
 */
package ru.myx.ae1.storage;

import java.util.Iterator;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.help.Convert;

/** @author myx */
public abstract class AbstractSchedule implements BaseSchedule {

	private static final String PREFFIX = "---";

	private final BaseObject schedules = new BaseNativeObject();

	private final BaseObject schedulesOriginal = new BaseNativeObject();

	private final boolean newSchedule;

	/**
	 *
	 */
	protected AbstractSchedule() {

		this.newSchedule = true;
	}

	/** @param newSchedule
	 * @param original
	 */
	protected AbstractSchedule(final boolean newSchedule, final BaseSchedule original) {

		if (original == null) {
			this.newSchedule = false;
		} else {
			this.newSchedule = newSchedule;
			original.scheduleFill(this, true);
			this.schedulesOriginal.baseDefineImportAllEnumerable(this.schedules);
		}
	}

	@Override
	public final void clear() {

		this.schedules.baseClear();
	}

	@Override
	public abstract void commit();

	@Override
	public boolean isEmpty() {

		return !Base.hasKeys(this.schedules);
	}

	@Override
	public final void schedule(final String name, final boolean replace, final long date, final String command, final BaseObject parameters) {

		final String key = AbstractSchedule.PREFFIX + name;
		this.scheduleGuid(key, replace, date, command, parameters);
	}

	@Override
	public final void scheduleCancel(final String name) {

		this.schedules.baseDelete(AbstractSchedule.PREFFIX + name);
	}

	@Override
	public final void scheduleCancelGuid(final String key) {

		this.schedules.baseDelete(key);
	}

	@Override
	public void scheduleFill(final BaseSchedule schedule, final boolean replace) {

		if (!this.newSchedule) {
			for (final Iterator<String> key = this.schedulesOriginal.baseKeysOwn(); key.hasNext();) {
				final String keyString = key.next();
				if (!Base.hasProperty(this.schedules, keyString)) {
					if (keyString.startsWith(AbstractSchedule.PREFFIX)) {
						schedule.scheduleCancel(keyString.substring(AbstractSchedule.PREFFIX.length()));
					} else {
						schedule.scheduleCancelGuid(keyString);
					}
				}
			}
		}
		for (final Iterator<String> key = this.schedules.baseKeysOwn(); key.hasNext();) {
			final String keyString = key.next();
			final BaseObject data = this.schedules.baseGet(keyString, BaseObject.UNDEFINED);
			final long date = Convert.MapEntry.toLong(data, "date", 0L);
			final String command = Base.getString(data, "command", "");
			final BaseObject parameters = data.baseGet("parameters", BaseObject.UNDEFINED);
			if (keyString.startsWith(AbstractSchedule.PREFFIX)) {
				final String name = keyString.substring(AbstractSchedule.PREFFIX.length());
				schedule.schedule(name, replace, date, command, parameters);
			} else {
				schedule.scheduleGuid(
						this.newSchedule
							? Engine.createGuid()
							: keyString,
						replace,
						date,
						command,
						parameters);
			}
		}
	}

	@Override
	public final void scheduleGuid(final String key, final boolean replace, final long date, final String command, final BaseObject parameters) {

		if (replace || !Base.hasProperty(this.schedules, key)) {
			this.schedules.baseDefine(//
					key,
					new BaseNativeObject().putAppend("date", Base.forDateMillis(date)).putAppend("command", command).putAppend("parameters", parameters)//
			);
		}
	}
}
