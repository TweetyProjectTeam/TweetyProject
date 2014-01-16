package net.sf.tweety.math.term;

import net.sf.tweety.math.*;

/**
 * This class represents a the nth root function
 * 
 * @author Matthias Thimm
 */
public class Root extends FunctionalTerm {

	/** The base of the root */
	private Term base;
	
	/**
	 * Creates a new square root.
	 * @param term the term inside the square root
	 */
	public Root(Term term) {
		this(term,new IntegerConstant(2));
	}
	
	/**
	 * Creates a new root for the given base.
	 * @param term the term inside the square root
	 * @param base the base of the root
	 */
	public Root(Term term, Term base) {
		super(term);
		this.base = base;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.FunctionalTerm#replaceTerm(net.sf.tweety.math.term.Term, net.sf.tweety.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution) {
		return new Root(this.getTerm().replaceTerm(toSubstitute, substitution),this.base);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.FunctionalTerm#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.getTerm().toString() + ")**(1.0/" + this.base + ")";
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.FunctionalTerm#value()
	 */
	@Override
	public Constant value() throws IllegalArgumentException {
		return new FloatConstant(Math.pow(this.getTerm().value().doubleValue(), 1.0/this.base.doubleValue()));
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
		return new Root(this.getTerm().simplify(),this.base);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#derive(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException {
		throw new RuntimeException("Implement me");
	}

}
