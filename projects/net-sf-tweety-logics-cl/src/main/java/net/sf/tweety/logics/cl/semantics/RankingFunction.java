package net.sf.tweety.logics.cl.semantics;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.cl.*;
import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula;
import net.sf.tweety.logics.pl.semantics.*;
import net.sf.tweety.logics.pl.syntax.*;


/**
 * A ranking function (or ordinal conditional function, OCF) that maps possible worlds
 * of a propositional language to integers. 
 * 
 * <br><br>See W. Spohn. Ordinal conditional functions: a dynamic theory of epistemic states.
 * In W.L. Harper and B. Skyrms, editors, Causation in Decision, Belief Change, and Statistics, II,
 * pages 105-134. Kluwer Academic Publishers, 1988.
 * 
 * @author Matthias Thimm
 *
 */
public class RankingFunction extends AbstractInterpretation {
	
	/**
	 * Integer used to define infinity.
	 */
	public static final Integer INFINITY = Integer.MAX_VALUE;
	
	/**
	 * The ranks of the possible worlds.
	 */
	private Map<PossibleWorld,Integer> ranks;
	
	/**
	 * The signature of the language this ranking function
	 * is defined on.
	 */
	private PropositionalSignature signature;
	
	/**
	 * Creates a new ranking function mapping each
	 * given interpretation to zero.
	 * @param signature the signature of the language this ranking function
	 * is defined on.
	 */
	public RankingFunction(PropositionalSignature signature){
		this.signature = signature;		
		this.ranks = new HashMap<PossibleWorld,Integer>();
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds(signature))
			this.ranks.put(w, 0);			
	}
	
	/**
	 * Gets the rank of the given possible world.
	 * @param w an possible world.
	 * @return the rank of the given possible world.
	 * @throws IllegalArgumentException if the given possible world has no
	 *   rank in this ranking function.
	 */
	public Integer rank(PossibleWorld w) throws IllegalArgumentException{
		if(!this.ranks.containsKey(w))
			throw new IllegalArgumentException("No rank defined for the possible world " + w);
		return this.ranks.get(w);
	}
	
	/**
	 * Sets the rank for the given possible world.
	 * @param w an possible world.
	 * @param value the rank for the possible world.
	 */
	public void setRank(PossibleWorld w, Integer value){		
		if(value < 0)
			throw new IllegalArgumentException("Illegal rank value " + value + ". Ranks must be greater or equal zero.");
		this.ranks.put(w, value);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logic.Interpretation#satisfies(net.sf.tweety.logic.Formula)
	 */
	@Override
	public boolean satisfies(Formula formula) throws IllegalArgumentException{
		if(!(formula instanceof Conditional))
			throw new IllegalArgumentException("Formula " + formula + " is not a conditional expression.");		
		Conditional c = (Conditional) formula;
		Integer rankPremiseAndConclusion = this.rank(c.getConclusion().combineWithAnd(c.getPremise().iterator().next()));
		Integer rankPremiseAndNotConclusion = this.rank(c.getConclusion().complement().combineWithAnd(c.getPremise().iterator().next()));
		return rankPremiseAndConclusion < rankPremiseAndNotConclusion;		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Interpretation#satisfies(net.sf.tweety.logic.KnowledgeBase)
	 */
	@Override
	public boolean satisfies(BeliefBase beliefBase){
		if(!(beliefBase instanceof ClBeliefSet))
			throw new IllegalArgumentException("Knowledge base is not a conditional knowledge base.");
		for(Formula f: ((ClBeliefSet)beliefBase))
			if(!this.satisfies(f))
				return false;
		return true;
	}
	
	/**
	 * Sets the rank of every interpretation i that does not satisfy
	 * the given set of formulas to RankingFunction.INFINITY.
	 * @param formulas a set of first-order formulas.
	 */
	public void forceStrictness(Set<PropositionalFormula> formulas){
		for(PossibleWorld w: this.ranks.keySet())
			if(!w.satisfies(formulas))
				this.setRank(w, RankingFunction.INFINITY);
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
	public Integer rank(Formula formula) throws IllegalArgumentException{
		Integer rank = RankingFunction.INFINITY;
		for(Interpretation i: this.ranks.keySet())
			if(i.satisfies(formula))
				if(this.ranks.get(i).compareTo(rank)<0)
					rank = this.ranks.get(i); 
		return rank;
	}
	
	/**
	 * Returns the minimal rank of this OCF.
	 * @return the minimal rank of this OCF.
	 */
	private Integer minimalRank(){
		Integer min = RankingFunction.INFINITY;
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
		for(PossibleWorld w: this.ranks.keySet()){
			if(this.rank(w) != RankingFunction.INFINITY)
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
	public Set<PossibleWorld> getPossibleWorlds(){
		Set<PossibleWorld> worlds = new HashSet<PossibleWorld>();
		for(PossibleWorld w: this.ranks.keySet())
			if(this.ranks.get(w) < RankingFunction.INFINITY)
				worlds.add(w);
		return worlds;
	}
	
	/**
	 * Returns the signature of the first-order language this ranking function
	 * is defined on.
	 */
	public PropositionalSignature getSignature(){
		return this.signature;
	}
	
	/**
	 * Checks whether the given possible world w verifies the given 
	 * conditional (B|A), i.e. whether w satisfies A and B
	 * @param w a possible world
	 * @param c a conditional.
	 * @return "true" if the given possible world verifies the given conditional. 
	 */
	public static boolean verifies(PossibleWorld w, Conditional c){
		SimpleLogicalFormula formula = c.getPremise().iterator().next().combineWithAnd(c.getConclusion());
		return w.satisfies(formula);
	}
	
	/**
	 * Checks whether the given possible world w falsifies the given 
	 * conditional (B|A), i.e. whether w satisfies A and not B
	 * @param w a possible world
	 * @param c a conditional.
	 * @return "true" if the given possible world falsifies the given conditional. 
	 */
	public static boolean falsifies(PossibleWorld w, Conditional c){
		SimpleLogicalFormula formula = c.getPremise().iterator().next().combineWithAnd(c.getConclusion().complement());
		return w.satisfies(formula);
	}
	
	/**
	 * Checks whether the given possible world w satisfies the given 
	 * conditional (B|A), i.e. whether w does not falsify c.
	 * @param w a possible world
	 * @param c a conditional.
	 * @return "true" if the given possible world satisfies the given conditional. 
	 */
	public static boolean satisfies(PossibleWorld w, Conditional c){
		return !RankingFunction.falsifies(w, c);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s = "[\n";
		Iterator<PossibleWorld> it = this.ranks.keySet().iterator();
		while(it.hasNext()){
			PossibleWorld w = it.next();
			s += "  " + w + " => ";
			if(this.rank(w).equals(RankingFunction.INFINITY))
				s += "INFINITY";
			else s += this.rank(w);
			s += "\n";
		}
		s += "]";
		return s;
	}
	
	@Override
	public boolean equals(Object other) {
		if(! (other instanceof RankingFunction))
			return false;
		
		RankingFunction oc = (RankingFunction)other;
		return ranks.equals(oc.ranks);
	}
}
