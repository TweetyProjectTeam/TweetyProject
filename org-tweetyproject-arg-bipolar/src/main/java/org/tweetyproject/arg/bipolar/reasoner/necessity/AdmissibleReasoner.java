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
package org.tweetyproject.arg.bipolar.reasoner.necessity;

import org.tweetyproject.arg.bipolar.syntax.*;
import org.tweetyproject.commons.util.SetTools;

import java.util.*;

/**
 * a set of arguments S is admissible iff it is strongly coherent and defends all of its arguments.
 *
 * @author Lars Bengel
 *
 */
public class AdmissibleReasoner {
	/**
	 *
	 * Return models
	 * @param bbase argumentation framework
	 * @return models
	 */
    public Collection<ArgumentSet> getModels(NecessityArgumentationFramework bbase) {
        Set<ArgumentSet> extensions = new HashSet<ArgumentSet>();
        Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets(bbase);

        for (Set<BArgument> ext: subsets) {
            if (!bbase.isStronglyCoherent(ext)) {
                continue;
            }
            boolean admissible = true;
            for (BArgument argument : ext) {
                if (!bbase.isAcceptable(argument, ext)) {
                    admissible = false;
                    break;
                }
            }
            if (admissible)
                extensions.add(new ArgumentSet(ext));
        }
        return extensions;
    }

    /**
     *
     * Return model
     * @param bbase argumentation framework
     * @return model
     */
    public ArgumentSet getModel(NecessityArgumentationFramework bbase) {
        // as the empty set is always self-supporting we return that one.
        return new ArgumentSet();
    }

    /** Default Constructor */
    public AdmissibleReasoner(){}
}
