package org.tweetyproject.logics.translators.adfconditional;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.tweetyproject.commons.AbstractInterpretation;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * This class models a three-valued interpretation for propositional logic
 * Formulas are interpreted using completions
 * Every atom is assigned one of the three truth values: TRUE, FALSE, UNDECIDED.
 * 
 * @author Jonas Schumacher
 */
public class ThreeValuedWorld extends AbstractInterpretation<PlBeliefSet,PlFormula>{

	/** The three truth values. */
	public enum TruthValue {
		/**true*/
		TRUE, 
		/**false*/
		FALSE, 
		/**undecided*/
		UNDECIDED;
		
		
		/**
		 * 
		 * @return"TRUE" iff 3-valued TruthValue is also "TRUE"  
		 */
		public boolean getClassical(){
			return this.equals(TRUE);
		}
		
		public String toString(){
			if(this.equals(TRUE)) return "T";
			if(this.equals(FALSE)) return "F";
			if(this.equals(UNDECIDED)) return "U";
			return "ERROR: No such symbol";
		}
	}
	
	/** The truth values of the propositions. */
	private Map<Proposition,TruthValue> values;
	
	/**
	 * Creates an empty 3-valued world  
	 */
	public ThreeValuedWorld(){
		this.values = new HashMap<Proposition,TruthValue>();
	}
	
	/**
	 * Creates a new world which is a copy of the given world	 
	 * @param other some other world
	 */
	public ThreeValuedWorld(ThreeValuedWorld other){
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
	
	/**
	 * Returns the signature of this world.
	 * @return the signature of this world.
	 */
	public PlSignature getSignature() {
		PlSignature sig = new PlSignature();
		sig.addAll(this.values.keySet());
		return sig;
	}
	
	/**
	 * This method calculates a collection of 2-valued worlds based on this 3-valued world
	 * UNDECIDED atoms are replaced by FALSE or TRUE
	 * @return Collection of 2-valued worlds
	 */
	public Collection<PossibleWorld> getTwoValuedSet() {
		Collection<PossibleWorld> collection2Val = new HashSet<PossibleWorld>();
		Collection<Proposition> undecidedPropositions = new HashSet<Proposition>();
		Collection<Proposition> truePropositions = new HashSet<Proposition>();
		
		
		for(Proposition p: this.values.keySet()) {
			switch (this.values.get(p)) {
			case TRUE:
				truePropositions.add(p);
				break;
			case FALSE:
				break;
			case UNDECIDED:
				undecidedPropositions.add(p);
				break;
			}
		}
		
		PossibleWorld referenceWorld2V = new PossibleWorld(truePropositions);
		collection2Val.add(referenceWorld2V);

		// System.out.println("All UNDECIDED atoms: " + undecidedPropositions);		
		// System.out.println("Reference world (ALL UNDECIDED atoms are replaced by FALSE and therefore not contained): " + referenceWorld2V);
		
		/*
		 * Create a BitSet with as many bits as there are UNDECIDED atoms in the 3-valued world
		 * Increment the BitSet and construct a new 2-valued world from it
		 */
		BitSet bs = new BitSet(undecidedPropositions.size());
		
		for (int i = 0; i < Math.pow(2, undecidedPropositions.size())-1; i++) {
			int clearBit = bs.nextClearBit(0);
			bs.set(clearBit, true);
			bs.set(0, clearBit, false);

			Collection<Proposition> propositionsToAdd = new HashSet<Proposition>();
			int index = 0;
			for (Proposition p: undecidedPropositions) {
				if (bs.get(index)) {
					propositionsToAdd.add(p);
				}
				index++;
			}
			propositionsToAdd.addAll(truePropositions);
			PossibleWorld updatedWorld2V = new PossibleWorld(propositionsToAdd);
			collection2Val.add(updatedWorld2V);
		}
		return collection2Val;
	}
	
	/*
	 * Evaluate a PlFormula on this world replacing UNDECIDED by FALSE
	 */
	@Override
	public boolean satisfies(PlFormula formula) {
		return this.satisfies3VL(formula).getClassical();
	}
	
	/**
	 * Determines the 3-valued truth value of the given formula.
	 * @param formula: some propositional formula
	 * @return the 3-valued truth value of the formula.
	 */
	public TruthValue satisfies3VL(PlFormula formula) {
		Collection<PossibleWorld> collection2Val = getTwoValuedSet();
		boolean isFirst = true;
		boolean unanimity = true; 
		TruthValue evaluation = TruthValue.TRUE;	// will be overwritten with first possible world
		for (PossibleWorld pw : collection2Val) {
			// The first possible World sets the truth value of the whole set
			if (isFirst) {
				if (pw.satisfies(formula)) {
					evaluation = TruthValue.TRUE;
				}
				else {
					evaluation = TruthValue.FALSE;
				}
				isFirst = false;
			}
			// All other worlds only have a relevance if they contradict the first world:
			else if (unanimity) {
				if (pw.satisfies(formula) && (evaluation == TruthValue.FALSE)) {
					unanimity = false;
					evaluation = TruthValue.UNDECIDED;
				}
				else if (!pw.satisfies(formula) && (evaluation == TruthValue.TRUE)) {
					unanimity = false;
					evaluation = TruthValue.UNDECIDED;
				}
			}
		}
		return evaluation;
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
		ThreeValuedWorld other = (ThreeValuedWorld) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
}
