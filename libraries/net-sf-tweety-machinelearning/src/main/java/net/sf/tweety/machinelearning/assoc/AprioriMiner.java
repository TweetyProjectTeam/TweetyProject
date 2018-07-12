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
package net.sf.tweety.machinelearning.assoc;

import java.util.Collection;
import java.util.HashSet;

/**
 * Implements the classical Apriori algorithm for association rule mining, cf.
 * [R. Agrawal, R. Srikant. Fast algorithms for mining association rules in large databases. VLDB 1994.]
 * 
 * @author Matthias Thimm
 *
 * @param <T> the type of items
 */
public class AprioriMiner<T> extends AbstractAssociationRuleMiner<T> {

	/** the minimum confidence for mined rules. */
	private double minsupport;
	/** the minimum support for mined rules. */
	private double minconf;	
	
	/**
	 * Creates a new Apriori miner with the given minimum support and
	 * minimum confidence values.
	 * @param minsupport the minimum confidence for mined rules.
	 * @param minconf the minimum support for mined rules.
	 */
	public AprioriMiner(double minsupport, double minconf){
		this.minsupport = minsupport;
		this.minconf = minconf;		
	}
	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.assoc.AssociationRuleMiner#mineRules(java.util.Collection, int, int)
	 */
	@Override
	public Collection<AssociationRule<T>> mineRules(Collection<Collection<T>> database, int conclusion_limit, int total_limit) {
		Collection<Collection<T>> sets = this.mineFrequentSets(database, total_limit);
		Collection<AssociationRule<T>> rules = new HashSet<AssociationRule<T>>();
		if(conclusion_limit < 1)
			return rules;
		for(Collection<T> set: sets){
			// determine all rules with single conclusion
			Collection<Collection<T>> lastLevel_conc = new HashSet<Collection<T>>();
			for(T item: set){
				AssociationRule<T> rule = new AssociationRule<T>();
				for(T item2: set)
					if(item == item2)
						rule.addToConclusion(item2);
					else rule.addToPremise(item2);
				if(rule.confidence(database) >= this.minconf){
					rules.add(rule);
					lastLevel_conc.add(rule.getConclusion());
				}
			}
			// iterate for conclusions with more elements
			int card = 1;
			while(!lastLevel_conc.isEmpty() && conclusion_limit > card){
				Collection<Collection<T>> nextLevel_conc = this.nextLevel(lastLevel_conc, card);
				card++;
				lastLevel_conc.clear();
				// check for min confidence
				for(Collection<T> conc: nextLevel_conc){
					AssociationRule<T> rule = new AssociationRule<T>();
					for(T item2: set)
						if(conc.contains(item2))
							rule.addToConclusion(item2);
						else rule.addToPremise(item2);
					if(rule.confidence(database) >= this.minconf){
						rules.add(rule);						
						lastLevel_conc.add(rule.getConclusion());
					}
				}
			}
		}
		return rules;
	}
			
	/**
	 * Extracts all sets of items from database with support at least
	 * <code>minsupport</code>.
	 * @param database some database
	 * @param maxsize the maximal size of mined item sets
	 * @return all sets of items from database with support at least
	 * <code>minsupport</code>.
	 */
	private Collection<Collection<T>> mineFrequentSets(Collection<Collection<T>> database, int maxsize){
		Collection<Collection<T>> sets = new HashSet<Collection<T>>();
		Collection<T> items = new HashSet<T>();
		for(Collection<T> t: database)
			items.addAll(t);
		// check all 1-element sets
		for(T item: items){
			if(AssociationRule.support(item, database) >= this.minsupport){
				Collection<T> set = new HashSet<T>();
				set.add(item);
				sets.add(set);
			}				
		}
		Collection<Collection<T>> lastLevel = new HashSet<Collection<T>>();
		lastLevel.addAll(sets);
		// iterate for larger sets
		int card = 1;
		while(!lastLevel.isEmpty() && card < maxsize){
			Collection<Collection<T>> nextLevel = this.nextLevel(lastLevel,card);
			card++;
			lastLevel.clear();
			//check for min support
			for(Collection<T> cand: nextLevel)
				if(AssociationRule.support(cand, database) >= this.minsupport){
					lastLevel.add(cand);
					sets.add(cand);
				}			
		}
		return sets;
	}
	
	/**
	 * Generates all sets of cardinality <code>cardinality</code> s.t. all subsets of cardinality
	 * <code>cardinality</code>-1 are contained in the given set <code>lastLevel</code>. 
	 * @param lastLevel a set of sets of the same cardinality <code>cardinality</code>
	 * @param cardinality the cardinality of all sets in <code>lastLevel</code>
	 * @return  all sets of cardinality <code>cardinality</code> s.t. all subsets of cardinality
	 * <code>cardinality</code>-1 are contained in the given set <code>lastLevel</code>.
	 */
	private Collection<Collection<T>> nextLevel(Collection<Collection<T>> lastLevel, int cardinality){
		Collection<Collection<T>> nextLevel = new HashSet<Collection<T>>();
		Collection<T> candidate;
		for(Collection<T> set1: lastLevel)
			for(Collection<T> set2: lastLevel){
				candidate = new HashSet<T>(set1);
				candidate.addAll(set2);
				if(candidate.size() == cardinality+1){
					if(this.checkSubsetCondition(candidate, lastLevel))
						nextLevel.add(candidate);
				}
			}		
		return nextLevel;
	}
	
	/**
	 * Checks whether all subsets of <code>set</code> obtained by removing
	 * exactly one element, are contained in the given <code>sets</code>. 
	 * @param set some set
	 * @param sets a set of sets
	 * @return "true" iff all subsets of <code>set</code> obtained by removing
	 * exactly one element, are contained in the given <code>sets</code>.
	 */
	private boolean checkSubsetCondition(Collection<T> set, Collection<Collection<T>> sets){
		Collection<T> sub = new HashSet<T>();
		for(T item: set){
			sub.clear();
			sub.addAll(set);
			sub.remove(item);
			if(!sets.contains(sub))
				return false;
		}
		return true;
	}
}
