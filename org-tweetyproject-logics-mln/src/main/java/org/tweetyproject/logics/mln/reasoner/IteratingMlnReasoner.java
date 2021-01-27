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
package org.tweetyproject.logics.mln.reasoner;

import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.mln.syntax.MarkovLogicNetwork;

/**
 * This MLN reasoner takes another MLN reasoner and performs several iterations
 * with this one and takes the average result as result.
 * 
 * @author Matthias Thimm
 */
public class IteratingMlnReasoner extends AbstractMlnReasoner{ 

	/** The reasoner inside this reasoner. */
	private AbstractMlnReasoner reasoner;
	
	/** The number of iterations. */
	private long numberOfIterations;
	
	/**
	 * Creates a new IteratingMlnReasoner for the given MLN reaasoner.
	 * @param reasoner some MLN reasoner.
	 * @param numberOfIterations the number of iterations for the reasoner 
	 */
	public IteratingMlnReasoner(AbstractMlnReasoner reasoner, long numberOfIterations){
		this.reasoner = reasoner;
		this.numberOfIterations = numberOfIterations;
	}
	

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.mln.AbstractMlnReasoner#doQuery(org.tweetyproject.logics.mln.MarkovLogicNetwork, org.tweetyproject.logics.fol.syntax.FolFormula, org.tweetyproject.logics.fol.syntax.FolSignature)
	 */
	@Override
	protected double doQuery(MarkovLogicNetwork mln, FolFormula query, FolSignature signature){
		double resultSum = 0;
		for(long i = 0; i < this.numberOfIterations; i++){
			resultSum += this.reasoner.doQuery(mln,query,signature);
		}
		double result = resultSum/this.numberOfIterations;
		return result;
	}

}