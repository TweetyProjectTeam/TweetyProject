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
package net.sf.tweety.logics.mln;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import net.sf.tweety.commons.util.DefaultSubsetIterator;
import net.sf.tweety.commons.util.RandomSubsetIterator;
import net.sf.tweety.logics.fol.semantics.*;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;


/**
 * This reasoner performs approximate reasoning with MLNs by considering
 * only a subset of all Herbrand interpretations. This subset is chosen
 * by first randomly selecting a set of Herbrand interpretations 
 * and then selecting the subset of this set with maximum weights.
 * 
 * @author Matthias Thimm
 */
public class ApproximateNaiveMlnReasoner extends AbstractMlnReasoner{

	/** The maximum number of interpretations selected from the whole
	 * set of interpretations. Is -1 if all interpretations are to be selected. */
	private long maxNumberOfSelectedInterpretations = -1;
	
	/** The maximum number of interpretations used for the model. Those interpretations
	 * are the subset of the interpretations selected with maximum weight. Is -1
	 * if all interpretations are used for the model.  It has to be
	 * maxNumberOfSelectedInterpretations >= maxNumberOfInterpretationsForModel. */
	private long maxNumberOfInterpretationsForModel = -1;
	
	/**
	 * A Herbrand interpretation with an annotated weight.
	 * @author Matthias Thimm
	 */
	private class WeightedHerbrandInterpretation implements Comparable<WeightedHerbrandInterpretation>{		
		HerbrandInterpretation interpretation;
		double weight;
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(WeightedHerbrandInterpretation arg0) {
			if(this.weight > arg0.weight)
				return 1;
			if(this.weight < arg0.weight)
				return -1;
			return 0;
		}		
	}

	/**
	 * Creates a new ApproximateNaiveMlnReasoner.
	 * @param maxNumberOfSelectedInterpretations the maximum number of interpretations selected from the whole
	 * set of interpretations. Is -1 if all interpretations are to be selected.
	 * @param maxNumberOfInterpretationsForModel the maximum number of interpretations used for the model. Those interpretations
	 * are the subset of the interpretations selected with maximum weight. Is -1
	 * if all interpretations are used for the model. It has to be maxNumberOfSelectedInterpretations >= maxNumberOfInterpretationsForModel.
	 */
	public ApproximateNaiveMlnReasoner(long maxNumberOfSelectedInterpretations, long maxNumberOfInterpretationsForModel){
		this.maxNumberOfSelectedInterpretations = maxNumberOfSelectedInterpretations;
		this.maxNumberOfInterpretationsForModel = maxNumberOfInterpretationsForModel;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.mln.AbstractMlnReasoner#doQuery(net.sf.tweety.logics.mln.MarkovLogicNetwork, net.sf.tweety.logics.fol.syntax.FolFormula, net.sf.tweety.logics.fol.syntax.FolSignature)
	 */
	@Override
	protected double doQuery(MarkovLogicNetwork mln, FolFormula query, FolSignature signature) {
		Map<HerbrandInterpretation,Double> model = this.computeModel(mln,signature);
		double prob = 0;
		for(HerbrandInterpretation hint: model.keySet())
			if(hint.satisfies(query))
				prob += model.get(hint);
		return prob;
	}

	/** Computes the model of the given MLN wrt. the optimization parameters
	 * @param mln some mln
	 * @param query some query
	 * @param signature some signature
	 * @return  the model of the given MLN wrt. the optimization parameters.
	 */
	public Map<HerbrandInterpretation,Double> computeModel(MarkovLogicNetwork mln, FolSignature signature){
		// Queue used for storing the interpretations with maximum weight
		PriorityQueue<WeightedHerbrandInterpretation> pq = new PriorityQueue<WeightedHerbrandInterpretation>();
		// The Herbrand base of the signature
		HerbrandBase hBase = new HerbrandBase(signature);
		// The iterator for Herbrand interpretations
		Iterator<Set<FOLAtom>> it;
		if(this.maxNumberOfSelectedInterpretations == -1 || Math.pow(2, hBase.getAtoms().size()) <= this.maxNumberOfSelectedInterpretations )
			it = new DefaultSubsetIterator<FOLAtom>(hBase.getAtoms());
		else it = new RandomSubsetIterator<FOLAtom>(hBase.getAtoms(),false);
		long count = 0;
		HerbrandInterpretation hInt;
		WeightedHerbrandInterpretation whInt;
		double sumOfWeights = 0;
		while(it.hasNext()){
			hInt = new HerbrandInterpretation(it.next());
			whInt = new WeightedHerbrandInterpretation();
			whInt.interpretation = hInt;
			whInt.weight = this.computeWeight(mln,hInt,signature);
			pq.add(whInt);
			sumOfWeights += whInt.weight;			
			while(pq.size() > this.maxNumberOfInterpretationsForModel){
				whInt = pq.remove();
				sumOfWeights -= whInt.weight;
			}
			count++;
			if(this.maxNumberOfSelectedInterpretations != -1 && this.maxNumberOfSelectedInterpretations <= count)
				break;
		}
		Map<HerbrandInterpretation,Double> result = new HashMap<HerbrandInterpretation,Double>();
		for(WeightedHerbrandInterpretation interpretation: pq){
			result.put(interpretation.interpretation, interpretation.weight/sumOfWeights);
		}
		return result;
	}
}
