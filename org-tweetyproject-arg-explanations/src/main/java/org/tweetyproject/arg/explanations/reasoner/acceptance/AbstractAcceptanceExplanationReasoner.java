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
 *  Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.explanations.reasoner.acceptance;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.semantics.Explanation;

import java.util.Collection;

/**
 * Abstract class for all acceptance explanation reasoners
 * An acceptance explanation reasoner provides explanations for the acceptance of some argument within an argumentation framework
 *
 * @author Lars Bengel
 */
public abstract class AbstractAcceptanceExplanationReasoner {
    /**
     * Computes some acceptance explanation for <code>argument</code>
     * @param theory some argumentation framework
     * @param argument some argument
     * @return some explanation for the acceptance of <code>argument</code> in <code>theory</code>
     */
    public Explanation getExplanation(DungTheory theory, Argument argument) {
        return getExplanations(theory, argument).iterator().next();
    }

    /**
     * Computes all acceptance explanations for <code>argument</code>
     * @param theory some argumentation framework
     * @param argument some argument
     * @return all explanations for the acceptance of <code>argument</code> in <code>theory</code>
     */
    public abstract Collection<Explanation> getExplanations(DungTheory theory, Argument argument);
}
