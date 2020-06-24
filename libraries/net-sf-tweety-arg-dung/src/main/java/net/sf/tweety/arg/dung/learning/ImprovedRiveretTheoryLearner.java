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

package net.sf.tweety.arg.dung.learning;


import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;

import java.util.*;

/**
 * Implementation of the algorithm for learning (grounded) labelings from with some improvements:
 * Riveret, Régis, and Guido Governatori. "On learning attacks in probabilistic abstract argumentation." 2016.
 *
 * - instead of stopping when no undecided attacks are left, we always use the full budget. While slower, greatly improves results.
 * - added additional parameter for pruning instead of pruning at 0. approx. -10% of budget gives better results. eg budget: 1000 threshold: -100
 * - added fifth rule to capture self-attacking arguments
 *
 * @author Lars Bengel
 */
public class ImprovedRiveretTheoryLearner {
    private WeightedDungTheory theory;
    private int cycles;
    private int MAX_CYCLES;


    /**
     * variable for tracking the amount of undecided attacks in the theory
     */
    private int undecidedAttacks;

    /**
     * initialize learner for the given set of arguments
     * @param arguments a set of arguments
     */
    public ImprovedRiveretTheoryLearner(Collection<Argument> arguments, int max_cycles) {
        this.cycles = 0;
        this.MAX_CYCLES = max_cycles;
        // initialize argumentation graph with all edges with weight 0
        this.theory = new WeightedDungTheory();
        this.theory.addAll(arguments);
        for (Argument a: this.theory) {
            for (Argument b: this.theory) {
                this.theory.addAttack(a, b, 0.0);
            }
        }
        this.undecidedAttacks = arguments.size() * arguments.size();
    }

    /**
     * learn the given label and update the weights of affected attacks according to the 4 rules
     * @param l a labeling
     */
    private void learnLabeling(Labeling l) {
        for (Attack attack: this.theory.getAttacks()) {
            Argument a = attack.getAttacker();
            Argument b = attack.getAttacked();
            ArgumentStatus lab_a = l.get(a);
            ArgumentStatus lab_b = l.get(b);

            if (lab_a == ArgumentStatus.IN && lab_b == ArgumentStatus.IN) {
                // Rule 1
                this.update(new Attack(b, a), -1.0);

            } else if (lab_a == ArgumentStatus.IN && lab_b == ArgumentStatus.UNDECIDED) {
                // Rule 2
                this.update(attack, -1.0);
                this.update(new Attack(b, a), -1.0);
            } else if (a != b && lab_a == ArgumentStatus.UNDECIDED && lab_b == ArgumentStatus.UNDECIDED) {
                // Rule 3
                Set<Argument> possibleAttackers = this.theory.getAttackers(a);
                possibleAttackers.addAll(this.theory.getAttackers(b));
                for (Argument c: possibleAttackers) {
                    if (c != a && c != b && (l.get(c) == ArgumentStatus.OUT || l.get(c) == null)) {
                        this.update(attack, 1.0);
                        this.update(new Attack(b, a), 1.0);
                        break;
                    }
                }

            } else if (lab_a == ArgumentStatus.OUT && lab_b == ArgumentStatus.IN) {
                // Rule 4
                for (Argument c: this.theory.getAttackers(a)) {
                    if (c != a && c != b && l.get(c) != ArgumentStatus.IN) {
                        this.update(attack, -1.0);
                        this.update(new Attack(b, a), 1.0);
                        break;
                    }
                }
            } else if (a == b && lab_a == ArgumentStatus.UNDECIDED) {
                //test rule
                this.update(attack,1.0);
            }

        }
    }

    /**
     * add the given value to the weight of the given attack
     * @param attack an attack
     * @param delta value to add
     */
    private void update(Attack attack, double delta) {
        if (this.theory.isAttackedBy(attack.getAttacked(), attack.getAttacker())) {
            double oldValue = theory.updateWeight(attack, delta);
            // track undecided attacks
            if (oldValue + delta == 0.0) {
                undecidedAttacks++;
            } else if (oldValue == 0.0) {
                undecidedAttacks--;
            }
        }
    }

    /**
     * learn random labelings from the given List until no undecided attacks are left in the theory
     * @param labelings a list of labelings
     * @param prune if true, remove discarded attacks after each step
     * @return the learned dung theory
     */
    public DungTheory learnLabelings(ArrayList<Labeling> labelings, boolean prune, int threshold) {
        while (this.cycles < this.MAX_CYCLES) {
            this.cycles++;
            // while there are undecided attacks, take random labeling with equal probability and learn it
            // TODO implement probability distribution for labelings
            int idx = new Random().nextInt(labelings.size());
            Labeling l = labelings.get(idx);
            this.learnLabeling(l);

            if (prune)
                this.theory.removeDiscardedAttacks(threshold);
        }
        return this.theory.getDungTheory();
    }

    /**
     * learn theory without pruning discarded attacks
     * @param labelings a list of labelings
     * @return the learned dung theory
     */
    public DungTheory learnLabelings(ArrayList<Labeling> labelings) {
        return this.learnLabelings(labelings, false, 0);
    }
}
