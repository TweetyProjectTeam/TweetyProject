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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Reasoner for weakly complete semantics as described in:
 *
 * see: Baumann, Brewka, Ulbricht:  Revisiting  the  foundations  of  abstract argumentation-semantics based on weak admissibility and weak defense.
 *
 * a set of arguments E is w-complete iff it is w-admissible and there exists no superset of E that is w-defended by E
 *
 * @author Lars Bengel
 */
public class WeaklyCompleteReasoner extends AbstractExtensionReasoner {

    @Override
    public Collection<Extension> getModels(DungTheory bbase) {
        Collection<Extension> wad_exts = new WeaklyAdmissibleReasoner().getModels(bbase);

        Collection<Extension> result = new HashSet<>();

        // check all w-admissible extensions of bbase
        for (Extension ext: wad_exts) {
            boolean w_complete = true;
            for (Set<Argument> S : new SetTools<Argument>().subsets(bbase)) {
                // S is a superset of ext
                if (!ext.equals(new Extension(S)) && S.containsAll(ext)) {
                    // S is w-defended by ext
                    System.out.println("Ext:" + ext);
                    System.out.println("Superset:" + S);
                    System.out.println("DEFENDS: " + isWeaklyDefendedBy(S, ext, bbase));
                    if (this.isWeaklyDefendedBy(S, ext, bbase)) {
                        w_complete = false;
                        break;
                    }
                }
            }
            if (w_complete) {
                result.add(ext);
            }
        }
        return result;
    }

    @Override
    public Extension getModel(DungTheory bbase) {
        return this.getModels(bbase).iterator().next();
    }

    /**
     * Computes whether E w-defends X
     * i.e. for every attacker y of X: 1.
     * E attacks y or
     * 2. y is not in any w-admissible set of the E-reduct of theory, y is not in E and there exists
     * a superset of X that is w-admissible in theory
     * @param X a set of arguments
     * @param E a set of arguments
     * @param theory a dung theory
     * @return true, if E w-defends X
     */
    public boolean isWeaklyDefendedBy(Collection<Argument> X, Collection<Argument> E, DungTheory theory) {

        // compute the set of arguments that are in a w-ad extension of the E-reduct of F
        DungTheory e_reduct = new WeaklyAdmissibleReasoner().getReduct(theory, new Extension(E));
        Collection<Extension> reduct_exts = new WeaklyAdmissibleReasoner().getModels(e_reduct);
        Collection<Argument> wad_arguments = new HashSet<>();
        for (Extension ext: reduct_exts) {
            wad_arguments.addAll(ext);
        }

        Collection<Extension> wad_exts = new WeaklyAdmissibleReasoner().getModels(theory);
        Collection<Set<Argument>> subsets = new SetTools<Argument>().subsets(theory);

        boolean superset_wad = false;
        for (Set<Argument> S: subsets) {
            // S is a superset of X
            if (S.containsAll(X)) {
                // S is w-admissible in F
                if (wad_exts.contains(new Extension(S))) {
                    superset_wad = true;
                    break;
                }
            }
        }
        // there exists no superset of X that is w-ad in F
        if (!superset_wad)
            return false;

        for (Argument y: new WeaklyAdmissibleReasoner().getAttackers(theory, X)) {
            // E attacks y
            if (theory.isAttacked(y, new Extension(E))) {
                continue;
            }

            // y is not in E
            if (E.contains(y)) {
                return false;
            }

            // y is in no w-ad extension of the E-reduct of F
            if (wad_arguments.contains(y)) {
                return false;
            }

        }
        return true;

    }
}
