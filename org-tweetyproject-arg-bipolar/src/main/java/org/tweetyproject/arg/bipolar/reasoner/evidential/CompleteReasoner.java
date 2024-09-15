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

import org.tweetyproject.arg.bipolar.syntax.*;
import java.util.*;

/**
 * a set of arguments S is complete iff it is admissible and all arguments acceptable wrt. S are in S.
 *
 * @author Lars Bengel
 *
 */
public class CompleteReasoner {
	/**
	 *
	 * Return models
	 * @param bbase argumentation framework
	 * @return models
	 */
    public Collection<ArgumentSet> getModels(EvidentialArgumentationFramework bbase) {
        Set<ArgumentSet> extensions = new HashSet<ArgumentSet>();
        // Check all admissible subsets
        AdmissibleReasoner admissibleReasoner = new AdmissibleReasoner();
        for(ArgumentSet ext: admissibleReasoner.getModels(bbase)){
            boolean complete = true;
            Set<BArgument> otherArguments = new HashSet<>(bbase);
            otherArguments.removeAll(ext);
            for (BArgument argument : otherArguments) {
                if (bbase.isAcceptable(argument, ext)) {
                    complete = false;
                    break;
                }
            }
            if (complete)
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
    public ArgumentSet getModel(EvidentialArgumentationFramework bbase) {
        // as the set only containing epsilon is always complete we return that one.
        ArgumentSet ext = new ArgumentSet();
        ext.add(bbase.getEta());
        return ext;
    }

    /** Default Constructor */
    public CompleteReasoner(){}
}
