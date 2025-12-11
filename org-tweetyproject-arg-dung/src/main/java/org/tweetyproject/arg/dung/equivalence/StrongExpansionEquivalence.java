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

import java.util.Collection;

/**
 * This class defines strong expansion equivalence for {@link DungTheory argumentation frameworks} wrt. some {@link Semantics semantics},
 * i.e., two AFs F and G are strong expansion equivalent iff they possess the same set of
 * {@link org.tweetyproject.arg.dung.semantics.Extension extensions} wrt. the {@link Semantics semantics} when conjoined
 * with some AF H that only adds arguments and attacks originating from new arguments, i.e., a strong normal expansion.
 *
 * Can be characterized by a syntactic kernel.
 *
 * @see "Ringo Baumann. 'Normal and strong expansion equivalence for argumentation frameworks.' Artificial Intelligence 193 (2012): 18-44."
 *
 * @author Lars Bengel
 */
public class StrongExpansionEquivalence implements Equivalence<DungTheory> {

    private final EquivalenceKernel kernel;

    /**
     * Initialize Strong Expansion Equivalence with the given kernel
     * @param kernel an equivalence kernel
     */
    public StrongExpansionEquivalence(EquivalenceKernel kernel) {
        this.kernel = kernel;
    }

    /**
     * Initializes a Strong Expansion Equivalence instance with a kernel for the given semantics
     * @param semantics some semantics
     */
    public StrongExpansionEquivalence(Semantics semantics) {
        switch (semantics) {
            case ST -> kernel = EquivalenceKernel.STABLE;
            case CO -> kernel = EquivalenceKernel.SE_COMPLETE;
            case GR,SAD -> kernel = EquivalenceKernel.SE_GROUNDED;
            case ADM,PR,ID,UC -> kernel = EquivalenceKernel.SE_ADMISSIBLE;
            case SST,EA -> kernel = EquivalenceKernel.ADMISSIBLE;
            default -> throw new IllegalArgumentException("Unsupported Semantics: " + semantics);
        }
    }

    @Override
    public boolean isEquivalent(DungTheory theory1, DungTheory theory2) {
        DungTheory kernelTheory1 = this.kernel.getKernel(theory1);
        DungTheory kernelTheory2 = this.kernel.getKernel(theory2);

        return kernelTheory1.equals(kernelTheory2);
    }

    @Override
    public boolean isEquivalent(Collection<DungTheory> theories) {
        DungTheory first = theories.iterator().next();

        for(DungTheory theory : theories) {
            if (!isEquivalent(theory, first)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return "Strong Expansion Equivalence";
    }
}
