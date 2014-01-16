package net.sf.tweety.math.term;

import java.util.*;

public abstract class AssociativeOperation extends Term {
	
	/**
	 * The terms of this operation.
	 */
	private List<Term> terms;
	
	/**
	 * Creates a new (empty) operation.
	 */
	public AssociativeOperation(){
		this.terms = new LinkedList<Term>();
	}
	
	/**
	 * Creates a new operation with the given terms.
	 * @param first a term.
	 * @param second a term.
	 */
	public AssociativeOperation(Term first, Term second){
		this();
		this.terms.add(first);
		this.terms.add(second);
	}
	
	/**
	 * Creates a new operation with the given list of terms.
	 * @param terms a list of terms.
	 */
	public AssociativeOperation(Collection<? extends Term> terms){
		this();
		this.terms.addAll(terms);
	}
	

	/**
	 * Returns the number of the terms in this operation.
	 * @return the number of the terms in this operation.
	 */
	public int size(){
		return this.terms.size();
	}
	
	/**
	 * Returns the terms of this operation.
	 * @return the terms of this operation.
	 */
	public List<Term> getTerms(){
		return this.terms;
	}
	
	/**
	 * Adds the given term to this operation.
	 * @param t a term.
	 */
	public void addTerm(Term t){
		this.terms.add(t);
	}
	
	/**
	 * Adds all the given terms of this operation.
	 * @param terms a collection of terms.
	 */
	public void addAllTerm(Collection<Term> terms){
		this.terms.addAll(terms);
	}
	
	/**
	 * Removes the given term from this operation. 
	 * @param t a term.
	 */
	public void removeTerm(Term t){
		this.terms.remove(t);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isInteger()
	 */
	@Override
	public boolean isInteger(){
		for(Term t: this.terms)
			if(!t.isInteger())
				return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getVariables()
	 */
	@Override
	public Set<Variable> getVariables(){
		Set<Variable> variables = new HashSet<Variable>();
		for(Term t: this.terms)
			variables.addAll(t.getVariables());		
		return variables;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getProducts()
	 */
	@Override
	public Set<Product> getProducts(){
		Set<Product> products = new HashSet<Product>();
		for(Term t: this.terms)
			products.addAll(t.getProducts());
		return products;
	}		
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getProducts()
	 */
	@Override
	public Set<Minimum> getMinimums(){
		Set<Minimum> minimums = new HashSet<Minimum>();
		for(Term t: this.terms)
			minimums.addAll(t.getMinimums());		
		return minimums;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getAbsoluteValues()
	 */
	@Override
	public Set<AbsoluteValue> getAbsoluteValues(){
		Set<AbsoluteValue> avs = new HashSet<AbsoluteValue>();
		for(Term t: this.terms)
			avs.addAll(t.getAbsoluteValues());		
		return avs;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#collapseAssociativeOperations()
	 */
	@Override
	public void collapseAssociativeOperations(){
		List<Term> terms = new LinkedList<Term>(this.getTerms());
		for(Term t: terms){
			t.collapseAssociativeOperations();
			if(t.getClass().equals(this.getClass())){
				this.removeTerm(t);
				this.addAllTerm(((AssociativeOperation) t).getTerms());
			}				
		}		
	}
	
}
