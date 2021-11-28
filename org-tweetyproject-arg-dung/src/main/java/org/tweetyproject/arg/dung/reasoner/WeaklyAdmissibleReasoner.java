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

package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * Reasoner for weak admissibility
 * the reasoner reduces the number of sets we have to check by computing candidate sets instead of checking all sets.
 * a candidate set of an Extension E is a conflict-free set S, which contains at least one attacker of E and
 * contains only arguments relevant to E and itself.
 * An argument is considered relevant to E iff it attacks any argument in the same component as E.
 *
 * see: Baumann, Brewka, Ulbricht:  Revisiting  the  foundations  of  abstract argumentation-semantics based on weak admissibility and weak defense.
 *
 * @author Lars Bengel
 */
public class WeaklyAdmissibleReasoner extends AbstractExtensionReasoner {
    /**
     * computes all weakly admissible sets of bbase. A set S is weakly admissible iff it is conflict-free
     * and no attacker of S is element of a weakly admissible set of the reduct of bbase wrt. S
     * @param bbase an argumentation framework
     * @return the weakly admissible sets of bbase
     */
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        DungTheory restrictedTheory = new DungTheory((DungTheory) bbase);
        // remove all self-attacking arguments
        for (Argument argument: (DungTheory) bbase) {
            if (restrictedTheory.isAttackedBy(argument, argument)) {
                restrictedTheory.remove(argument);
            }
        }

        Collection<Extension<DungTheory>> extensions = new HashSet<>();
        Set<Set<Argument>> cfSets = this.getConflictFreeSets(restrictedTheory, new HashSet<>(restrictedTheory));
        for (Set<Argument> args: cfSets) {
            Extension<DungTheory> ext = new Extension<DungTheory>(args);
            if (isWeaklyAdmissible(restrictedTheory, ext))
                extensions.add(ext);
        }
        return extensions;
    }

    /**
     * returns one weakly admissible set of bbase
     * @param bbase an argumentation framework
     * @return a weakly admissible set
     */
    public Extension<DungTheory> getModel(DungTheory bbase) {
        // empty set is always weakly admissible
        return new Extension<DungTheory>();
    }

    /**
     * checks whether ext is weakly admissible in bbase
     * @param bbase an argumentation framework
     * @param ext an extension
     * @return "true" if ext is weakly admissible in bbase
     */
    protected boolean isWeaklyAdmissible(DungTheory bbase, Extension<DungTheory> ext) {
        // empty set is always weakly admissible
        if (ext.isEmpty()) {
            return true;
        }

        Set<Set<Argument>> subsets = this.getAttackingCandidates(bbase, ext);
        for (Set<Argument> args: subsets) {
            Extension<DungTheory> subExt = new Extension<DungTheory>(args);

            // if we find one weakly admissible attacker in the reduct, then subExt is not weakly admissible
            if (isWeaklyAdmissible(this.getReduct(bbase, ext), subExt))
                return false;
        }
        return true;
    }

    /**
     * computes all conflict-free sets, which attack any argument in ext
     * @param bbase an argumentation framework
     * @param ext an extension
     * @return all sets which attack ext and are conflict-free
     */
    public Set<Set<Argument>> getAttackingCandidates(DungTheory bbase, Collection<Argument> ext) {
        Set<Set<Argument>> sets = new HashSet<>();
        // only consider attackers which are not deactivated by ext
        Collection<Argument> attackers = this.getAttackers(bbase, ext);
        attackers.removeAll(getAttacked(bbase, ext));
        DungTheory reduct_ext = this.getReduct(bbase, new Extension<DungTheory>(ext));
        for (Argument attacker: attackers) {
            DungTheory reduct = new DungTheory(reduct_ext);
            reduct.remove(attacker);
            reduct.removeAll(bbase.getAttacked(attacker));

            // filter out arguments which cannot be in the same weakly admissible set as attacker
            // ie. arguments attacked by attacker or arguments attacking attacker
            Set<Argument> candidates = new HashSet<>(reduct);
            candidates.removeAll(bbase.getAttackers(attacker));

            // remove all arguments from candidates, which have no connection with attacker in the framework
            // because they are not relevant for the weak admissibility of attacker
            Set<Argument> component = this.getComponent(reduct_ext, attacker);
            candidates.retainAll(component);

            // remove argument, which do not attack any argument in the framework
            // because adding them to the set would do nothing for its weak admissibility
            for (Argument candidate: new HashSet<>(candidates)) {
                if (reduct.getAttacked(candidate).isEmpty())
                    candidates.remove(candidate);
            }
            Set<Set<Argument>> subsets = getConflictFreeCandidateSets(reduct, candidates);
            for (Set<Argument> subset: subsets) {
                subset.add(attacker);
                sets.add(new HashSet<>(subset));
            }
            Set<Argument> set = new HashSet<>();
            set.add(attacker);
            sets.add(set);
        }
        return sets;
    }

    /**
     * computes all conflict-free sets of bbase, that contain only arguments in candidates and all arguments are relevant
     * an argument is considered relevant iff it attacks any argument in the same component as the given argument A
     * @param bbase an argumentation framework
     * @param candidates a set of arguments
     * @return conflict-free sets in bbase
     */
    public Set<Set<Argument>> getConflictFreeCandidateSets(DungTheory bbase, Collection<Argument> candidates) {
        Set<Set<Argument>> subsets = new HashSet<>();
        if (candidates.size() == 0 || bbase.size() == 0) {
            subsets.add(new HashSet<>());
        } else {
            for (Argument element: candidates) {
                DungTheory remainingTheory = new DungTheory(bbase);
                remainingTheory.remove(element);
                remainingTheory.removeAll(bbase.getAttacked(element));

                // filter remaining candidates for this conflict-free set S by removing arguments
                // attacked by S and arguments attacking S
                Set<Argument> remainingCandidates = new HashSet<>(candidates);
                remainingCandidates.remove(element);
                remainingCandidates.removeAll(bbase.getAttacked(element));
                remainingCandidates.removeAll(bbase.getAttackers(element));

                // remove all arguments which are not connected to any argument in candidates
                Set<Argument> component = this.getComponent(bbase, new HashSet<>(candidates), candidates);
                remainingCandidates.retainAll(component);

                // remove arguments which do not attack anything from candidates
                for (Argument candidate: new HashSet<>(remainingCandidates)) {
                    if (remainingTheory.getAttacked(candidate).isEmpty()) {
                        remainingCandidates.remove(candidate);
                    }
                }

                Set<Set<Argument>> subsubsets = this.getConflictFreeCandidateSets(remainingTheory, remainingCandidates);

                for (Set<Argument> subsubset : subsubsets) {
                    subsets.add(new HashSet<>(subsubset));
                    subsubset.add(element);
                    subsets.add(new HashSet<>(subsubset));
                }
            }
        }

        return subsets;
    }

    /**
     * finds the set of arguments S = {a | there exists an edge between a and argument}
     * @param bbase an argumentation framework
     * @param argument an argument
     * @return the set of arguments connected with argument
     */
    public Set<Argument> getComponent(DungTheory bbase, Argument argument) {
        Set<Argument> arguments = new HashSet<>();
        arguments.add(argument);
        return getComponent(bbase, arguments, arguments);
    }

    /**
     * recursively checks edges for all nodes connected with the given arguments
     * @param bbase an argumentation framework
     * @param arguments the current component
     * @param toCheck set of arguments, for which we have not yet checked the edges
     * @return the set of arguments connected with the given arguments
     */
    private Set<Argument> getComponent(DungTheory bbase, Collection<Argument> arguments, Collection<Argument> toCheck) {
        if (toCheck.isEmpty()) {
            return new HashSet<>(arguments);
        }
        Set<Argument> newArguments = new HashSet<>();
        newArguments.addAll(this.getAttacked(bbase, toCheck));
        newArguments.addAll(this.getAttackers(bbase, toCheck));
        newArguments.removeAll(arguments);
        arguments.addAll(newArguments);
        return getComponent(bbase, arguments, newArguments);
    }

    /**
     * computes all conflict-free sets of bbase, that contain only arguments in candidates
     * @param bbase an argumentation framework
     * @param candidates a set of arguments
     * @return conflict-free sets in bbase
     */
    public Set<Set<Argument>> getConflictFreeSets(DungTheory bbase, Collection<Argument> candidates) {
        Set<Set<Argument>> subsets = new HashSet<>();
        if (candidates.size() == 0 || bbase.size() == 0) {
            subsets.add(new HashSet<>());
        } else {
            for (Argument element: candidates) {
                DungTheory remainingTheory = new DungTheory(bbase);
                remainingTheory.remove(element);
                remainingTheory.removeAll(bbase.getAttacked(element));

                Set<Argument> remainingCandidates = new HashSet<>(candidates);
                remainingCandidates.remove(element);
                remainingCandidates.removeAll(bbase.getAttacked(element));
                remainingCandidates.removeAll(bbase.getAttackers(element));

                Set<Set<Argument>> subsubsets = this.getConflictFreeSets(remainingTheory, remainingCandidates);

                for (Set<Argument> subsubset : subsubsets) {
                    subsets.add(new HashSet<>(subsubset));
                    subsubset.add(element);
                    subsets.add(new HashSet<>(subsubset));
                }
            }
        }

        return subsets;
    }

    /**
     * computes the reduct of bbase wrt. ext
     * ie. removes all arguments from bbase which are either in ext or are attacked by any element of ext
     * @param bbase an argumentation framework
     * @param ext an extension
     * @return the reduct of bbase wrt. ext
     */
    public DungTheory getReduct(DungTheory bbase, Extension<DungTheory> ext) {
        DungTheory reduct = new DungTheory(bbase);

        reduct.removeAll(ext);
        reduct.removeAll(getAttacked(bbase, ext));
        return reduct;
    }

    /**
     * computes the set of arguments attacked by an element of arguments in bbase
     * @param bbase an argumentation framework
     * @param arguments a set of arguments
     * @return the set of arguments attacked by arguments
     */
    protected Collection<Argument> getAttacked(DungTheory bbase, Collection<Argument> arguments) {
        Collection<Argument> attacked = new HashSet<>();
        for (Argument argument: arguments) {
            attacked.addAll(bbase.getAttacked(argument));
        }
        return attacked;
    }

    /**
     * computes the set of arguments attacking an element of arguments in bbase
     * @param bbase an argumentation framework
     * @param arguments a set of arguments
     * @return the set of arguments attacking arguments
     */
    protected Collection<Argument> getAttackers(DungTheory bbase, Collection<Argument> arguments) {
        Collection<Argument> attackers = new HashSet<>();
        for (Argument argument: arguments) {
            attackers.addAll(bbase.getAttackers(argument));
        }
        return attackers;
    }
    
    
}
