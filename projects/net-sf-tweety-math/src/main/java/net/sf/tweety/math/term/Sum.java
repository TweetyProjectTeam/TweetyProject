/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.math.term;

import java.util.*;

import net.sf.tweety.math.*;

/**
 * This class models a sum of two terms.
 * @author Matthias Thimm
 */
public class Sum extends AssociativeOperation{
	
	/**
	 * Creates a new (empty) sum.
	 */
	public Sum(){
		super();
	}
	
	/**
	 * Creates a new sum with the given terms.
	 * @param first a term.
	 * @param second a term.
	 */
	public Sum(Term first, Term second){
		super(first,second);
	}		
	
	/**
	 * Creates a new sum with the given list of terms.
	 * @param terms a list of terms.
	 */
	public Sum(Collection<? extends Term> terms){
		super(terms);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#value()
	 */
	@Override
	public Constant value(){
		Constant value = new IntegerConstant(0);
		for(Term t: this.getTerms()){
			Constant tValue = t.value();
			if((value instanceof IntegerConstant) && (tValue instanceof IntegerConstant))
				value = new IntegerConstant(((IntegerConstant)value).getValue() + ((IntegerConstant)tValue).getValue());
			else if((value instanceof IntegerConstant) && (tValue instanceof FloatConstant))			
				value = new FloatConstant(((IntegerConstant)value).getValue() + ((FloatConstant)tValue).getValue());
			else if((value instanceof FloatConstant) && (tValue instanceof IntegerConstant))			
				value = new FloatConstant(((FloatConstant)value).getValue() + ((IntegerConstant)tValue).getValue());
			else if((value instanceof FloatConstant) && (tValue instanceof FloatConstant))			
				value = new FloatConstant(((FloatConstant)value).getValue() + ((FloatConstant)tValue).getValue());
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
		return new Sum(newTerms);
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
			Sum m = new Sum(t1,t2);
			this.addTerm(m);			
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toLinearForm()
	 */
	@Override
	public Sum toLinearForm() throws IllegalArgumentException{
		if(!this.isLinear())
			throw new IllegalArgumentException("The term '" + this + "' cannot be brought into linear form because it is non-linear.");
		Sum sum = new Sum();
		for(Term t: this.getTerms())
			sum.addAllTerm(t.toLinearForm().getTerms());		
		// check for variables occurring multiple times
		Stack<Term> terms = new Stack<Term>();
		terms.addAll(sum.getTerms());
		while(!terms.isEmpty()){
			Product p1 = (Product) terms.pop();			
			for(Term t2: terms){
				Product p2 = (Product) t2;
				Product modified = null;
				// distinguish cases
				// NOTE: The products must both contain exactly two elements
				// or each one constant
				if(p1.size() == 1 && p2.size() == 1){
					if(p1.getTerms().get(0) instanceof Constant && p2.getTerms().get(0) instanceof Constant){
						// p1 and p2 consist both of single constants						
						modified = new Product();
						modified.addTerm(p1.getTerms().get(0).add(p2.getTerms().get(0)).value());
					}else throw new IllegalArgumentException("Something is wrong: products are to contain either a constant and a variable or just a constant.");
				}else if(p1.size() == 2 && p2.size() == 2){
					Variable v1 = (Variable) ((p1.getTerms().get(0) instanceof Variable)?(p1.getTerms().get(0)):(p1.getTerms().get(1)));
					Constant c1 = (Constant) ((p1.getTerms().get(0) instanceof Constant)?(p1.getTerms().get(0)):(p1.getTerms().get(1)));
					Variable v2 = (Variable) ((p2.getTerms().get(0) instanceof Variable)?(p2.getTerms().get(0)):(p2.getTerms().get(1)));
					Constant c2 = (Constant) ((p2.getTerms().get(0) instanceof Constant)?(p2.getTerms().get(0)):(p2.getTerms().get(1)));
					if(v1.equals(v2))
						modified = c1.add(c2).value().mult(v1);										
				}
				if(modified != null){
					terms.remove(p2);
					terms.push(modified);
					sum.removeTerm(p1);
					sum.removeTerm(p2);
					sum.addTerm(modified);
					break;				
				}
			}
		}
		return sum;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#derive(net.sf.tweety.math.term.Variable)
	 */
	@Override
	public Term derive(Variable v) throws NonDifferentiableException {
		if(!this.getVariables().contains(v)) return new IntegerConstant(0);
		Sum derivation = new Sum();
		for(Term t: this.getTerms())
			derivation.addTerm(t.derive(v));
		return derivation;
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
	 * @see net.sf.tweety.math.term.Term#simplify()
	 */
	@Override
	public Term simplify(){
		if(this.getTerms().size() == 0) return new IntegerConstant(0);
		if(this.getTerms().size() == 1) return this.getTerms().get(0).simplify();		
		List<Term> terms = new ArrayList<Term>();
		Constant c = null;
		for(Term t: this.getTerms()){
			t = t.simplify();
			if(t instanceof Constant){
				if(t.doubleValue() == 0) continue;
				if(c == null)
					c = (Constant) t;
				else c = new FloatConstant(c.doubleValue() + t.doubleValue());					
			}else terms.add(t);
		}
		if( c != null)
			terms.add(c);
		if(terms.size() == 1) return terms.get(0);
		if(terms.size() == 0) return new IntegerConstant(0);
		return new Sum(terms);		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toString()
	 */
	@Override
	public String toString(){
		String result = "";
		for(Term t: this.getTerms()){
			if(result.equals("")){				
				result += t;
			}else{
				result += " + " + t;				
			}
		}
		return result;
	}
}
