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
package org.tweetyproject.arg.prob.syntax;

import java.util.HashMap;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.arg.prob.semantics.ProbabilisticExtension;
import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.math.probability.Probability;

/**
 * A partial probability assignment for abstract argumentation theories.
 * @author Matthias Thimm
 */
public class PartialProbabilityAssignment extends HashMap<Argument,Probability> implements BeliefBase{

	/** For serialization.*/
	private static final long serialVersionUID = 7051185602937753358L;

	/**
	 * Checks whether the given probabilistic extension is compliant with this
	 * partial probability assignment, i.e. whether the probabilities of the 
	 * arguments coincide.
	 * @param pext some probabilistic extension.
	 * @return "true" iff the given probabilistic extension is compliant.
	 */
	@SuppressWarnings("unlikely-arg-type")
	public boolean isCompliant(ProbabilisticExtension pext){
		for(Argument a: this.keySet())
			if(pext.get(a).getValue() < this.get(a).getValue() - Probability.PRECISION ||
					pext.get(a).getValue() > this.get(a).getValue() + Probability.PRECISION)
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.BeliefBase#getSignature()
	 */
	@Override
	public Signature getMinimalSignature() {
		DungSignature sig = new DungSignature();
		for(Argument a: this.keySet())
			sig.add(a);
		return sig;
	}
}
