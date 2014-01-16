package net.sf.tweety.math.func;

import java.util.Vector;

import net.sf.tweety.math.term.Logarithm;
import net.sf.tweety.math.term.Term;

/**
 * The entropy function.
 * @author Matthias Thimm
 */
public class EntropyFunction implements RealValuedFunction {

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.Function#eval(java.lang.Object)
	 */
	@Override
	public Double eval(Vector<Double> x) {
		Double result = 0d;
		for(Double d: x){
			if(d < 0)
				throw new IllegalArgumentException("Entropy is undefined if negative elements are present.");
			if(d > 0)
				result -= Math.log(d) * d;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.RealValuedFunction#getTerm(java.util.Vector)
	 */
	@Override
	public Term getTerm(Vector<Term> element) {
		Term result = null;
		for(Term t: element){
			if(result == null)
				result = t.mult(new Logarithm(t));
			else result = result.minus(t.mult(new Logarithm(t)));
		}
		return result;
	}

}
