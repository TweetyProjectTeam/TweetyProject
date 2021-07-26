package org.tweetyproject.logics.translators.adfcl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.tweetyproject.commons.AbstractInterpretation;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * This class models a three-valued interpretation for propositional logic from
 * Priest's three valued logic (3VL) [Priest, G.: Logic of paradox. Journal of Philosophical Logic 8, 219-241 (1979)].
 * 
 * Every proposition is assigned one of three truth values: true, false, both.
 * 
 * Adapted from class "PriestWorld" for the use of a wider range of operators
 * 
 * @author Jonas Schumacher
 */
public class PriestWorldAdapted extends AbstractInterpretation<PlBeliefSet,PlFormula>{

	/** The three truth values. */
	public enum TruthValue {
		TRUE, FALSE, BOTH;
		
		/*
		 * J: Strong negation
		 */
		public TruthValue neg(){
			if(this.equals(TRUE)) return FALSE;
			if(this.equals(FALSE)) return TRUE;
			return BOTH;
		}	
		/*
		 * J: Weak negation
		 * return false, if interpretation is true
		 * return true in all other case 
		 */
		public TruthValue weakNeg(){
			if(this.equals(TRUE)) return FALSE;
			return TRUE;
		}
		
		/*
		 * J: Return "True" iff TruthValue is True  
		 * (!) Compared to the original PriestWorld, return False if value is Both/Undecided (!)
		 */
		public boolean getClassical(){
			return this.equals(TRUE);
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
	public PriestWorldAdapted(){
		this.values = new HashMap<Proposition,TruthValue>();
	}
	
	/**
	 * Creates a new world which is a copy of the given world	 
	 * @param other some other world
	 */
	public PriestWorldAdapted(PriestWorldAdapted other){
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
	 * @see org.tweetyproject.Interpretation#satisfies(org.tweetyproject.Formula)
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
		
		/*
		 * Strong Negation
		 */
		if(formula instanceof Negation)
			return this.satisfies3VL(((Negation)formula).getFormula()).neg();	
		
		/*
		 *  Weak Negation
		 *  J: This extracts the "inner" part of the Negation
		 *  J: After calculating the truth value of the inner part, it applies weakNeg() to it
		 */
		if(formula instanceof WeakNegation) {
			//System.out.println(((WeakNegation)formula).getFormula());
			return this.satisfies3VL(((WeakNegation)formula).getFormula()).weakNeg();
		}

		/*
		 *  Indecision
		 *  J: Reduce calculation to more basic concepts
		 */
		if(formula instanceof Indecision) {
			PlFormula inner = ((Indecision)formula).getFormula();
			TruthValue val1 = this.satisfies3VL(inner).neg();
			TruthValue val2 = this.satisfies3VL(inner);
			return (val1.or(val2)).weakNeg();
		}
		
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
	 * @see org.tweetyproject.Interpretation#satisfies(org.tweetyproject.BeliefBase)
	 */
	@Override
	public boolean satisfies(PlBeliefSet beliefBase) throws IllegalArgumentException {
		for(PlFormula f: beliefBase)
			if(!this.satisfies(f))
				return false;
		return true;
	}
	
	/**
	 * Returns the signature of this world.
	 * @return the signature of this world.
	 */
	public PlSignature getSignature() {
		PlSignature sig = new PlSignature();
		sig.addAll(this.values.keySet());
		return sig;
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
		PriestWorldAdapted other = (PriestWorldAdapted) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
	
	/*
	 * J: Count number of propositions with interpretation "BOTH"
	 */
	public int countUndecided() {
		int result = 0;
		for (Proposition p : this.values.keySet()) {
			if (this.get(p) == TruthValue.BOTH) {
				result++;
			}
		}
		return result;
	}
}
