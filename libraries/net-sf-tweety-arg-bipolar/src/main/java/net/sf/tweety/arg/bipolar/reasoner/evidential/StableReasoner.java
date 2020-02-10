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

import net.sf.tweety.arg.bipolar.syntax.*;

import java.util.*;


/**
 * a set of arguments S is stable iff it is conflict-free, self-supporting and for any argument a e-supported by bbase,
 * where a is not in S, S e-support attacks either a or every set of arguments minimally e-supporting a.
 *
 * @author Lars Bengel
 *
 */
public class StableReasoner {
    public Collection<ArgumentSet> getModels(EvidentialArgumentationFramework bbase) {
        //TODO efficiency
        Collection<ArgumentSet> preferredExtensions = new PreferredReasoner().getModels(bbase);
        Set<ArgumentSet> result = new HashSet<>();
        for(ArgumentSet ext: preferredExtensions){
            Set<BArgument> eSupportedArguments = bbase.getEvidenceSupportedArguments();
            eSupportedArguments.removeAll(ext);
            boolean attacksAllESupportedArguments = true;
            for (BArgument argument: eSupportedArguments){
                if (bbase.isEvidenceSupportedAttack(ext, argument)) {
                    continue;
                }
                boolean attacksAllESupporters = true;
                for (Set<BArgument> supporter: bbase.getMinimalEvidentialSupporters(argument)) {
                    boolean attacksSupporter = false;
                    for (BArgument arg: supporter) {
                        if (bbase.isEvidenceSupportedAttack(ext, arg)) {
                            attacksSupporter = true;
                            break;
                        }
                    }
                    attacksAllESupporters &= attacksSupporter;
                }
                attacksAllESupportedArguments &= attacksAllESupporters;
            }

            if (attacksAllESupportedArguments)
                result.add(ext);
        }
        return result;
    }

    public ArgumentSet getModel(EvidentialArgumentationFramework bbase) {
        //TODO efficiency
        Collection<ArgumentSet> preferredExtensions = new PreferredReasoner().getModels(bbase);
        for(ArgumentSet ext: preferredExtensions){
            Set<BArgument> eSupportedArguments = bbase.getEvidenceSupportedArguments();
            eSupportedArguments.removeAll(ext);
            boolean attacksAllESupportedArguments = true;
            for (BArgument argument: eSupportedArguments){
                if (bbase.isEvidenceSupportedAttack(ext, argument)) {
                    continue;
                }
                boolean attacksAllESupporters = true;
                for (Set<BArgument> supporter: bbase.getMinimalEvidentialSupporters(argument)) {
                    boolean attacksSupporter = false;
                    for (BArgument arg: supporter) {
                        if (bbase.isEvidenceSupportedAttack(ext, arg)) {
                            attacksSupporter = true;
                            break;
                        }
                    }
                    attacksAllESupporters &= attacksSupporter;
                }
                attacksAllESupportedArguments &= attacksAllESupporters;
            }

            if (attacksAllESupportedArguments)
                return ext;
        }
        return null;
    }
}
