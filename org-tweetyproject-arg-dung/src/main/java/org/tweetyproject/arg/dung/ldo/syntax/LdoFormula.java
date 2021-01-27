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
package org.tweetyproject.arg.dung.ldo.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.ldo.semantics.LdoInterpretation;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.logics.commons.syntax.interfaces.ClassicalFormula;
import org.tweetyproject.logics.commons.syntax.interfaces.Conjunctable;
import org.tweetyproject.logics.commons.syntax.interfaces.Disjunctable;
import org.tweetyproject.logics.pl.syntax.PlPredicate;
import org.tweetyproject.math.probability.Probability;

/**
 * This abstract class specifies the general methods of all Ldo-formulas
 * (LDO - Logic of dialectical outcomes, cf. [Hunter, Thimm, 2015])
 * 
 * @author Matthias Thimm
 *
 */
public abstract class LdoFormula implements ClassicalFormula{

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.SimpleLogicalFormula#getAtoms()
	 */
	@Override
	public abstract Set<LdoArgument> getAtoms();

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.Conjuctable#combineWithAnd(org.tweetyproject.logics.commons.syntax.interfaces.Conjuctable)
	 */
	@Override
	public LdoConjunction combineWithAnd(Conjunctable f){
		if(!(f instanceof LdoFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a ldo formula.");
		return new LdoConjunction(this,(LdoFormula)f);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.Disjunctable#combineWithOr(org.tweetyproject.logics.commons.syntax.interfaces.Disjunctable)
	 */
	@Override
	public LdoDisjunction combineWithOr(Disjunctable f){
		if(!(f instanceof LdoFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a ldo formula.");
		return new LdoDisjunction(this,(LdoFormula)f);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.SimpleLogicalFormula#getPredicates()
	 */
	@Override
	public abstract Set<PlPredicate> getPredicates();
	
	/**
	 * Returns all literals, i.e. all formulas of the form "a" or "!a"
	 * where "a" is a proposition, that appear in this formula.
	 * @return all literals appearing in this formula.
	 */
	public abstract Set<LdoFormula> getLiterals();

	/**
	 * Returns the dividers for this formula, i.e. all sub-theories of the given
	 * theory such that this formula is satisfied by this sub-theory.
	 * @param theory some argumentation framework
	 * @param semantics some semantics
	 * @return the set of dividers of this formula
	 */
	public Collection<DungTheory> getDividers(DungTheory theory, Semantics semantics){
		Collection<DungTheory> result = new HashSet<DungTheory>();
		for(Graph<Argument> g: theory.getSubgraphs()){
			DungTheory sub = new DungTheory(g);
			LdoInterpretation i = new LdoInterpretation(sub,semantics);
			if(i.satisfies(this))
				result.add(sub);			
		}		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.syntax.interfaces.ProbabilityAware#getUniformProbability()
	 */
	@Override
	public Probability getUniformProbability(){
		throw new UnsupportedOperationException("Not supported.");
	}
  
	@Override
	public ClassicalFormula complement(){
		if(this instanceof LdoNegation)
			return ((LdoNegation)this).getFormula();
		return new LdoNegation(this);
	}

	@Override
	public boolean isLiteral() {
		return false;
	}
	
	@Override
	public Class<PlPredicate> getPredicateCls() {
		return PlPredicate.class;
	}
	
	@Override
	public DungSignature getSignature() {
		return new DungSignature();
	}
	
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract LdoFormula clone();
	
}
