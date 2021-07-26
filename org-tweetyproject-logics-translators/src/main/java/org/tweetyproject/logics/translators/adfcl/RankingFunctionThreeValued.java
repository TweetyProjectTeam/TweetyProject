package org.tweetyproject.logics.translators.adfcl;

import java.util.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.cl.syntax.*;
import org.tweetyproject.logics.pl.syntax.*;


/**
 * A ranking function (or ordinal conditional function, OCF) that maps possible worlds
 * of a propositional language to integers. 
 * 
 * Adapted from class "RankingFunction" for the use of Three Valued Logic
 * 
 * @author Jonas Schumacher
 *
 */
public class RankingFunctionThreeValued extends AbstractInterpretation<ClBeliefSet,Conditional> {
	
	/**
	 * Integer used to define infinity.
	 */
	public static final Integer INFINITY = Integer.MAX_VALUE;
	
	/**
	 * The ranks of the possible worlds.
	 * Now based on PriestWorld_adapted
	 */
	private Map<PriestWorldAdapted,Integer> ranks;
	
	/**
	 * The signature of the language this ranking function
	 * is defined on.
	 */
	private PlSignature signature;
	
	/**
	 * Creates a new ranking function mapping each
	 * given interpretation to zero.
	 * @param signature the signature of the language this ranking function
	 * is defined on.
	 */
	public RankingFunctionThreeValued(PlSignature signature){
		this.signature = signature;		
		this.ranks = new HashMap<PriestWorldAdapted,Integer>();
		
		PriestWorldIterator pwIterator = new PriestWorldIterator(signature);
		while (pwIterator.hasNext()) {
			this.ranks.put(pwIterator.next(), 0);
		}	
	}
	
	/**
	 * Gets the rank of the given possible world.
	 * @param w an possible world.
	 * @return the rank of the given possible world.
	 * @throws IllegalArgumentException if the given possible world has no
	 *   rank in this ranking function.
	 */
	public Integer rank(PriestWorldAdapted w) throws IllegalArgumentException{
		if(!this.ranks.containsKey(w))
			throw new IllegalArgumentException("No rank defined for the possible world " + w);
		return this.ranks.get(w);
	}
	
	/**
	 * Sets the rank for the given possible world.
	 * @param w an possible world.
	 * @param value the rank for the possible world.
	 */
	public void setRank(PriestWorldAdapted w, Integer value){		
		if(value < 0)
			throw new IllegalArgumentException("Illegal rank value " + value + ". Ranks must be greater or equal zero.");
		this.ranks.put(w, value);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logic.Interpretation#satisfies(org.tweetyproject.logic.Formula)
	 */
	@Override
	public boolean satisfies(Conditional formula) throws IllegalArgumentException{
		Conditional c = (Conditional) formula;
		Integer rankPremiseAndConclusion = this.rank(c.getConclusion().combineWithAnd(c.getPremise().iterator().next()));
		Integer rankPremiseAndNotConclusion = this.rank((PlFormula)c.getConclusion().complement().combineWithAnd(c.getPremise().iterator().next()));
		return rankPremiseAndConclusion < rankPremiseAndNotConclusion;		
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.Interpretation#satisfies(org.tweetyproject.logic.KnowledgeBase)
	 */
	@Override
	public boolean satisfies(ClBeliefSet beliefBase){
		for(Formula f: beliefBase)
			if(!(f instanceof Conditional))
				throw new IllegalArgumentException();
			else if(!this.satisfies((Conditional)f))
				return false;
		return true;
	}
	
	/**
	 * Sets the rank of every interpretation i that does not satisfy
	 * the given set of formulas to RankingFunction.INFINITY.
	 * @param formulas a set of first-order formulas.
	 */
	public void forceStrictness(Set<PlFormula> formulas){
		for(PriestWorldAdapted w: this.ranks.keySet())
			if(!w.satisfies(formulas))
				this.setRank(w, RankingFunctionThreeValued.INFINITY);
	}
	
	/**
	 * Gets the rank of the given formula. Throws an IllegalArgumentException when
	 * the language of the formula does not correspond to the language of the
	 * interpretations this ranking function is defined on. Otherwise the rank of a formula
	 * is defined as the minimal rank of its satisfying interpretations.
	 * @param formula a formula.
	 * @return the rank of the given formula.
	 * @throws IllegalArgumentException if the languages of the formula does not correspond to the language of the
	 * 		interpretations this ranking function is defined on.
	 */
	public Integer rank(PlFormula formula) throws IllegalArgumentException{
		Integer rank = RankingFunctionThreeValued.INFINITY;
		// J: Iterate over all possible worlds 
		for(PriestWorldAdapted i: this.ranks.keySet()) {
			//System.out.println(i + " >>> " + i.satisfies(formula));
			
			// J: Check if the observed possible world satisfies the given formula
			// J: Here we expect 
			if(i.satisfies(formula))
				// J: Overwrite the current rank if the observed possible world has a lower rank!
				if(this.ranks.get(i).compareTo(rank)<0)
					rank = this.ranks.get(i); 
		}
		return rank;
	}
	
	/**
	 * Returns the minimal rank of this OCF.
	 * @return the minimal rank of this OCF.
	 */
	private Integer minimalRank(){
		Integer min = RankingFunctionThreeValued.INFINITY;
		for(Integer i: this.ranks.values())
			if(i < min)
				min = i;
		return min;
	}
	
	/**
	 * Normalizes this OCF, i.e. appropriately shifts the ranks
	 * such that the minimal rank equals zero. 
	 */
	public void normalize(){
		Integer minimalRank = this.minimalRank();
		for(PriestWorldAdapted w: this.ranks.keySet()){
			if(this.rank(w) != RankingFunctionThreeValued.INFINITY)
				this.ranks.put(w, this.rank(w)-minimalRank);
		}
			
	}
	
	/**
	 * Checkes whether this OCF is normalized, i.e. whether its
	 * minimal rank value is zero.
	 * @return "true" if this OCF is normalized
	 */
	public boolean isNormalized(){
		return this.minimalRank() == 0;
	}
	
	/**
	 * Returns all interpretations that are mapped to a rank
	 * unequal to INFINITY.
	 * @return all interpretations that are mapped to a rank
	 * unequal to INFINITY.
	 */
	public Set<PriestWorldAdapted> getPriestWorlds(){
		Set<PriestWorldAdapted> worlds = new HashSet<PriestWorldAdapted>();
		for(PriestWorldAdapted w: this.ranks.keySet())
			if(this.ranks.get(w) < RankingFunctionThreeValued.INFINITY)
				worlds.add(w);
		return worlds;
	}
	
	/**
	 * Returns all interpretations that are mapped to 0
	 * @return all interpretations that are mapped to 0
	 */
	public Set<PriestWorldAdapted> getPlausibleWorlds(){
		Set<PriestWorldAdapted> worlds = new HashSet<PriestWorldAdapted>();
		for(PriestWorldAdapted w: this.ranks.keySet())
			if(this.ranks.get(w) == 0)
				worlds.add(w);
		return worlds;
	}
	
	/**
	 * Returns the signature of the first-order language this ranking function
	 * is defined on.
	 * @return the signature of the first-order language this ranking function
	 * is defined on.
	 */
	public PlSignature getSignature(){
		return this.signature;
	}
	
	/**
	 * Checks whether the given possible world w verifies the given 
	 * conditional (B|A), i.e. whether w satisfies A and B
	 * @param w a possible world
	 * @param c a conditional.
	 * @return "true" if the given possible world verifies the given conditional. 
	 */
	public static boolean verifies(PriestWorldAdapted w, Conditional c){
		PlFormula formula = c.getPremise().iterator().next().combineWithAnd(c.getConclusion());
		return w.satisfies(formula);
	}
	
	/**
	 * Checks whether the given possible world w falsifies the given 
	 * conditional (B|A), i.e. whether w satisfies A and not B
	 * @param w a possible world
	 * @param c a conditional.
	 * @return "true" if the given possible world falsifies the given conditional. 
	 */
	public static boolean falsifies(PriestWorldAdapted w, Conditional c){
		PlFormula formula = c.getPremise().iterator().next().combineWithAnd(c.getConclusion().complement());
		return w.satisfies(formula);
	}
	
	/**
	 * Checks whether the given possible world w satisfies the given 
	 * conditional (B|A), i.e. whether w does not falsify c.
	 * @param w a possible world
	 * @param c a conditional.
	 * @return "true" if the given possible world satisfies the given conditional. 
	 */
	public static boolean satisfies(PriestWorldAdapted w, Conditional c){
		return !RankingFunctionThreeValued.falsifies(w, c);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s = "[\n";
		Iterator<PriestWorldAdapted> it = this.ranks.keySet().iterator();
		while(it.hasNext()){
			PriestWorldAdapted w = it.next();
			s += "  " + w + " => ";
			if(this.rank(w).equals(RankingFunctionThreeValued.INFINITY))
				s += "INFINITY";
			else s += this.rank(w);
			s += "\n";
		}
		s += "]";
		return s;
	}
	
	@Override
	public boolean equals(Object other) {
		if(! (other instanceof RankingFunctionThreeValued))
			return false;
		
		RankingFunctionThreeValued oc = (RankingFunctionThreeValued)other;
		return ranks.equals(oc.ranks);
	}
}
