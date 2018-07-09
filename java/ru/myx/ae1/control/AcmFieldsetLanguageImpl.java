package ru.myx.ae1.control;

import java.util.function.Function;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.eval.CompileTargetMode;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.eval.LanguageImpl;
import ru.myx.ae3.exec.Instruction;
import ru.myx.ae3.exec.ModifierArgumentA30IMM;
import ru.myx.ae3.exec.OperationsA10;
import ru.myx.ae3.exec.ProgramAssembly;
import ru.myx.ae3.exec.ResultHandler;

/*
 * Created on 03.09.2005
 */
final class AcmFieldsetLanguageImpl implements LanguageImpl {
	
	@Override
	public final void compile(final String identity, final Function<String, String> folder, final String name, final ProgramAssembly assembly, final CompileTargetMode mode)
			throws Evaluate.CompilationException {
		
		final String source = folder.apply(name);
		if (source == null) {
			return;
		}
		final Instruction instruction = OperationsA10.XFLOAD_P.instruction( //
				new ModifierArgumentA30IMM(ControlFieldset.materializeFieldset(source)),
				0,
				mode == CompileTargetMode.INLINE
					? ResultHandler.FA_BNN_NXT
					: ResultHandler.FC_PNN_RET);
		assembly.addInstruction(instruction);
	}

	@Override
	public String[] getAssociatedAliases() {
		
		return new String[]{
				//
				"ACM.FIELDSET", //
				"XML.FIELDSET", //
				"XML->FIELDSET", //
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
		
		return "ACM.FIELDSET";
	}
}
