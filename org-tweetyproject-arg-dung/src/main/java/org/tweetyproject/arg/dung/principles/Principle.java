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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.principles;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.postulates.Postulate;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;

import java.util.Collection;

/**
 * Models a principle for argumentation semantics i.e. a property that
 * can be satisfied or violated by a semantics.
 *
 * @author Lars Bengel
 */
public abstract class Principle implements Postulate<Argument> {

    /** The admissibility principle **/
    public static final Principle ADMISSIBILITY = new AdmissibilityPrinciple();

    /* (non-Javadoc)
     * @see org.tweetyproject.commons.postulates.Postulate#isApplicable(java.util.Collection)
     */
    @Override
    public abstract boolean isApplicable(Collection<Argument> kb);

    /* (non-Javadoc)
     * @see org.tweetyproject.commons.postulates.Postulate#isSatisfied(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.postulates.PostulateEvaluatable)
     */
    @Override
    public boolean isSatisfied(Collection<Argument> kb, PostulateEvaluatable<Argument> ev) {
        if(ev instanceof AbstractExtensionReasoner)
            return this.isSatisfied(kb, (AbstractExtensionReasoner) ev);
        throw new RuntimeException("PostulateEvaluatable of type AbstractExtensionReasoner expected.");
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.commons.postulates.Postulate#isSatisfied(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.postulates.PostulateEvaluatable)
     */
    public abstract boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev);
}
