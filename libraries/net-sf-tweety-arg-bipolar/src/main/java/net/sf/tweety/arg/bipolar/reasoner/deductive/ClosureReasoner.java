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
package net.sf.tweety.arg.bipolar.reasoner.deductive;

import net.sf.tweety.arg.bipolar.syntax.*;
import net.sf.tweety.commons.util.SetTools;

import java.util.*;

/**
 * a set of arguments S is closed under the support relation iff all arguments supported by an element of S are in S.
 *
 * @author Lars Bengel
 *
 */
public class ClosureReasoner {

    /* (non-Javadoc)
     * @see net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner#getModels(net.sf.tweety.arg.baf.syntax.syntax.BipolarArgFramework)
     */
    public Collection<ArgumentSet> getModels(DeductiveArgumentationFramework bbase) {
        Set<ArgumentSet> extensions = new HashSet<>();
        // Check all subsets
        for(Set<BArgument> ext: new SetTools<BArgument>().subsets(bbase))
            if(bbase.isClosed(new ArgumentSet(ext)))
                extensions.add(new ArgumentSet(ext));
        return extensions;
    }

    public ArgumentSet getModel(DeductiveArgumentationFramework bbase) {
        // as the empty set is always closed we return that one.
        return new ArgumentSet();
    }
}
