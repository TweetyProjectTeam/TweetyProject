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
package org.tweetyproject.beliefdynamics.operators;

import java.util.Collection;

import org.tweetyproject.arg.deductive.accumulator.SimpleAccumulator;
import org.tweetyproject.arg.deductive.categorizer.ClassicalCategorizer;
import org.tweetyproject.beliefdynamics.DefaultMultipleBaseExpansionOperator;
import org.tweetyproject.beliefdynamics.LeviMultipleBaseRevisionOperator;
import org.tweetyproject.beliefdynamics.MultipleBaseRevisionOperator;
import org.tweetyproject.beliefdynamics.selectiverevision.MultipleSelectiveRevisionOperator;
import org.tweetyproject.beliefdynamics.selectiverevision.MultipleTransformationFunction;
import org.tweetyproject.beliefdynamics.selectiverevision.argumentative.ArgumentativeTransformationFunction;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class is an exemplary instantiation of a revision operator based on deductive argumentation [Kruempelmann:2011] where
 * several parameters have been fixed:
 * - the inner revision is a Levi revision which bases on the random kernel contraction
 * - the transformation function is credulous
 * - the accumulator used for deductive argumentation is the simple accumulator
 * - the categorizer used for deductive argumentation is the classical categorizer
 *
 * @author Matthias Thimm
 */
public class ArgumentativeRevisionOperator extends MultipleBaseRevisionOperator<PlFormula>{

	    /**
     * Default constructor
     */
    public ArgumentativeRevisionOperator() {
        // Default constructor
    }

	/* (non-Javadoc)
	 * @see org.tweetyproject.beliefdynamics.MultipleBaseRevisionOperator#revise(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<PlFormula> revise(Collection<PlFormula> base, Collection<PlFormula> formulas) {
		MultipleBaseRevisionOperator<PlFormula> kernelRevision = new LeviMultipleBaseRevisionOperator<PlFormula>(
				new RandomKernelContractionOperator(),
				new DefaultMultipleBaseExpansionOperator<PlFormula>());
		MultipleTransformationFunction<PlFormula> transFunc = new ArgumentativeTransformationFunction(
				new ClassicalCategorizer(),
				new SimpleAccumulator(),
				new PlBeliefSet(base),
				false);
		MultipleSelectiveRevisionOperator<PlFormula> rev = new MultipleSelectiveRevisionOperator<PlFormula>(transFunc, kernelRevision);
		return rev.revise(base, formulas);
	}

}
