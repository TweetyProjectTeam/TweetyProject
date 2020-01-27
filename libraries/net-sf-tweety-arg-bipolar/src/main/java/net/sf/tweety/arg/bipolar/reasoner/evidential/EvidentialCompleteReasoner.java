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
import net.sf.tweety.arg.bipolar.syntax.BArgument;
import net.sf.tweety.arg.bipolar.syntax.EvidentialArgumentationFramework;
import java.util.*;

public class EvidentialCompleteReasoner {
    //TODO make more efficient. dont check all arguments in bbase
    public Collection<ArgumentSet> getModels(EvidentialArgumentationFramework bbase) {
        Set<ArgumentSet> extensions = new HashSet<ArgumentSet>();
        // Check all admissible subsets
        AdmissibleReasoner admissibleReasoner = new AdmissibleReasoner();
        for(ArgumentSet ext: admissibleReasoner.getModels(bbase)){
            boolean complete = true;
            Set<BArgument> otherArguments = new HashSet<BArgument>(bbase);
            otherArguments.removeAll(ext);
            for (BArgument argument : otherArguments) {
                complete &= !bbase.isAcceptable(argument, ext);
            }
            if (complete)
                extensions.add(new ArgumentSet(ext));
        }
        return extensions;
    }

    public ArgumentSet getModel(EvidentialArgumentationFramework bbase) {
        // as the set only containing epsilon is always complete we return that one.
        ArgumentSet ext = new ArgumentSet();
        ext.add(bbase.getEta());
        return ext;
    }
}
