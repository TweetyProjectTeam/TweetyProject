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
package net.sf.tweety.logics.ml.syntax;

import java.util.Set;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.math.probability.Probability;

/**
 * This class models a modal formula, i.e. it encapsulates an modal operator
 * and a formula (either a modal formula or a FolFormula).
 *  
 * @author Matthias Thimm
 */
public abstract class ModalFormula extends FolFormula {

	/**
	 * The inner formula of this modal formula 
	 */
	private RelationalFormula formula;
	
	public ModalFormula(RelationalFormula formula){
		if(!(formula instanceof ModalFormula) && !(formula instanceof FolFormula))
			throw new IllegalArgumentException("Expecting first-order formula or modal formula for inner formula.");
		this.formula = formula;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Formula#getSignature()
	 */
	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		sig.addAll(this.getTerms(Constant.class));
		sig.addAll(this.getFunctors());
		sig.addAll(this.getPredicates());
		return sig;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#getUniformProbability()
	 */
	@Override
	public Probability getUniformProbability() {
		throw new UnsupportedOperationException("IMPLEMENT ME"); //TODO return this.formula.getUniformProbability()?
	}
	
	/**
	 * Returns the inner formula of this modal formula.
	 * @return the inner formula of this modal formula.
	 */
	public RelationalFormula getFormula(){
		return this.formula;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#getPredicates()
	 */
	public Set<? extends Predicate> getPredicates(){
		return this.formula.getPredicates();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#getFunctors()
	 */
	public Set<Functor> getFunctors(){
		return this.formula.getFunctors();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#getAtoms()
	 */
	@SuppressWarnings("unchecked")
	public Set<FOLAtom> getAtoms(){
		return (Set<FOLAtom>) this.formula.getAtoms();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#containsQuantifier()
	 */
	public boolean containsQuantifier(){
		return this.formula.containsQuantifier();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#isClosed()
	 */
	public boolean isClosed(){
		return this.formula.isClosed();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#isClosed(java.util.Set)
	 */
	public boolean isClosed(Set<Variable> boundVariables){
		return this.formula.isClosed(boundVariables);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#getUnboundVariables()
	 */
	public Set<Variable> getUnboundVariables(){
		return this.getTerms(Variable.class);
	}
	
	@Override
	public boolean isWellBound(){
		return this.formula.isWellBound();
	}
	
	@Override
	public boolean isWellBound(Set<Variable> boundVariables){
		return this.formula.isWellBound(boundVariables);
	}
	
	@Override
	public Conjunction combineWithAnd(Conjuctable f){
		if(!(f instanceof ModalFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a modal formula.");
		return new Conjunction(this,(ModalFormula)f);
	}
	
	@Override
	public Disjunction combineWithOr(Disjunctable f){
		if(!(f instanceof ModalFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a modal formula.");
		return new Disjunction(this,(ModalFormula)f);
	}
	
	@Override
	public RelationalFormula complement(){		
		return new Negation(this);
	}

	@Override
	public boolean isLiteral() {
		return formula.isLiteral();
	}

	@Override
	public Set<Term<?>> getTerms() {
		return formula.getTerms();
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return formula.getTerms(cls);
	}

	@Override
	public Set<Variable> getQuantifierVariables() {
		return formula.getQuantifierVariables();
	}

	//@Override
	//public RelationalFormula substitute(Term<?> v, Term<?> t)
	//		throws IllegalArgumentException {
	//	return formula.substitute(v, t);
	//}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModalFormula other = (ModalFormula) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}
	
	public boolean containsModalityOperator(){
		return true;
	}

	
}
