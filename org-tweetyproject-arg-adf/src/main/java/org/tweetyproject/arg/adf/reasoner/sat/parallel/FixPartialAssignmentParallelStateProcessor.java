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
package org.tweetyproject.arg.adf.reasoner.sat.parallel;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.FixThreeValuedPartialSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.InterpretationIterator;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * 
 * @author Mathias Hofer
 *
 */
public final class FixPartialAssignmentParallelStateProcessor{

	public void process(Collection<? extends SatSolverState> states, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		Iterator<Interpretation> iterator = new InterpretationIterator(arguments(adf, states.size()));
		for (SatSolverState state : states) {
			Interpretation partial = iterator.next(); // number of states = number of interpretations
			FixThreeValuedPartialSatEncoding.encode(partial, state::add, mapping, adf);
		}
	}
	
	private List<Argument> arguments(AbstractDialecticalFramework adf, int stateCount) {
		int argumentsToFix = (int) (Math.log(stateCount) / Math.log(3)); // 3 = number of truth values
		
		if (argumentsToFix > adf.size()) {
			throw new IllegalArgumentException("The given ADF does not support a parallelization level of " + stateCount);
		}
		
		return adf.getArguments().stream()
//				.sorted(new DegreeComparator(adf, DegreeType.UNDIRECTED)) TODO: check if ordering makes difference
				.limit(argumentsToFix)
				.collect(Collectors.toList());
	}
	
}
