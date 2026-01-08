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
 *  Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;


/**
 * This class defines strong expansion equivalence for {@link DungTheory argumentation frameworks} wrt. some {@link Semantics semantics},
 * i.e., two AFs F and G are strong expansion equivalent iff they possess the same set of
 * {@link org.tweetyproject.arg.dung.semantics.Extension extensions} wrt. the {@link Semantics semantics} when conjoined
 * with some AF H that only adds arguments and attacks originating from old arguments, i.e., a weak normal expansion.
 *
 *
 * @see "Ringo Baumann and Gerhard Brewka. 'The equivalence zoo for Dung-style semantics.' Journal of Logic and Computation 28.3 (2018): 477-498."
 *
 * @author Lars Bengel
 */
public class WeakExpansionEquivalence implements Equivalence<DungTheory> {

    /** the semantics of this equivalence instance **/
    private final Semantics semantics;

    /**
     * Initializes a new instance of this equivalence wrt. the given semantics
     * @param semantics some semantics
     */
    public WeakExpansionEquivalence(Semantics semantics) {
        this.semantics = semantics;
    }

    @Override
    public boolean isEquivalent(DungTheory theory1, DungTheory theory2) {
        switch (semantics) {
            case ADM,PR,CO,GR,SAD -> {
                Collection<Extension<DungTheory>> exts1 = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics).getModels(theory1);
                Collection<Extension<DungTheory>> exts2 = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics).getModels(theory2);
                if (!new HashSet<>(theory1).equals(new HashSet<>(theory2))) return false;
                if (!exts1.equals(exts2)) return false;

                for (Extension<DungTheory> ext : exts1) {
                    Labeling lab1 = new Labeling(theory1, ext);
                    Labeling lab2 = new Labeling(theory2, ext);
                    if (!lab1.getArgumentsOfStatus(ArgumentStatus.UNDECIDED).equals(lab2.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)))
                        return false;
                }
                return true;
            }
            case ST -> {
                Collection<Extension<DungTheory>> exts1 = new SimpleStableReasoner().getModels(theory1);
                Collection<Extension<DungTheory>> exts2 = new SimpleStableReasoner().getModels(theory2);
                if (exts1.isEmpty() && exts2.isEmpty()) return true;
                if (!new HashSet<>(theory1).equals(new HashSet<>(theory2))) return false;
                return exts1.equals(exts2);
            }
            default -> {
                throw new IllegalArgumentException("Unsupported Semantics");
            }
        }
    }

    @Override
    public boolean isEquivalent(Collection<DungTheory> theories) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getName() {
        return "Weak Expansion Equivalence";
    }
}
