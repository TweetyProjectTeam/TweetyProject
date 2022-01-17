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
 * This class models a four-valued interpretation for propositional logic
 * Formulas are interpreted using completions
 * Every atom is assigned one of the four truth values: TRUE, FALSE, UNDECIDED, INCONSISTENT
 * 
 * @author Jonas Schumacher
 */
public class FourValuedWorld extends AbstractInterpretation<PlBeliefSet,PlFormula>{
/**
 * 
 * @author Jonas Schumacher
 *
 */
	public enum TruthValue {
		/**true*/
		TRUE, 
		/**false*/
		FALSE, 
		/**undecided*/
		UNDECIDED, 
		/**inconsistent*/
		INCONSISTENT;
		
		
		/**
		 * 
		 * @return Return "TRUE" iff 4-valued TruthValue is also "TRUE"  
		 */
		public boolean getClassical(){
			return this.equals(TRUE);
		}

		public String toString(){
			if(this.equals(TRUE)) return "T";
			if(this.equals(FALSE)) return "F";
			if(this.equals(UNDECIDED)) return "U";
			if(this.equals(INCONSISTENT)) return "I";
			return "ERROR: No such symbol";
		}
	}
	
	private Map<Proposition,TruthValue> values;
	
	/**
	 * Creates an empty 4-valued world  
	 */
	public FourValuedWorld(){
		this.values = new HashMap<Proposition,TruthValue>();
	}
	
	/**
	 * Creates a new world which is a copy of the given world	 
	 * @param other some other world
	 */
	public FourValuedWorld(FourValuedWorld other){
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
	 * Decide whether this world is two-valued or not
	 * @return isTwoValued
	 */
	public boolean isTwoValued() {
		for (Proposition p : this.getSignature()) {
			if ((this.get(p) == TruthValue.UNDECIDED) || (this.get(p) == TruthValue.INCONSISTENT)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method puts the 4 truth values into an information order
	 * @param firstTruthValue firstTruthValue
	 * @param secondTruthValue secondTruthValue
	 * @return true, if the first TruthValue is at least as informative as the second one
	 */
	public static boolean informationOrder(TruthValue firstTruthValue, TruthValue secondTruthValue) {
		boolean result = false;
		switch (firstTruthValue) {
		case INCONSISTENT:
			result = true;			
			break;
		case TRUE:
			if (secondTruthValue == TruthValue.UNDECIDED || secondTruthValue == TruthValue.TRUE) {
				result = true;
			}
			break;
		case FALSE:
			if (secondTruthValue == TruthValue.UNDECIDED || secondTruthValue == TruthValue.FALSE) {
				result = true;
			}
			break;
		case UNDECIDED:
			if (secondTruthValue == TruthValue.UNDECIDED) {
				result = true;
			}
			break;
		}
		return result;
	}
	
	/**
	 * This method compares this world to another world by comparing their information-order
	 * @param otherWorld = the world to be compared to
	 * @return true, if this world is more informative
	 */
	public boolean isAtLeastAsInformativeAs(FourValuedWorld otherWorld) {
		if (!this.getSignature().equals(otherWorld.getSignature())) {
			throw new IllegalArgumentException("Worlds must have the same signature");
		}
		// If we find one proposition where 1st world is not as informative as the 2nd world, the worlds are either incomparable or 2nd world is more informative 
		for (Proposition p : this.getSignature()) {
			if (!FourValuedWorld.informationOrder(this.get(p), otherWorld.get(p))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method returns the least informative world among a collection of FourValuedWorlds
	 * @param coll = possible input worlds
	 * @return = least informative worlds
	 * @param getMostInformative getMostInformative
	 */
	public static Collection<FourValuedWorld> reduceToLeastInformativeWorlds(Collection<FourValuedWorld> coll, boolean getMostInformative) {
		if (coll.isEmpty()) {
			throw new IllegalArgumentException("Collection must contain elements");
		}
		Collection<FourValuedWorld> leastInformativeWorlds = new HashSet<FourValuedWorld>(coll);
		Collection<FourValuedWorld> mostInformativeWorlds = new HashSet<FourValuedWorld>(coll);
		// worldA is the world to be checked for being an information minimal one
		// worldB is the world to be checked for being an information maximal one
		for (FourValuedWorld worldA : coll) {
			for (FourValuedWorld worldB : coll) {
				if (!worldA.equals(worldB)) {
					if (worldA.isAtLeastAsInformativeAs(worldB)) {
						// if we find any other world for which A is more informative, then A can't be information-minimal and needs to be removed
						leastInformativeWorlds.remove(worldA);
						// if we find any other world which is more informative than B, then B can't be information-maximal and needs to be removed
						mostInformativeWorlds.remove(worldB);
					}
				}
			}
		}
		if (getMostInformative) {
			return mostInformativeWorlds;
		}
		else {
			return leastInformativeWorlds;
		}
	}
	
	/**
	 * This method calculates a collection of 3-valued worlds based on this 4-valued world
	 * INCONSISTENT atoms are replaced by FALSE or TRUE
	 * @return Collection of 3-valued worlds
	 */
	public Collection<ThreeValuedWorld> getThreeValuedSet() {
		Collection<ThreeValuedWorld> collection3Val = new HashSet<ThreeValuedWorld>();
		Collection<Proposition> inconsistentPropositions = new HashSet<Proposition>();
		
		ThreeValuedWorld referenceWorld3V = new ThreeValuedWorld();
		
		for(Proposition p: this.values.keySet()) {
			switch (this.values.get(p)) {
			case TRUE:
				referenceWorld3V.set(p, ThreeValuedWorld.TruthValue.TRUE);
				break;
			case FALSE:
				referenceWorld3V.set(p, ThreeValuedWorld.TruthValue.FALSE);
				break;
			case UNDECIDED:
				referenceWorld3V.set(p, ThreeValuedWorld.TruthValue.UNDECIDED);
				break;
			case INCONSISTENT:
				referenceWorld3V.set(p, ThreeValuedWorld.TruthValue.FALSE);
				inconsistentPropositions.add(p);
				break;
			}
		}

		// System.out.println("All INCONSISTENT atoms: " + inconsistentPropositions);		
		// System.out.println("Reference world (all INCONSISTENT atoms replaced by FALSE): " + referenceWorld3V);
		
		/*
		 * Create a BitSet with as many bits as there are INCONSISTENT atoms in the 4-valued world
		 * Increment the BitSet and construct a new 3-valued world from it
		 */
		BitSet bs = new BitSet(inconsistentPropositions.size());
		collection3Val.add(referenceWorld3V);
		
		for (int i = 0; i < Math.pow(2, inconsistentPropositions.size())-1; i++) {
			int clearBit = bs.nextClearBit(0);
			bs.set(clearBit, true);
			bs.set(0, clearBit, false);
			
			ThreeValuedWorld updatedWorld3V = new ThreeValuedWorld(referenceWorld3V);
			int index = 0;
			for (Proposition p: inconsistentPropositions) {
				if (bs.get(index)) {
					updatedWorld3V.set(p, ThreeValuedWorld.TruthValue.TRUE);
				}
				index++;
			}
			collection3Val.add(updatedWorld3V);
		}
		return collection3Val;
	}
	
	/**
	 * This method takes a set of 3-valued worlds and returns a set of sets of 2-valued worlds
	 * where undecided atoms are replaced by False or True
	 * @param coll3V: 
	 * @return set of sets
	 */
	public Collection<Collection<PossibleWorld>> getTwoValuedSetOfSets(Collection<ThreeValuedWorld> coll3V) {
		Collection<Collection<PossibleWorld>> setOfSets2Val = new HashSet<Collection<PossibleWorld>>();
		
		/*
		 * Iterate over all 3-valued worlds and convert each into a set of 2-valued worlds
		 */
		for (ThreeValuedWorld world3V: coll3V) {
			setOfSets2Val.add(world3V.getTwoValuedSet());
		}

		return setOfSets2Val;
	}
	
	/* 
	 * Evaluate a PlFormula on this world replacing both UNDECIDED and INCONSISTENT by FALSE
	 */
	@Override
	public boolean satisfies(PlFormula formula) throws IllegalArgumentException {
		return this.satisfies4VL(formula).getClassical();
	}
	
	/**
	 * Determines the 4-valued truth value of the given formula.
	 * @param formula: some propositional formula
	 * @return the 4-valued truth value of the formula.
	 */
	public TruthValue satisfies4VL(PlFormula formula) {
		
		Collection<ThreeValuedWorld> collection3Val = getThreeValuedSet();
		
		boolean truthValueAlreadySet = false;
		boolean noUndecided = true;
		boolean noInconsistent = true;
		
		TruthValue evaluation = TruthValue.TRUE;	// will be overwritten
		boolean value = true;						// will be overwritten
		
		for (ThreeValuedWorld world3val : collection3Val) {
			/*
			 * We loop until we find at least one world which is TRUE / FALSE: this world sets the "value"
			 * If no "UNDECIDED" world came before, we also set the evaluation to TRUE / FALSE
			 */
			if (!truthValueAlreadySet) {
				switch (world3val.satisfies3VL(formula)) {
				case TRUE:
					if (noUndecided) {
						evaluation = TruthValue.TRUE;
					}
					value = true;
					truthValueAlreadySet = true;
					break;
				case FALSE:
					if (noUndecided) {
						evaluation = TruthValue.FALSE;
					}
					value = false;
					truthValueAlreadySet = true;
					break;
				case UNDECIDED:
					evaluation = TruthValue.UNDECIDED;
					noUndecided= false;
					break;
				}
			}
			/*
			 *  If we don't reach this part, we simply return UNDECIDED, because the truth value hasn't been set yet!
			 *  If we reach this part, we have certainly set the value "value" to either TRUE or FALSE
			 *  The "evaluation" field could be anything
			 */
			else if (noInconsistent) {
				/*
				 *  Now the cases "TRUE" and "FALSE" only have relevance, if they turn the evaluation inconsistent
				 *  The case "UNDECIDED" will always return an UNDECIDED evaluation
				 */
				switch (world3val.satisfies3VL(formula)) {
				case TRUE:
					if (!value) {
						evaluation = TruthValue.INCONSISTENT;
					}
					break;
				case FALSE:
					if (value) {
						evaluation = TruthValue.INCONSISTENT;
					}					
					break;
				case UNDECIDED:
					evaluation = TruthValue.UNDECIDED;
					break;
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
	/**
	 * 
	 * @return values as string
	 */
	public String printValues() {
		String pureValues = "";
		for (TruthValue val : this.values.values()) {
			pureValues += val;
		}
		return pureValues;
	}
	/**
	 * 
	 * @param coll worlds
	 * @return the string representation
	 */
	public static String printCollection(Collection<FourValuedWorld> coll) {
		String output = "{";
		for (FourValuedWorld world : coll) {
			output += world.printValues() + ", ";
		}
		if (output.length() >= 2) {
			output = output.substring(0, output.length()-2);
		}
		output += "}";
		return output;
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
		FourValuedWorld other = (FourValuedWorld) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
}
