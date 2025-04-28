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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence;

import org.tweetyproject.arg.dung.equivalence.kernel.EquivalenceKernel;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class defines normal expansion equivalence for {@link DungTheory argumentation frameworks} wrt. some {@link Semantics semantics},
 * i.e., two AFs F andG are strong expansion equivalent iff they possess the same set of
 * {@link org.tweetyproject.arg.dung.semantics.Extension extensions} wrt. the {@link Semantics semantics} when conjoined
 * with some AF H that only adds arguments and attacks that involve at least one new argument.
 * Can be characterized by a syntactic kernel and is actually equivalent to strong equivalence wrt. the respective semantics.
 *
 * @see "Baumann, Ringo. 'Normal and strong expansion equivalence for argumentation frameworks.' Artificial Intelligence 193 (2012): 18-44."
 *
 * @author Lars Bengel
 */
public class NormalExpansionEquivalence extends StrongEquivalence {

    /**
     * Initialize Normal Expansion Equivalence for the given semantics
     * @param semantics some semantics
     */
    public NormalExpansionEquivalence(Semantics semantics) {
        super(semantics);
    }

    /**
     * Initialize Normal Expansion Equivalence with the given kernel
     * @param kernel an equivalence kernel
     */
    public NormalExpansionEquivalence(EquivalenceKernel kernel) {
        super(kernel);
    }

    @Override
    public String getName() {
        return "Normal Expansion Equivalence";
    }
}
