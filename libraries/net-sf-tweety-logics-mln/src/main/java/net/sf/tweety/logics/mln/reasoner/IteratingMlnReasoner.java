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

import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.mln.syntax.MarkovLogicNetwork;

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
	 * @see net.sf.tweety.logics.mln.AbstractMlnReasoner#doQuery(net.sf.tweety.logics.mln.MarkovLogicNetwork, net.sf.tweety.logics.fol.syntax.FolFormula, net.sf.tweety.logics.fol.syntax.FolSignature)
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