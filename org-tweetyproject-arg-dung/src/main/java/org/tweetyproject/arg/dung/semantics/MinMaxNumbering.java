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
package org.tweetyproject.arg.dung.semantics;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * Class representing Min-Max numberings for labelings of argumentation frameworks
 *
 * @author Lars Bengel
 */
public class MinMaxNumbering implements Map<Argument, Integer> {
    /** maximum number of iteration after which all still changing values are considered infinite */
    private static final int CUTOFF = 100;
    private Map<Argument, Integer> numbering;

    /**
     * Initializes a new empty Min-Max Numbering
     */
    public MinMaxNumbering() {
        this.numbering = new HashMap<>();
    }

    /**
     * Initializes a new Min-Max Numbering for the given theory and extension
     * @param theory some argumentation framework
     * @param extension some extension
     */
    public MinMaxNumbering(ArgumentationFramework<Argument> theory, Extension<? extends ArgumentationFramework<Argument>> extension) {
        this(theory, new Labeling(theory, extension));
    }

    /**
     * Initialize a new Min-Max Numbering from the given theory and labeling
     * @param theory some argumentation framework
     * @param labeling some labeling
     */
    public MinMaxNumbering(ArgumentationFramework<Argument> theory, Labeling labeling) {
        this();
        Map<Argument, Integer> valuations = new HashMap<>();
        Map<Argument, Integer> valuationsOld;

        int it = 0;
        do {
            valuationsOld = (Map<Argument, Integer>) ((HashMap<Argument, Integer>) valuations).clone();
            for (Argument arg: (DungTheory) theory) {
                if (labeling.get(arg)==ArgumentStatus.UNDECIDED) continue;
                valuations.put(arg, calculateMinMaxValue((DungTheory) theory, labeling, valuationsOld, arg));
            }
            it++;
        } while (getDistance(valuationsOld, valuations) >= 1 && it < CUTOFF);

        for (Argument arg: valuations.keySet()) {
            if (valuations.get(arg) == CUTOFF) {
                valuations.put(arg, Integer.MAX_VALUE);
            }
        }
        this.numbering = valuations;
    }

    /**
     * Computes the new minmax value for the given argument
     * @param theory some argumentation framework
     * @param labeling some labeling
     * @param vOld the old minmax-numbering
     * @param argument some argument
     * @return the updated minmax value for the given argument
     */
    private int calculateMinMaxValue(DungTheory theory, Labeling labeling, Map<Argument, Integer> vOld, Argument argument) {
        Collection<Integer> attackerValues = new HashSet<>();
        if (labeling.get(argument) == ArgumentStatus.IN) {
            for (Argument attacker: theory.getAttackers(argument)) {
                if (labeling.get(attacker)==ArgumentStatus.OUT) {
                    attackerValues.add(vOld.getOrDefault(attacker, 0));
                }
            }
            int maxValue = getMaximum(attackerValues);
            return maxValue==Integer.MAX_VALUE ? Integer.MAX_VALUE : maxValue+1;
        } else if (labeling.get(argument) == ArgumentStatus.OUT) {
            for (Argument attacker: theory.getAttackers(argument)) {
                if (labeling.get(attacker)==ArgumentStatus.IN) {
                    attackerValues.add(vOld.getOrDefault(attacker, 0));
                }
            }
            int minValue = getMinimum(attackerValues);
            return minValue==Integer.MAX_VALUE ? Integer.MAX_VALUE : minValue+1;
        } else {
            throw new IllegalArgumentException("Cannot compute MinMax value for Argument with label UNDECIDED.");
        }
    }

    /**
     * returns the minimum value of the given collection of integers
     * @param values a collection of integers
     * @return the minimum value of <code>values</code>, returns <code>Integer.MAX_VALUE</code> if <code>values</code> is empty
     */
    private int getMinimum(Collection<Integer> values) {
        if (values.isEmpty()) return Integer.MAX_VALUE;
        return Collections.min(values);
    }

    /**
     * returns the maximum value of the given collection of integers
     * @param values a collection of integers
     * @return the maximum value of <code>values</code>, returns <code>0</code> if <code>values</code> is empty
     */
    private int getMaximum(Collection<Integer> values) {
        if (values.isEmpty()) return 0;
        return Collections.max(values);
    }

    /**
     * Computes the Euclidean distance between to the given map of values.
     * @param vOld first array
     * @param v second array
     * @return distance between v and vOld
     */
    private int getDistance(Map<Argument,Integer> vOld, Map<Argument,Integer> v) {
        int sum = 0;
        for (Argument arg: v.keySet()) {
            sum += v.getOrDefault(arg, 0) - vOld.getOrDefault(arg, 0);
        }
        return sum;
    }

    public Collection<Argument> getArgumentsOfRank(Integer value) {
        Extension<ArgumentationFramework<Argument>> ext = new Extension<>();
        for (Argument a : this.numbering.keySet())
            if (this.numbering.get(a).equals(value))
                ext.add(a);
        return ext;
    }

    @Override
    public int size() {
        return this.numbering.size();
    }

    @Override
    public boolean isEmpty() {
        return this.numbering.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.numbering.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.numbering.containsValue(value);
    }

    @Override
    public Integer get(Object key) {
        return this.numbering.get(key);
    }

    @Override
    public Integer put(Argument key, Integer value) {
        return this.numbering.put(key, value);
    }

    @Override
    public Integer remove(Object key) {
        return this.numbering.remove(key);
    }

    @Override
    public void putAll(Map<? extends Argument, ? extends Integer> m) {
        this.numbering.putAll(m);
    }

    @Override
    public void clear() {
        this.numbering.clear();
    }

    @Override
    public Set<Argument> keySet() {
        return this.numbering.keySet();
    }

    @Override
    public Collection<Integer> values() {
        return this.numbering.values();
    }

    @Override
    public Set<Entry<Argument, Integer>> entrySet() {
        return this.numbering.entrySet();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
