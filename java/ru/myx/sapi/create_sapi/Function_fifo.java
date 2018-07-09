/**
 *
 */
package ru.myx.sapi.create_sapi;

import java.util.LinkedList;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import ru.myx.ae3.base.BaseFunctionAbstract;
import ru.myx.ae3.base.BaseLinkedList;
import ru.myx.ae3.base.BaseLinkedListLimited;
import ru.myx.ae3.base.BaseList;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.ExecCallableBoth;

/** @author myx */
public final class Function_fifo extends BaseFunctionAbstract implements ExecCallableBoth.NativeJ1 {

	@Override
	public BaseList<Object> callNJ1(@Nullable final BaseObject instance, @NotNull final BaseObject argument) {

		final int limit = argument.baseToJavaInteger();
		if (limit == 0) {
			return new BaseLinkedList<>();
		}
		if (limit < 1) {
			throw new IllegalArgumentException("limit is less than 1!");
		}
		return new BaseLinkedListLimited<>(limit);
	}

	@Override
	public Class<? extends Object> execResultClassJava() {

		return LinkedList.class;
	}

}
