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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * a set of arguments S is preferred iff it is maximal wrt set inclusion admissible.
 *
 * @author Lars Bengel
 *
 */
public class PreferredReasoner {
    public Collection<ArgumentSet> getModels(NecessityArgumentationFramework bbase) {
        Collection<ArgumentSet> completeExtensions = new CompleteReasoner().getModels(bbase);
        Set<ArgumentSet> result = new HashSet<ArgumentSet>();
        boolean maximal;
        for(ArgumentSet e1: completeExtensions){
            maximal = true;
            for(ArgumentSet e2: completeExtensions)
                if(e1 != e2 && e2.containsAll(e1)){
                    maximal = false;
                    break;
                }
            if(maximal)
                result.add(e1);
        }
        return result;
    }

    public ArgumentSet getModel(NecessityArgumentationFramework bbase) {
        // just return the first found preferred extension
        Collection<ArgumentSet> completeExtensions = new CompleteReasoner().getModels(bbase);
        boolean maximal;
        for(ArgumentSet e1: completeExtensions){
            maximal = true;
            for(ArgumentSet e2: completeExtensions)
                if(e1 != e2 && e2.containsAll(e1)){
                    maximal = false;
                    break;
                }
            if(maximal)
                return e1;
        }
        // this should not happen
        throw new RuntimeException("Hmm, did not find a maximal set in a finite number of sets. Should not happen.");
    }
}
