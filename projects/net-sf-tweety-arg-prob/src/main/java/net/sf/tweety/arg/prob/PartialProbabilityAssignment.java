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
package net.sf.tweety.arg.prob;

import java.util.HashMap;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungSignature;
import net.sf.tweety.arg.prob.semantics.ProbabilisticExtension;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.math.probability.Probability;

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
	public boolean isCompliant(ProbabilisticExtension pext){
		for(Argument a: this.keySet())
			if(pext.get(a).getValue() < this.get(a).getValue() - Probability.PRECISION ||
					pext.get(a).getValue() > this.get(a).getValue() + Probability.PRECISION)
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBase#getSignature()
	 */
	@Override
	public Signature getSignature() {
		DungSignature sig = new DungSignature();
		for(Argument a: this.keySet())
			sig.add(a);
		return sig;
	}
}
