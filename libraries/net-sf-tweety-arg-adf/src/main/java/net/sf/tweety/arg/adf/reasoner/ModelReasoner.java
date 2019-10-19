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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner;

import java.util.Iterator;

import net.sf.tweety.arg.adf.reasoner.strategy.ModelIterator;
import net.sf.tweety.arg.adf.reasoner.strategy.model.SatModelReasonerStrategy;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * 
 * @author Mathias Hofer
 *
 */
public class ModelReasoner extends AbstractDialecticalFrameworkReasoner {
	
	private ReasonerStrategy strategy;

	public ModelReasoner(IncrementalSatSolver solver) {
		this.strategy = new SatModelReasonerStrategy(solver);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner#modelIterator(net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public Iterator<Interpretation> modelIterator(AbstractDialecticalFramework adf) {
		return new ModelIterator(strategy, adf);
	}

}
