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
package net.sf.tweety.arg.prob.dynamics;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.prob.PartialProbabilityAssignment;
import net.sf.tweety.arg.prob.semantics.ProbabilisticExtension;

/**
 * This interface provides common methods for change operators
 * for probabilistic argumentation.
 * @author Matthias Thimm
 */
public interface ChangeOperator {

	/**
	 * Given a partial probability assignment ppa and an argumentation theory, compute
	 * the closest probabilistic extension that is adequate for observing the theory in 
	 * the state ppa.
	 * @param ppa some partial probability assignment.
	 * @param theory some theory.
	 * @return the closest probabilistic extension that is adequate for observing the theory in 
	 * the state ppa.
	 */
	public ProbabilisticExtension change(PartialProbabilityAssignment ppa, DungTheory theory);
	
	/**
	 * Given a probabilistic extension and an argumentation theory, compute
	 * the closest probabilistic extension that is adequate for observing the theory in 
	 * the state p.
	 * @param p some probabilistic extension.
	 * @param theory some theory.
	 * @return the closest probabilistic extension that is adequate for observing the theory in 
	 * the state p.
	 */
	public ProbabilisticExtension change(ProbabilisticExtension p, DungTheory theory);
}
