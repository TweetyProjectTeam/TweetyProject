/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.ml;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.util.DefaultSubsetIterator;
import net.sf.tweety.commons.util.RandomSubsetIterator;
import net.sf.tweety.logics.fol.semantics.*;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.pcl.semantics.ProbabilityDistribution;
import net.sf.tweety.math.probability.Probability;

/**
 * This reasoner performs approximate reasoning with MLNs by considering
 * only a subset of all Herbrand interpretations. This subset is chosen
 * by first randomly selecting a set of Herbrand interpretations 
 * and then selecting the subset of this set with maximum weights.
 * 
 * @author Matthias Thimm
 */
public class ApproximateNaiveMlnReasoner extends AbstractMlnReasoner{

	/** The approximated model of the MLN (saved for avoid double
	 * computation). */
	private ProbabilityDistribution<HerbrandInterpretation> prob = null;
	
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
	 * Creates a new ApproximateNaiveMlnReasoner for the given Markov logic network.
	 * @param beliefBase a Markov logic network. 
	 * @param signature another signature (if the probability distribution should be defined 
	 * on that one (that one should subsume the signature of the Markov logic network)
	 * @param maxNumberOfSelectedInterpretations the maximum number of interpretations selected from the whole
	 * set of interpretations. Is -1 if all interpretations are to be selected.
	 * @param maxNumberOfInterpretationsForModel the maximum number of interpretations used for the model. Those interpretations
	 * are the subset of the interpretations selected with maximum weight. Is -1
	 * if all interpretations are used for the model. It has to be maxNumberOfSelectedInterpretations >= maxNumberOfInterpretationsForModel.
	 */
	public ApproximateNaiveMlnReasoner(BeliefBase beliefBase, FolSignature signature, long maxNumberOfSelectedInterpretations, long maxNumberOfInterpretationsForModel){
		super(beliefBase, signature);		
		this.maxNumberOfSelectedInterpretations = maxNumberOfSelectedInterpretations;
		this.maxNumberOfInterpretationsForModel = maxNumberOfInterpretationsForModel;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.AbstractMlnReasoner#reset()
	 */
	public void reset(){
		this.prob = null;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.AbstractMlnReasoner#doQuery(net.sf.tweety.logics.firstorderlogic.syntax.FolFormula)
	 */
	@Override
	protected double doQuery(FolFormula query) {
		if(this.prob == null)
			this.prob = this.computeModel();
		return this.prob.probability(query).doubleValue();
	}

	/** Computes the model of the given MLN wrt. the optimization parameters.
	 * @return the model of the given MLN wrt. the optimization parameters.
	 */
	private ProbabilityDistribution<HerbrandInterpretation> computeModel(){
		// Queue used for storing the interpretations with maximum weight
		PriorityQueue<WeightedHerbrandInterpretation> pq = new PriorityQueue<WeightedHerbrandInterpretation>();
		// The Herbrand base of the signature
		HerbrandBase hBase = new HerbrandBase(this.getSignature());
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
			whInt.weight = this.computeWeight(hInt);
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
		ProbabilityDistribution<HerbrandInterpretation> result = new ProbabilityDistribution<HerbrandInterpretation>(this.getSignature());
		for(WeightedHerbrandInterpretation interpretation: pq){
			result.put(interpretation.interpretation, new Probability(interpretation.weight/sumOfWeights));
		}
		return result;
	}
}
