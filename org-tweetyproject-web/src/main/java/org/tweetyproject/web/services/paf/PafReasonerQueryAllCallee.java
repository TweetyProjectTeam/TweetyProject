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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.prob.reasoner.AbstractPafReasoner;
import org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.web.services.Callee;

import java.util.Map;

/**
 * Callable that queries the probability of a single argument in a PAF.
 */
public class PafReasonerQueryAllCallee extends Callee {

    /** the underlying PAFReasoner */
    private final AbstractPafReasoner reasoner;
    /** the PAF */
    private final ProbabilisticArgumentationFramework paf;
    /** the inference mode */
    private final InferenceMode inferenceMode;

    /**
     * Initialize a new QueryAll callee
     * @param reasoner      the reasoner
     * @param paf           some PAF
     * @param inferenceMode the inference mode
     */
    public PafReasonerQueryAllCallee(AbstractPafReasoner reasoner,
                                  ProbabilisticArgumentationFramework paf,
                                  InferenceMode inferenceMode) {
        this.reasoner = reasoner;
        this.paf = paf;
        this.inferenceMode = inferenceMode;
    }

    @Override
    public Map<Argument, Double> call() throws Exception {
        return reasoner.queryAll(paf, inferenceMode);
    }
}
