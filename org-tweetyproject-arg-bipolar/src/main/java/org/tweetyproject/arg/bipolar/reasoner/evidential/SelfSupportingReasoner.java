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
package org.tweetyproject.arg.bipolar.reasoner.evidential;

import org.tweetyproject.arg.bipolar.syntax.ArgumentSet;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.EvidentialArgumentationFramework;
import org.tweetyproject.commons.util.SetTools;
import java.util.*;

/**
 * a set of arguments S is self-supporting iff all arguments in S are
 * e-supported by S.
 *
 * @author Lars Bengel
 *
 */
public class SelfSupportingReasoner {
    /**
     * Computes all self-supporting argument sets from the given evidential
     * argumentation framework.
     *
     * @param bbase The evidential argumentation framework containing arguments and
     *              support relations.
     * @return A collection of self-supporting argument sets.
     */
    public Collection<ArgumentSet> getModels(EvidentialArgumentationFramework bbase) {
        Set<ArgumentSet> extensions = new HashSet<ArgumentSet>();
        // Check all subsets
        for (Set<BArgument> ext : new SetTools<BArgument>().subsets(bbase)) {
            boolean selfSupporting = true;
            for (BArgument argument : ext) {
                if (!bbase.hasEvidentialSupport(argument, ext)) {
                    selfSupporting = false;
                    break;
                }
            }
            if (selfSupporting)
                extensions.add(new ArgumentSet(ext));
        }
        return extensions;
    }

    /**
     *
     * Return a model
     *
     * @param bbase arg framework
     * @return a model
     */
    public ArgumentSet getModel(EvidentialArgumentationFramework bbase) {
        // as the empty set is always self-supporting we return that one.
        return new ArgumentSet();
    }
}
