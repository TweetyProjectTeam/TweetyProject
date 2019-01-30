package net.sf.tweety.arg.adf.semantics;

import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.commons.AbstractInterpretation;

/**
 * TODO: inherit from a common AbstractArgumentationInterpretation class, which is currently not possible since the one in net.sf.tweety.arg.dung is not general enough.
 * 
 * @author Mathias Hofer
 *
 */
public class Model extends AbstractInterpretation<AbstractDialecticalFramework, Argument>{

	@Override
	public boolean satisfies(Argument formula) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean satisfies(AbstractDialecticalFramework beliefBase) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

}
