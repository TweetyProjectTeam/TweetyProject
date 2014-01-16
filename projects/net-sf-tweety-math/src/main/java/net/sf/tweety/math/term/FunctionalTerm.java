package net.sf.tweety.math.term;

import java.util.*;

/**
 * Instances of this class represent a functional term on some inner term.
 * 
 * @author Matthias Thimm
 */
public abstract class FunctionalTerm extends Term {

	/**
	 * The inner term of this operation.
	 */
	private Term term;
	
	/**
	 * Creates a new functional term with the given inner term.
	 * @param term a term
	 */
	public FunctionalTerm(Term term){
		this.term = term;
	}
	
	/**
	 * Returns the inner term.
	 * @return the inner term.
	 */
	public Term getTerm(){
		return this.term;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#collapseAssociativeOperations()
	 */
	@Override
	public void collapseAssociativeOperations() {
		this.term.collapseAssociativeOperations();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#expandAssociativeOperations()
	 */
	@Override
	public void expandAssociativeOperations(){
		this.term.expandAssociativeOperations();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getMinimums()
	 */
	@Override
	public Set<Minimum> getMinimums() {
		return this.term.getMinimums();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getProducts()
	 */
	@Override
	public Set<Product> getProducts() {
		return this.term.getProducts();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getVariables()
	 */
	@Override
	public Set<Variable> getVariables() {
		return this.term.getVariables();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getAbsoluteValues()
	 */
	@Override
	public Set<AbsoluteValue> getAbsoluteValues(){
		return this.term.getAbsoluteValues();		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isInteger()
	 */
	@Override
	public boolean isInteger() {
		return this.term.isInteger();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toLinearForm()
	 */
	@Override
	public Sum toLinearForm() throws IllegalArgumentException{
		throw new IllegalArgumentException("The term '" + this + "' cannot be brought into linear form because it is non-linear.");
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#replaceTerm(net.sf.tweety.math.term.Term, net.sf.tweety.math.term.Term)
	 */
	@Override
	public abstract Term replaceTerm(Term toSubstitute, Term substitution);
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toString()
	 */
	@Override
	public abstract String toString();


	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#value()
	 */
	@Override
	public abstract Constant value() throws IllegalArgumentException;

}
