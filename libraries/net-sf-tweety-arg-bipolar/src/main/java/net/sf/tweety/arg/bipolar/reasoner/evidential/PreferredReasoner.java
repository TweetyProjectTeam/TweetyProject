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
package net.sf.tweety.arg.bipolar.reasoner.evidential;

import net.sf.tweety.arg.bipolar.syntax.ArgumentSet;
import net.sf.tweety.arg.bipolar.syntax.EvidentialArgumentationFramework;

import java.util.*;

public class PreferredReasoner {
    public Collection<ArgumentSet> getModels(EvidentialArgumentationFramework bbase) {
        Collection<ArgumentSet> completeExtensions = new EvidentialCompleteReasoner().getModels(bbase);
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

    public ArgumentSet getModel(EvidentialArgumentationFramework bbase) {
        // just return the first found preferred extension
        Collection<ArgumentSet> completeExtensions = new EvidentialCompleteReasoner().getModels(bbase);
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
