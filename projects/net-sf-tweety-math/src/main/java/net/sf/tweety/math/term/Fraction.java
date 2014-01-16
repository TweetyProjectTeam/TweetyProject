package net.sf.tweety.math.term;

import java.util.*;

import net.sf.tweety.math.*;

/**
 * Instances of this class represent fractions of two terms.
 * 
 * @author Matthias Thimm
 */
public class Fraction extends Term {

	/**
	 * The nominator of the fraction.
	 */
	private Term nominator;

	/**
	 * The denominator of the fraction.
	 */
	private Term denominator;
	
	/**
	 * Creates a new fraction of the two given terms.
	 * @param nominator a term.
	 * @param denominator a term.
	 */
	public Fraction(Term nominator, Term denominator){
		this.nominator = nominator;
		this.denominator = denominator;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#collapseAssociativeOperations()
	 */
	@Override
	public void collapseAssociativeOperations() {
		this.nominator.collapseAssociativeOperations();
		this.denominator.collapseAssociativeOperations();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#derive(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException {
		if(!this.getVariables().contains(v)) return new IntegerConstant(0);
		Term newNominator = this.nominator.derive(v).mult(this.denominator).minus(this.nominator.mult(this.denominator.derive(v)));
		Term newDenominator = this.denominator.mult(this.denominator);
		Fraction derivation = new Fraction(newNominator,newDenominator);
		return derivation;
	}

	@Override
	public void expandAssociativeOperations() {
		this.nominator.expandAssociativeOperations();
		this.denominator.expandAssociativeOperations();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getAbsoluteValues()
	 */
	@Override
	public Set<AbsoluteValue> getAbsoluteValues() {
		Set<AbsoluteValue> result = this.nominator.getAbsoluteValues();
		result.addAll(this.denominator.getAbsoluteValues());
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getMinimums()
	 */
	@Override
	public Set<Minimum> getMinimums() {
		Set<Minimum> result = this.nominator.getMinimums();
		result.addAll(this.denominator.getMinimums());
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getProducts()
	 */
	@Override
	public Set<Product> getProducts() {
		Set<Product> result = this.nominator.getProducts();
		result.addAll(this.denominator.getProducts());
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getVariables()
	 */
	@Override
	public Set<Variable> getVariables() {
		Set<Variable> result = this.nominator.getVariables();
		result.addAll(this.denominator.getVariables());
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isInteger()
	 */
	@Override
	public boolean isInteger() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isContinuous(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v){
		return this.nominator.isContinuous(v) && this.denominator.isContinuous(v);		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#replaceTerm(net.sf.tweety.math.term.Term, net.sf.tweety.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution) {
		return new Fraction(this.nominator.replaceTerm(toSubstitute, substitution),this.denominator.replaceTerm(toSubstitute, substitution));
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		Term nominator = this.nominator.simplify();
		Term denominator = this.denominator.simplify();
		if(nominator instanceof Constant && denominator instanceof Constant)
			return new FloatConstant(nominator.doubleValue()/denominator.doubleValue());
		if(nominator instanceof Constant){
			if(nominator.doubleValue() == 0)
				return nominator;
			return new Fraction(nominator,denominator);
		}
		if(denominator instanceof Constant)
			return new FloatConstant(1/denominator.doubleValue()).mult(nominator).simplify();		
		return new Fraction(nominator,denominator);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toLinearForm()
	 */
	@Override
	public Sum toLinearForm() throws IllegalArgumentException {
		throw new IllegalArgumentException("This term cannot be brought into linear form.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.nominator.toString() + ")/(" + this.denominator.toString() + ")";
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#value()
	 */
	@Override
	public Constant value() throws IllegalArgumentException {
		double nom = (this.nominator.value() instanceof IntegerConstant)?(((IntegerConstant)this.nominator.value()).getValue()):(((FloatConstant)this.nominator.value()).getValue());
		double denom = (this.denominator.value() instanceof IntegerConstant)?(((IntegerConstant)this.denominator.value()).getValue()):(((FloatConstant)this.denominator.value()).getValue());
		return new FloatConstant(nom/denom);
	}

}
