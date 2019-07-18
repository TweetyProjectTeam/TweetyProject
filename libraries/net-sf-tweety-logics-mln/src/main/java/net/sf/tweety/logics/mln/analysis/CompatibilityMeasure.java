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
package net.sf.tweety.logics.mln.analysis;

import java.util.List;

import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.mln.reasoner.AbstractMlnReasoner;
import net.sf.tweety.logics.mln.syntax.MarkovLogicNetwork;

/**
 * This interface represents a compatibility measure for MLNs.
 * Given a set of MLNs it returns a value indicating how compatible
 * those MLNs are (i.e. how much the probabilities change when merging
 * the MLNs).
 * 
 * @author Matthias Thimm
 */
public interface CompatibilityMeasure {

	/**
	 * Measures the compatibility of the given MLNs wrt. the given signatures using the
	 * given reasoner.
	 * @param mlns a list of MLNs.
	 * @param reasoner some reasoner.
	 * @param signatures a set of signatures, one for each MLN.
	 * @return the compatibility value
	 */
	public abstract double compatibility(List<MarkovLogicNetwork> mlns, AbstractMlnReasoner reasoner, List<FolSignature> signatures);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
}
