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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Basic Implementation of a reasoner for initial sets
 * A set of arguments S is considered initial iff it is non-empty and minimal among the non-empty admissible sets
 *
 * @see "Yuming Xu and Claudette Cayrol. 'Initial sets in abstract argumentation frameworks' (2016)"
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation' Argument & Computation (2022)"
 *
 * @author Lars Bengel
 */
public class SimpleInitialReasoner extends AbstractExtensionReasoner {

/**
 * The {@code Initial} enum represents the three different types of initial sets
 */
public enum Initial {

    /**
     * Unattacked initial sets
     */
    UA("unattacked", "ua"),

    /**
     * Unchallenged initial sets
     */
    UC("unchallenged", "uc"),

    /**
     * Challenged initial sets
     */
    C("challenged", "c");

    /**
     * A full description of the argument's status.
     */
    private final String description;

    /**
     * An abbreviation of the argument's status.
     */
    private final String abbreviation;

    /**
     * Constructs an {@code Initial} enum constant with the specified description and abbreviation.
     *
     * @param desc   The full description of the argument's status.
     * @param abbrev The abbreviation of the argument's status.
     */
    Initial(String desc, String abbrev) {
        this.description = desc;
        this.abbreviation = abbrev;
    }
}

    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Collection<Extension<DungTheory>> admExtensions = new SimpleAdmissibleReasoner().getModels(bbase);
        admExtensions.remove(new Extension<DungTheory>());

        Collection<Extension<DungTheory>> initExtensions = new HashSet<>();

        boolean minimal;
        for(Extension<DungTheory> e1: admExtensions){
            minimal = true;
            for(Extension<DungTheory> e2: admExtensions)
                if(e1 != e2 && e1.containsAll(e2)){
                    minimal = false;
                    break;
                }
            if(minimal)
                initExtensions.add(e1);
        }

        return initExtensions;
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        return null;
    }

    /**
     * An initial set S is called unattacked iff there is no attacker of S in F
     * @param ext an extension S of theory
     * @param theory a dung theory F
     * @return true if S is unattacked in F
     */
    public boolean isUnattacked(Extension<DungTheory> ext, DungTheory theory) {
        return theory.getAttackers(ext).isEmpty();
    }

    /**
     * An initial set S is called unchallenged in F iff S is not unattacked and there is no other initial set of F which attacks S
     * @param ext an extension S of theory
     * @param theory a dung theory F
     * @return true if S is unchallenged in F
     */
    public boolean isUnchallenged(Extension<DungTheory> ext, DungTheory theory) {
        return !isUnattacked(ext, theory) && !isChallenged(ext, theory);
    }

    /**
     * An initial set S is called challenged in F iff there is some other initial set of F which attacks S
     * @param ext an extension S of theory
     * @param theory a dung theory F
     * @return true if S is challenged in F
     */
    public boolean isChallenged(Extension<DungTheory> ext, DungTheory theory) {
        Collection<Extension<DungTheory>> initExtensions = this.getModels(theory);

        // method is only supposed to be used with initial sets
        if (!initExtensions.contains(ext)) {
            throw new IllegalArgumentException("Extensions must be an initial set of theory");
        }

        for (Extension<DungTheory> ext2: initExtensions) {
            if (theory.isAttacked(ext, ext2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper function that computes the initial sets of F and labels them in three categories
     * @param theory some argumentation theory
     * @return a map contain the three groups of initial sets
     */
    public static Map<Initial,Collection<Extension<DungTheory>>> partitionInitialSets(DungTheory theory) {
        Collection<Extension<DungTheory>> unattacked = new HashSet<>();
        Collection<Extension<DungTheory>> unchallenged = new HashSet<>();
        Collection<Extension<DungTheory>> challenged = new HashSet<>();

        Collection<Extension<DungTheory>> extensions = new SimpleInitialReasoner().getModels(theory);

        for (Extension<DungTheory> ext: extensions) {
            Collection<Argument> attackers = theory.getAttackers(ext);

            // if S is not attacked at all, it is unattacked
            if (attackers.isEmpty()) {
                unattacked.add(ext);
            } else {
                // compute intersection between all attackers of S and the set of arguments that are in some initial set
                Collection<Argument> initialAttackers = new HashSet<>();
                for (Extension<DungTheory> ext2 : extensions) {
                    initialAttackers.addAll(ext2);
                }
                attackers.retainAll(initialAttackers);

                // if the intersection is empty, then S is unchallenged. Otherwise, it is challenged
                if (attackers.isEmpty()) {
                    unchallenged.add(ext);
                } else {
                    challenged.add(ext);
                }
            }
        }

        Map<Initial,Collection<Extension<DungTheory>>> initialSets = new HashMap<>();
        initialSets.put(Initial.UA, unattacked);
        initialSets.put(Initial.UC, unchallenged);
        initialSets.put(Initial.C, challenged);
        return initialSets;
    }
}
