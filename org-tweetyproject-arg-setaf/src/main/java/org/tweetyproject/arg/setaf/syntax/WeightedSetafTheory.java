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

package org.tweetyproject.arg.setaf.syntax;

import java.util.*;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Minimalistic Implementation of a weighted argumentation theory
 * used for learning argumentation theories from labelings
 *
 * @author  Sebastian Franke
 */
public class WeightedSetafTheory extends DungTheory {

    /**
     * listing of weights of every edge in the argumentation graph
     */
    public Map<Attack, Double> weights;

    /**
     * initialize a new weighted argumentation theory
     */
    public WeightedSetafTheory() {
        super();
        weights = new HashMap<>();
    }

    /**
     * return weight of the attack between the given arguments
     * @param attacker an argument
     * @param attacked an argument
     * @return weight of the attack
     */
    public Double getWeight(Argument attacker, Argument attacked) {
        return this.getWeight(new Attack(attacker, attacked));
    }

    /**
     * return weight of the given attack, weight is 0 if the attack is not present
     * @param attack an attack
     * @return weight of attack
     */
    public Double getWeight(Attack attack) {
        return this.weights.getOrDefault(attack, 0.0);
    }

    /**
     * sets the weight of the given attack to the given value
     * @param attack an attack
     * @param weight new value for the weight
     * @return null or the previously associated value of attack
     */
    public Double setWeight(Attack attack, double weight) {
        return this.weights.put(attack, weight);
    }

    /**
     * updates the weight of the given attack with the given value and return old value
     * @param attack an attack
     * @param weight new value for the weight
     * @return previously associated value of attack
     */
    public Double updateWeight(Attack attack, double weight) {
        double old = this.getWeight(attack);
        this.weights.put(attack, old + weight);
        return old;
    }

    /**
     * compute Dung Theory only containing attacks with weights greater than zero
     * @return computed Dung Theory
     */
    public DungTheory getDungTheory() {
        return getDungTheory(0);
    }

    /**
     * compute Dung Theory only containing attacks with weight above the given threshold
     * @param threshold cutoff for attacks
     * @return Dung Theory wrt. given threshold
     */
    public DungTheory getDungTheory(double threshold) {
        DungTheory theory = new DungTheory();
        theory.addAll(this);
        for (Attack attack: this.getAttacks()) {
            if (this.getWeight(attack) > threshold) {
                theory.add(attack);
            }
        }
        return theory;
    }

    /**
     * add attack between both arguments to the theory and set weight to given value
     * @param attacker an argument
     * @param attacked an argument
     * @param weight the weight of the attack
     * @return "true" if attack between the arguments was added
     */
    public boolean addAttack(Argument attacker, Argument attacked, double weight) {
        //TODO secure statement
        this.setWeight(new Attack(attacker, attacked), weight);
        return super.addAttack(attacker, attacked);
    }

    /**
     * add attack between both arguments to the theory and set weight to 1
     * @param attacker an argument
     * @param attacked an argument
     * @return "true" if attack between the arguments was added
     */
    @Override
    public boolean addAttack(Argument attacker, Argument attacked) {
        return this.addAttack(attacker, attacked, 1.0);
    }

    /**
     * remove attack from theory and reset weight
     * @param attack an attack
     * @return "true" if attack successfully removed
     */
    @Override
    public boolean remove(Attack attack) {
        //TODO secure statement
        this.weights.remove(attack);
        return super.remove(attack);
    }

    /**
     * remove all attacks with weight &lt; threshold
     * @param threshold some threshold
     * @return "true" if all attacks were removed
     */
    public boolean removeDiscardedAttacks(int threshold) {
        boolean result = true;
        for (Attack att: this.getAttacks()) {
            if (this.getWeight(att) < threshold) {
                result &= this.remove(att);
            }
        }
        return result;
    }

    public boolean removeDiscardedAttacks() {
        return this.removeDiscardedAttacks(0);
    }
}
