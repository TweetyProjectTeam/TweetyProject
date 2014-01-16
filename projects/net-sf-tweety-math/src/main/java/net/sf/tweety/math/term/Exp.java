package net.sf.tweety.math.term;

import net.sf.tweety.math.*;

/**
 * This class represents an exponential expression by "e".
 * 
 * @author Matthias Thimm
 */
public class Exp extends FunctionalTerm {
	
	/**
	 * Creates a new exponential term with the given term.
	 * @param term the potentiated term.
	 */
	public Exp(Term term) {
		super(term);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.FunctionalTerm#replaceTerm(net.sf.tweety.math.term.Term, net.sf.tweety.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution) {
		return new Exp(this.getTerm().replaceTerm(toSubstitute, substitution));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.FunctionalTerm#toString()
	 */
	@Override
	public String toString() {
		return "exp(" + this.getTerm() + ")";
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.FunctionalTerm#value()
	 */
	@Override
	public Constant value() throws IllegalArgumentException {
		return new FloatConstant(Math.exp(this.getTerm().doubleValue()));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#derive(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException {
		Product t = new Product();
		t.addTerm(this.getTerm().derive(v));
		t.addTerm(this);
		return t;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isContinuous(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v) {
		return this.getTerm().isContinuous(v);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#simplify()
	 */
	@Override
	public Term simplify() {
		return new Exp(this.getTerm().simplify());
	}

}
