package org.tweetyproject.logics.translators.adfpossibilistic;

import java.util.*;

import org.tweetyproject.logics.pl.semantics.*;
import org.tweetyproject.logics.pl.syntax.*;


/**
 * Models a possibility distribution (PD) as defined in 
 * Christoph Beierle / Gabriele Kern-Isberner: Methoden Wissensbasierter Systeme
 * 
 * @author Jonas Schumacher
 *
 */
public class PossibilityDistribution {
	
	/**
	 * Upper and lower bound
	 */
	public static final double LOWER_BOUND = 0.0;
	public static final double UPPER_BOUND = 1.0;
	
	/**
	 * The possibility measures of the possible worlds.
	 */
	private Map<PossibleWorld,Double> possibility;
	
	/**
	 * The signature of the language this possibility distribution (PD) is defined on.
	 */
	private PlSignature signature;
	
	/**
	 * Creates a new possibility distribution (PD) mapping each given interpretation to 1.0 (least specific value = maximal entropy).
	 * @param signature the signature of the language this PD is defined on.
	 */
	public PossibilityDistribution(PlSignature signature){
		this.signature = signature;		
		this.possibility = new HashMap<PossibleWorld,Double>();
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds(signature.toCollection()))
			this.possibility.put(w, PossibilityDistribution.UPPER_BOUND);		
	}
	
	/**
	 * Gets the possibility of the given possible world.
	 * @param w an possible world.
	 * @return the possibility of the given possible world.
	 * @throws IllegalArgumentException if the given possible world has no possibility in this PD.
	 */
	public Double getPossibility(PossibleWorld w) throws IllegalArgumentException{
		if(!this.possibility.containsKey(w))
			throw new IllegalArgumentException("No possibility defined for the possible world " + w);
		return this.possibility.get(w);
	}
	
	/**
	 * Sets the possibility for the given possible world.
	 * @param w an possible world.
	 * @param value the possibility for the possible world.
	 */
	public void setPossibility(PossibleWorld w, Double value){		
		if((value < 0.0) || (value > 1.0)) {
			throw new IllegalArgumentException("Illegal possibility value " + value + ". Possibility measures must be in [0,1]");			
		}
		this.possibility.put(w, value);
	}
	
	/**
	 * Gets the possibility measure of the given formula. Throws an IllegalArgumentException when
	 * the language of the formula does not correspond to the language of the
	 * interpretations this PD is defined on. Otherwise the possibility of a formula
	 * is defined as the maximal possibility of its satisfying interpretations.
	 * @param formula a formula.
	 * @return the possibility of the given formula.
	 * @throws IllegalArgumentException if the languages of the formula does not correspond to the language of the
	 * 		interpretations this PD is defined on.
	 */
	public Double getPossibility(PlFormula formula) throws IllegalArgumentException{
		Double posFormula = PossibilityDistribution.LOWER_BOUND;
		for(PossibleWorld i: this.possibility.keySet()) {
			if(i.satisfies(formula)) {
				if(this.possibility.get(i).compareTo(posFormula)>0) {
					posFormula = this.possibility.get(i);										
				}
			}
		}
		return posFormula;
	}
	
	/**
	 * Gets the necessity measure of the given formula.
	 * @param formula a formula.
	 * @return the necessity of the given formula.
	 * @throws IllegalArgumentException if the languages of the formula does not correspond to the language of the
	 * 		interpretations this PD is defined on.
	 */
	public Double getNecessity(PlFormula formula) throws IllegalArgumentException{
		Double necFormula = PossibilityDistribution.UPPER_BOUND;
		for(PossibleWorld i: this.possibility.keySet()) {
			// Iterate over all worlds in which formula is not true 
			if(!i.satisfies(formula)) {
				// get the inverse possibility
				Double currentValue = Double.valueOf(1.0)-this.possibility.get(i);
				// find minimum value of inverse possibility
				if(currentValue.compareTo(necFormula)<0) {
					// this should work even when currentValue is changed in the future
					necFormula = currentValue;
				}
			}
		}

		return necFormula;
	}
	

	/**
	 * Check whether this PD is normalized, i.e. whether its max. possibility value equals one.
	 * @return "true" if this PD is normalized
	 */
	public boolean isNormalized(){
		Double max = PossibilityDistribution.UPPER_BOUND;
		for(Double i: this.possibility.values())
			if(i < max)
				max = i;
		return max == PossibilityDistribution.UPPER_BOUND;
	}
	
	/**
	 * Returns all interpretations that are mapped to a possibility unequal to zero.
	 * @return all interpretations that are mapped to a possibility unequal to zero.
	 */
	public Set<PossibleWorld> getPossibleWorlds(){
		Set<PossibleWorld> worlds = new HashSet<PossibleWorld>();
		for(PossibleWorld w: this.possibility.keySet())
			if(this.possibility.get(w) > PossibilityDistribution.LOWER_BOUND)
				worlds.add(w);
		return worlds;
	}
	
	/**
	 * Returns all interpretations that are mapped to 1
	 * @return all interpretations that are mapped to 1
	 */
	public Set<PossibleWorld> getPlausibleWorlds(){
		Set<PossibleWorld> worlds = new HashSet<PossibleWorld>();
		for(PossibleWorld w: this.possibility.keySet())
			if(this.possibility.get(w) == PossibilityDistribution.UPPER_BOUND)
				worlds.add(w);
		return worlds;
	}
	
	/**
	 * Returns the signature of the first-order language this PD is defined on.
	 * @return the signature of the first-order language this PD is defined on.
	 */
	public PlSignature getSignature(){
		return this.signature;
	}
	
	/**
	 * Check whether this PD is at least as specific as another PD "other"
	 * @param other: another PD to be compared with
	 * @return true, if this PD is at least as specific as the other PD
	 */
	public boolean atLeastAsSpecificAs(PossibilityDistribution other) {
		// Check, if signatures match
		if(this.signature != other.getSignature()) {
			throw new IllegalArgumentException("Possibility distributions must have the same signature.");			
		}
		boolean result = true;
		// Iterate over all possible worlds and check if possibility is higher or lower than that of the other PD
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds(this.signature.toCollection())) {
			if (getPossibility(w) > other.getPossibility(w)) {
				result = false;
			}
		}

		return result;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s = "[\n";
		Iterator<PossibleWorld> it = this.possibility.keySet().iterator();
		while(it.hasNext()){
			PossibleWorld w = it.next();
			s += "  " + w + " => ";
			s += this.getPossibility(w);
			s += "\n";
		}
		s += "]";
		return s;
	}
	
	@Override
	public boolean equals(Object other) {
		if(! (other instanceof PossibilityDistribution))
			return false;
		
		PossibilityDistribution oc = (PossibilityDistribution)other;
		return possibility.equals(oc.possibility);
	}
}
