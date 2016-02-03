/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.rpcl.syntax;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.logics.fol.syntax.Tautology;
import net.sf.tweety.logics.rcl.syntax.RelationalConditional;
import net.sf.tweety.math.probability.Probability;

/**
 * This class represents a relational probabilistic conditional, i.e. a structure (B|A)[p]
 * with first-order formulas A and B and a probability p.<br>
 * <br>
 * Premise and conclusion of this conditional must confirm to a fol language without
 * quantifiers and without functions (@see net.sf.tweety.logics.firstorderlogic.lang.FolLanguageNoQuantifiersNoFunctions)
 * 
 * @author Matthias Thimm
 */
public class RelationalProbabilisticConditional extends RelationalConditional {
	
	/**
	 * The probability of the formula.
	 */
	private Probability probability;
	
	/**
	 * Creates a new conditional with the given premise, conclusion and probability.
	 * @param premise a fol formula.
	 * @param conclusion a fol formula.
	 * @param probability a probability.
	 */
	public RelationalProbabilisticConditional(FolFormula premise, FolFormula conclusion, Probability probability){
		super(premise,conclusion);		
		this.probability = probability;
	}
	
	/**
	 * Creates a new conditional with the given conclusion and probability and
	 * a tautological premise.
	 * @param conclusion a fol formula.
	 * @param probability a probability.
	 */
	public RelationalProbabilisticConditional(FolFormula conclusion, Probability probability){
		this(new Tautology(),conclusion,probability);
	}
	
	/**
	 * Creates a new relational probabilistic conditional with the given conditional and probability
	 * @param conditional a relational conditional.
	 * @param probability a probability.
	 */
	public RelationalProbabilisticConditional(RelationalConditional conditional, Probability probability){
		this(conditional.getPremise().iterator().next(),conditional.getConclusion(),probability);
	}
	
	/**
	 * Returns the probability of this conditional.
	 * @return the probability of this conditional.
	 */
	public Probability getProbability(){
		return this.probability;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t)	throws IllegalArgumentException {
		return new RelationalProbabilisticConditional((RelationalConditional)super.substitute(v, t),this.probability);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + "[" + this.probability + "]";
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.ClassicalFormula#complement()
	 */
	@Override
	public RelationalProbabilisticConditional complement() {
		return new RelationalProbabilisticConditional(this,this.probability.complement());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((probability == null) ? 0 : probability.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelationalProbabilisticConditional other = (RelationalProbabilisticConditional) obj;
		if (probability == null) {
			if (other.probability != null)
				return false;
		} else if (!probability.equals(other.probability))
			return false;
		return true;
	}
}
