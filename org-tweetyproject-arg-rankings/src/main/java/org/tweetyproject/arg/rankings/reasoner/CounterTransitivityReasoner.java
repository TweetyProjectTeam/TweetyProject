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
package org.tweetyproject.arg.rankings.reasoner;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.semantics.LatticeArgumentRanking;


/**
 * This class implements the argument ranking approach of [Pu, Luo,
 * Ranking Arguments based on Counter-Transitivity 2017].
 * 
 * 
 * @author Sebastian Franke
 */
public class CounterTransitivityReasoner  extends AbstractRankingReasoner<LatticeArgumentRanking>{
	
	LatticeArgumentRanking rank;
	solver sol;
	public enum solver {
	   quality, cardinality, qualityFirst, cardinalityFirst, gfpCardinality, simpleDominance
	}
	
	
	
	public CounterTransitivityReasoner(solver sol, LatticeArgumentRanking rank) {
		this.sol= sol;
		this.rank = rank;
	}
	
	@Override
	public Collection<LatticeArgumentRanking> getModels(ArgumentationFramework bbase) {
		Collection<LatticeArgumentRanking> ranks = new HashSet<LatticeArgumentRanking>();
		ranks.add(this.getModel(bbase));
		
			
		return ranks;
	}

	@Override
	public LatticeArgumentRanking getModel(ArgumentationFramework bbase) {
		//switch the type of solver chosen in the constructor
		switch(this.sol) {
			case cardinality:
				return cardinality((DungTheory)bbase);
			case gfpCardinality:
				return gfpCardinality((DungTheory)bbase);
			case quality:
				return quality((DungTheory)bbase, this.rank);
			case qualityFirst:
				return qualityFirst((DungTheory)bbase, this.rank);
			case cardinalityFirst:
				return cardinalityFirst((DungTheory)bbase, rank);
			case simpleDominance:
				return simpleDominance((DungTheory)bbase, rank);
			default:{
				System.out.println("The chosen function does not exist.");
			}
		}
		return null;
	}
	
	/**
	 * orders arguments after the amount of attackers they have.
	 * The less attackers, the higher the rank
	 */
	public LatticeArgumentRanking cardinality(DungTheory af) {
		LatticeArgumentRanking card = new LatticeArgumentRanking(af);
		//compare every argument to every argument
		for(Argument arg1 : af) {
			for(Argument arg2 : af) {
				if(arg1 != arg2) {
					//compare the sizes of the attacker-sets
					if(af.getAttackers(arg1).size() > af.getAttackers(arg2).size()) {
						card.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);

					}
					else if(af.getAttackers(arg1).size() < af.getAttackers(arg2).size()) {
						card.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
					}
					else if(af.getAttackers(arg1).size() == af.getAttackers(arg2).size()) {
						card.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
						card.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
					}
				}
			}
			
		}
		
		return card;
	}
	

	/**
	 * ranks arguments according to a given ranking. It decides the highest ranked attacker of each argument wrt the underlying ranking
	 * and then ranks them after their best attacker
	 */
	public LatticeArgumentRanking quality(DungTheory af, LatticeArgumentRanking ra) {

		
		LatticeArgumentRanking mostValueableAttacker = new LatticeArgumentRanking(af);
		HashMap<Argument, Argument> maxAttacker = new HashMap<Argument, Argument>();
		//find the strongest attacker of each argument
		for(Argument arg : af) {
			//create a placeholder argument for the strongest attacker
			Argument max = new Argument("placeholder");
			if(af.getAttackers(arg).size() == 0)
				maxAttacker.put(arg,  null);
			else {

				max = af.getAttackers(arg).iterator().next();
				for(Argument att : af.getAttackers(arg)) {
					if(ra.isStrictlyLessOrEquallyAcceptableThan(max, att)) {
						max = att;
					}
					maxAttacker.put(arg,  max);
				}
			}
			
		}
		//compare every argument to every argument
		for(Argument arg1 : maxAttacker.keySet()) {
			for(Argument arg2 : maxAttacker.keySet()) {
				if(arg1 != arg2) {
					//if an argument has no attackers, it is always superior
					if(maxAttacker.get(arg1) == null) {
						mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
					}
					if(maxAttacker.get(arg2) == null) {
						mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
					}
					if(maxAttacker.get(arg1) != null && maxAttacker.get(arg2) != null) {
						if(ra.compare(maxAttacker.get(arg1), maxAttacker.get(arg2)) == 0) {
							mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
							mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
						}
						else if(ra.compare(maxAttacker.get(arg1), maxAttacker.get(arg2)) > 0) {
							mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
							
						}
						else if(ra.compare(maxAttacker.get(arg1), maxAttacker.get(arg2)) < 0) {
							mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
							
						}
					}
				}
			}
		}
		
		
		return mostValueableAttacker;
	}
	
	/**
	 * ranks arguments according to the quality function. If 2 arguments have the same avlue according to quality, the decsision is left to cardinality
	 */
	public LatticeArgumentRanking qualityFirst(DungTheory af, LatticeArgumentRanking ra) {
		LatticeArgumentRanking qual = quality(af, ra);
		LatticeArgumentRanking card = cardinality(af);
		LatticeArgumentRanking result = new LatticeArgumentRanking(af);
		//compare every argument to every argument
		for(Argument arg1 : af) {
			for(Argument arg2 : af) {
				if(arg1 != arg2) {
					//only check cardinality, if quality is the same
					if(qual.compare(arg1, arg2) == 0) {
						if(card.compare(arg1, arg2) < 0) {
							result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
						}
						else if(card.compare(arg1, arg2) > 0){
							result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
						}
						else {
								result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
								result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
						}
					}
					else if(qual.compare(arg1, arg2) < 0){
						result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
					}
					else if(qual.compare(arg1, arg2) > 0){
						result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * ranks arguments according to the cardinality function. If 2 arguments have the same value according to cardinality, the decision is left to quality
	 */
	public LatticeArgumentRanking cardinalityFirst(DungTheory af, LatticeArgumentRanking ra) {
		LatticeArgumentRanking qual = quality(af, ra);
		LatticeArgumentRanking card = cardinality(af);
		LatticeArgumentRanking result = new LatticeArgumentRanking(af);
		//compare every argument to every argument
		for(Argument arg1 : af) {
			for(Argument arg2 : af) {
				if(arg1 != arg2) {
					//only check quality, if cardinality is the same
					if(card.compare(arg1, arg2) == 0) {
						if(qual.compare(arg1, arg2) < 0) {
							result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
						}
						else if(qual.compare(arg1, arg2) > 0){
							result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
						}
						else {
								result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
								result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
						}
					}
					else if(card.compare(arg1, arg2) < 0){
						result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
					}
					else if(card.compare(arg1, arg2) > 0){
						result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * calculates the greatest fix point.
	 * First the quality is computed based on a ranking that only ranks unattacked arguments higher than the rest. 
	 * Based on this ranking a cardinality first ranking  is constructed. Based on this new ranking a quality ranking is constructed.
	 * This process is repeated until two consecutive iterations of cardinality first reach the same conclusion
	 */
	public LatticeArgumentRanking gfpCardinality(DungTheory af) {
		
		LatticeArgumentRanking ra = new LatticeArgumentRanking(af);
		//set the rank in which only not attacked arguments are ranked higehr
		for(Argument a : af) {
			for(Argument b : af) {
				if(af.getAttackers(a).size() == 0 && af.getAttackers(b).size() > 0) {
					ra.setStrictlyLessOrEquallyAcceptableThan(b,  a);
				}
			}
		}
		
		LatticeArgumentRanking quality = this.quality(af, ra);
		LatticeArgumentRanking card = this.cardinalityFirst(af, ra);
		
		LatticeArgumentRanking card2 = new LatticeArgumentRanking(af);
		//check if the last iteraions yield the same result
		while(!card.isSame(card2)) {
			
			quality = this.quality(af, card);
			card = card2;
			card2 = this.cardinalityFirst(af, quality);

		}
		return card2;
	}
	
	/**
	 * only ranks two arguments if one is better according to quality and cardinality
	 */
	public LatticeArgumentRanking simpleDominance(DungTheory af, LatticeArgumentRanking ra) {
		LatticeArgumentRanking card = this.cardinality(af);
		LatticeArgumentRanking qual = this.quality(af, ra);
		LatticeArgumentRanking result = new LatticeArgumentRanking(af);
		//compare every argument to every argument
		for(Argument a : af) {
			for(Argument b : af) {
				//check if the argument is superior in both categories
				if(card.compare(a, b) == 1 && qual.compare(a, b) == 1)
					result.setStrictlyLessOrEquallyAcceptableThan(b, a);
			}
		}
		
		return result;
	}



}
