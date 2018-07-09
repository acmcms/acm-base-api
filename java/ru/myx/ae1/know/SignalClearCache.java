/**
 *
 */
package ru.myx.ae1.know;

import java.util.Collections;

import ru.myx.ae1.control.MultivariantString;
import java.util.function.Function;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.command.SimpleCommand;
import ru.myx.ae3.exec.Exec;

final class SignalClearCache extends SimpleCommand implements Function<Void, Object> {
	
	private static final BaseObject STR_CLEAR_CACHE = MultivariantString.getString("Clear whole cache", Collections.singletonMap("ru", "Сброс системного кеша"));

	SignalClearCache() {
		this.setAttributeIntern("id", "clear_cache");
		this.setAttributeIntern("title", SignalClearCache.STR_CLEAR_CACHE);
		this.recalculate();
		this.setCommandPermission("clear_cache");
		this.setCommandIcon("command-dispose");
	}

	@Override
	public Object apply(final Void arg) {
		
		final Server server = Context.getServer(Exec.currentProcess());
		server.getCache().clear();
		server.logQuickTaskUsage("ACM_ROOT_COMMAND_CLEAR_CACHE", BaseObject.UNDEFINED);
		return MultivariantString.getString("All cached elements were discarded!", Collections.singletonMap("ru", "Системные кэши успешно очищены!"));
	}

	@Override
	public String getTitle() {
		
		return SignalClearCache.STR_CLEAR_CACHE.toString();
	}
}
