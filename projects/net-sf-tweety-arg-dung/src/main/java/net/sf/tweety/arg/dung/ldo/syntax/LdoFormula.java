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
package net.sf.tweety.arg.dung.ldo.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.ldo.semantics.LdoInterpretation;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungSignature;
import net.sf.tweety.graphs.Graph;
import net.sf.tweety.logics.commons.syntax.interfaces.ClassicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.pl.syntax.PropositionalPredicate;
import net.sf.tweety.math.probability.Probability;

/**
 * This abstract class specifies the general methods of all Ldo-formulas
 * (LDO - Logic of dialectical outcomes, cf. [Hunter, Thimm, 2015])
 * 
 * @author Matthias Thimm
 *
 */
public abstract class LdoFormula implements ClassicalFormula{

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula#getAtoms()
	 */
	@Override
	public abstract Set<LdoArgument> getAtoms();

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable#combineWithAnd(net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable)
	 */
	@Override
	public LdoConjunction combineWithAnd(Conjuctable f){
		if(!(f instanceof LdoFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a ldo formula.");
		return new LdoConjunction(this,(LdoFormula)f);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable#combineWithOr(net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable)
	 */
	@Override
	public LdoDisjunction combineWithOr(Disjunctable f){
		if(!(f instanceof LdoFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a ldo formula.");
		return new LdoDisjunction(this,(LdoFormula)f);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula#getPredicates()
	 */
	@Override
	public abstract Set<PropositionalPredicate> getPredicates();
	
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
	public Collection<DungTheory> getDividers(DungTheory theory, int semantics){
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
	 * @see net.sf.tweety.logics.commons.syntax.interfaces.ProbabilityAware#getUniformProbability()
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
	public Class<PropositionalPredicate> getPredicateCls() {
		return PropositionalPredicate.class;
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
