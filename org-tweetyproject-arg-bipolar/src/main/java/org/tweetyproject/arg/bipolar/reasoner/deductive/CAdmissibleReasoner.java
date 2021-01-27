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

package org.tweetyproject.arg.bipolar.reasoner.deductive;

import org.tweetyproject.arg.bipolar.syntax.*;

import java.util.*;

/**
 * reasoner for admissibility in bipolar argumentation frameworks with deductive support interpretation
 * a set of arguments is c-admissible iff it is admissible wrt. the complex attacks in the framework and closed wrt.
 * the support relation
 */
public class CAdmissibleReasoner {
    public Collection<ArgumentSet> getModels(DeductiveArgumentationFramework bbase) {
        Collection<ArgumentSet> extensions = new HashSet<>();
        for (ArgumentSet ext: new DAdmissibleReasoner().getModels(bbase)) {
            if (bbase.isClosed(ext)) {
                extensions.add(ext);
            }
        }
        return extensions;
    }
}
