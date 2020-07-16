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

package net.sf.tweety.arg.dung.reasoner;

import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.commons.util.SetTools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * Reasoner for ordering semantics
 *
 * @author Lars Bengel
 */
public class OrderingSemanticsReasoner {
    private OrderingSemantics semantics1;
    private OrderingSemantics semantics2;
    private Method getCompareSet1;
    private Method getCompareSet2;

    /**
     * create a reasoner for the given ordering semantics
     * @param semantics an ordering semantics
     */
    public OrderingSemanticsReasoner(OrderingSemantics semantics) throws NoSuchMethodException {
        this.semantics1 = semantics;
        this.getCompareSet1 = this.getCompareMethod(semantics);
        this.getCompareSet2 = OrderingSemanticsReasoner.class.getMethod("getEmpty", Extension.class, DungTheory.class);
    }

    /**
     * create a reasoner for the given combination of ordering semantics
     * @param semantics1 an ordering semantics
     * @param semantics2 an ordering semantics
     */
    public OrderingSemanticsReasoner(OrderingSemantics semantics1, OrderingSemantics semantics2) throws NoSuchMethodException {
        this.semantics1 = semantics1;
        this.semantics2 = semantics2;
        this.getCompareSet1 = this.getCompareMethod(semantics1);
        this.getCompareSet2 = this.getCompareMethod(semantics2);
    }

    /**
     * compute the ordering over all subsets of theory wrt. to the ordering semantics
     * this is done by counting the attacks in a theoretical meta graph, where directed edges represent subset
     * relations over the semantics compare sets.
     * @param theory a dung theory
     * @return a list representing the ordering of all subsets of the given graph wrt. the ordering semantics
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    public List<Collection<Collection<Argument>>> getModels(DungTheory theory) throws InvocationTargetException, IllegalAccessException {
        // compute "compare sets" for each subset of theory
        Map<Collection<Argument>, Collection<Argument>> exts1 = new HashMap<>();
        Map<Collection<Argument>, Collection<Argument>> exts2 = new HashMap<>();
        Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(theory);
        for (Collection<Argument> subset: subsets) {
            Object[] parameters = new Object[2];
            parameters[0] = new Extension(subset);
            parameters[1] = theory;
            exts1.put(subset, (Collection<Argument>) this.getCompareSet1.invoke(this, parameters));
            exts2.put(subset, (Collection<Argument>) this.getCompareSet2.invoke(this, parameters));
        }

        // compare all subsets and count number of attacks in the theoretical meta argumentation graph
        // more attacks means higher level in the ordering
        Map<Collection<Argument>, Integer> numAttacks = new HashMap<>();
        for (Collection<Argument> subset1: subsets) {
            numAttacks.putIfAbsent(subset1, 0);
            Collection<Argument> cs1_1 = exts1.get(subset1);
            for (Collection<Argument> subset2: subsets) {
                Collection<Argument> cs2_1 = exts1.get(subset2);
                Collection<Argument> union1 = new HashSet<>(cs1_1);
                union1.addAll(cs2_1);
                if (union1.equals(cs1_1) && !union1.equals(cs2_1)) {
                    // subset2 is subset of subset1 iff the union of the compare sets equals the compare set of 1 and not 2
                    numAttacks.put(subset2, numAttacks.getOrDefault(subset2, 0) + 1);
                } else if (!union1.equals(cs1_1) && union1.equals(cs2_1)) {
                    // subset1 is subset of subset2 iff the union of the compare sets equals the compare set of 2 and not 1
                    numAttacks.put(subset1, numAttacks.getOrDefault(subset1, 0) + 1);
                } else if (union1.equals(cs1_1) && union1.equals(cs2_1)) {
                    // both sets are equal in regards to the first ordering semantics
                    Collection<Argument> cs1_2 = exts2.get(subset1);
                    Collection<Argument> cs2_2 = exts2.get(subset2);
                    Collection<Argument> union2 = new HashSet<>(cs1_2);
                    union2.addAll(cs2_2);
                    if (union2.equals(cs1_2) && !union2.equals(cs2_2)) {
                        // subset2 is subset of subset1 in regards to the second ordering semantics
                        numAttacks.put(subset2, numAttacks.getOrDefault(subset2, 0) + 1);
                    } else if (!union2.equals(cs1_2) && union2.equals(cs2_2)) {
                        // subset1 is subset of subset2 in regards to the second ordering semantics
                        numAttacks.put(subset1, numAttacks.getOrDefault(subset1, 0) + 1);
                    }
                }
            }
        }

        // get the list of "levels" in the resulting ordering
        // extensions on the same level are considered equal or incomparable
        List<Integer> levels = new ArrayList<>(new HashSet<>(numAttacks.values()));
        levels.sort(Collections.reverseOrder());

        // temporarily write the ordering in a ordered map
        Map<Integer, Collection<Collection<Argument>>> result = new LinkedHashMap<>();

        for (int level: levels) {
            result.put(level, new HashSet<>());
        }
        for (Collection<Argument> subset: subsets) {
            // add the subset to the "level" it belongs in
            result.get(numAttacks.get(subset)).add(subset);
        }

        // return the list of levels, which is ordered from best to worst
        return new ArrayList<>(result.values());
    }

    /**
     * return the corresponding method for each ordering semantics
     * @param semantics an ordering semantic
     * @return the corresponding method
     * @throws NoSuchMethodException should never happen
     */
    private Method getCompareMethod(OrderingSemantics semantics) throws NoSuchMethodException {
        switch (semantics) {
            case CF:
                return OrderingSemanticsReasoner.class.getMethod("getConflicts", Extension.class, DungTheory.class);
            case AD:
                return OrderingSemanticsReasoner.class.getMethod("getUndefended", Extension.class, DungTheory.class);
            case ST:
                return OrderingSemanticsReasoner.class.getMethod("getUnattacked", Extension.class, DungTheory.class);
            case DN:
                return OrderingSemanticsReasoner.class.getMethod("getDefendedNotIn", Extension.class, DungTheory.class);
            default:
                throw new IllegalArgumentException("Unknown semantics.");
        }

    }

    /**
     * dummy method used if only one ordering semantics is specified
     * @param ext an extension
     * @param theory a dung theory
     * @return always return empty set
     */
    public Collection<Argument> getEmpty(Extension ext, DungTheory theory) {
        return new HashSet<>();
    }

    /**
     * computes the set of conflicts occurring inside ext
     * @param ext an extension
     * @param theory a dung theory
     * @return set of conflict in ext
     */
    public Collection<Argument> getConflicts(Extension ext, DungTheory theory) {
        Collection<Argument> conflicts = new HashSet<>();
        for (Attack att: theory.getAttacks()) {
            if (ext.contains(att.getAttacker()) && ext.contains(att.getAttacked())) {
                conflicts.add(new Argument(att.toString()));
            }
        }
        return conflicts;
    }

    /**
     * computes the set of arguments in ext, which are not defended by ext against outside attackers
     * @param ext an extension
     * @param theory a dung theory
     * @return set of arguments in ext which are not defended by ext
     */
    public Collection<Argument> getUndefended(Extension ext, DungTheory theory) {
        Collection<Argument> undefended = new HashSet<>();
        for (Argument arg: ext) {
            for (Argument attacker: theory.getAttackers(arg)) {
                if (!ext.contains(attacker) && !theory.isAttacked(attacker, ext)) {
                    undefended.add(arg);
                    break;
                }
            }
        }
        return undefended;
    }

    /**
     * computes the set of arguments outside of ext, which are not attacked by ext
     * @param ext an extension
     * @param theory a dung theory
     * @return set of arguments in theory \ ext which are not attacked by ext
     */
    public Collection<Argument> getUnattacked(Extension ext, DungTheory theory) {
        Collection<Argument> unattacked = new HashSet<>();
        for (Argument arg: theory) {
            if (!ext.contains(arg) && !theory.isAttacked(arg, ext)) {
                unattacked.add(arg);
            }
        }
        return unattacked;
    }

    /**
     * computes the set of arguments outside of ext, which are defended by ext
     * @param ext an extension
     * @param theory a dung theory
     * @return set of arguments in theory \ ext which are defended by ext
     */
    public Collection<Argument> getDefendedNotIn(Extension ext, DungTheory theory) {
        Collection<Argument> defendedNotIn = new HashSet<>();
        Collection<Argument> args = new HashSet<>(theory);
        args.removeAll(ext);
        for (Argument arg: args) {
            boolean defended = true;
            if (ext.contains(arg) || theory.isAttacked(arg, ext)) {
                continue;
            }
            for (Argument b: theory.getAttackers(arg)) {
                if (!theory.isAttacked(b, ext)) {
                    defended = false;
                    break;
                }
            }
            if (defended) {
                defendedNotIn.add(arg);
            }
        }
        return defendedNotIn;
    }

    /**
     * print out table showing the sets according to the given ordering semantics
     * @param theory a dung theory
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    public void show(DungTheory theory) throws InvocationTargetException, IllegalAccessException {
        // compute "compare sets" for each subset of theory
        Map<Collection<Argument>, Collection<Argument>> exts1 = new HashMap<>();
        Map<Collection<Argument>, Collection<Argument>> exts2 = new HashMap<>();
        Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(theory);
        for (Collection<Argument> subset: subsets) {
            Object[] parameters = new Object[2];
            parameters[0] = new Extension(subset);
            parameters[1] = theory;
            exts1.put(subset, (Collection<Argument>) this.getCompareSet1.invoke(this, parameters));
            exts2.put(subset, (Collection<Argument>) this.getCompareSet2.invoke(this, parameters));
        }
        // print out in table format
        if (this.semantics2 != null) {
            // two columns for the two given ordering semantics
            Object[] row = new String[]{"Extension", this.semantics1.toString(), this.semantics2.toString()};
            System.out.format("%-20s%20s%20s\n", row);
            for (Collection<Argument> subset: subsets) {
                row = new String[] {subset.toString(), exts1.get(subset).toString(), exts2.get(subset).toString()};
                System.out.format("%-20s%20s%20s\n", row);
            }
        } else {
            // one column
            Object[] row = new String[]{"Extension", this.semantics1.toString()};
            System.out.format("%-20s%20s\n", row);
            for (Collection<Argument> subset: subsets) {
                row = new String[] {subset.toString(), exts1.get(subset).toString()};
                System.out.format("%-20s%20s\n", row);
            }
        }
    }
}
