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
package net.sf.tweety.math.opt;

import java.util.*;

import net.sf.tweety.math.equation.*;
import net.sf.tweety.math.term.*;


/**
 * This class models a general constraint satisfaction problem.
 * @author Matthias Thimm
 */
public class ConstraintSatisfactionProblem extends HashSet<Statement>{
	
	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new and empty csp.
	 */
	public ConstraintSatisfactionProblem(){
		super();
	}
	
	/**
	 * Creates a new csp with the given statements
	 * @param statements a collection of statements.
	 */
	public ConstraintSatisfactionProblem(Collection<? extends Statement> statements){
		super(statements);
	}
	
	/**
	 * Normalizes this problem, i.e. every constraint is brought into
	 * an equivalent form "T > 0" or "T >= 0" or "T = 0" or "T != 0". 
	 * @return a csp.
	 */
	public ConstraintSatisfactionProblem toNormalizedForm(){
		ConstraintSatisfactionProblem csp = new ConstraintSatisfactionProblem();
		for(Statement s: this)
			csp.add(s.toNormalizedForm());
		return csp;
	}
		
	/**
	 * Checks whether every constraint of this problem is linear.
	 * @return "true" if every constraint of this problem is linear.
	 */
	public boolean isLinear(){
		try{
		for(Statement s: this)
			if(!s.getLeftTerm().isLinear() || !s.getRightTerm().isLinear())
				return false;
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Checks whether this problem is integer, i.e. whether all variables
	 * are integer variables.
	 * @return "true" if this problem is integer.
	 */
	public boolean isInteger(){		
		for(Variable v: this.getVariables())
			if(!(v instanceof IntegerVariable))
				return false;
		return true;		
	}
	
	/**
	 * Checks whether this problem uses no minimum function.
	 * @return "true" if this problem uses no minimum function.
	 */
	public boolean isMinimumFree(){
		for(Statement s: this){
			if(!s.getLeftTerm().getMinimums().isEmpty())
				 return false;
			if(!s.getRightTerm().getMinimums().isEmpty())
				 return false;
		}
		return true;
	}

	/**
	 * Resolves all occurrences of minimums by substituting
	 * a minimum "min{a,b}" by "0.5 a + 0.5 b - abs(a-b)".
	 */
	public void resolveMinimums(){
		// expand all minimums		
		for(Statement s: this)
			s.expandAssociativeOperations();
		// resolve minimums in statements
		Set<Statement> newConstraints = new HashSet<Statement>();
		for(Statement s: this){	
			while(!s.getMinimums().isEmpty()){
				Minimum m = s.getMinimums().iterator().next();
				Term replacement = new FloatConstant(0.5F);
				replacement = replacement.mult(m.getTerms().get(0));
				replacement = replacement.add((new FloatConstant(0.5F).mult(m.getTerms().get(1))));
				replacement = replacement.minus(new AbsoluteValue(m.getTerms().get(0).minus(m.getTerms().get(1))));
				s = s.replaceTerm(m, replacement);				
			}
			newConstraints.add(s);
		}	
		this.clear();
		this.addAll(newConstraints);
	}
	
	/**
	 * Resolves all occurrences of maximums by substituting
	 * a maximum "max{a,b}" by "0.5 a + 0.5 b + abs(a-b)".
	 */
	public void resolveMaximums(){
		// expand all maximums		
		for(Statement s: this)
			s.expandAssociativeOperations();
		// resolve maximums in statements
		Set<Statement> newConstraints = new HashSet<Statement>();
		for(Statement s: this){	
			while(!s.getMaximums().isEmpty()){
				Maximum m = s.getMaximums().iterator().next();
				Term replacement = new FloatConstant(0.5F);
				replacement = replacement.mult(m.getTerms().get(0));
				replacement = replacement.add((new FloatConstant(0.5F).mult(m.getTerms().get(1))));
				replacement = replacement.add(new AbsoluteValue(m.getTerms().get(0).minus(m.getTerms().get(1))));
				s = s.replaceTerm(m, replacement);				
			}
			newConstraints.add(s);
		}	
		this.clear();
		this.addAll(newConstraints);
	}
	
	/**
	 * Returns all variables of this problem.
	 * @return all variables of this problem.
	 */
	public Set<Variable> getVariables(){
		Set<Variable> variables = new HashSet<Variable>();
		for(Statement s: this){
			variables.addAll(s.getLeftTerm().getVariables());
			variables.addAll(s.getRightTerm().getVariables());
		}
		return variables;
	}
	
	/**
	 * Returns all minimums appearing in this problem.
	 * @return all minimums appearing in this problem.
	 */
	public Set<Minimum> getMinimums(){
		Set<Minimum> minimums = new HashSet<Minimum>();
		for(Statement s: this){
			minimums.addAll(s.getLeftTerm().getMinimums());
			minimums.addAll(s.getRightTerm().getMinimums());
		}
		return minimums;
	}
	
	/**
	 * This method collapses all associative operations appearing
	 * in the target function and the constraints, e.g. every
	 * min{min{a,b},c} becomes min{a,b,c}
	 */
	public void collapseAssociativeOperations(){
		for(Statement s: this)
			s.collapseAssociativeOperations();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s = "";
		for(Statement c: this)
			s += c + "\n";
		return s;
	}
	
	/* (non-Javadoc)
	 * @see java.util.HashSet#clone()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ConstraintSatisfactionProblem clone(){
		return new ConstraintSatisfactionProblem((Set<Statement>)super.clone());
	}
		
}
