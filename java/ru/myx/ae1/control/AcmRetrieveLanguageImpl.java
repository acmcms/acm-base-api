package ru.myx.ae1.control;

import java.util.function.Function;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.eval.CompileTargetMode;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.eval.LanguageImpl;
import ru.myx.ae3.exec.ProgramAssembly;

/*
 * Created on 03.09.2005
 */
final class AcmRetrieveLanguageImpl implements LanguageImpl {

	@Override
	public final void compile(final String identity, final Function<String, String> folder, final String name, final ProgramAssembly assembly, final CompileTargetMode mode)
			throws Evaluate.CompilationException {
		
		final String source = folder.apply(name);
		if (source == null) {
			return;
		}
		assembly.addInstruction(new AcmRetrieveRenderer(ControlFieldset.materializeFieldset(source)));
	}

	@Override
	public String[] getAssociatedAliases() {

		return new String[]{
				//
				"ACM.RETRIEVE", //
				"XML.RETRIEVE", //
				"XML->RETRIEVE", //
		};
	}

	@Override
	public String[] getAssociatedExtensions() {

		return null;
	}

	@Override
	public String[] getAssociatedMimeTypes() {

		return null;
	}

	@Override
	public String getKey() {

		return "ACM.RETRIEVE";
	}
}
