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

/**
 * Callable that queries the probability of a single argument in a PAF.
 */
public class PafReasonerQueryCallee extends Callee {

    private final AbstractPafReasoner reasoner;
    private final ProbabilisticArgumentationFramework paf;
    private final Argument argument;
    private final InferenceMode inferenceMode;

    public PafReasonerQueryCallee(AbstractPafReasoner reasoner,
            ProbabilisticArgumentationFramework paf,
            Argument argument,
            InferenceMode inferenceMode) {
        this.reasoner = reasoner;
        this.paf = paf;
        this.argument = argument;
        this.inferenceMode = inferenceMode;
    }

    @Override
    public Double call() throws Exception {
        return reasoner.query(paf, argument, inferenceMode);
    }
}
