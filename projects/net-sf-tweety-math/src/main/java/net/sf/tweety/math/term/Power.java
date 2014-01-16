package net.sf.tweety.math.term;

import net.sf.tweety.math.*;

/**
 * This class represents a term raised to some power.
 * 
 * @author Matthias Thimm
 */
public class Power extends FunctionalTerm {

	/** The power. */ 
	private Term power;
	
	/**
	 * Creates a new power term with the given term and power.
	 * @param term the potentiated term.
	 */
	public Power(Term term, Term power) {
		super(term);
		this.power = power;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.FunctionalTerm#replaceTerm(net.sf.tweety.math.term.Term, net.sf.tweety.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution) {
		return new Power(this.getTerm().replaceTerm(toSubstitute, substitution),this.power.replaceTerm(toSubstitute, substitution));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.FunctionalTerm#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.getTerm().toString() + ")**(" + this.power.toString() + ")";
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.FunctionalTerm#value()
	 */
	@Override
	public Constant value() throws IllegalArgumentException {
		return new FloatConstant(Math.pow(this.getTerm().doubleValue(), this.power.doubleValue()));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isContinuous(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v) {
		return this.getTerm().isContinuous(v) && this.power.isContinuous(v);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#simplify()
	 */
	@Override
	public Term simplify() {
		return new Power(this.getTerm().simplify(),this.power.simplify());
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#derive(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException {
		return new Power(this.getTerm(),this.power).mult(this.power.derive(v).mult(new Logarithm(this.getTerm())).add(new Fraction(this.power.derive(v),this.getTerm()).mult(this.getTerm().derive(v)) ));
	}

}
