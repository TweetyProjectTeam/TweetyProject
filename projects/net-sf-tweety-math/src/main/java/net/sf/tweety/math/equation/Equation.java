/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.math.equation;

import net.sf.tweety.math.term.*;

/**
 * This class represent an equation of two terms.
 * @author Matthias Thimm
 */
public class Equation extends Statement{

	/**
	 * Creates a new equation with the given terms.
	 * @param leftTerm a term.
	 * @param rightTerm a term.
	 */
	public Equation(Term leftTerm, Term rightTerm){
		super(leftTerm,rightTerm); 
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.equation.Statement#replaceTerm(net.sf.tweety.math.term.Term, net.sf.tweety.math.term.Term)
	 */
	@Override
	public Statement replaceTerm(Term toSubstitute, Term substitution){
		return new Equation(this.getLeftTerm().replaceTerm(toSubstitute, substitution),this.getRightTerm().replaceTerm(toSubstitute, substitution));
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.equation.Statement#isNormalized()
	 */
	@Override
	public boolean isNormalized(){
		if(this.getRightTerm() instanceof Constant){
			if(this.getRightTerm() instanceof FloatConstant){
				if(((FloatConstant)this.getRightTerm()).getValue() == 0)
					return true;
			}
			if(this.getRightTerm() instanceof IntegerConstant){
				if(((IntegerConstant)this.getRightTerm()).getValue() == 0)
					return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.equation.Statement#toNormalizedForm()
	 */
	@Override
	public Statement toNormalizedForm(){
		// Check whether it is already normalized
		if(this.isNormalized()) return this;
		// rearrange the terms
		return new Equation(this.getLeftTerm().minus(this.getRightTerm()),new IntegerConstant(0));		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.equation.Statement#toLinearForm()
	 */
	@Override
	public Statement toLinearForm(){
		Term left = this.getLeftTerm().toLinearForm();
		Term right = (this.isNormalized())?(this.getRightTerm()):(this.getRightTerm().toLinearForm());
		return new Equation(left,right);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.equation.Statement#getRelationSymbol()
	 */
	@Override
	public String getRelationSymbol(){
		return "=";
	}
}
