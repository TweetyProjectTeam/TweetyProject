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
package net.sf.tweety.arg.bipolar.reasoner.necessity;

import net.sf.tweety.arg.bipolar.syntax.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * a set of arguments S is stable iff the set of arguments deactivated by S equals A\S,
 * where A is the set of all arguments in the argumentation framework.
 *
 * @author Lars Bengel
 *
 */
public class StableReasoner {
    public Collection<ArgumentSet> getModels(NecessityArgumentationFramework bbase) {
        //TODO efficiency
        Collection<ArgumentSet> preferredExtensions = new PreferredReasoner().getModels(bbase);
        Set<ArgumentSet> result = new HashSet<>();
        for(ArgumentSet ext: preferredExtensions){
            Set<BArgument> deactivatedArguments = bbase.getDeactivatedArguments(ext);
            Set<BArgument> arguments = new HashSet<>(bbase);
            arguments.removeAll(ext);
            if (deactivatedArguments.equals(arguments))
                result.add(ext);
        }
        return result;
    }

    public ArgumentSet getModel(NecessityArgumentationFramework bbase) {
        Collection<ArgumentSet> preferredExtensions = new PreferredReasoner().getModels(bbase);
        for(ArgumentSet ext: preferredExtensions){
            Set<BArgument> deactivatedArguments = bbase.getDeactivatedArguments(ext);
            Set<BArgument> arguments = new HashSet<>(bbase);
            arguments.removeAll(ext);
            if (deactivatedArguments.equals(arguments))
                return ext;
        }
        return null;
    }
}
