package net.sf.tweety.math.func;

import java.util.Vector;

import net.sf.tweety.math.term.Term;

/**
 * A real-valued function.
 * 
 * @author Matthias Thimm
 *
 */
public interface SimpleRealValuedFunction extends SimpleFunction<Vector<Double>,Double>{
	
	public Term getTerm(Vector<Term> element);
}
