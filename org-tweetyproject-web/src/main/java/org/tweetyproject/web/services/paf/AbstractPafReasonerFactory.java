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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.paf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.prob.reasoner.AbstractPafReasoner;
import org.tweetyproject.arg.prob.reasoner.MonteCarloPafReasoner;
import org.tweetyproject.arg.prob.reasoner.SimplePafReasoner;
import org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework;
import org.tweetyproject.math.probability.Probability;

/**
 * Factory for building {@link ProbabilisticArgumentationFramework} instances and
 * {@link AbstractPafReasoner} instances from web request data.
 */
public abstract class AbstractPafReasonerFactory {

    /** Default number of Monte Carlo trials when none is specified. */
    public static final int DEFAULT_NR_OF_TRIALS = 10000;

    /**
     * Builds a {@link ProbabilisticArgumentationFramework} from the given parameters.
     *
     * <p>Arguments are named "1", "2", ..., {@code nr_of_arguments}. If
     * {@code argumentProbabilities} is null or shorter than {@code nr_of_arguments},
     * missing probabilities default to 1.0. Likewise for {@code attackProbabilities}.</p>
     *
     * @param nr_of_arguments      total number of arguments
     * @param argumentProbabilities existence probability per argument (0-indexed, 1.0 default)
     * @param attacks               attack pairs [attacker, attacked] (1-based indices)
     * @param attackProbabilities   existence probability per attack (0-indexed, 1.0 default)
     * @return the constructed PAF
     */
    public static ProbabilisticArgumentationFramework getPaf(
            int nr_of_arguments,
            List<Double> argumentProbabilities,
            List<List<Integer>> attacks,
            List<Double> attackProbabilities) {

        ProbabilisticArgumentationFramework paf = new ProbabilisticArgumentationFramework();

        List<Argument> arguments = new ArrayList<>();
        for (int i = 1; i <= nr_of_arguments; i++) {
            Argument arg = new Argument(Integer.toString(i));
            double prob = (argumentProbabilities != null && i - 1 < argumentProbabilities.size())
                    ? argumentProbabilities.get(i - 1) : 1.0;
            paf.add(arg, new Probability(prob));
            arguments.add(arg);
        }

        if (attacks != null) {
            for (int j = 0; j < attacks.size(); j++) {
                List<Integer> pair = attacks.get(j);
                Argument attacker = arguments.get(pair.get(0) - 1);
                Argument attacked = arguments.get(pair.get(1) - 1);
                Attack att = new Attack(attacker, attacked);
                double prob = (attackProbabilities != null && j < attackProbabilities.size())
                        ? attackProbabilities.get(j) : 1.0;
                paf.add(att, new Probability(prob));
            }
        }

        return paf;
    }

    /**
     * Returns the {@link Argument} with the given 1-based index from the PAF.
     *
     * @param paf      the framework
     * @param index    1-based argument index
     * @return the corresponding argument
     */
    public static Argument getArgument(ProbabilisticArgumentationFramework paf, int index) {
        return new Argument(Integer.toString(index));
    }

    /**
     * Returns the supported semantics for PAF reasoning (reuses Dung semantics).
     *
     * @return array of supported {@link Semantics}
     */
    public static Semantics[] getSemantics() {
        return Semantics.values();
    }

    /**
     * Returns the available solver identifiers.
     *
     * @return list of solver ids
     */
    public static List<String> getSolvers() {
        return Arrays.asList("simple", "montecarlo");
    }

    /**
     * Creates a PAF reasoner for the given semantics and solver selection.
     *
     * @param semantics    Dung semantics to use
     * @param solver       "simple" or "montecarlo"
     * @param nrOfTrials   number of Monte Carlo trials (ignored for "simple")
     * @return the reasoner
     */
    public static AbstractPafReasoner getReasoner(Semantics semantics, String solver, int nrOfTrials) {
        if ("montecarlo".equalsIgnoreCase(solver)) {
            int trials = nrOfTrials > 0 ? nrOfTrials : DEFAULT_NR_OF_TRIALS;
            return new MonteCarloPafReasoner(semantics, trials);
        }
        return new SimplePafReasoner(semantics);
    }
}
