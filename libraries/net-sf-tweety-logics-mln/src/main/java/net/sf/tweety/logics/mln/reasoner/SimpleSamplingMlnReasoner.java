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
package net.sf.tweety.logics.mln.reasoner;

import java.util.Iterator;
import java.util.Set;

import net.sf.tweety.commons.util.RandomSubsetIterator;
import net.sf.tweety.logics.fol.semantics.HerbrandBase;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.logics.fol.syntax.FolAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.mln.syntax.MarkovLogicNetwork;

/**
 * This MLN reasoner employs simple random sampling from
 * the set of interpretations to compute the probability of a formula.
 *  
 * @author Matthias Thimm
 *
 */
public class SimpleSamplingMlnReasoner extends AbstractMlnReasoner{

	/** The computation is aborted when the given precision is reached for at least
	 * numOfPositive number of consecutive tests. */
	private double precision = 0.00001;	
	private int numOfPositiveTests = 1000;
	
	/**
	 * Creates a new SimpleSamplingMlnReasoner for the given Markov logic network.
	 * 
	 * @param precision the precision
	 * @param numOfPositiveTests the number of positive consecutive tests on precision
	 */
	public SimpleSamplingMlnReasoner(double precision, int numOfPositiveTests) {
		this.precision = precision;
		this.numOfPositiveTests = numOfPositiveTests;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.AbstractMlnReasoner#doQuery(net.sf.tweety.logics.firstorderlogic.syntax.FolFormula)
	 */
	@Override
	protected double doQuery(MarkovLogicNetwork mln, FolFormula query, FolSignature signature) {
		// The Herbrand base of the signature
		HerbrandBase hBase = new HerbrandBase(signature);
		// for sampling
		Iterator<Set<FolAtom>> it = new RandomSubsetIterator<FolAtom>(hBase.getAtoms(),false);		
		double previousProb = 0;
		double currentProb = 0;
		int consecutiveTests = 0;
		double completeMass = 0;
		long satisfiedMass = 0;
		do{
			HerbrandInterpretation sample = new HerbrandInterpretation(it.next());
			double weight = this.computeWeight(mln,sample,signature);
			if(sample.satisfies(query))
				satisfiedMass += weight;
			completeMass += weight;
			previousProb = currentProb;
			currentProb = (completeMass == 0) ? 0 : satisfiedMass/completeMass;			
			if(Math.abs(previousProb-currentProb) < this.precision){
				consecutiveTests++;
				if(consecutiveTests >= this.numOfPositiveTests)
					break;
			}else consecutiveTests = 0;
		}while(true);		
		return currentProb;
	}


}
