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
package net.sf.tweety.logics.rcl.semantics;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.fol.semantics.*;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.rcl.*;
import net.sf.tweety.logics.rcl.syntax.*;

/**
 * A relational ranking function (or relational ordinal conditional function, ROCF) that maps
 * Herbrand interpretations to integers. 
 * 
 * <br><br>See W. Spohn. Ordinal conditional functions: a dynamic theory of epistemic states.
 * In W.L. Harper and B. Skyrms, editors, Causation in Decision, Belief Change, and Statistics, II,
 * pages 105-134. Kluwer Academic Publishers, 1988.
 * 
 * <br><br>See also [Kern-Isberner,Thimm, "A Ranking Semantics for Relational Defaults", in preparation].
 * 
 * @author Matthias Thimm
 *
 */
public class RelationalRankingFunction extends AbstractInterpretation {

	/**
	 * Integer used to define infinity.
	 */
	public static final Integer INFINITY = Integer.MAX_VALUE;
	
	/**
	 * The ranks of the Herbrand interpretations.
	 */
	private Map<HerbrandInterpretation,Integer> ranks;
	
	/**
	 * The signature of the language this ranking function
	 * is defined on.
	 */
	private FolSignature signature;
	
	/**
	 * Creates a new ranking function mapping each
	 * given interpretation to zero.
	 * @param signature the signature of the language this ranking function
	 * is defined on.
	 */
	public RelationalRankingFunction(FolSignature signature){
		this.signature = signature;		
		this.ranks = new HashMap<HerbrandInterpretation,Integer>();
		HerbrandBase hBase = new HerbrandBase(this.signature);
		for(HerbrandInterpretation w: hBase.allHerbrandInterpretations())
			this.ranks.put(w, 0);			
	}
	
	/**
	 * Gets the rank of the given Herbrand interpretation.
	 * @param w a Herbrand interpretation.
	 * @return the rank of the given Herbrand interpretation.
	 * @throws IllegalArgumentException if the given Herbrand interpretation has no
	 *   rank in this ranking function.
	 */
	public Integer rank(HerbrandInterpretation w) throws IllegalArgumentException{
		if(!this.ranks.containsKey(w))
			throw new IllegalArgumentException("No rank defined for the Herbrand interpretation " + w);
		return this.ranks.get(w);
	}
	
	/**
	 * Sets the rank for the given Herbrand interpretation.
	 * @param w a Herbrand interpretation.
	 * @param value the rank for the Herbrand interpretation.
	 */
	public void setRank(HerbrandInterpretation w, Integer value){		
		if(value < 0)
			throw new IllegalArgumentException("Illegal rank value " + value + ". Ranks must be greater or equal zero.");
		this.ranks.put(w, value);
	}
	
	/**
	 * Gets the rank of the given sentence (ground formula). Throws an IllegalArgumentException when
	 * the language of the formula does not correspond to the language of the
	 * interpretations this ranking function is defined on or the formula is not a sentence. Otherwise the rank of a formula
	 * is defined as the minimal rank of its satisfying interpretations.
	 * @param formula a formula.
	 * @return the rank of the given formula.
	 * @throws IllegalArgumentException if the languages of the formula does not correspond to the language of the
	 * 		interpretations this ranking function is defined on or the formula is not a sentence.
	 */
	public Integer rank(FolFormula formula) throws IllegalArgumentException{
		if(!formula.isClosed())
			throw new IllegalArgumentException("Formula " + formula + " is not closed.");
		Integer rank = RelationalRankingFunction.INFINITY;
		for(Interpretation i: this.ranks.keySet())
			if(i.satisfies(formula))
				if(this.ranks.get(i).compareTo(rank)<0)
					rank = this.ranks.get(i); 
		return rank;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Interpretation#satisfies(net.sf.tweety.Formula)
	 */
	@Override
	public boolean satisfies(Formula formula) throws IllegalArgumentException {
		if(!(formula instanceof RelationalConditional))
			throw new IllegalArgumentException("Formula " + formula + " is not a relational conditional expression.");
		RelationalConditional rc = (RelationalConditional) formula;
		// if conditional is ground check for classical satisfiability
		if(rc.isGround()){
			Integer rankPremiseAndConclusion = this.rank(rc.getConclusion().combineWithAnd(rc.getPremise().iterator().next()));
			Integer rankPremiseAndNotConclusion = this.rank((HerbrandInterpretation) rc.getConclusion().complement().combineWithAnd(rc.getPremise().iterator().next()));
			return rankPremiseAndConclusion < rankPremiseAndNotConclusion;
		}
		// following [Kern-Isberner,Thimm, "A Ranking Semantics for Relational Defaults", in preparation],
		// a relational ranking function satisfies an open conditional if the set of prototypes is non-empty AND (
		// either the minimal rank of conclusion and premise is smaller than the minimal rank of negated conclusion and
		// premise (wrt. all instances) OR the prototypes of the conditional have a smaller rank in its negated form
		// as the prototypes of the complement conditional in the positive form)		
		Set<RelationalConditional> prototypes = this.getPrototypes(rc);
		if(prototypes.isEmpty())
			return false;
		int minPositive = -1;
		int minNegative = -1;
		for(RelationalFormula rf: rc.allGroundInstances(this.signature.getConstants())){
			RelationalConditional instance = (RelationalConditional) rf;
			int rankPremiseAndConclusion = this.rank(instance.getConclusion().combineWithAnd(instance.getPremise().iterator().next()));
			int rankPremiseAndNotConclusion = this.rank((HerbrandInterpretation) instance.getConclusion().complement().combineWithAnd(instance.getPremise().iterator().next()));
			if(minPositive == -1) minPositive = rankPremiseAndConclusion;
			else minPositive = minPositive > rankPremiseAndConclusion ? rankPremiseAndConclusion : minPositive;
			if(minNegative == -1) minNegative = rankPremiseAndNotConclusion;
			else minNegative = minNegative > rankPremiseAndNotConclusion ? rankPremiseAndNotConclusion : minNegative;
		}
		if(minPositive < minNegative) return true;		
		Set<RelationalConditional> antiPrototypes = this.getPrototypes(new RelationalConditional(rc.getPremise().iterator().next(), (FolFormula)rc.getConclusion().complement()));
		for(RelationalConditional prot: prototypes){
			for(RelationalConditional anti: antiPrototypes){
				int rankPremiseAndConclusion = this.rank(anti.getConclusion().combineWithAnd(anti.getPremise().iterator().next()));
				int rankPremiseAndNotConclusion = this.rank((HerbrandInterpretation) prot.getConclusion().complement().combineWithAnd(prot.getPremise().iterator().next()));
				if(rankPremiseAndConclusion >= rankPremiseAndNotConclusion)
					return false;
			}
		}		
		return true;
		/* ============================ THIRD VERSION
		// a relational ranking function satisfies an open conditional if there is one instantiation such that
		// (1) the rank of premise and conclusion is minimal under all instances
		// (2) the rank of premise and negated conclusion is minimal under all instances
		// (3) the conditional is classically accepted
		Set<RelationalFormula> allInstances = rc.allGroundInstances(this.signature.getConstants());
		for(RelationalFormula rf: allInstances){
			// check (3)
			if(!this.satisfies(rf)) continue;
			// check (1) and (2)
			RelationalConditional instance = (RelationalConditional) rf;
			FolFormula conjPremConc = instance.getPremise().iterator().next().combineWithAnd(instance.getConclusion());
			FolFormula conjPremNegConc =  instance.getPremise().iterator().next().combineWithAnd(instance.getConclusion().complement());
			Integer concRank = this.rank(conjPremConc);
			Integer negConcRank = this.rank(conjPremNegConc);			
			boolean success = true;
			for(RelationalFormula rf2: allInstances){
				RelationalConditional otherInstance = (RelationalConditional) rf2;						
				FolFormula otherConjPremConc = otherInstance.getPremise().iterator().next().combineWithAnd(otherInstance.getConclusion());
				FolFormula otherConjPremNegConc = otherInstance.getPremise().iterator().next().combineWithAnd(otherInstance.getConclusion().complement());
				Integer otherConcRank = this.rank(otherConjPremConc);
				Integer otherNegConcRank = this.rank(otherConjPremNegConc);
				if(otherConcRank < concRank || otherNegConcRank < negConcRank){
					success = false;
					break;
				}
			}			
			if(success)
				return true;
		}
		return false;*/		
		/* ============================ SECOND VERSION
		// a relational ranking function satisfies an open conditional if (1) there is one instantiation 
		// where the conjunction of conclusion and premise has rank strictly smaller than all instantiations
		// of the conjunction of the premise and the negated conclusion AND (2) the rank of the negated premise is maximal
		// under all negated premises
		Set<RelationalFormula> allInstances = rc.allGroundInstances(this.signature.getConstants());
		for(RelationalFormula rf: allInstances){
			RelationalConditional instance = (RelationalConditional) rf;
			FolFormula conjPremConc = instance.getPremise().iterator().next().combineWithAnd(instance.getConclusion());
			FolFormula negPremise =  instance.getPremise().iterator().next().complement();
			Integer instanceRank = this.rank(conjPremConc);
			Integer premRank = this.rank(negPremise);
			boolean success = true;
			for(RelationalFormula rf2: allInstances){
				RelationalConditional otherInstance = (RelationalConditional) rf2;
				// check (1)				
				FolFormula otherConjPremConc = otherInstance.getPremise().iterator().next().combineWithAnd(otherInstance.getConclusion().complement());
				Integer otherInstanceRank = this.rank(otherConjPremConc);
				if(otherInstanceRank <= instanceRank){
					success = false;
					break;
				}
				// check (2)
				FolFormula otherNegPremise = otherInstance.getPremise().iterator().next().complement();
				Integer otherPremRank = this.rank(otherNegPremise);
				if(otherPremRank > premRank){
					success = false;
					break;
				}
			}			
			if(success)
				return true;
		}
		return false;*/		
		/* ========================== FIRST VERSION		 
		// a relational ranking function satisfies an open conditional if there is one instantiation
		// where the premise has minimal rank and the conditional is classically satisfied		
		Set<RelationalFormula> allInstances = rc.allGroundInstances(this.signature.getConstants());
		for(RelationalFormula rf: allInstances){
			RelationalConditional instance = (RelationalConditional) rf;
			// check satisfaction
			if(!this.satisfies(instance)) continue;
			// check minimality of premise
			Integer instanceRank = this.rank(instance.getPremise().iterator().next());
			for(RelationalFormula rf2: allInstances){
				RelationalConditional otherInstance = (RelationalConditional) rf2;
				if(instance != otherInstance)
					if(this.rank(otherInstance.getPremise().iterator().next()) < instanceRank)
						continue;
			}
			return true;
		}
		return false;
		*/
	}

	/** Returns the set of instances with weak prototypes.
	 * @param rc a relational conditional
	 * @return the set of instances with weak prototypes.
	 */
	private Set<RelationalConditional> getWeakPrototypes(RelationalConditional rc){
		Set<RelationalConditional> weakPrototypes = new HashSet<RelationalConditional>();
		for(RelationalFormula rf: rc.allGroundInstances(this.signature.getConstants())){
			RelationalConditional instance = (RelationalConditional) rf;
			if(weakPrototypes.isEmpty()){
				weakPrototypes.add(instance);
			}else{
				RelationalConditional previousInstance = weakPrototypes.iterator().next();
				int instanceRank = this.rank(instance.getConclusion().combineWithAnd(instance.getPremise().iterator().next()));
				int previousRank = this.rank(previousInstance.getConclusion().combineWithAnd(previousInstance.getPremise().iterator().next()));
				if(instanceRank == previousRank)
					weakPrototypes.add(instance);
				else if(instanceRank < previousRank){
					weakPrototypes.clear();
					weakPrototypes.add(instance);
				}
			}
		}
		//remove those instances that are not accepted
		Set<RelationalConditional> result = new HashSet<RelationalConditional>();
		for(RelationalConditional r: weakPrototypes)
			if(this.satisfies(r))
				result.add(r);
		return result;
	}
	
	/** Returns the set of instances with prototypes.
	 * @param rc a relational conditional
	 * @return the set of instances with prototypes.
	 */
	private Set<RelationalConditional> getPrototypes(RelationalConditional rc){
		Set<RelationalConditional> weakPrototypes = this.getWeakPrototypes(rc);
		Set<RelationalConditional> prototypes = new HashSet<RelationalConditional>();
		for(RelationalConditional r: weakPrototypes){
			RelationalConditional instance = r;
			if(prototypes.isEmpty()){
				prototypes.add(instance);
			}else{
				RelationalConditional previousInstance = prototypes.iterator().next();
				int instanceRank = this.rank((HerbrandInterpretation) instance.getConclusion().complement().combineWithAnd(instance.getPremise().iterator().next()));
				int previousRank = this.rank((HerbrandInterpretation) previousInstance.getConclusion().complement().combineWithAnd(previousInstance.getPremise().iterator().next()));
				if(instanceRank == previousRank)
					prototypes.add(instance);
				else if(instanceRank < previousRank){
					prototypes.clear();
					prototypes.add(instance);
				}
			}
		}		
		return prototypes;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Interpretation#satisfies(net.sf.tweety.BeliefBase)
	 */
	@Override
	public boolean satisfies(BeliefBase beliefBase)	throws IllegalArgumentException {
		if(!(beliefBase instanceof RclBeliefSet))
			throw new IllegalArgumentException("Knowledge base is not a relational conditional knowledge base.");
		for(Formula f: ((RclBeliefSet)beliefBase))
			if(!this.satisfies(f))
				return false;
		return true;
	}

	/**
	 * Returns the minimal rank of this OCF.
	 * @return the minimal rank of this OCF.
	 */
	private Integer minimalRank(){
		Integer min = RelationalRankingFunction.INFINITY;
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
		for(HerbrandInterpretation w: this.ranks.keySet()){
			if(this.rank(w) != RelationalRankingFunction.INFINITY)
				this.ranks.put(w, this.rank(w)-minimalRank);
		}			
	}

	/**
	 * Returns all interpretations that are mapped to a rank
	 * unequal to INFINITY.
	 * @return all interpretations that are mapped to a rank
	 * unequal to INFINITY.
	 */
	public Set<HerbrandInterpretation> getPossibleInterpretations(){
		Set<HerbrandInterpretation> worlds = new HashSet<HerbrandInterpretation>();
		for(HerbrandInterpretation w: this.ranks.keySet())
			if(this.ranks.get(w) < RelationalRankingFunction.INFINITY)
				worlds.add(w);
		return worlds;
	}
	
	/** Returns the number of instances of "rc" that are falsified by
	 * the given interpretation.
	 * @param w a Herbrand interpretation.
	 * @param rc a relational conditional.
	 * @return the number of instances of "rc" that are falsified by
	 * the given interpretation.
	 */
	public Integer numFalsifiedInstances(HerbrandInterpretation w, RelationalConditional rc){
		Set<RelationalFormula> instances = rc.allGroundInstances(this.signature.getConstants());
		int num = 0;
		for(RelationalFormula rf: instances){
			FolFormula f = ((RelationalConditional)rf).getPremise().iterator().next().combineWithAnd(((RelationalConditional)rf).getConclusion().complement());
			if(w.satisfies(f)) num++;			
		}
		return num;
	}
	
	/** Returns the number of instances of "rc" that are verified by
	 * the given interpretation.
	 * @param w a Herbrand interpretation.
	 * @param rc a relational conditional.
	 * @return the number of instances of "rc" that are verified by
	 * the given interpretation.
	 */
	public Integer numVerifiedInstances(HerbrandInterpretation w, RelationalConditional rc){
		Set<RelationalFormula> instances = rc.allGroundInstances(this.signature.getConstants());
		int num = 0;
		for(RelationalFormula rf: instances){
			FolFormula f = ((RelationalConditional)rf).getPremise().iterator().next().combineWithAnd(((RelationalConditional)rf).getConclusion());
			if(w.satisfies(f)) num++;			
		}
		return num;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s = "[\n";
		Iterator<HerbrandInterpretation> it = this.ranks.keySet().iterator();
		while(it.hasNext()){
			HerbrandInterpretation w = it.next();
			s += "  " + w + " => ";
			if(this.rank(w).equals(RelationalRankingFunction.INFINITY))
				s += "INFINITY";
			else s += this.rank(w);
			s += "\n";
		}
		s += "]";
		return s;
	}
}
