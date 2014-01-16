package net.sf.tweety.math.term;

import java.util.*;

/**
 * This class models an abstract constant, e.g. a float or an integer.
 * @author Matthias Thimm
 */
public abstract class Constant extends Term{
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#value()
	 */
	@Override
	public Constant value(){
		return this;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getVariables()
	 */
	@Override
	public Set<Variable> getVariables(){
		return new HashSet<Variable>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getProducts()
	 */
	@Override
	public Set<Product> getProducts(){
		return new HashSet<Product>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getProducts()
	 */
	@Override
	public Set<Minimum> getMinimums(){
		return new HashSet<Minimum>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getAbsoluteValues()
	 */
	@Override
	public Set<AbsoluteValue> getAbsoluteValues(){
		return new HashSet<AbsoluteValue>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#collapseAssociativeOperations()
	 */
	@Override
	public void collapseAssociativeOperations(){
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#expandAssociativeOperations()
	 */
	@Override
	public void expandAssociativeOperations(){
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		return this;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toLinearForm()
	 */
	@Override
	public Sum toLinearForm() throws IllegalArgumentException{
		Sum sum = new Sum();
		Product p = new Product();
		p.addTerm(this);
		sum.addTerm(p);
		return sum;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#derive(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v){		
		return new IntegerConstant(0);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isContinuous(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v){
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#replaceTerm(net.sf.tweety.math.term.Term, net.sf.tweety.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution){
		if(toSubstitute == this)
			return substitution;
		return this;
	}
}
