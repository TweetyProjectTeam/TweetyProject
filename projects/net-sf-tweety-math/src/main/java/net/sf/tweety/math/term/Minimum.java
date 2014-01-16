package net.sf.tweety.math.term;

import java.util.*;

import net.sf.tweety.math.*;

/**
 * This class models the minimum of two terms.
 * @author Matthias Thimm
 */
public class Minimum extends AssociativeOperation{

	private static final String MINIMUM_STRING_REPRESENTATION = "min"; 
	
	/**
	 * Creates a new minimum with the given terms.
	 * @param first a term.
	 * @param second a term.
	 */
	public Minimum(Term first, Term second){
		super(first,second);
	}
	
	/**
	 * Creates a new minimum with the given list of terms.
	 * @param terms a list of terms.
	 */
	public Minimum(List<Term> terms){
		super(terms);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#value()
	 */
	@Override
	public Constant value(){
		Constant value = new IntegerConstant(1);
		for(Term t: this.getTerms()){
			Constant tValue = t.value();
			if((value instanceof IntegerConstant) && (tValue instanceof IntegerConstant))
				value = (((IntegerConstant)value).getValue() < ((IntegerConstant)tValue).getValue())?(value):(tValue);
			else if((value instanceof IntegerConstant) && (tValue instanceof FloatConstant))			
				value = (((IntegerConstant)value).getValue() < ((FloatConstant)tValue).getValue())?(value):(tValue);
			else if((value instanceof FloatConstant) && (tValue instanceof IntegerConstant))			
				value = (((FloatConstant)value).getValue() < ((IntegerConstant)tValue).getValue())?(value):(tValue);
			else if((value instanceof FloatConstant) && (tValue instanceof FloatConstant))			
				value = (((FloatConstant)value).getValue() < ((FloatConstant)tValue).getValue())?(value):(tValue);
			else throw new IllegalArgumentException("Unrecognized atomic term type.");					
		}
		return value;	
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#replaceTerm(net.sf.tweety.math.term.Term, net.sf.tweety.math.term.Term)
	 */
	@Override
	public Term replaceTerm(Term toSubstitute, Term substitution){
		if(toSubstitute == this)
			return substitution;
		List<Term> newTerms = new ArrayList<Term>();
		for(Term t: this.getTerms())
			newTerms.add(t.replaceTerm(toSubstitute, substitution));
		return new Minimum(newTerms);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#getMinimums()
	 */
	@Override
	public Set<Minimum> getMinimums(){
		Set<Minimum> minimums = super.getMinimums();
		minimums.add(this);
		return minimums;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#expandAssociativeOperations()
	 */
	@Override
	public void expandAssociativeOperations(){
		while(this.size() > 2){
			Term t1 = this.getTerms().get(0);
			Term t2 = this.getTerms().get(1);
			this.removeTerm(t1);
			this.removeTerm(t2);
			Minimum m = new Minimum(t1,t2);
			this.add(m);			
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toLinearForm()
	 */
	@Override
	public Sum toLinearForm() throws IllegalArgumentException{
		throw new IllegalArgumentException("The term '" + this + "' cannot be brought into linear form because it is non-linear.");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#derive(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException{
		throw new NonDifferentiableException();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		List<Term> terms =new ArrayList<Term>();
		Constant c = null;
		for(Term t: this.getTerms()){
			t = t.simplify();
			if(t instanceof Constant){
				if(c == null)
					c = (Constant) t;
				else c = (c.doubleValue() > t.doubleValue())?((Constant)t):(c);
			}else terms.add(t);
		}
		if( c != null)
			terms.add(c);
		if(terms.size() == 1) return terms.get(0);
		return new Minimum(terms);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isContinuous(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public boolean isContinuous(Variable v){
		for(Term t: this.getTerms())
			if(!t.isContinuous(v))
				return false;
		return true;		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toString()
	 */
	@Override
	public String toString(){
		String result = "";
		for(Term t: this.getTerms())
			if(result.equals(""))
				result += Minimum.MINIMUM_STRING_REPRESENTATION + "{" + t;
			else result += "," + t; 
		return result + "}";
	}
	
}
