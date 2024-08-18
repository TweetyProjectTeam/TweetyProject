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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.commons;

/**
 * This interface represents a general contract for qualitative reasoners, which are objects
 * that can query a belief base with a specific formula and return a boolean result (`TRUE` or `FALSE`)
 * based on the reasoning process. The reasoning process is qualitative, meaning it does not involve
 * probabilities or numerical values but focuses on logical entailment or satisfaction.
 *
 * @param <B> The type of belief base that the reasoner can query. This is a subclass of {@link BeliefBase}.
 * @param <F> The type of formulas that the reasoner can handle. This is a subclass of {@link Formula}.
 *
 * @author Matthias Thimm
 */
public interface QualitativeReasoner<B extends BeliefBase, F extends Formula> extends Reasoner<Boolean, B, F> {

    /**
     * Queries the given belief base with the provided formula and returns a boolean result.
     * The result indicates whether the formula is entailed or satisfied by the belief base
     * according to the qualitative reasoning method implemented by the reasoner.
     *
     * @param beliefbase The belief base to be queried.
     * @param formula The formula for which the query is made.
     * @return `TRUE` if the formula is entailed or satisfied by the belief base, `FALSE` otherwise.
     */
    @Override
    public Boolean query(B beliefbase, F formula);

    /**
     * Checks whether the underlying solver or reasoning mechanism used by this reasoner is installed
     * and available for use. This can be helpful when the reasoner depends on external tools or libraries
     * for performing the reasoning tasks.
     *
     * @return `true` if the solver is installed and available, `false` otherwise.
     */
    public abstract boolean isInstalled();
}