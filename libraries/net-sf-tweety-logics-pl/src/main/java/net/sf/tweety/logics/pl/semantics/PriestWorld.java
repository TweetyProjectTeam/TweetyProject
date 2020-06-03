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
package net.sf.tweety.logics.pl.semantics;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.sf.tweety.commons.AbstractInterpretation;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Equivalence;
import net.sf.tweety.logics.pl.syntax.ExclusiveDisjunction;
import net.sf.tweety.logics.pl.syntax.Implication;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * A three-valued interpretation for propositional logic from
 * Priest's three valued logic (3VL) [Priest, G.: Logic of paradox. Journal of Philosophical Logic 8, 219-241 (1979)].
 * 
 * Every proposition gets one of three truth values: true, false, both.
 * 
 * @author Matthias Thimm
 */
public class PriestWorld extends AbstractInterpretation<PlBeliefSet,PlFormula>{

	/** The three truth values. */
	public enum TruthValue {
		TRUE, FALSE, BOTH;
		public TruthValue neg(){
			if(this.equals(TRUE)) return FALSE;
			if(this.equals(FALSE)) return TRUE;
			return BOTH;
		}		
		public boolean getClassical(){
			return this.equals(BOTH) || this.equals(TRUE);
		}
		public TruthValue and(TruthValue v){
			if(this.equals(FALSE) || v.equals(FALSE)) return FALSE;
			if(this.equals(BOTH) || v.equals(BOTH)) return BOTH;
			return TRUE;
		}
		public TruthValue or(TruthValue v){
			if(this.equals(TRUE) || v.equals(TRUE)) return TRUE;
			if(this.equals(BOTH) || v.equals(BOTH)) return BOTH;
			return FALSE;
		}
		public String toString(){
			if(this.equals(TRUE)) return "T";
			if(this.equals(FALSE)) return "F";
			return "B";
		}
	};
	
	/** The truth values of the propositions. */
	private Map<Proposition,TruthValue> values;
	
	/**
	 * Creates a new world where all propositions get the truth value FALSE.  
	 */
	public PriestWorld(){
		this.values = new HashMap<Proposition,TruthValue>();
	}
	
	/**
	 * Creates a new world which is a copy of the given world	 
	 * @param other some other world
	 */
	public PriestWorld(PriestWorld other){
		this.values = new HashMap<Proposition,TruthValue>(other.values);
	}
	
	/**
	 * Sets the value of the given proposition.
	 * @param p some proposition.
	 * @param val some truth value.
	 */
	public void set(Proposition p, TruthValue val){
		this.values.put(p, val);
	}
	
	/**
	 * Returns the truth value of the given proposition.
	 * @param p a proposition
	 * @return a truth value.
	 */
	public TruthValue get(Proposition p){
		if(!this.values.containsKey(p))
			return TruthValue.FALSE;
		return this.values.get(p);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Interpretation#satisfies(net.sf.tweety.Formula)
	 */
	@Override
	public boolean satisfies(PlFormula formula) throws IllegalArgumentException {
		return this.satisfies3VL(formula).getClassical();
	}

	/**
	 * Determines the 3-valued truth value of the given formula.
	 * @param formula some formula
	 * @return the 3-valued truth value of the formula.
	 * @throws IllegalArgumentException if the formula is of unknown type
	 */
	public TruthValue satisfies3VL(PlFormula formula) throws IllegalArgumentException {
		if(formula instanceof Contradiction)
			return TruthValue.FALSE;
		if(formula instanceof Tautology)
			return TruthValue.TRUE;
		if(formula instanceof Proposition)
			return this.get((Proposition)formula);
		if(formula instanceof Negation)
			return this.satisfies3VL(((Negation)formula).getFormula()).neg();		
		if(formula instanceof Conjunction){
			Conjunction c = (Conjunction) formula;
			TruthValue val = TruthValue.TRUE;
			for(PlFormula f : c)
				val = val.and(this.satisfies3VL(f));
			return val;
		}
		if(formula instanceof Disjunction){
			Disjunction d = (Disjunction) formula;
			TruthValue val = TruthValue.FALSE;
			for(PlFormula f: d)
				val = val.or(this.satisfies3VL(f));
			return val;
		}
		if(formula instanceof ExclusiveDisjunction){
			ExclusiveDisjunction x = (ExclusiveDisjunction) formula;
			Conjunction c = x.toCnf();
			TruthValue val = TruthValue.TRUE;
			for(PlFormula f : c)
				val = val.and(this.satisfies3VL(f));
			return val;
		}
		if (formula instanceof Implication) {
			Implication i = (Implication) formula;
			Disjunction d = new Disjunction(new Negation(i.getFormulas().getFirst()), i.getFormulas().getSecond()); 
			return this.satisfies3VL(d);
		}
		if(formula instanceof Equivalence) {
			Equivalence e = (Equivalence) formula;
			Disjunction d1 = new Disjunction(new Negation(e.getFormulas().getFirst()), e.getFormulas().getSecond()); 
			Disjunction d2 = new Disjunction(new Negation(e.getFormulas().getSecond()), e.getFormulas().getFirst()); 
			return this.satisfies3VL(new Conjunction(d1,d2));
		}
		throw new IllegalArgumentException("Propositional formula " + formula + " is of unknown type.");
	}
	
	/**
	 * Returns the binary base of this world, i.e. the set of all propositions
	 * which are assigned either to TRUE or FALSE.
	 * @return the binary base of this world.
	 */
	public Collection<Proposition> getBinarybase(){
		Collection<Proposition> binarybase = new HashSet<Proposition>();
		for(Proposition p: this.values.keySet())
			if(!this.values.get(p).equals(TruthValue.BOTH))
				binarybase.add(p);
		return binarybase;
	}
	
	/**
	 * Returns the conflict base of this world, i.e. the set of all propositions
	 * which are assigned to BOTH.
	 * @return the conflict base of this world.
	 */
	public Collection<Proposition> getConflictbase(){
		Collection<Proposition> cbase = new HashSet<Proposition>();
		for(Proposition p: this.values.keySet())
			if(this.values.get(p).equals(TruthValue.BOTH))
				cbase.add(p);
		return cbase;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Interpretation#satisfies(net.sf.tweety.BeliefBase)
	 */
	@Override
	public boolean satisfies(PlBeliefSet beliefBase) throws IllegalArgumentException {
		for(PlFormula f: beliefBase)
			if(!this.satisfies(f))
				return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.values.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PriestWorld other = (PriestWorld) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
}
