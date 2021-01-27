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
package org.tweetyproject.logics.rpcl.semantics;

import java.util.*;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.fol.syntax.*;
import org.tweetyproject.logics.pcl.semantics.*;
import org.tweetyproject.logics.rpcl.syntax.*;
import org.tweetyproject.math.probability.Probability;


/**
 * Instances of this class represent condensed probability distributions, rf. [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 *
 */
public class CondensedProbabilityDistribution extends RpclProbabilityDistribution<ReferenceWorld> {

	/**
	 * Creates a new condensed probability distribution for the given signature.
	 * @param semantics the semantics used for this distribution.
	 * @param signature a fol signature.
	 */
	public CondensedProbabilityDistribution(RpclSemantics semantics, FolSignature signature){
		super(semantics,signature);		
	}
		
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.rpcl.semantics.RpclProbabilityDistribution#satisfies(org.tweetyproject.logics.rpcl.syntax.RelationalProbabilisticConditional)
	 */
	@Override
	public boolean satisfies(RelationalProbabilisticConditional formula) throws IllegalArgumentException {
		return this.getSemantics().satisfies(this, formula);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.rpcl.semantics.RpclProbabilityDistribution#satisfies(org.tweetyproject.logics.rpcl.RpclBeliefSet)
	 */
	@Override
	public boolean satisfies(RpclBeliefSet beliefBase)	throws IllegalArgumentException {
		for(RelationalProbabilisticConditional f: beliefBase)
			if(!this.satisfies(f)) return false;
		return true;
	}	
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.relationalprobabilisticconditionallogic.semantics.RpclProbabilityDistribution#entropy()
	 */
	@Override
	public double entropy(){
		double entropy = 0;
		for(ReferenceWorld i : this.keySet())
			if(this.get(i).getValue() != 0)
				entropy -= ((ReferenceWorld)i).spanNumber() * this.get(i).getValue() * Math.log(this.get(i).getValue());
		return entropy;
	}
	
	/**
	 * Returns the condensed entropy of this distribution (neglecting multiplicators of
	 * reference worlds.
	 * @return the condensed entropy of this distribution
	 */
	public double condensedEntropy(){
		return super.entropy();
	}
		
	/**
	 * Returns the uniform distribution on the given signature.
	 * @param semantics the semantics used for the distribution
	 * @param signature a fol signature
	 * @param equivalenceClasses the set of equivalence classes
	 * @return the uniform distribution on the given signature.
	 */
	public static CondensedProbabilityDistribution getUniformDistribution(RpclSemantics semantics, FolSignature signature, Set<Set<Constant>> equivalenceClasses){
		CondensedProbabilityDistribution p = new CondensedProbabilityDistribution(semantics,signature);
		Set<ReferenceWorld> interpretations = ReferenceWorld.enumerateReferenceWorlds(signature.getPredicates(), equivalenceClasses); 
		double size = 0;
		for(ReferenceWorld i: interpretations)
			size += i.spanNumber();
		for(ReferenceWorld i: interpretations)
			p.put(i, new Probability(i.spanNumber()*(1/size)));
		return p;
	}
	
	/**
	 * Returns a random distribution on the given signature.
	 * @param semantics the semantics used for the distribution
	 * @param signature a fol signature
	 * @param equivalenceClasses the set of equivalence classes
	 * @return a random distribution on the given signature.
	 */
	public static CondensedProbabilityDistribution getRandomDistribution(RpclSemantics semantics, FolSignature signature, Set<Set<Constant>> equivalenceClasses){
		CondensedProbabilityDistribution p = new CondensedProbabilityDistribution(semantics,signature);
		Set<ReferenceWorld> interpretations = ReferenceWorld.enumerateReferenceWorlds(signature.getPredicates(), equivalenceClasses); 
		Random rand = new Random();
		List<Double> probs = new ArrayList<Double>();
		for(int i = 0; i < interpretations.size(); i++)
			probs.add(rand.nextDouble());
		ProbabilityDistribution.normalize(probs);
		Iterator<Double> itProbs = probs.iterator();
		for(ReferenceWorld i: interpretations)
			p.put(i, new Probability(itProbs.next()/i.spanNumber()));
		return p;
	}
	
	/**
	 * Converts this condensed probability distribution into an ordinary
	 * probability distribution.
	 * @return a probability distribution.
	 */
	public RpclProbabilityDistribution<ReferenceWorld> toProbabilityDistribution(){
		//TODO implement me
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.relationalprobabilisticconditionallogic.semantics.RpclProbabilityDistribution#probability(org.tweetyproject.logics.firstorderlogic.syntax.FolFormula)
	 */
	public Probability probability(FolFormula f){
		Probability p = new Probability(0d);
		for(ReferenceWorld w: this.keySet()){
			ReferenceWorld rw = (ReferenceWorld) w;
			p = p.add(this.get(rw).mult(rw.getMultiplicator(f)));
		}
		return p;
	}
}
