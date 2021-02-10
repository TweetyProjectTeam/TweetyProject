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
package org.tweetyproject.arg.adf.reasoner.sat.query;

import org.tweetyproject.arg.adf.reasoner.sat.pipeline.Configuration;
import org.tweetyproject.arg.adf.reasoner.sat.pipeline.Execution;
import org.tweetyproject.arg.adf.reasoner.sat.pipeline.Semantics;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
final class InterpretationSatQuery extends SatQuery<Interpretation>{

	public InterpretationSatQuery(AbstractDialecticalFramework adf, Semantics semantics, Configuration configuration) {
		super(adf, semantics, configuration);
	}
	
	@Override
	public SatQuery<Interpretation> configure(Configuration configuration) {
		return new InterpretationSatQuery(adf, semantics, configuration);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.sat.query.SatQuery#execute(net.sf.tweety.arg.adf.reasoner.sat.pipeline.Execution)
	 */
	@Override
	Interpretation execute(Execution execution) {
		return new ModelIterator(execution).next();
	}
	
}
